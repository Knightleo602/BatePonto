package com.knightleo.bateponto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.knightleo.bateponto.data.entity.DateConverters
import com.knightleo.bateponto.data.entity.DayMark
import com.knightleo.bateponto.data.entity.TimeConverter
import com.knightleo.bateponto.data.entity.TimeMark
import kotlin.reflect.KProperty

@Database(entities = [DayMark::class, TimeMark::class], version = 1)
@TypeConverters(DateConverters::class, TimeConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun dayMarkDao(): DayMarkDAO
}

object DatabaseHolder {
    private var database: AppDatabase? = null
    operator fun getValue(thisRef: Context, property: KProperty<*>): AppDatabase {
        return database ?: run {
            database = Room.databaseBuilder(
                thisRef,
                AppDatabase::class.java,
                "bateponto-db"
            ).build()
            database!!
        }
    }
}