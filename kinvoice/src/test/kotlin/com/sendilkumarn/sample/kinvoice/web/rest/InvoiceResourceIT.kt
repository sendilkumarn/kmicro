package com.sendilkumarn.sample.kinvoice.web.rest

import com.sendilkumarn.sample.kinvoice.KinvoiceApp
import com.sendilkumarn.sample.kinvoice.domain.Invoice
import com.sendilkumarn.sample.kinvoice.domain.enumeration.InvoiceStatus
import com.sendilkumarn.sample.kinvoice.domain.enumeration.PaymentMethod
import com.sendilkumarn.sample.kinvoice.repository.InvoiceRepository
import com.sendilkumarn.sample.kinvoice.service.InvoiceService
import com.sendilkumarn.sample.kinvoice.web.rest.errors.ExceptionTranslator
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator

/**
 * Integration tests for the [InvoiceResource] REST controller.
 *
 * @see InvoiceResource
 */
@SpringBootTest(classes = [KinvoiceApp::class])
@AutoConfigureMockMvc
@WithMockUser
class InvoiceResourceIT {

    @Autowired
    private lateinit var invoiceRepository: InvoiceRepository

    @Autowired
    private lateinit var invoiceService: InvoiceService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    private lateinit var restInvoiceMockMvc: MockMvc

