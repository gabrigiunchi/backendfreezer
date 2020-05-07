package com.it.gabrigiunchi.freezer.model.dto.output

import com.it.gabrigiunchi.freezer.enums.ItemType
import com.it.gabrigiunchi.freezer.model.Item
import java.time.OffsetDateTime

data class ItemDTO(
        val id: Int,
        val name: String,
        val type: ItemType,
        val quantity: Int,
        val storeDate: OffsetDateTime,
        val expirationDate: OffsetDateTime?,
        val note: String,
        val userId: Int
) {
    constructor(item: Item) : this(
            item.id,
            item.name,
            item.type,
            item.quantity,
            item.storeDate,
            item.expirationDate,
            item.note,
            item.user.id

    )
}