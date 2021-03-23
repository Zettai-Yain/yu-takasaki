package com.nanabell.nico.takasaki.domain

import net.dv8tion.jda.api.entities.Member

data class DiscordUser(
    val id: Long,
    val name: String,
    val discriminator: String,
    val nickname: String?
) {
    constructor(member: Member) : this(
        member.idLong,
        member.user.name,
        member.user.discriminator,
        member.nickname
    )
}
