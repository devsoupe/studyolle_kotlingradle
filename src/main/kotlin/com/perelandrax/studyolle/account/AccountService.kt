package com.perelandrax.studyolle.account

import com.perelandrax.studyolle.domain.Account
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class AccountService(
    val accountRepository: AccountRepository,
    val javaMailSender: JavaMailSender
) {
    fun proccessNewAccount(signUpForm: SignUpForm) {
        val newAccount = saveNewAccount(signUpForm)
        newAccount.generateEmailCheckToken()
        sendSignUpConfirmEmail(newAccount)
    }

    private fun saveNewAccount(signUpForm: SignUpForm): Account {
        val account = Account(
            email = signUpForm.email,
            nickname = signUpForm.nickname, // TODO encoding 해야함 (hash)
            password = signUpForm.password,
            studyCreatedByWeb = true,
            studyUpdatedByWeb = true
        )

        return accountRepository.save(account)
    }

    private fun sendSignUpConfirmEmail(newAccount: Account) {
        val mailMessage = SimpleMailMessage().apply {
            setTo(newAccount.email)
            setSubject("스터이올래, 회원 가입 인증")
            setText("/check-mail-token?token=$${newAccount.emailCheckToken}&email=${newAccount.email}")
        }

        javaMailSender.send(mailMessage)
    }
}