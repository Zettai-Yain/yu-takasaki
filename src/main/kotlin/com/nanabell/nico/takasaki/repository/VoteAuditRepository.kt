package com.nanabell.nico.takasaki.repository

import com.nanabell.nico.takasaki.entity.ReactionAuditEntity
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface VoteAuditRepository : JpaRepository<ReactionAuditEntity, Long>
