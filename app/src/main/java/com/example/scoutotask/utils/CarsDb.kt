package com.example.scoutotask.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CarsDb (context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cars.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_CARS = "cars"
        private const val COLUMN_ID = "id"
        private const val COLUMN_CAR_MAKE = "car_make"
        private const val COLUMN_CAR_MODEL = "car_model"
        private const val COLUMN_CAR_IMAGE = "car_image"
        private const val COLUMN_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_CARS_TABLE = ("CREATE TABLE $TABLE_CARS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_CAR_MAKE TEXT, "
                + "$COLUMN_CAR_MODEL TEXT, "
                + "$COLUMN_CAR_IMAGE TEXT, "
                + "$COLUMN_USER_ID INTEGER NOT NULL)")
        db.execSQL(CREATE_CARS_TABLE)
    }

    // Upgrade the database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Upgrade logic, if needed
    }

    // Insert a new car entry into the "cars" table
    fun insertCar(carMake: String, carModel: String, carImage: String?, userId: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("car_make", carMake)
        values.put("car_model", carModel)
        values.put("car_image", carImage)
        values.put("user_id", userId)
        val id = db.insert("cars", null, values)
        db.close()
        return id
    }

    // Update a car entry in the "cars" table based on the car ID
    fun updateCar(carId: Int, carMake: String, carModel: String, carImage: String?): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("car_make", carMake)
        values.put("car_model", carModel)
        values.put("car_image", carImage)
        val rowsAffected = db.update("cars", values, "id=?", arrayOf(carId.toString()))
        db.close()
        return rowsAffected
    }

    // Delete a car entry from the "cars" table based on the car ID
    fun deleteCar(carId: Int): Int {
        val db = this.writableDatabase
        val rowsAffected = db.delete("cars", "id=?", arrayOf(carId.toString()))
        db.close()
        return rowsAffected
    }

    @SuppressLint("Range")
    fun getCarsData(user: Int): List<Car> {
        val carsList = mutableListOf<Car>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_CARS WHERE $COLUMN_USER_ID = $user"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val carId = cursor.getInt(cursor.getColumnIndex("id"))
                val carMake = cursor.getString(cursor.getColumnIndex("car_make"))
                val carModel = cursor.getString(cursor.getColumnIndex("car_model"))
                val carImage = cursor.getString(cursor.getColumnIndex("car_image"))
                val userId = cursor.getInt(cursor.getColumnIndex("user_id"))

                if (user != userId) {
                    continue
                }
                val car = Car(carId, carMake, carModel, carImage, userId)
                carsList.add(car)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return carsList
    }

    // Close the database connection
    fun closeDb() {
        val db = this.readableDatabase
        if (db != null && db.isOpen) {
            db.close()
        }
    }
}

data class Car(val id: Int, val carMake: String, val carModel: String, val carImage: String?, val userId: Int)