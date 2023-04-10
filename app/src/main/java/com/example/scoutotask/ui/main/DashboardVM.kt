package com.example.scoutotask.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scoutotask.utils.*
import kotlinx.coroutines.launch

class DashboardVM : ViewModel() {

    private var carsDb : CarsDb? = null

    var makesList = listOf<Make>()
    var modelsList = listOf<Model>()
    val makeToIdMap = HashMap<String, Int>()
    val makes = arrayListOf<String>()
    val models = arrayListOf<String>()
    val listProcessed = MutableLiveData(false)
    val modelListProcessed = MutableLiveData(false)

    var carsList = mutableListOf<Car>()
    val carsListProcessed = MutableLiveData(false)

    fun getMakes() {
        viewModelScope.launch {
            RetrofitClient.getMakes()
        }
    }

    fun getModel(makeId: Int) {
        viewModelScope.launch {
            RetrofitClient.getModel(makeId)
        }
    }

    fun getMakesList() {
        makesList.forEach {
            it.makeName?.let { it1 -> makes.add(it1) }
            it.makeName?.let { it1 -> makeToIdMap[it1] = it.makeId }
        }
        listProcessed.value = true
    }

    fun getModelsList() {
        models.clear()
        modelsList.forEach {
            it.modelName?.let { it1 -> models.add(it1) }
        }
        modelListProcessed.value = true
    }

    fun addCar(userId: Int, carMake: String, carModel: String, context: Context) {
        viewModelScope.launch {
            if(carsDb == null) {
                carsDb = CarsDb(context)
            }

            carsDb?.insertCar(carMake, carModel, null, userId)
            getCars(userId, context)
        }
    }

    fun deleteCar(carId: Int, userId: Int, context: Context) {
        viewModelScope.launch {
            if(carsDb == null) {
                carsDb = CarsDb(context)
            }

            carsDb?.deleteCar(carId)
            getCars(userId, context)
        }
    }

    fun getCars(userId: Int, context: Context) {
        viewModelScope.launch {
            if(carsDb == null) {
                carsDb = CarsDb(context)
            }

            carsList = carsDb?.getCarsData(userId) as MutableList<Car>
            carsListProcessed.value = true
        }
    }

    fun updateImg(carId: Int, carMake: String, carModel: String , imgUri: String, context: Context, userId: Int) {
        viewModelScope.launch {
            if(carsDb == null) {
                carsDb = CarsDb(context)
            }

            carsDb?.updateCar(carId, carMake, carModel, imgUri)
            getCars(userId, context)
        }
    }

    fun closeDb() {
        carsDb?.closeDb()
    }

}