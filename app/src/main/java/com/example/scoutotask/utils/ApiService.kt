package com.example.scoutotask.utils

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiService {
    @get:GET("vehicles/getallmakes?format=json")
    val allMakes: Call<MakeResponse?>?

    @GET("vehicles/GetModelsForMakeId/{makeId}?format=json")
    fun getModelsForMakeId(@Path("makeId") makeId: Int): Call<ModelResponse?>?
}

class MakeResponse {
    @SerializedName("Results")
    val results: List<Make>? = null
}

class ModelResponse{
    @SerializedName("Results")
    val results: List<Model>? = null
}

class Make {
    @SerializedName("Make_ID")
    val makeId = 0

    @SerializedName("Make_Name")
    val makeName: String? = null
}

class Model {
    @SerializedName("Make_ID")
    val makeId = 0

    @SerializedName("Make_Name")
    val makeName: String? = null

    @SerializedName("Model_ID")
    val modelId = 0

    @SerializedName("Model_Name")
    val modelName: String? = null
}