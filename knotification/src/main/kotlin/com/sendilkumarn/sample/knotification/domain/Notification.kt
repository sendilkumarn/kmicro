package com.sendilkumarn.sample.knotification.domain

import com.sendilkumarn.sample.knotification.domain.enumeration.NotificationType
import io.swagger.annotations.ApiModel
import java.io.Serializable
import java.time.Instant
import javax.validation.constraints.*
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

/**
 * Entities for knotification microservice
 */
@ApiModel(description = "Entities for knotification microservice")
@Document(collection = "notification")
data class Notification(
    @Id
    var id: String? = null,
    @get: NotNull
    @Field("date")
    var date: Instant? = null,

    @Field("details")
    var details: String? = null,

    @get: NotNull
    @Field("sent_date")
    var sentDate: Instant? = null,

    @get: NotNull
    @Field("format")
    var format: NotificationType? = null,

    @get: NotNull
    @Field("user_id")
    var userId: Long? = null,

    @get: NotNull
    @Field("product_id")
    var productId: Long? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Notification) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Notification{" +
        "id=$id" +
        ", date='$date'" +
        ", details='$details'" +
        ", sentDate='$sentDate'" +
        ", format='$format'" +
        ", userId=$userId" +
        ", productId=$productId" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
