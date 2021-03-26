package com.nanabell.nico.takasaki.job

import com.nanabell.nico.takasaki.service.UserSyncService
import io.micronaut.context.annotation.Parallel
import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.annotation.PostConstruct
import javax.inject.Singleton

@Parallel
@Singleton
@Requires(beans = [UserSyncService::class])
class UserSyncJob(private val service: UserSyncService) {

    private val logger = LoggerFactory.getLogger(UserSyncJob::class.java)

    @PostConstruct
    fun init() {
        logger.info("Started ${this::class.java.simpleName}. Syncing Users ever 12h")
    }

    @Scheduled(initialDelay = "5s", fixedRate = "2h")
    fun schedule() {
        if (service.lastSync.isAfter(Instant.now().minus(12, ChronoUnit.HOURS))) {
            logger.info("Skipping full User Sync. Last Sync less than 12h ago!")
            return
        }

        service.fullSync()
    }

}
