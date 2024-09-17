package com.knightleo.bateponto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.knightleo.bateponto.data.entity.User
import com.knightleo.bateponto.data.entity.WorkTime
import kotlin.reflect.KProperty

@Database(entities = [User::class, WorkTime::class], version = 1)
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