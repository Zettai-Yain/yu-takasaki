package com.nanabell.nico.takasaki.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties(JDAConfig.CONFIG_PREFIX)
class JDACredentials {

    var token: String = "invalid-token"

}
