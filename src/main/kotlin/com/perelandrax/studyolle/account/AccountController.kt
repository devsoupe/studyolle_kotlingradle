package com.perelandrax.studyolle.account

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
    val accountService: AccountService
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
}