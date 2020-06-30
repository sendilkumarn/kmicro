package com.sendilkumarn.sample.kstore.service
import com.sendilkumarn.sample.kstore.domain.Customer
import com.sendilkumarn.sample.kstore.repository.CustomerRepository
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Customer].
 */
@Service
@Transactional
class CustomerService(
    private val customerRepository: CustomerRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a customer.
     *
     * @param customer the entity to save.
     * @return the persisted entity.
     */
    fun save(customer: Customer): Customer {
        log.debug("Request to save Customer : {}", customer)
        return customerRepository.save(customer)
    }

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Customer> {
        log.debug("Request to get all Customers")
        return customerRepository.findAll(pageable)
    }

    /**
     * Get one customer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Customer> {
        log.debug("Request to get Customer : {}", id)
        return customerRepository.findById(id)
    }

    /**
     * Delete the customer by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Customer : {}", id)

        customerRepository.deleteById(id)
    }
}
