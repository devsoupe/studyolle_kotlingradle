package com.perelandrax.studyolle.account

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    fun signUpForm() {
        mockMvc.perform(get("/sign-up"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(view().name("account/sign-up"))
            .andExpect(model().attributeExists("signUpForm"))
    }
}