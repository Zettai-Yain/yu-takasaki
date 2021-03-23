package com.nanabell.nico.takasaki.service

import com.nanabell.nico.takasaki.entity.ReactionAuditEntity
import com.nanabell.nico.takasaki.repository.VoteAuditRepository
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Requires

@Context
@Requires(beans = [VoteAuditRepository::class])
class ReactionAuditService(private val repository: VoteAuditRepository) {

    fun persist(entity: ReactionAuditEntity) {
        repository.save(entity)
    }

}
