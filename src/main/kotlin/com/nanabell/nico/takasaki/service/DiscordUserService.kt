package com.nanabell.nico.takasaki.service

import com.nanabell.nico.takasaki.config.JDAConfig
import io.micronaut.context.annotation.Context
import io.micronaut.context.exceptions.ConfigurationException
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Context
class DiscordUserService(private val jda: JDA, private val config: JDAConfig) {

    private val logger = LoggerFactory.getLogger(DiscordUserService::class.java)
    private lateinit var guild: Guild

    @PostConstruct
    fun init() {
        guild = jda.getGuildById(config.guild) ?: throw ConfigurationException("Guild: ${config.guild} not found!")
        logger.info("Initialized ${this::class.simpleName} with Guild $guild")
    }

    fun find(id: Long): Maybe<Member> {
        return retrieveMemberById(id)
    }

    fun find(input: String): Flowable<Member> {
        return retrieveMembersByPrefix(input)
            .switchIfEmpty(retrieveMemberById(input).toFlowable())
    }

    fun findAll(): Flowable<Member> {
        return Flowable.create({ emitter ->
            val task = guild.loadMembers { member ->
                emitter.onNext(member)
            }

            task.onSuccess { emitter.onComplete() }
            task.onError { emitter.onError(it) }
        }, BackpressureStrategy.BUFFER)
    }


    private fun retrieveMemberById(input: String): Maybe<Member> {
        val id = input.toLongOrNull() ?: return Maybe.empty()
        return retrieveMemberById(id)
    }

    private fun retrieveMemberById(id: Long): Maybe<Member> {
        return Maybe.fromFuture(guild.retrieveMemberById(id).submit())
    }

    private fun retrieveMembersByPrefix(input: String): Flowable<Member> {
        return Flowable.create({ emitter ->
            guild.retrieveMembersByPrefix(input, 100)
                .onSuccess { members ->
                    for (member in members) {
                        emitter.onNext(member)
                    }

                    emitter.onComplete()
                }

                .onError { error ->
                    emitter.onError(error)
                }

        }, BackpressureStrategy.BUFFER)
    }

    @PreDestroy
    fun dispose() {
        logger.info("Shutting down ${this::class.simpleName}")
    }

}
