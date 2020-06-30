package com.sendilkumarn.sample.kinvoice.domain

import com.sendilkumarn.sample.kinvoice.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipmentTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Shipment::class)
        val shipment1 = Shipment()
        shipment1.id = 1L
        val shipment2 = Shipment()
        shipment2.id = shipment1.id
        assertThat(shipment1).isEqualTo(shipment2)
        shipment2.id = 2L
        assertThat(shipment1).isNotEqualTo(shipment2)
        shipment1.id = null
        assertThat(shipment1).isNotEqualTo(shipment2)
    }
}
