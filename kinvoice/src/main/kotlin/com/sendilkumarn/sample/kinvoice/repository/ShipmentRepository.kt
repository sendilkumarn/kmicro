package com.sendilkumarn.sample.kinvoice.repository

import com.sendilkumarn.sample.kinvoice.domain.Shipment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Shipment] entity.
 */
@Suppress("unused")
@Repository
interface ShipmentRepository : JpaRepository<Shipment, Long>
