package com.perelandrax.studyolle.account

import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class SignUpFormValidator(val accountRepository: AccountRepository) : Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return clazz.isAssignableFrom(SignUpForm::class.java)
    }

    override fun validate(target: Any, errors: Errors) {
        val signUpForm = target as SignUpForm

        if (accountRepository.existsByEmail(signUpForm.email)) {
            errors.rejectValue("email", "invalid.email", arrayOf(signUpForm.email), "이미 사용중인 이메일입니다.")
        }

        if (accountRepository.existsByNickname(signUpForm.nickname)) {
            errors.rejectValue("nickname", "invalid.nickname", arrayOf(signUpForm.nickname), "이미 사용중인 닉네임입니다.")
        }
    }
}