package com.nanabell.nico.takasaki.metrics

import com.nanabell.nico.takasaki.config.JDAConfig
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Parallel
import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import io.micronaut.scheduling.annotation.Scheduled
import net.dv8tion.jda.api.JDA
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

    private val gauge = Gauge.builder("members", this::getMemberCount)
        .description("Current Members in the Guild. This value might not be 100% accurate!")
        .tag("guild", "${config.guild}")
        .baseUnit("Member")
        .register(registry)

    @PostConstruct
    fun init() {
        logger.info("Starting Scheduler for ${this::class.java.simpleName}")
    }


    @Scheduled(initialDelay = "30s", fixedRate = "5m")
    fun scheduled() {
        logger.info("Submitted current Member count ${gauge.value()} to MetricsRegistry")
    }

    private fun getMemberCount(): Int {
        val guild = jda.getGuildById(config.guild)
        if (guild == null) {
            logger.error("JDA Failed to retrieve Guild ${config.guild}. Is the Bot Connected to specified Guild?")
            return -1
        }

        return guild.memberCount
    }

}
