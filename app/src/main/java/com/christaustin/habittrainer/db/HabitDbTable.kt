package com.christaustin.habittrainer.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.christaustin.habittrainer.Habit
import java.io.ByteArrayOutputStream

/**
 * Class to handle database table
 */
class HabitDbTable(context: Context) {
    private val TAG = HabitDbTable::class.java.simpleName
    private val dbHelper = HabitTrainerDb(context)

    fun store(habit: Habit): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        with (values) {
            put(HabitEntry.TITLE_COL, habit.title)
            put(HabitEntry.DESCR_COL, habit.description)
            put(HabitEntry.IMAGE_COL, toByteArray(habit.image))
        }

        val id = db.transaction {
            insert(HabitEntry.TABLE_NAME, null, values)
        }

        Log.d(TAG, "Stored a new habit to the DB $habit")

        return id
    }

    fun readAllHabits(): List<Habit> {
        val cols = arrayOf(HabitEntry._ID, HabitEntry.TITLE_COL, HabitEntry.DESCR_COL, HabitEntry.IMAGE_COL)
        val order = "${HabitEntry._ID} ASC"
        val db = dbHelper.readableDatabase

        val cursor = db.doQuery(HabitEntry.TABLE_NAME, cols, orderBy = order)

        val habits = mutableListOf<Habit>()

        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndex(HabitEntry.TITLE_COL))
            val description = cursor.getString(cursor.getColumnIndex(HabitEntry.DESCR_COL))
            val byteArray = cursor.getBlob(cursor.getColumnIndex(HabitEntry.IMAGE_COL))
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            habits.add(Habit(title, description, bitmap))
        }
        cursor.close()

        return habits
    }

    private fun toByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }
}

private fun SQLiteDatabase.doQuery(table: String, columns: Array<String>, selection: String? = null,
                                   selectionArgs: Array<String>? = null, groupBy: String? = null,
                                   having: String? = null, orderBy: String? = null): Cursor {
    return query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
}

private inline fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.() -> T): T {
    beginTransaction()
    val result = try {
        val returnValue = function()
        setTransactionSuccessful()

        returnValue
    } finally {
        endTransaction()
    }
    close()

    return result
}