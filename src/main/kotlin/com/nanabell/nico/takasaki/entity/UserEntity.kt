package com.nanabell.nico.takasaki.entity

import net.dv8tion.jda.api.entities.Member
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
data class UserEntity(

    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "username")
    var name: String,

    @Column(name = "nickname")
    var nickname: String?

) {
    constructor(member: Member) : this(member.idLong, member.user.asTag, member.nickname)
}
