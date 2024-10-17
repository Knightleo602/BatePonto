package com.knightleo.bateponto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.knightleo.bateponto.data.entity.DayMark
import com.knightleo.bateponto.data.entity.Job
import com.knightleo.bateponto.data.entity.TimeMark
import com.knightleo.bateponto.data.entity.User
import com.knightleo.bateponto.domain.R

private const val DB_NAME = "bateponto-db"

@Database(
    entities = [User::class, DayMark::class, TimeMark::class, Job::class],
    version = 2,
)
@TypeConverters(
    DayTypeConverter::class,
    OffsetTimeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dayMarkDao(): DayMarkDAO
}

internal fun buildDatabase(context: Context): AppDatabase = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    DB_NAME
).run {
    val defaultJobName = context.getString(R.string.default_job_name)
    addMigrations(migrationV1toV2(defaultJobName))
    build()
}