package com.perelandrax.studyolle.account

import com.perelandrax.studyolle.domain.Account
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
    val accountRepository: AccountRepository,
    val javaMailSender: JavaMailSender,
    val passwordEncoder: PasswordEncoder
//    val authenticationManager: AuthenticationManager
) {
    @Transactional
    fun proccessNewAccount(signUpForm: SignUpForm): Account {
        val newAccount = saveNewAccount(signUpForm)
        newAccount.generateEmailCheckToken()
        sendSignUpConfirmEmail(newAccount)
        return newAccount
    }

    private fun saveNewAccount(signUpForm: SignUpForm): Account {
        val account = Account(
            email = signUpForm.email,
            nickname = signUpForm.nickname,
            password = passwordEncoder.encode(signUpForm.password),
            studyCreatedByWeb = true,
            studyUpdatedByWeb = true
        )

        return accountRepository.save(account)
    }

    private fun sendSignUpConfirmEmail(newAccount: Account) {
        val mailMessage = SimpleMailMessage().apply {
            setTo(newAccount.email)
            setSubject("스터이올래, 회원 가입 인증")
            setText("/check-email-token?token=${newAccount.emailCheckToken}&email=${newAccount.email}")
        }

        javaMailSender.send(mailMessage)
    }

    fun login(account: Account) {
        // 정석 (password가 plaintext여야 함)
//        val token = UsernamePasswordAuthenticationToken(username, password)
//        val authenticate = authenticationManager.authenticate(token)
//        val context = SecurityContextHolder.getContext()
//        context.authentication = authenticate

        // 변형된 방법
        val token = UsernamePasswordAuthenticationToken(
            account.nickname,
            account.password,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )
        val context = SecurityContextHolder.getContext()
        context.authentication = token
    }
}