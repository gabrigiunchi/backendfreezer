package com.it.gabrigiunchi.freezer

import com.fasterxml.jackson.databind.ObjectMapper
import com.it.gabrigiunchi.freezer.model.dto.input.CreateItemDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import java.io.IOException

@WithMockUser(username = "gabrigiunchi", password = "aaaa", authorities = ["ADMINISTRATOR"])
abstract class BaseRestTest : BaseTest() {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Throws(IOException::class)
    protected fun json(o: Any): String {
        return ObjectMapper().writeValueAsString(o)
    }

    @Throws(IOException::class)
    protected fun json(o: CreateItemDTO): String {
        return ObjectMapper().writeValueAsString(mapOf(
                Pair("name", o.name),
                Pair("type", o.type),
                Pair("quantity", o.quantity),
                Pair("storeDate", o.storeDate.toString()),
                Pair("expirationDate", o.expirationDate.toString()),
                Pair("note", o.note)
        ))
    }

}