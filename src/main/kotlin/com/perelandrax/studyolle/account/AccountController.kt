package com.perelandrax.studyolle.account

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import java.time.LocalDateTime
import javax.validation.Valid

@Controller
class AccountController(
    val signUpFormValidator: SignUpFormValidator,
    val accountService: AccountService,
    val accountRepository: AccountRepository
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
    fun signUpSubmit(@Valid @ModelAttribute signUpForm: SignUpForm, errors: Errors): String {
        if (errors.hasErrors()) {
            return "account/sign-up"
        }

        accountService.proccessNewAccount(signUpForm)

        return "redirect:/"
    }

    @GetMapping("/check-email-token")
    fun checkEmailToken(token: String, email: String, model: Model): String {
        val account = accountRepository.findByEmail(email)
        val view = "account/checked-email"

        if (account == null) {
            model.addAttribute("error", "wrong.email")
            return view
        }

        if (account.emailCheckToken.equals(token).not()) {
            model.addAttribute("error", "wrong.email")
            return view
        }

        account.emailVerified = true
        account.joinedAt = LocalDateTime.now()

        model.addAttribute("numberOfUser", accountRepository.count())
        model.addAttribute("nickname", account.nickname)

        return view
    }

}