package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SharePlateDatabase
import com.example.data.local.SharePlateLocalRepository
import com.example.data.local.dao.HistoricalRescueDao
import com.example.data.local.dao.OfflineDraftDao
import com.example.data.local.entity.HistoricalRescueEntity
import com.example.data.local.entity.OfflineDraftEntity
import com.example.model.DietaryType
import com.example.model.FoodCategory
import com.example.model.OfflineListingDraft
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SharePlateRoomDatabaseTest {

    private lateinit var database: SharePlateDatabase
    private lateinit var draftDao: OfflineDraftDao
    private lateinit var rescueDao: HistoricalRescueDao
    private lateinit var localRepository: SharePlateLocalRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SharePlateDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        draftDao = database.offlineDraftDao()
        rescueDao = database.historicalRescueDao()
        localRepository = SharePlateLocalRepository(draftDao, rescueDao)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testInsertAndRetrieveOfflineDraft() = runBlocking {
        val draft = OfflineDraftEntity(
            id = "draft-101",
            title = "Surplus Artisan Baguettes",
            description = "Crispy baguettes baked at 05:00 AM.",
            category = FoodCategory.BAKED_GOODS.name,
            dietaryType = DietaryType.VEGAN.name,
            quantity = 14.5,
            unit = "kg",
            estimatedServings = 35,
            prepTime = "Today, 05:00",
            useByDeadline = "Tomorrow, 20:00",
            pickupDeadline = "Tonight, 21:00",
            storageCondition = "Ambient dry",
            temperatureReading = "20.5°C",
            allergens = listOf("Gluten", "Sesame"),
            packagingRequirement = "Bakery paper sacks",
            pickupWindow = "19:00 - 21:00",
            address = "42 Baker Street",
            safetyAttested = true,
            isSynced = false
        )

        draftDao.insertDraft(draft)

        val retrieved = draftDao.getDraftById("draft-101")
        assertNotNull(retrieved)
        assertEquals("Surplus Artisan Baguettes", retrieved?.title)
        assertEquals(2, retrieved?.allergens?.size)
        assertTrue(retrieved?.allergens?.contains("Sesame") == true)
        assertFalse(retrieved?.isSynced == true)

        val pending = draftDao.getPendingDrafts().first()
        assertEquals(1, pending.size)
        assertEquals("draft-101", pending[0].id)
    }

    @Test
    fun testMarkDraftAsSyncedAndClear() = runBlocking {
        val draft1 = OfflineDraftEntity(
            id = "draft-sync-1",
            title = "Vegetable Stew Trays",
            description = "Hot stew chilled to 3.4°C",
            category = FoodCategory.COOKED_MEALS.name,
            dietaryType = DietaryType.VEGETARIAN.name,
            quantity = 20.0,
            unit = "kg",
            estimatedServings = 40,
            prepTime = "Today, 11:00",
            useByDeadline = "Tomorrow, 14:00",
            pickupDeadline = "Today, 18:00",
            storageCondition = "Rapid chilled",
            packagingRequirement = "Stainless food pans",
            pickupWindow = "16:00 - 18:00",
            address = "12 Main St",
            safetyAttested = true,
            isSynced = false
        )

        draftDao.insertDraft(draft1)
        assertEquals(1, draftDao.getPendingDraftsCount().first())

        draftDao.markDraftAsSynced("draft-sync-1")
        val updated = draftDao.getDraftById("draft-sync-1")
        assertTrue(updated?.isSynced == true)
        assertEquals(0, draftDao.getPendingDraftsCount().first())

        // Clear synced drafts
        draftDao.clearSyncedDrafts()
        val allDrafts = draftDao.getAllDrafts().first()
        assertTrue(allDrafts.isEmpty())
    }

    @Test
    fun testDomainDraftConversionInRepository() = runBlocking {
        val domainDraft = OfflineListingDraft(
            id = "domain-draft-55",
            title = "Organic Fruit Crates",
            description = "Apples, bananas and oranges from corporate pantry.",
            category = FoodCategory.FRESH_PRODUCE,
            dietaryType = DietaryType.VEGAN,
            quantity = 18.0,
            unit = "kg",
            estimatedServings = 45,
            prepTime = "Today, 09:00",
            useByDeadline = "In 3 days",
            pickupDeadline = "Tonight, 20:00",
            storageCondition = "Ambient cool",
            temperatureReading = "14.0°C",
            allergens = emptyList(),
            packagingRequirement = "Wooden crates",
            pickupWindow = "17:00 - 20:00",
            address = "500 Tech Blvd",
            safetyAttested = true
        )

        localRepository.saveDomainDraft(domainDraft)

        val retrieved = draftDao.getDraftById("domain-draft-55")
        assertNotNull(retrieved)
        val convertedBack = retrieved!!.toDomainModel()
        assertEquals(FoodCategory.FRESH_PRODUCE, convertedBack.category)
        assertEquals(DietaryType.VEGAN, convertedBack.dietaryType)
        assertEquals(18.0, convertedBack.quantity, 0.01)
    }

    @Test
    fun testHistoricalRescuePersistenceAndAggregations() = runBlocking {
        val rescue1 = HistoricalRescueEntity(
            id = "hist-asgn-1",
            assignmentId = "asgn-1",
            listingId = "list-1",
            listingTitle = "Roasted Chicken & Veg Trays",
            category = FoodCategory.COOKED_MEALS.name,
            donorOrg = "The Grand Horizon Hotel",
            pickupAddress = "100 Grand Ave",
            recipientOrg = "Hope Valley Community Shelter",
            dropoffAddress = "250 River St",
            volunteerId = "vol-1",
            volunteerName = "Marcus Rivera",
            quantityKg = 24.5,
            estimatedMeals = 61,
            co2PreventedKg = 61.25,
            status = "DELIVERED",
            transitTemperatureCelsius = 3.6,
            handoverToken = "QR-DELIVERY-1",
            notes = "Cold chain verified compliant."
        )

        val rescue2 = HistoricalRescueEntity(
            id = "hist-asgn-2",
            assignmentId = "asgn-2",
            listingId = "list-2",
            listingTitle = "Ciabatta & Sourdough Loaves",
            category = FoodCategory.BAKED_GOODS.name,
            donorOrg = "Old Town Bakery",
            pickupAddress = "45 High St",
            recipientOrg = "Hope Valley Community Shelter",
            dropoffAddress = "250 River St",
            volunteerId = "vol-2",
            volunteerName = "Aisha Patel",
            quantityKg = 15.5,
            estimatedMeals = 38,
            co2PreventedKg = 38.75,
            status = "DELIVERED",
            transitTemperatureCelsius = null,
            handoverToken = "QR-DELIVERY-2",
            notes = "Delivered on time."
        )

        rescueDao.insertRescues(listOf(rescue1, rescue2))

        val allRescues = rescueDao.getAllHistoricalRescues().first()
        assertEquals(2, allRescues.size)

        val recipientRescues = rescueDao.getRescuesByRecipient("Hope Valley Community Shelter").first()
        assertEquals(2, recipientRescues.size)

        val totalKg = rescueDao.getTotalRescuedKg().first()
        assertEquals(40.0, totalKg, 0.01)

        val totalMeals = rescueDao.getTotalEstimatedMeals().first()
        assertEquals(99, totalMeals)

        val totalCo2 = rescueDao.getTotalCo2PreventedKg().first()
        assertEquals(100.0, totalCo2, 0.01)
    }
}
