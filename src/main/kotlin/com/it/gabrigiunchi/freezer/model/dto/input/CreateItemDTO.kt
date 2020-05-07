package com.it.gabrigiunchi.freezer.model.dto.input

import com.it.gabrigiunchi.freezer.enums.ItemType
import java.time.OffsetDateTime

data class CreateItemDTO(
        val name: String,
        val type: ItemType,
        val quantity: Int,
        val storeDate: OffsetDateTime,
        val expirationDate: OffsetDateTime?,
        val note: String
)