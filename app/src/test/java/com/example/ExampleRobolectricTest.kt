package com.example

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.example.ui.adaptive.ViewportMode
import com.example.ui.adaptive.WindowSizeClassType
import com.example.ui.adaptive.calculateEffectiveSizeClass
import com.example.ui.adaptive.calculateWindowSizeClass
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SharePlate", appName)
  }

  @Test
  fun `test desktop breakpoint at 1024dp and 1440dp`() {
    assertEquals(WindowSizeClassType.EXPANDED, calculateWindowSizeClass(1024.dp))
    assertEquals(WindowSizeClassType.EXPANDED, calculateWindowSizeClass(1440.dp))
    assertEquals(WindowSizeClassType.EXPANDED, calculateWindowSizeClass(1920.dp))
  }

  @Test
  fun `test tablet breakpoint between 768dp and 1023dp`() {
    assertEquals(WindowSizeClassType.MEDIUM, calculateWindowSizeClass(768.dp))
    assertEquals(WindowSizeClassType.MEDIUM, calculateWindowSizeClass(800.dp))
    assertEquals(WindowSizeClassType.MEDIUM, calculateWindowSizeClass(1023.dp))
  }

  @Test
  fun `test mobile breakpoint below 768dp`() {
    assertEquals(WindowSizeClassType.COMPACT, calculateWindowSizeClass(320.dp))
    assertEquals(WindowSizeClassType.COMPACT, calculateWindowSizeClass(375.dp))
    assertEquals(WindowSizeClassType.COMPACT, calculateWindowSizeClass(411.dp))
    assertEquals(WindowSizeClassType.COMPACT, calculateWindowSizeClass(767.dp))
  }

  @Test
  fun `test viewport modes`() {
    assertEquals(WindowSizeClassType.EXPANDED, calculateEffectiveSizeClass(400.dp, ViewportMode.DESKTOP))
    assertEquals(WindowSizeClassType.MEDIUM, calculateEffectiveSizeClass(400.dp, ViewportMode.TABLET))
    assertEquals(WindowSizeClassType.COMPACT, calculateEffectiveSizeClass(1200.dp, ViewportMode.MOBILE))
    assertEquals(WindowSizeClassType.EXPANDED, calculateEffectiveSizeClass(1440.dp, ViewportMode.AUTO))
    assertEquals(WindowSizeClassType.COMPACT, calculateEffectiveSizeClass(375.dp, ViewportMode.AUTO))
  }
}
