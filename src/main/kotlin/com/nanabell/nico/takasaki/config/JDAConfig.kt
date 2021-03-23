package com.nanabell.nico.takasaki.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(JDAConfig.CONFIG_PREFIX)
class JDAConfig {

    var guild: Long = 0

    companion object {
        const val CONFIG_PREFIX = "jda"
    }
}
