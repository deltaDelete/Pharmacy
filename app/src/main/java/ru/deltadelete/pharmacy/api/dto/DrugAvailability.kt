package ru.deltadelete.pharmacy.api.dto

data class DrugAvailability(
    val drugId: Long,
    val quantity: Int,
    val warehouses: List<Warehouse>
)

data class Warehouse(
    val warehouseId: Long,
    val name: String
)