    private lateinit var invoice: Invoice

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val invoiceResource = InvoiceResource(invoiceService)
         this.restInvoiceMockMvc = MockMvcBuilders.standaloneSetup(invoiceResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        invoice = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createInvoice() {
        val databaseSizeBeforeCreate = invoiceRepository.findAll().size

        // Create the Invoice
        restInvoiceMockMvc.perform(
            post("/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invoice))
        ).andExpect(status().isCreated)

        // Validate the Invoice in the database
        val invoiceList = invoiceRepository.findAll()
        assertThat(invoiceList).hasSize(databaseSizeBeforeCreate + 1)
        val testInvoice = invoiceList[invoiceList.size - 1]
        assertThat(testInvoice.code).isEqualTo(DEFAULT_CODE)
        assertThat(testInvoice.date).isEqualTo(DEFAULT_DATE)
        assertThat(testInvoice.details).isEqualTo(DEFAULT_DETAILS)
        assertThat(testInvoice.status).isEqualTo(DEFAULT_STATUS)
        assertThat(testInvoice.paymentMethod).isEqualTo(DEFAULT_PAYMENT_METHOD)
        assertThat(testInvoice.paymentDate).isEqualTo(DEFAULT_PAYMENT_DATE)
        assertThat(testInvoice.paymentAmount).isEqualTo(DEFAULT_PAYMENT_AMOUNT)
    }

    @Test
    @Transactional
    fun createInvoiceWithExistingId() {
        val databaseSizeBeforeCreate = invoiceRepository.findAll().size

        // Create the Invoice with an existing ID
        invoice.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvoiceMockMvc.perform(
            post("/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invoice))
        ).andExpect(status().isBadRequest)

        // Validate the Invoice in the database
        val invoiceList = invoiceRepository.findAll()
        assertThat(invoiceList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkCodeIsRequired() {
        val databaseSizeBeforeTest = invoiceRepository.findAll().size
        // set the field null
        invoice.code = null

        // Create the Invoice, which fails.

        restInvoiceMockMvc.perform(
            post("/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invoice))
        ).andExpect(status().isBadRequest)

        val invoiceList = invoiceRepository.findAll()
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkDateIsRequired() {
        val databaseSizeBeforeTest = invoiceRepository.findAll().size
        // set the field null
        invoice.date = null

        // Create the Invoice, which fails.

        restInvoiceMockMvc.perform(
            post("/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invoice))
        ).andExpect(status().isBadRequest)

        val invoiceList = invoiceRepository.findAll()
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkStatusIsRequired() {
        val databaseSizeBeforeTest = invoiceRepository.findAll().size
        // set the field null
        invoice.status = null

        // Create the Invoice, which fails.

        restInvoiceMockMvc.perform(
            post("/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invoice))
        ).andExpect(status().isBadRequest)

        val invoiceList = invoiceRepository.findAll()
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPaymentMethodIsRequired() {
        val databaseSizeBeforeTest = invoiceRepository.findAll().size
        // set the field null
        invoice.paymentMethod = null

        // Create the Invoice, which fails.

        restInvoiceMockMvc.perform(
            post("/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invoice))
        ).andExpect(status().isBadRequest)

        val invoiceList = invoiceRepository.findAll()
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPaymentDateIsRequired() {
        val databaseSizeBeforeTest = invoiceRepository.findAll().size
        // set the field null
        invoice.paymentDate = null

        // Create the Invoice, which fails.

        restInvoiceMockMvc.perform(
            post("/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invoice))
        ).andExpect(status().isBadRequest)

        val invoiceList = invoiceRepository.findAll()
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPaymentAmountIsRequired() {
        val databaseSizeBeforeTest = invoiceRepository.findAll().size
        // set the field null
        invoice.paymentAmount = null

        // Create the Invoice, which fails.

        restInvoiceMockMvc.perform(
            post("/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invoice))
        ).andExpect(status().isBadRequest)

        val invoiceList = invoiceRepository.findAll()
        assertThat(invoiceList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllInvoices() {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice)

        // Get all the invoiceList
        restInvoiceMockMvc.perform(get("/api/invoices?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoice.id?.toInt())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].paymentDate").value(hasItem(DEFAULT_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentAmount").value(hasItem(DEFAULT_PAYMENT_AMOUNT?.toInt()))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getInvoice() {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice)

        val id = invoice.id
        assertNotNull(id)

        // Get the invoice
        restInvoiceMockMvc.perform(get("/api/invoices/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(invoice.id?.toInt()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.paymentMethod").value(DEFAULT_PAYMENT_METHOD.toString()))
            .andExpect(jsonPath("$.paymentDate").value(DEFAULT_PAYMENT_DATE.toString()))
            .andExpect(jsonPath("$.paymentAmount").value(DEFAULT_PAYMENT_AMOUNT?.toInt())) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingInvoice() {
        // Get the invoice
        restInvoiceMockMvc.perform(get("/api/invoices/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateInvoice() {
        // Initialize the database
        invoiceService.save(invoice)

        val databaseSizeBeforeUpdate = invoiceRepository.findAll().size

        // Update the invoice
        val id = invoice.id
        assertNotNull(id)
        val updatedInvoice = invoiceRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedInvoice are not directly saved in db
        em.detach(updatedInvoice)
        updatedInvoice.code = UPDATED_CODE
        updatedInvoice.date = UPDATED_DATE
        updatedInvoice.details = UPDATED_DETAILS
        updatedInvoice.status = UPDATED_STATUS
        updatedInvoice.paymentMethod = UPDATED_PAYMENT_METHOD
        updatedInvoice.paymentDate = UPDATED_PAYMENT_DATE
        updatedInvoice.paymentAmount = UPDATED_PAYMENT_AMOUNT

        restInvoiceMockMvc.perform(
            put("/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedInvoice))
        ).andExpect(status().isOk)

        // Validate the Invoice in the database
        val invoiceList = invoiceRepository.findAll()
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate)
        val testInvoice = invoiceList[invoiceList.size - 1]
        assertThat(testInvoice.code).isEqualTo(UPDATED_CODE)
        assertThat(testInvoice.date).isEqualTo(UPDATED_DATE)
        assertThat(testInvoice.details).isEqualTo(UPDATED_DETAILS)
        assertThat(testInvoice.status).isEqualTo(UPDATED_STATUS)
        assertThat(testInvoice.paymentMethod).isEqualTo(UPDATED_PAYMENT_METHOD)
        assertThat(testInvoice.paymentDate).isEqualTo(UPDATED_PAYMENT_DATE)
        assertThat(testInvoice.paymentAmount).isEqualTo(UPDATED_PAYMENT_AMOUNT)
    }

    @Test
    @Transactional
    fun updateNonExistingInvoice() {
        val databaseSizeBeforeUpdate = invoiceRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceMockMvc.perform(
            put("/api/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(invoice))
        ).andExpect(status().isBadRequest)

        // Validate the Invoice in the database
        val invoiceList = invoiceRepository.findAll()
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteInvoice() {
        // Initialize the database
        invoiceService.save(invoice)

        val databaseSizeBeforeDelete = invoiceRepository.findAll().size

        // Delete the invoice
        restInvoiceMockMvc.perform(
            delete("/api/invoices/{id}", invoice.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val invoiceList = invoiceRepository.findAll()
        assertThat(invoiceList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_CODE = "AAAAAAAAAA"
        private const val UPDATED_CODE = "BBBBBBBBBB"

        private val DEFAULT_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_DETAILS = "AAAAAAAAAA"
        private const val UPDATED_DETAILS = "BBBBBBBBBB"

        private val DEFAULT_STATUS: InvoiceStatus = InvoiceStatus.PAID
        private val UPDATED_STATUS: InvoiceStatus = InvoiceStatus.ISSUED

        private val DEFAULT_PAYMENT_METHOD: PaymentMethod = PaymentMethod.CREDIT_CARD
        private val UPDATED_PAYMENT_METHOD: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY

        private val DEFAULT_PAYMENT_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_PAYMENT_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_PAYMENT_AMOUNT: BigDecimal = BigDecimal(1)
        private val UPDATED_PAYMENT_AMOUNT: BigDecimal = BigDecimal(2)

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Invoice {
            val invoice = Invoice(
                code = DEFAULT_CODE,
                date = DEFAULT_DATE,
                details = DEFAULT_DETAILS,
                status = DEFAULT_STATUS,
                paymentMethod = DEFAULT_PAYMENT_METHOD,
                paymentDate = DEFAULT_PAYMENT_DATE,
                paymentAmount = DEFAULT_PAYMENT_AMOUNT
            )

            return invoice
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Invoice {
            val invoice = Invoice(
                code = UPDATED_CODE,
                date = UPDATED_DATE,
                details = UPDATED_DETAILS,
                status = UPDATED_STATUS,
                paymentMethod = UPDATED_PAYMENT_METHOD,
                paymentDate = UPDATED_PAYMENT_DATE,
                paymentAmount = UPDATED_PAYMENT_AMOUNT
            )

            return invoice
        }
    }
}
