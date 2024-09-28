package com.knightleo.bateponto.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.aakira.napier.Napier

internal fun migrationV1toV2(defaultJobName: String): Migration {
    return object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            Napier.d("Migrating database from version 1 to 2")
            val q1 =
                "CREATE TABLE IF NOT EXISTS `Job` (`name` TEXT NOT NULL, `userId` INTEGER NOT NULL, `startTime` TEXT, `lunchTime` TEXT, `lunchEndTime` TEXT, `endTime` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`userId`) REFERENCES `User`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )"
            val q2 =
                "INSERT INTO Job (`name`, `userId`, `startTime`, `lunchTime`, `lunchEndTime`, `endTime`, `id`) VALUES('$defaultJobName', 1, NULL, NULL, NULL, NULL, 0)"
            val q3 = "ALTER TABLE DayMark RENAME COLUMN userId TO jobId"
            val q4 = "UPDATE DayMark SET `jobId`=0"
            val q5 = "ALTER TABLE DayMark RENAME TO DayMarkOld"
            val q6 =
                "CREATE TABLE IF NOT EXISTS `DayMark` (`day` TEXT NOT NULL, `jobId` INTEGER NOT NULL, PRIMARY KEY(`day`), FOREIGN KEY(`jobId`) REFERENCES `Job`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )"
            val q7 = "INSERT INTO DayMark SELECT * FROM DayMarkOld"
            val q8 = "DROP TABLE DayMarkOld"
            val q9 = "CREATE INDEX IF NOT EXISTS `index_DayMark_jobId` ON `DayMark` (`jobId`)"
            val q10 = "CREATE INDEX IF NOT EXISTS `index_Job_userId` ON `Job` (`userId`)"

            val queries = arrayOf(q1, q2, q3, q4, q5, q6, q7, q8, q9, q10)

            db.execSQL("PRAGMA foreign_keys=OFF")
            db.beginTransaction()
            try {
                queries.forEach { db.execSQL(it) }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
                db.execSQL("PRAGMA foreign_keys=ON")
            }
        }
    }
}
