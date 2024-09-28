package com.knightleo.bateponto.data

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.knightleo.bateponto.ui.stringPadded
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.OffsetTime

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
    fun migrateToV2Test() {
        helper.createDatabase(testDbName, 1).apply {
            execSQL("INSERT INTO User VALUES(1, 'Knight')")
            repeat(5) {
                val s = (it + 1).stringPadded()
                execSQL("""INSERT INTO DayMark VALUES('2023-01-$s', 1)""")
                val offsetTime = OffsetTime.of(
                    8,
                    0,
                    0,
                    0 + it,
                    OffsetTime.now().offset
                )
                execSQL("""INSERT INTO TimeMark VALUES('$offsetTime', '2023-01-$s')""")
            }
            close()
        }
        val db = helper.runMigrationsAndValidate(testDbName, 2, true, migrationV1toV2("job"))
        assertEquals(1, db.query("SELECT COUNT(*) FROM User").let {
            it.moveToFirst()
            it.getInt(0)
        })
        assertEquals(5, db.query("SELECT COUNT(*) FROM DayMark").let {
            it.moveToFirst()
            it.getInt(0)
        })
        assertEquals(5, db.query("SELECT COUNT(*) FROM TimeMark").let {
            it.moveToFirst()
            it.getInt(0)
        })
        val jobs = db.query("SELECT * FROM Job").also {
            it.moveToFirst()
        }
        assertEquals(1, jobs.count)
        assertEquals("job", jobs.getString(0))
        val names = db.query("SELECT * FROM DayMark").columnNames
        assertArrayEquals(arrayOf("day", "jobId"), names)
        val exec =
            db.query("SELECT * FROM TimeMark INNER JOIN DayMark ON TimeMark.day=DayMark.day WHERE DayMark.jobId=0")
        assertEquals(5, exec.count)
    }
}