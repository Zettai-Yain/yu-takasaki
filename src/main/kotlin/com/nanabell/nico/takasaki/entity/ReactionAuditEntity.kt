package com.nanabell.nico.takasaki.entity

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "reactions_audit")
data class ReactionAuditEntity(

    @Id
    @Column(name = "seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val seq: Long = 0,

    @Column(name = "timestamp")
    var timestamp: OffsetDateTime,

    @Column(name = "type")
    var type: VoteType,

    @Column(name = "voter")
    var voter: Long,

    @Column(name = "voted")
    var voted: Long,

    @Column(name = "reaction")
    var reaction: String
) {

    constructor(event: GuildMessageReactionAddEvent) : this(
        0,
        OffsetDateTime.now(),
        VoteType.ADD,
        event.userIdLong,
        event.messageIdLong,
        if (!event.reactionEmote.isEmote) event.reactionEmote.asCodepoints else event.reactionEmote.id
    )

    constructor(event: GuildMessageReactionRemoveEvent) : this(
        0,
        OffsetDateTime.now(),
        VoteType.REMOVE,
        event.userIdLong,
        event.messageIdLong,
        if (!event.reactionEmote.isEmote) event.reactionEmote.asCodepoints else event.reactionEmote.id
    )

    enum class VoteType {
        ADD,
        REMOVE
    }
}
