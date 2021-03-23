package com.nanabell.nico.takasaki

import io.micronaut.runtime.Micronaut.build
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import java.lang.management.ManagementFactory


@OpenAPIDefinition(
    info = Info(
        title = "Yu Takasaki",
        description = "Discord User Information Provider and metrics collector Service",
        contact = Contact(name = "Nanabell"),
        license = License(name = "MIT")
    )
)
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        val builder = build()
            .args(*args)
            .packages("com.nanabell.nico.takasaki")
            .defaultEnvironments("prod")

        if (isDevEnv())
            builder.environments("dev")

        builder.start()
    }


    private fun isDevEnv(): Boolean {
        for (inputArgument in ManagementFactory.getRuntimeMXBean().inputArguments) {
            if (inputArgument.startsWith("-javaagent")
                || inputArgument.startsWith("-agentpath")
                || inputArgument.startsWith("-agentlib")
            )
                return true
        }

        return false
    }

}
