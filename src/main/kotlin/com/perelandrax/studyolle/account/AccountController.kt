package com.perelandrax.studyolle.account

import com.perelandrax.studyolle.domain.Account
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import javax.validation.Valid

@Controller
class AccountController(
    val signUpFormValidator: SignUpFormValidator,
    val accountRepository: AccountRepository,
    val javaMailSender: JavaMailSender
) {

    @InitBinder("signUpForm")
    fun initBinder(webDataBinder: WebDataBinder) {
        webDataBinder.addValidators(signUpFormValidator)
    }

    @GetMapping("/sign-up")
    fun signUpForm(model: Model): String {
//        model.addAttribute("signUpForm", SignUpForm())
        model.addAttribute(SignUpForm())
        return "account/sign-up"
    }

    @PostMapping("/sign-up")
    fun signUpSubmit(
        @Valid @ModelAttribute signUpForm: SignUpForm,
        errors: Errors
    ): String {
        if (errors.hasErrors()) {
            return "account/sign-up"
        }

        val account = Account(
            email = signUpForm.email,
            nickname = signUpForm.nickname, // TODO encoding 해야함 (hash)
            password = signUpForm.password,
            studyCreatedByWeb = true,
            studyUpdatedByWeb = true
        )

        val newAccount = accountRepository.save(account).apply {
            generateEmailCheckToken()
        }

        val mailMessage = SimpleMailMessage().apply {
            setTo(newAccount.email)
            setSubject("스터이올래, 회원 가입 인증")
            setText("/check-mail-token?token=$${newAccount.emailCheckToken}&email=${newAccount.email}")
        }

        javaMailSender.send(mailMessage)

        return "redirect:/"
    }
}