package com.fei.semestralnezadanie.backend.api

import com.fei.semestralnezadanie.Config
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(Config.Api.BASE_URL)
    .build()

object Api {
    val retrofitService: Controller by lazy {
        retrofit.create(Controller::class.java)
    }
}