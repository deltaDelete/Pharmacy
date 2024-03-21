package ru.deltadelete.pharmacy.api.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Drug(
    @JsonProperty("drugId")
    val id: Long,
    val name: String,
    val description: String,
    val price: Float
) {
    val image: String
        @JsonIgnore
        get() = "http://localhost:3001/api/pharmacy/drug/$id/image"
}