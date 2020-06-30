package com.sendilkumarn.sample.kinvoice.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Properties specific to Kinvoice.
 *
 * Properties are configured in the `application.yml` file.
 * See [io.github.jhipster.config.JHipsterProperties] for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
class ApplicationProperties
