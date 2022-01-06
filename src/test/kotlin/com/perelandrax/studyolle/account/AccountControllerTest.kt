package com.perelandrax.studyolle.account

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @MockBean
    private lateinit var javaMailSender: JavaMailSender

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    fun signUpForm() {
        mockMvc.perform(get("/sign-up"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(view().name("account/sign-up"))
            .andExpect(model().attributeExists("signUpForm"))
    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    fun signUpSubmit_with_wrong_input() {
        mockMvc.perform(
            post("/sign-up")
                .param("nickname", "keesun")
                .param("email", "email..")
                .param("password", "12345")
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(view().name("account/sign-up"))
    }

    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    fun signUpSubmit_with_correct_input() {
        mockMvc.perform(
            post("/sign-up")
                .param("nickname", "keesun")
                .param("email", "keesun@email.com")
                .param("password", "12345678")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(view().name("redirect:/"))

        assertTrue(accountRepository.existsByEmail("keesun@email.com"))

        then(javaMailSender).should().send(any(SimpleMailMessage::class.java))
    }
}