package com.sendilkumarn.sample.kinvoice.domain

import com.sendilkumarn.sample.kinvoice.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InvoiceTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Invoice::class)
        val invoice1 = Invoice()
        invoice1.id = 1L
        val invoice2 = Invoice()
        invoice2.id = invoice1.id
        assertThat(invoice1).isEqualTo(invoice2)
        invoice2.id = 2L
        assertThat(invoice1).isNotEqualTo(invoice2)
        invoice1.id = null
        assertThat(invoice1).isNotEqualTo(invoice2)
    }
}
