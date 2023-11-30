package com.example.notes.DateBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notes.Models.Notes

@Database(entities = [Notes::class], version = 4, exportSchema = false)
abstract class RoomDB : RoomDatabase() {

    companion object { //Static objects
        private var database: RoomDB? = null
        private const val DATABASE_NAME = "NoteApp"

        fun getInstance(context: Context): RoomDB {
            return database ?: synchronized(this) {
                database ?: buildDatabase(context).also { database = it }
            }
        }

        private fun buildDatabase(context: Context): RoomDB {
            return Room.databaseBuilder(context, RoomDB::class.java, DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }

        abstract fun mainDao(): MainDao
}