package com.perelandrax.studyolle.account

import com.perelandrax.studyolle.domain.Account
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @MockBean
    private lateinit var javaMailSender: JavaMailSender

    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    fun checkEmailToken_with_wrong_input() {
        mockMvc.perform(
            get("/check-email-token")
                .param("token", "wrong_token")
                .param("email", "email@email.com")
        )
            .andExpect(status().isOk)
            .andExpect(model().attributeExists("error"))
            .andExpect(view().name("account/checked-email"))
            .andExpect(unauthenticated())
    }

    @DisplayName("인증 메일 확인 - 입력값 정상")
    @Test
    fun checkEmailToken() {
        val account = Account(
            email = "test@email.com",
            password = "12345678",
            nickname = "keesun"
        )

        val newAccount = accountRepository.save(account)
        newAccount.generateEmailCheckToken()

        mockMvc.perform(
            get("/check-email-token")
                .param("token", newAccount.emailCheckToken)
                .param("email", newAccount.email)
        )
            .andExpect(status().isOk)
            .andExpect(model().attributeDoesNotExist("error"))
            .andExpect(model().attributeExists("nickname"))
            .andExpect(model().attributeExists("numberOfUser"))
            .andExpect(view().name("account/checked-email"))
            .andExpect(authenticated().withUsername("keesun"))
    }

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    fun signUpForm() {
        mockMvc.perform(get("/sign-up"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(view().name("account/sign-up"))
            .andExpect(model().attributeExists("signUpForm"))
            .andExpect(unauthenticated())
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
            .andExpect(unauthenticated())
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
            .andExpect(authenticated().withUsername("keesun"))

        val account = accountRepository.findByEmail("keesun@email.com")
        assertNotNull(account)
        assertNotEquals(account?.password, "12345678")
        assertNotNull(account?.emailCheckToken)
        then(javaMailSender).should().send(any(SimpleMailMessage::class.java))
    }


}