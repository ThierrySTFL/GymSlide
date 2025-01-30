package com.example.slidegym.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "WorkoutDB"
        private const val DATABASE_VERSION = 1

        const val TABLE_EXERCISES = "exercises"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_REPS = "reps"
        const val COLUMN_IS_COMPLETED = "is_completed"
        const val COLUMN_WEEKDAY = "weekday"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_EXERCISES_TABLE = """
            CREATE TABLE $TABLE_EXERCISES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_WEIGHT REAL NOT NULL,
                $COLUMN_REPS INTEGER NOT NULL,
                $COLUMN_IS_COMPLETED INTEGER NOT NULL DEFAULT 0,
                $COLUMN_WEEKDAY TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(CREATE_EXERCISES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISES")
        onCreate(db)
    }
}
