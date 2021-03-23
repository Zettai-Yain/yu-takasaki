package com.nanabell.nico.takasaki.listener

import com.nanabell.nico.takasaki.config.JDAConfig
import com.nanabell.nico.takasaki.entity.ReactionAuditEntity
import com.nanabell.nico.takasaki.service.ReactionAuditService
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Requires
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Context
@Requires(beans = [ReactionAuditService::class])
class ReactionAddedListener(private val jda: JDA, private val service: ReactionAuditService, private val config: JDAConfig) : EventListener {

    private val logger = LoggerFactory.getLogger(ReactionAddedListener::class.java)

    @PostConstruct
    fun initialize() {
        jda.addEventListener(this)
        logger.info("Registered ${this::class.simpleName} to JDA Event Listener")
    }

    override fun onEvent(event: GenericEvent) {
        if (event !is GuildMessageReactionAddEvent) return
        if (event.guild.idLong != config.guild) return

        val audit = ReactionAuditEntity(event)
        service.persist(audit)

        logger.debug("GuildMessageReactionAddEvent from ${event.user} -> ${event.messageId} with ${event.reactionEmote}")
    }

    @PreDestroy
    fun dispose() {
        jda.removeEventListener(this)
        logger.info("Removed ${this::class.simpleName} from JDA Event Listener")
    }

}
