package com.nanabell.nico.takasaki.service

import com.nanabell.nico.takasaki.entity.UserEntity
import com.nanabell.nico.takasaki.repository.UserRepository
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.util.NamedThreadFactory
import io.micronaut.context.annotation.Parallel
import io.micronaut.context.annotation.Requires
import io.reactivex.schedulers.Schedulers
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.inject.Singleton

@Parallel
@Singleton
@Requires(beans = [UserRepository::class])
class UserSyncService(
    private val userService: DiscordUserService,
    private val repository: UserRepository,
    private val registry: MeterRegistry,
    private val jda: JDA
) : ListenerAdapter() {

    private val scheduler = Schedulers.from(Executors.newFixedThreadPool(2, NamedThreadFactory("user-sync")))
    private val logger = LoggerFactory.getLogger(UserSyncService::class.java)
    var lastSync: Instant = Instant.EPOCH
        private set

    @PostConstruct
    fun init() {
        logger.info("Initialized ${this::class.java.simpleName}")
        jda.addEventListener(this)
    }

    fun fullSync() {
        logger.info("Starting full User Sync")
        val timer = registry.timer("user.sync.full")
        val start = Clock.SYSTEM.monotonicTime()

        userService.findAll()
            .doOnNext { member: Member? ->
                if (member == null) {
                    logger.error("Retrieved a null user on Full Sync?!")
                    return@doOnNext
                }

                syncMember(member)
            }.doOnComplete {
                val diff = Clock.SYSTEM.monotonicTime() - start
                timer.record(diff, TimeUnit.NANOSECONDS)
                lastSync = Instant.now()
                logger.info("Finished full User Sync in ${Duration.of(diff, ChronoUnit.NANOS).toSeconds()} seconds")
            }.subscribeOn(scheduler, true)
            .subscribe()
    }

    private fun syncMember(member: Member) {
        val entity = UserEntity(member)

        if (repository.existsById(member.idLong))
            repository.update(entity)
        else
            repository.save(entity)

        logger.debug("Successfully synced User ${entity.name} with Database")
    }

    override fun onGuildMemberUpdate(event: GuildMemberUpdateEvent) {
        scheduler.scheduleDirect {
            registry.timer("user.sync").record { syncMember(event.member) }
            logger.info("Synced ${event.member.user.asTag} due to GuildMemberUpdateEvent")
        }
    }
}
