package com.sendilkumarn.sample.kstore.web.rest

import com.sendilkumarn.sample.kstore.domain.Customer
import com.sendilkumarn.sample.kstore.service.CustomerService
import com.sendilkumarn.sample.kstore.web.rest.errors.BadRequestAlertException
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

private const val ENTITY_NAME = "customer"
/**
 * REST controller for managing [com.sendilkumarn.sample.kstore.domain.Customer].
 */
@RestController
@RequestMapping("/api")
class CustomerResource(
    private val customerService: CustomerService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /customers` : Create a new customer.
     *
     * @param customer the customer to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new customer, or with status `400 (Bad Request)` if the customer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/customers")
    fun createCustomer(@Valid @RequestBody customer: Customer): ResponseEntity<Customer> {
        log.debug("REST request to save Customer : {}", customer)
        if (customer.id != null) {
            throw BadRequestAlertException(
                "A new customer cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = customerService.save(customer)
        return ResponseEntity.created(URI("/api/customers/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /customers` : Updates an existing customer.
     *
     * @param customer the customer to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated customer,
     * or with status `400 (Bad Request)` if the customer is not valid,
     * or with status `500 (Internal Server Error)` if the customer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/customers")
    fun updateCustomer(@Valid @RequestBody customer: Customer): ResponseEntity<Customer> {
        log.debug("REST request to update Customer : {}", customer)
        if (customer.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = customerService.save(customer)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                     customer.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /customers` : get all the customers.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of customers in body.
     */
    @GetMapping("/customers")
    fun getAllCustomers(pageable: Pageable): ResponseEntity<List<Customer>> {
        log.debug("REST request to get a page of Customers")
        val page = customerService.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /customers/:id` : get the "id" customer.
     *
     * @param id the id of the customer to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the customer, or with status `404 (Not Found)`.
     */
    @GetMapping("/customers/{id}")
    fun getCustomer(@PathVariable id: Long): ResponseEntity<Customer> {
        log.debug("REST request to get Customer : {}", id)
        val customer = customerService.findOne(id)
        return ResponseUtil.wrapOrNotFound(customer)
    }
    /**
     *  `DELETE  /customers/:id` : delete the "id" customer.
     *
     * @param id the id of the customer to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/customers/{id}")
    fun deleteCustomer(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Customer : {}", id)

        customerService.delete(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
