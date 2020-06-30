package com.sendilkumarn.sample.kstore.domain

import com.sendilkumarn.sample.kstore.domain.enumeration.Gender
import io.swagger.annotations.ApiModel
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

/**
 * Entities for Store Gateway
 */
@ApiModel(description = "Entities for Store Gateway")
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @get: NotNull
    @Column(name = "first_name", nullable = false)
    var firstName: String? = null,

    @get: NotNull
    @Column(name = "last_name", nullable = false)
    var lastName: String? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    var gender: Gender? = null,

    @get: NotNull
    @get: Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false)
    var email: String? = null,

    @get: NotNull
    @Column(name = "phone", nullable = false)
    var phone: String? = null,

    @get: NotNull
    @Column(name = "address_line_1", nullable = false)
    var addressLine1: String? = null,

    @Column(name = "address_line_2")
    var addressLine2: String? = null,

    @get: NotNull
    @Column(name = "city", nullable = false)
    var city: String? = null,

    @get: NotNull
    @Column(name = "country", nullable = false)
    var country: String? = null,

    @OneToOne(optional = false) @NotNull
    @JoinColumn(unique = true)
    var user: User? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Customer) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Customer{" +
        "id=$id" +
        ", firstName='$firstName'" +
        ", lastName='$lastName'" +
        ", gender='$gender'" +
        ", email='$email'" +
        ", phone='$phone'" +
        ", addressLine1='$addressLine1'" +
        ", addressLine2='$addressLine2'" +
        ", city='$city'" +
        ", country='$country'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
