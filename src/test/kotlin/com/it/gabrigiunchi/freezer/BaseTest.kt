package com.it.gabrigiunchi.freezer

import com.it.gabrigiunchi.freezer.dao.ItemDAO
import com.it.gabrigiunchi.freezer.dao.UserDAO
import com.it.gabrigiunchi.freezer.enums.ItemType
import com.it.gabrigiunchi.freezer.enums.UserType
import com.it.gabrigiunchi.freezer.model.AppUser
import com.it.gabrigiunchi.freezer.model.Item
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.OffsetDateTime
import javax.transaction.Transactional

@SpringBootTest(classes = [FreezerApplication::class])
@AutoConfigureMockMvc
@Transactional
abstract class BaseTest {


    @Autowired
    protected lateinit var userDAO: UserDAO

    @Autowired
    protected lateinit var itemDAO: ItemDAO


    fun createMockUser(username: String,
                       password: String = "aaaa",
                       type: UserType = UserType.ADMINISTRATOR,
                       name: String = "aaaa",
                       surname: String = "bbbb"): AppUser =
            this.userDAO.save(
                    AppUser(
                            username,
                            BCryptPasswordEncoder().encode(password),
                            name,
                            surname,
                            "",
                            type
                    )
            )

    fun createMockItem(name: String, user: AppUser, expiredDate: OffsetDateTime = OffsetDateTime.now().plusDays(1)): Item =
            this.itemDAO.save(
                    Item(
                            name,
                            ItemType.OTHER,
                            1,
                            OffsetDateTime.now(),
                            expiredDate,
                            user
                    )
            )
}