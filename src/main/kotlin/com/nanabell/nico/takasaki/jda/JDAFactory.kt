package com.nanabell.nico.takasaki.jda

import com.nanabell.nico.takasaki.config.JDACredentials
import io.micronaut.context.annotation.Factory
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import javax.inject.Singleton

@Factory
class JDAFactory {

    @Singleton
    fun buildJDA(config: JDACredentials): JDA {
        val builder = JDABuilder.create(config.token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS)
        builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
        builder.setMemberCachePolicy(MemberCachePolicy.ALL)

        return builder.build().awaitReady()
    }

}
