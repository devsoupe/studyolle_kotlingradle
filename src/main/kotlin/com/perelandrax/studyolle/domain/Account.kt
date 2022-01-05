package com.perelandrax.studyolle.domain

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class Account(

    @Id @GeneratedValue var id: Long? = null,
    @Column(unique = true) var email: String? = null,
    @Column(unique = true) var nickname: String? = null,
    var password: String? = null,
    var emailVerified: Boolean? = null,
    var emailCheckToken: String? = null,
    var joinedAt: LocalDateTime? = null,
    var bio: String? = null,
    var url: String? = null,
    var occupation: String? = null,
    var location: String? = null,
    @Lob @Basic(fetch = FetchType.EAGER)
    var profileImage: String? = null,
    var studyCreatedByEmail: Boolean? = null,
    var studyCreatedByWeb: Boolean? = null,
    var studyEnrollmentResultEmail: Boolean? = null,
    var studyEnrollmentResultWeb: Boolean? = null,
    var studyUpdatedByEmail: Boolean? = null,
    var studyUpdatedByWeb: Boolean? = null
) {
    fun generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString()
    }
}