package com.sendilkumarn.sample.kinvoice.web.rest

import com.sendilkumarn.sample.kinvoice.domain.Shipment
import com.sendilkumarn.sample.kinvoice.service.ShipmentService
import com.sendilkumarn.sample.kinvoice.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.PaginationUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private const val ENTITY_NAME = "kinvoiceShipment"
/**
 * REST controller for managing [com.sendilkumarn.sample.kinvoice.domain.Shipment].
 */
@RestController
@RequestMapping("/api")
class ShipmentResource(
    private val shipmentService: ShipmentService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /shipments` : Create a new shipment.
     *
     * @param shipment the shipment to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new shipment, or with status `400 (Bad Request)` if the shipment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/shipments")
    fun createShipment(@Valid @RequestBody shipment: Shipment): ResponseEntity<Shipment> {
        log.debug("REST request to save Shipment : {}", shipment)
        if (shipment.id != null) {
            throw BadRequestAlertException(
                "A new shipment cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = shipmentService.save(shipment)
        return ResponseEntity.created(URI("/api/shipments/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /shipments` : Updates an existing shipment.
     *
     * @param shipment the shipment to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated shipment,
     * or with status `400 (Bad Request)` if the shipment is not valid,
     * or with status `500 (Internal Server Error)` if the shipment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/shipments")
    fun updateShipment(@Valid @RequestBody shipment: Shipment): ResponseEntity<Shipment> {
        log.debug("REST request to update Shipment : {}", shipment)
        if (shipment.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = shipmentService.save(shipment)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     shipment.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /shipments` : get all the shipments.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of shipments in body.
     */
    @GetMapping("/shipments")
    fun getAllShipments(pageable: Pageable): ResponseEntity<List<Shipment>> {
        log.debug("REST request to get a page of Shipments")
        val page = shipmentService.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /shipments/:id` : get the "id" shipment.
     *
     * @param id the id of the shipment to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the shipment, or with status `404 (Not Found)`.
     */
    @GetMapping("/shipments/{id}")
    fun getShipment(@PathVariable id: Long): ResponseEntity<Shipment> {
        log.debug("REST request to get Shipment : {}", id)
        val shipment = shipmentService.findOne(id)
        return ResponseUtil.wrapOrNotFound(shipment)
    }
    /**
     *  `DELETE  /shipments/:id` : delete the "id" shipment.
     *
     * @param id the id of the shipment to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/shipments/{id}")
    fun deleteShipment(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Shipment : {}", id)

        shipmentService.delete(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
