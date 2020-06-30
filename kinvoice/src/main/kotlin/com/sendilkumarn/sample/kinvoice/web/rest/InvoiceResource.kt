package com.sendilkumarn.sample.kinvoice.web.rest

import com.sendilkumarn.sample.kinvoice.domain.Invoice
import com.sendilkumarn.sample.kinvoice.service.InvoiceService
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

private const val ENTITY_NAME = "kinvoiceInvoice"
/**
 * REST controller for managing [com.sendilkumarn.sample.kinvoice.domain.Invoice].
 */
@RestController
@RequestMapping("/api")
class InvoiceResource(
    private val invoiceService: InvoiceService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /invoices` : Create a new invoice.
     *
     * @param invoice the invoice to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new invoice, or with status `400 (Bad Request)` if the invoice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/invoices")
    fun createInvoice(@Valid @RequestBody invoice: Invoice): ResponseEntity<Invoice> {
        log.debug("REST request to save Invoice : {}", invoice)
        if (invoice.id != null) {
            throw BadRequestAlertException(
                "A new invoice cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = invoiceService.save(invoice)
        return ResponseEntity.created(URI("/api/invoices/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /invoices` : Updates an existing invoice.
     *
     * @param invoice the invoice to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated invoice,
     * or with status `400 (Bad Request)` if the invoice is not valid,
     * or with status `500 (Internal Server Error)` if the invoice couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/invoices")
    fun updateInvoice(@Valid @RequestBody invoice: Invoice): ResponseEntity<Invoice> {
        log.debug("REST request to update Invoice : {}", invoice)
        if (invoice.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = invoiceService.save(invoice)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     invoice.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /invoices` : get all the invoices.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of invoices in body.
     */
    @GetMapping("/invoices")
    fun getAllInvoices(pageable: Pageable): ResponseEntity<List<Invoice>> {
        log.debug("REST request to get a page of Invoices")
        val page = invoiceService.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /invoices/:id` : get the "id" invoice.
     *
     * @param id the id of the invoice to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the invoice, or with status `404 (Not Found)`.
     */
    @GetMapping("/invoices/{id}")
    fun getInvoice(@PathVariable id: Long): ResponseEntity<Invoice> {
        log.debug("REST request to get Invoice : {}", id)
        val invoice = invoiceService.findOne(id)
        return ResponseUtil.wrapOrNotFound(invoice)
    }
    /**
     *  `DELETE  /invoices/:id` : delete the "id" invoice.
     *
     * @param id the id of the invoice to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/invoices/{id}")
    fun deleteInvoice(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Invoice : {}", id)

        invoiceService.delete(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
