package ru.deltadelete.pharmacy.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create

object ApiClient {

    val drugService: DrugService by lazy {
        create()
    }

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:3001/api/pharmacy/")
        .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
        .build()

    inline fun <reified T> create(): T {
        return retrofit.create<T>()
    }
}

