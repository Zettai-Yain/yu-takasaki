package com.nanabell.nico.takasaki.metrics

import com.nanabell.nico.takasaki.config.JDAConfig
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Parallel
import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import io.micronaut.scheduling.annotation.Scheduled
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct
import javax.inject.Singleton

@Parallel
@Singleton
@Requires(property = "micronaut.metrics.enabled", value = StringUtils.TRUE)
class MemberCountMeter(
    private val jda: JDA,
    private val config: JDAConfig,
    registry: MeterRegistry
) {

    private val logger = LoggerFactory.getLogger(MemberCountMeter::class.java)
    private var metadata: Guild.MetaData = Guild.MetaData(-1, -1, -1, -1)

    private val memberMeter = Gauge.builder("users.total") { metadata.approximateMembers }
        .description("Total amount of Discord Users")
        .register(registry)


    private val presenceMeter = Gauge.builder("users.online") { metadata.approximatePresences }
        .description("Amount of Discord Users which are currently Online")
        .register(registry)

    @PostConstruct
    fun init() {
        logger.info("Starting Scheduler for ${this::class.java.simpleName}")
    }

    @Scheduled(initialDelay = "2s", fixedRate = "5m")
    fun scheduled() {
        logger.debug("Refreshing MemberCountMeter")
        val guild = jda.getGuildById(config.guild)
        if (guild == null) {
            logger.error("JDA Failed to retrieve Guild ${config.guild}. Is the Bot Connected to specified Guild?")
            return
        }

        guild.retrieveMetaData().queue {
            metadata = it
            logger.debug("Updating MemberCount [${presenceMeter.value().toInt()}/${memberMeter.value().toInt()}]")
        }
    }
}
