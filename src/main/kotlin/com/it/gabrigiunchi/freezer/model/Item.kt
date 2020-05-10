package com.it.gabrigiunchi.freezer.model

import com.it.gabrigiunchi.freezer.enums.ItemType
import com.it.gabrigiunchi.freezer.model.dto.input.CreateItemDTO
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
class Item(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Int,
        var name: String,

        @Enumerated
        var type: ItemType,
        var quantity: Int,
        var storeDate: OffsetDateTime,
        var expirationDate: OffsetDateTime?,
        var note: String,

        @ManyToOne
        @OnDelete(action = OnDeleteAction.CASCADE)
        val user: AppUser
) {

    constructor(name: String, type: ItemType, quantity: Int, storeDate: OffsetDateTime, expirationDate: OffsetDateTime?, user: AppUser) :
            this(-1, name, type, quantity, storeDate, expirationDate, "", user)

    constructor(name: String, type: ItemType, quantity: Int, storeDate: OffsetDateTime, expirationDate: OffsetDateTime?, note: String, user: AppUser) :
            this(-1, name, type, quantity, storeDate, expirationDate, note, user)

    constructor(id: Int, dto: CreateItemDTO, user: AppUser) : this(
            id,
            dto.name,
            dto.type,
            dto.quantity,
            dto.storeDate,
            dto.expirationDate,
            dto.note,
            user
    )
}