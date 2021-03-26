package com.nanabell.nico.takasaki.repository

import com.nanabell.nico.takasaki.entity.UserEntity
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface UserRepository : CrudRepository<UserEntity, Long>
