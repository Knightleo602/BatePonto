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
    addMigrations(*MIGRATIONS)
    build()
}