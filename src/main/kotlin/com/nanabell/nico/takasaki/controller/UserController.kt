package com.nanabell.nico.takasaki.controller

import com.nanabell.nico.takasaki.domain.DiscordRole
import com.nanabell.nico.takasaki.domain.DiscordUser
import com.nanabell.nico.takasaki.service.DiscordUserService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.hateoas.JsonError
import io.reactivex.Maybe
import io.reactivex.Single
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.tags.Tags
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.requests.ErrorResponse
import org.slf4j.LoggerFactory

@Tags(Tag(name = "Users"))
@Controller("/users")
class UserController(private val service: DiscordUserService) {

    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                array = ArraySchema(schema = Schema(implementation = DiscordUser::class))
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Query Parameter specified but blank"
        ),
        ApiResponse(
            responseCode = "5xx",
            description = "Runtime Server Error"
        )
    )
    @Operation(summary = "Retrieve a list of Discord Users")
    @Get("{?query}", produces = [MediaType.APPLICATION_JSON])
    fun get(@QueryValue query: String?): Single<List<DiscordUser>> {
        if (query == null)
            return service.findAll().map { DiscordUser(it) }.toList()

        if (query.isBlank())
            throw IllegalArgumentException("Query Parameter cannot be blank!")


        return service.find(query).map { DiscordUser(it) }.toList()
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = DiscordUser::class)
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        ApiResponse(
            responseCode = "5xx",
            description = "Runtime Server Error"
        )
    )
    @Operation(summary = "Get a User by ID")
    @Get("{id}", produces = [MediaType.APPLICATION_JSON])
    fun get(id: Long): Maybe<DiscordUser> {
        return service.find(id).map { DiscordUser(it) }
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                array = ArraySchema(schema = Schema(implementation = DiscordRole::class))
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        ApiResponse(
            responseCode = "5xx",
            description = "Runtime Server Error"
        )
    )
    @Operation(summary = "Get a Users Roles by ID")
    @Get("{id}/roles", produces = [MediaType.APPLICATION_JSON])
    fun getRoles(id: Long): Single<List<DiscordRole>> {
        return service.find(id).flatMapSingle { member -> Single.just(member.roles.map { DiscordRole(it) }) }
    }


    @Error(IllegalArgumentException::class)
    fun argumentException(e: IllegalArgumentException): HttpResponse<JsonError> {
        logger.debug("UserController Exception", e)
        return HttpResponse.notFound(JsonError(e.message))
    }

    @Error(ErrorResponseException::class)
    fun jdaError(e: ErrorResponseException): HttpResponse<JsonError> {
        if (e.errorResponse == ErrorResponse.UNKNOWN_USER)
            return HttpResponse.notFound(JsonError(e.meaning))

        logger.debug("UserController Exception", e)
        return HttpResponse.serverError(JsonError(e.message))
    }

}
