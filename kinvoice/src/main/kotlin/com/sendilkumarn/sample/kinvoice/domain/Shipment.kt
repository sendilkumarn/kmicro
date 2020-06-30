package com.sendilkumarn.sample.kinvoice.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * A Shipment.
 */
@Entity
@Table(name = "shipment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Shipment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "tracking_code")
    var trackingCode: String? = null,

    @get: NotNull
    @Column(name = "date", nullable = false)
    var date: Instant? = null,

    @Column(name = "details")
    var details: String? = null,

    @ManyToOne(optional = false) @NotNull
    @JsonIgnoreProperties(value = ["shipments"], allowSetters = true)
    var invoice: Invoice? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Shipment) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Shipment{" +
        "id=$id" +
        ", trackingCode='$trackingCode'" +
        ", date='$date'" +
        ", details='$details'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
