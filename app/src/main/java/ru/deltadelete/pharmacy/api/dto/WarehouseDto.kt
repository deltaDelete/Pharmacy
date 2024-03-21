package ru.deltadelete.pharmacy.api.dto

/**
 * Класс данных используемый только вместе с [DrugAvailability]
 */
data class WarehouseDto(
    val warehouseId: Long,
    val name: String,
    val quantity: Int
)