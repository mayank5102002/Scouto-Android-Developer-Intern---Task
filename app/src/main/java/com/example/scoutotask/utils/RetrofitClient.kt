package com.example.scoutotask.utils

import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "https://vpic.nhtsa.dot.gov/api/"

    var makesList : List<Make>? = null
    val makesFetched = MutableLiveData(false)

    var modelList : List<Model>? = null
    val modelsFetched = MutableLiveData(false)

    private var apiService : ApiService? = null

    fun getMakes() {
        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create API service
        apiService = retrofit.create(ApiService::class.java)

        // Make first API call to get all makes
        apiService?.allMakes?.enqueue(object : Callback<MakeResponse?> {
            override fun onResponse(call: Call<MakeResponse?>, response: Response<MakeResponse?>) {
                if (response.isSuccessful) {
                    val apiResponse: MakeResponse? = response.body()
                    makesList = apiResponse?.results
                    makesFetched.value = true
                } else {
                    // Handle error response for first API call
                    println(
                        "Error: " + response.code().toString() + " " + response.message()
                    )
                }
            }

            override fun onFailure(call: Call<MakeResponse?>, t: Throwable) {
                // Handle network error for first API call
                t.printStackTrace()
            }
        })
    }

    fun getModel(makeId: Int) {
        apiService?.getModelsForMakeId(makeId)?.enqueue(object : Callback<ModelResponse?> {
            override fun onResponse(
                call: Call<ModelResponse?>,
                response: Response<ModelResponse?>
            ) {
                if (response.isSuccessful) {
                    val modelResponse: ModelResponse? = response.body()
                    val models: List<Model>? = modelResponse?.results
                    // Handle the response data for models
                    if (models != null) {
                        modelList = models
                        modelsFetched.value = true
                    }
                } else {
                    // Handle error response for second API call
                    println(
                        "Error: " + response.code()
                            .toString() + " " + response.message()
                    )
                }
            }

            override fun onFailure(call: Call<ModelResponse?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}