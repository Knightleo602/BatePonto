package com.knightleo.bateponto.data

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.knightleo.bateponto.ui.stringPadded
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationsTest {
    private val testDbName = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun migrateToV2() {
        var db = helper.createDatabase(testDbName, 1).apply {
            execSQL("INSERT INTO User VALUES(1, 'Knight')")
            repeat(5) {
                val s = it.stringPadded()
                execSQL("INSERT INTO DayMark VALUES('2023-01-$s, 1)")
                execSQL("INSERT INTO TimeMark VALUES('08-00-00', '2023-01-$s")
            }
            close()
        }
        db = helper.runMigrationsAndValidate(testDbName, 2, true)
    }
}