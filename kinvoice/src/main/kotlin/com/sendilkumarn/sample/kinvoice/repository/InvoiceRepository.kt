package com.sendilkumarn.sample.kinvoice.repository

import com.sendilkumarn.sample.kinvoice.domain.Invoice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Invoice] entity.
 */
@Suppress("unused")
@Repository
interface InvoiceRepository : JpaRepository<Invoice, Long>
