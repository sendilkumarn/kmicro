package com.sendilkumarn.sample.kinvoice.service
import com.sendilkumarn.sample.kinvoice.domain.Shipment
import com.sendilkumarn.sample.kinvoice.repository.ShipmentRepository
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Shipment].
 */
@Service
@Transactional
class ShipmentService(
    private val shipmentRepository: ShipmentRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a shipment.
     *
     * @param shipment the entity to save.
     * @return the persisted entity.
     */
    fun save(shipment: Shipment): Shipment {
        log.debug("Request to save Shipment : {}", shipment)
        return shipmentRepository.save(shipment)
    }

    /**
     * Get all the shipments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Shipment> {
        log.debug("Request to get all Shipments")
        return shipmentRepository.findAll(pageable)
    }

    /**
     * Get one shipment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Shipment> {
        log.debug("Request to get Shipment : {}", id)
        return shipmentRepository.findById(id)
    }

    /**
     * Delete the shipment by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Shipment : {}", id)

        shipmentRepository.deleteById(id)
    }
}
