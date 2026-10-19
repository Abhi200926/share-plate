package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

    val isConfigured: Boolean
        get() = apiKey.isNotBlank() && !apiKey.contains("MY_GEMINI_API_KEY")

    suspend fun generateText(
        prompt: String,
        systemInstruction: String = "You are SharePlate AI, an expert food rescue logistics and food safety assistant.",
        model: String = "gemini-2.5-flash"
    ): Result<String> = withContext(Dispatchers.IO) {
        if (!isConfigured) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured or is a placeholder. Using local rule-based engine.")
            )
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                // System Instruction
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    })
                })
                // Contents
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
                // Generation Config
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.4)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                return@withContext Result.failure(Exception("Gemini API returned code ${response.code}: $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    return@withContext Result.success(text)
                }
            }
            Result.failure(Exception("No content returned from Gemini candidate."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzeFoodSafetyImage(
        base64Image: String,
        description: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (!isConfigured) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key not configured. Using rule-based safety checklist.")
            )
        }

        try {
            val model = "gemini-3.1-pro-preview"
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

            val prompt = """
                You are a senior Certified Food Safety Auditor for SharePlate.
                Inspect this image of surplus food packaging and prep.
                Food details: $description
                
                Evaluate:
                1. Container integrity, tamper-evident seals, and packaging cleanliness.
                2. Visual freshness cues (discoloration, condensation, oxidation).
                3. Temperature holding and transport risk assessment.
                4. Mandatory recommendation: APPROVE for listing or FLAG for physical auditor check.
                
                IMPORTANT DISCLAIMER: Human physical inspection and calibrated thermometer readings are legally mandatory before distribution.
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", "image/jpeg")
                                    put("data", base64Image)
                                })
                            })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                return@withContext Result.failure(Exception("Gemini vision call failed: $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val text = candidates.getJSONObject(0)
                    .optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.getJSONObject(0)
                    ?.optString("text")
                if (!text.isNullOrBlank()) {
                    return@withContext Result.success(text)
                }
            }
            Result.failure(Exception("Empty vision inspection response."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
