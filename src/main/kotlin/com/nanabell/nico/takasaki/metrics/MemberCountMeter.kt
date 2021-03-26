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

    private val memberMeter = Gauge.builder("guild") { metadata.approximateMembers }
        .description("Current Members in the Guild. This value might not be 100% accurate!")
        .tag("type", "member")
        .baseUnit("Member")
        .register(registry)

    private val presenceMeter = Gauge.builder("guild") { metadata.approximatePresences }
        .description("Current Members in the Guild. This value might not be 100% accurate!")
        .tag("type", "presence")
        .baseUnit("Member")
        .register(registry)

    @PostConstruct
    fun init() {
        logger.info("Starting Scheduler for ${this::class.java.simpleName}")
    }

    @Scheduled(initialDelay = "30s", fixedRate = "5m")
    fun scheduled() {
        logger.debug("Refreshing MemberCountMeter")
        val guild = jda.getGuildById(config.guild)
        if (guild == null) {
            logger.error("JDA Failed to retrieve Guild ${config.guild}. Is the Bot Connected to specified Guild?")
            return
        }

        metadata = guild.retrieveMetaData().complete()
        logger.info("Submitted MemberCount [${presenceMeter.value().toInt()}/${memberMeter.value().toInt()}] to MetricsRegistry")
    }
}
