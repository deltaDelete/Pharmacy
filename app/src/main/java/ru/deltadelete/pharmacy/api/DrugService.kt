package ru.deltadelete.pharmacy.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.deltadelete.pharmacy.api.dto.Drug
import ru.deltadelete.pharmacy.api.dto.DrugAvailability

interface DrugService {
    @GET("drug")
    fun getDrugList(
        @Query("take") take: Long? = null,
        @Query("skip") skip: Long? = null
    ): Call<List<Drug>>

    @GET("drug/{id}/amount")
    suspend fun getDrugIdAmount(
        @Path("id")
        id: Long
    ): Response<List<DrugAvailability>>

    suspend fun getDrugListAsync(
        @Query("take") take: Long? = null,
        @Query("skip") skip: Long? = null
    ): Response<List<Drug>>
}