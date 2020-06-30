package com.sendilkumarn.sample.kstore.web.filter

import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.regex.Pattern
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties
import org.springframework.cloud.netflix.zuul.web.ZuulHandlerMapping
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

/**
 * Detects if a call is to be forwarded to a microservice and adds it to zuul routes
 */
@Component
@Order(1)
class RouteDetectorFilter(@Qualifier("restTemplate") private val restTemplate: RestTemplate, private val zuulProperties: ZuulProperties, private val zuulHandlerMapping: ZuulHandlerMapping) : Filter {
    private val log = LoggerFactory.getLogger(RouteDetectorFilter::class.java)

    @Throws(IOException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {

        val requestURL = (request as HttpServletRequest).getRequestURI()

        val pattern = Pattern.compile("/services/(.*?)/.*")
        val matcher = pattern.matcher(requestURL)

        // match a service-call by a rule like /services/{serviceName}/**
        if (matcher.find()) {
            val serviceName = matcher.group(1)

            // test, if the service is missing in zuul routes
            if (!zuulProperties.getRoutes().containsKey(serviceName)) {
                try {
                    val uri = URI(
                        "http",
                        null,
                        serviceName,
                        80,
                        "/management/health",
                        null,
                        null
                    )

                    // try to reach the health endpoint
                    val responseEntity = restTemplate.getForEntity(uri, String::class.java)
                    if (!responseEntity.getStatusCode().isError()) {
                        val route = ZuulProperties.ZuulRoute(
                            serviceName,
                            "/" + serviceName + "/**",
                            null,
                            "http://" + serviceName + "/",
                            true,
                            false,
                            hashSetOf<String>()
                        )

                        // update routes
                        zuulProperties.getRoutes().put(serviceName, route)
                        zuulHandlerMapping.setDirty(true)
                        log.info("added route {} dynamically", route)
                    } else {
                        log.warn("could not reach health endpoint of service {}", serviceName)
                    }
                } catch (e: URISyntaxException) {
                    log.error("could not parse URI", e)
                }
            }
        }

        chain.doFilter(request, response)
    }
}
