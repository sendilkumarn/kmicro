package com.sendilkumarn.sample.kinvoice.domain

import com.sendilkumarn.sample.kinvoice.domain.enumeration.InvoiceStatus
import com.sendilkumarn.sample.kinvoice.domain.enumeration.PaymentMethod
import io.swagger.annotations.ApiModel
import java.io.Serializable
import java.math.BigDecimal
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * Entities for kinvoice microservice
 */
@ApiModel(description = "Entities for kinvoice microservice")
@Entity
@Table(name = "invoice")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Invoice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "code", nullable = false)
    var code: String? = null,

    @get: NotNull
    @Column(name = "date", nullable = false)
    var date: Instant? = null,

    @Column(name = "details")
    var details: String? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: InvoiceStatus? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    var paymentMethod: PaymentMethod? = null,

    @get: NotNull
    @Column(name = "payment_date", nullable = false)
    var paymentDate: Instant? = null,

    @get: NotNull
    @Column(name = "payment_amount", precision = 21, scale = 2, nullable = false)
    var paymentAmount: BigDecimal? = null,

    @OneToMany(mappedBy = "invoice")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    var shipments: MutableSet<Shipment> = mutableSetOf()

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addShipment(shipment: Shipment): Invoice {
        this.shipments.add(shipment)
        shipment.invoice = this
        return this
    }

    fun removeShipment(shipment: Shipment): Invoice {
        this.shipments.remove(shipment)
        shipment.invoice = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Invoice) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Invoice{" +
        "id=$id" +
        ", code='$code'" +
        ", date='$date'" +
        ", details='$details'" +
        ", status='$status'" +
        ", paymentMethod='$paymentMethod'" +
        ", paymentDate='$paymentDate'" +
        ", paymentAmount=$paymentAmount" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
