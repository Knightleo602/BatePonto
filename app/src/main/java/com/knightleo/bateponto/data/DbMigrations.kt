package com.knightleo.bateponto.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private val migration_1_2: Migration
    get() = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val q1 = "CREATE TABLE IF NOT EXISTS `Job` (`name` TEXT NOT NULL, `userId` " +
                    "INTEGER NOT NULL, `startTime` TEXT, `lunchTime` TEXT, `lunchEndTime` TEXT, " +
                    "`endTime` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL;\n"
            val q2 = "ALTER TABLE `DayMark` RENAME COLUMN `userId` TO `jobId`;"
            db.execSQL(q1 + q2)
        }
    }


internal val MIGRATIONS = arrayOf(migration_1_2)
