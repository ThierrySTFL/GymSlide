package com.example.slidegym.data.local

import android.content.ContentValues
import android.content.Context
import com.example.slidegym.data.model.Exercise
import com.example.slidegym.data.model.WeekDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ExerciseRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    suspend fun addExercise(exercise: Exercise, weekDay: WeekDay): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, exercise.name)
            put(DatabaseHelper.COLUMN_WEIGHT, exercise.weight)
            put(DatabaseHelper.COLUMN_REPS, exercise.reps)
            put(DatabaseHelper.COLUMN_IS_COMPLETED, if (exercise.isCompleted) 1 else 0)
            put(DatabaseHelper.COLUMN_WEEKDAY, weekDay.name)
        }

        db.insert(DatabaseHelper.TABLE_EXERCISES, null, values)
    }

    suspend fun getExercisesForDay(weekDay: WeekDay): List<Exercise> = withContext(Dispatchers.IO) {
        val exercises = mutableListOf<Exercise>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_EXERCISES,
            null,
            "${DatabaseHelper.COLUMN_WEEKDAY} = ?",
            arrayOf(weekDay.name),
            null,
            null,
            "${DatabaseHelper.COLUMN_ID} DESC"
        )

        while (cursor.moveToNext()) {
            exercises.add(
                Exercise(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                    weight = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT)),
                    reps = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REPS)),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_COMPLETED)) == 1
                )
            )
        }

        cursor.close()
        exercises
    }

    suspend fun updateExercise(exercise: Exercise): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, exercise.name)
            put(DatabaseHelper.COLUMN_WEIGHT, exercise.weight)
            put(DatabaseHelper.COLUMN_REPS, exercise.reps)
            put(DatabaseHelper.COLUMN_IS_COMPLETED, if (exercise.isCompleted) 1 else 0)
        }

        db.update(
            DatabaseHelper.TABLE_EXERCISES,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(exercise.id.toString())
        )
    }

    suspend fun deleteExercise(exerciseId: Long): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase

        db.delete(
            DatabaseHelper.TABLE_EXERCISES,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(exerciseId.toString())
        )
    }
}
