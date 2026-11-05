package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Pad Start", appName)
    }

    @Test
    fun `toggle pinned package updates preferences`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = AppRepository(context)

        val testPkg = "com.test.sampleapp"
        val initiallyPinned = repository.isPinned(testPkg)

        repository.togglePin(testPkg)
        assertEquals(!initiallyPinned, repository.isPinned(testPkg))

        repository.togglePin(testPkg)
        assertEquals(initiallyPinned, repository.isPinned(testPkg))
    }
}
