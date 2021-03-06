package com.nanabell.nico.takasaki.metrics

import com.nanabell.nico.takasaki.config.JDAConfig
import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Context
@Requires(property = "micronaut.metrics.enabled", value = StringUtils.TRUE)
class ReactionRemovedMeter(
    private val jda: JDA,
    private val config: JDAConfig,
    private val metrics: MeterRegistry
) : EventListener {

    private val logger = LoggerFactory.getLogger(ReactionRemovedMeter::class.java)

    @PostConstruct
    fun initialize() {
        jda.addEventListener(this)
        logger.info("Registered ${this::class.simpleName} to JDA Event Listener")
    }

    override fun onEvent(event: GenericEvent) {
        if (event !is GuildMessageReactionRemoveEvent || event.guild.idLong != config.guild) return

        metrics.counter("user.reaction", "type", "remove", "user", event.userId).increment()
        metrics.counter("message.reaction", "type", "remove", "message_identifier", "${event.channel.id}/${event.messageId}").increment()
        logger.info("GuildMessageReactionRemoveEvent from ${event.user} -> ${event.messageId} with ${event.reactionEmote}")
    }

    @PreDestroy
    fun dispose() {
        jda.removeEventListener(this)
        logger.info("Removed ${this::class.simpleName} from JDA Event Listener")
    }

}
