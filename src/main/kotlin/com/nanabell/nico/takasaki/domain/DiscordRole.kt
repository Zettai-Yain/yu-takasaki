package com.nanabell.nico.takasaki.domain

import net.dv8tion.jda.api.entities.Role

data class DiscordRole(
    val id: Long,
    val name: String
) {

    constructor(role: Role) : this(role.idLong, role.name)

}
