package com.perelandrax.studyolle.account

import com.perelandrax.studyolle.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
interface AccountRepository : JpaRepository<Account, Long> {

    fun existsByEmail(email: String?): Boolean

    fun existsByNickname(nickname: String?): Boolean

    fun findByEmail(email: String): Account?
}
