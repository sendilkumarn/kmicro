package com.sendilkumarn.sample.kinvoice.service
import com.sendilkumarn.sample.kinvoice.domain.Invoice
import com.sendilkumarn.sample.kinvoice.repository.InvoiceRepository
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Invoice].
 */
@Service
@Transactional
class InvoiceService(
    private val invoiceRepository: InvoiceRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a invoice.
     *
     * @param invoice the entity to save.
     * @return the persisted entity.
     */
    fun save(invoice: Invoice): Invoice {
        log.debug("Request to save Invoice : {}", invoice)
        return invoiceRepository.save(invoice)
    }

    /**
     * Get all the invoices.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<Invoice> {
        log.debug("Request to get all Invoices")
        return invoiceRepository.findAll(pageable)
    }

    /**
     * Get one invoice by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<Invoice> {
        log.debug("Request to get Invoice : {}", id)
        return invoiceRepository.findById(id)
    }

    /**
     * Delete the invoice by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Invoice : {}", id)

        invoiceRepository.deleteById(id)
    }
}
