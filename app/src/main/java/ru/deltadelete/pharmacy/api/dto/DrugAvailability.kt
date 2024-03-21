package ru.deltadelete.pharmacy.api.dto

data class DrugAvailability(
    val drugId: Long,
    val quantity: Int,
    val warehouses: List<WarehouseDto>
)

