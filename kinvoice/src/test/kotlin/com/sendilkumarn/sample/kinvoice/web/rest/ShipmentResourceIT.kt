package com.sendilkumarn.sample.kinvoice.web.rest

import com.sendilkumarn.sample.kinvoice.KinvoiceApp
import com.sendilkumarn.sample.kinvoice.domain.Invoice
import com.sendilkumarn.sample.kinvoice.domain.Shipment
import com.sendilkumarn.sample.kinvoice.repository.ShipmentRepository
import com.sendilkumarn.sample.kinvoice.service.ShipmentService
import com.sendilkumarn.sample.kinvoice.web.rest.errors.ExceptionTranslator
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
 * Integration tests for the [ShipmentResource] REST controller.
 *
 * @see ShipmentResource
 */
@SpringBootTest(classes = [KinvoiceApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ShipmentResourceIT {

    @Autowired
    private lateinit var shipmentRepository: ShipmentRepository

    @Autowired
    private lateinit var shipmentService: ShipmentService

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

    private lateinit var restShipmentMockMvc: MockMvc

    private lateinit var shipment: Shipment

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val shipmentResource = ShipmentResource(shipmentService)
         this.restShipmentMockMvc = MockMvcBuilders.standaloneSetup(shipmentResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        shipment = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createShipment() {
        val databaseSizeBeforeCreate = shipmentRepository.findAll().size

        // Create the Shipment
        restShipmentMockMvc.perform(
            post("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(shipment))
        ).andExpect(status().isCreated)

        // Validate the Shipment in the database
        val shipmentList = shipmentRepository.findAll()
        assertThat(shipmentList).hasSize(databaseSizeBeforeCreate + 1)
        val testShipment = shipmentList[shipmentList.size - 1]
        assertThat(testShipment.trackingCode).isEqualTo(DEFAULT_TRACKING_CODE)
        assertThat(testShipment.date).isEqualTo(DEFAULT_DATE)
        assertThat(testShipment.details).isEqualTo(DEFAULT_DETAILS)
    }

    @Test
    @Transactional
    fun createShipmentWithExistingId() {
        val databaseSizeBeforeCreate = shipmentRepository.findAll().size

        // Create the Shipment with an existing ID
        shipment.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentMockMvc.perform(
            post("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(shipment))
        ).andExpect(status().isBadRequest)

        // Validate the Shipment in the database
        val shipmentList = shipmentRepository.findAll()
        assertThat(shipmentList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkDateIsRequired() {
        val databaseSizeBeforeTest = shipmentRepository.findAll().size
        // set the field null
        shipment.date = null

        // Create the Shipment, which fails.

        restShipmentMockMvc.perform(
            post("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(shipment))
        ).andExpect(status().isBadRequest)

        val shipmentList = shipmentRepository.findAll()
        assertThat(shipmentList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllShipments() {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment)

        // Get all the shipmentList
        restShipmentMockMvc.perform(get("/api/shipments?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipment.id?.toInt())))
            .andExpect(jsonPath("$.[*].trackingCode").value(hasItem(DEFAULT_TRACKING_CODE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getShipment() {
        // Initialize the database
        shipmentRepository.saveAndFlush(shipment)

        val id = shipment.id
        assertNotNull(id)

        // Get the shipment
        restShipmentMockMvc.perform(get("/api/shipments/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipment.id?.toInt()))
            .andExpect(jsonPath("$.trackingCode").value(DEFAULT_TRACKING_CODE))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS)) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingShipment() {
        // Get the shipment
        restShipmentMockMvc.perform(get("/api/shipments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateShipment() {
        // Initialize the database
        shipmentService.save(shipment)

        val databaseSizeBeforeUpdate = shipmentRepository.findAll().size

        // Update the shipment
        val id = shipment.id
        assertNotNull(id)
        val updatedShipment = shipmentRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedShipment are not directly saved in db
        em.detach(updatedShipment)
        updatedShipment.trackingCode = UPDATED_TRACKING_CODE
        updatedShipment.date = UPDATED_DATE
        updatedShipment.details = UPDATED_DETAILS

        restShipmentMockMvc.perform(
            put("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedShipment))
        ).andExpect(status().isOk)

        // Validate the Shipment in the database
        val shipmentList = shipmentRepository.findAll()
        assertThat(shipmentList).hasSize(databaseSizeBeforeUpdate)
        val testShipment = shipmentList[shipmentList.size - 1]
        assertThat(testShipment.trackingCode).isEqualTo(UPDATED_TRACKING_CODE)
        assertThat(testShipment.date).isEqualTo(UPDATED_DATE)
        assertThat(testShipment.details).isEqualTo(UPDATED_DETAILS)
    }

    @Test
    @Transactional
    fun updateNonExistingShipment() {
        val databaseSizeBeforeUpdate = shipmentRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentMockMvc.perform(
            put("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(shipment))
        ).andExpect(status().isBadRequest)

        // Validate the Shipment in the database
        val shipmentList = shipmentRepository.findAll()
        assertThat(shipmentList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteShipment() {
        // Initialize the database
        shipmentService.save(shipment)

        val databaseSizeBeforeDelete = shipmentRepository.findAll().size

        // Delete the shipment
        restShipmentMockMvc.perform(
            delete("/api/shipments/{id}", shipment.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val shipmentList = shipmentRepository.findAll()
        assertThat(shipmentList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_TRACKING_CODE = "AAAAAAAAAA"
        private const val UPDATED_TRACKING_CODE = "BBBBBBBBBB"

        private val DEFAULT_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_DETAILS = "AAAAAAAAAA"
        private const val UPDATED_DETAILS = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Shipment {
            val shipment = Shipment(
                trackingCode = DEFAULT_TRACKING_CODE,
                date = DEFAULT_DATE,
                details = DEFAULT_DETAILS
            )

            // Add required entity
            val invoice: Invoice
            if (em.findAll(Invoice::class).isEmpty()) {
                invoice = InvoiceResourceIT.createEntity(em)
                em.persist(invoice)
                em.flush()
            } else {
                invoice = em.findAll(Invoice::class).get(0)
            }
            shipment.invoice = invoice
            return shipment
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Shipment {
            val shipment = Shipment(
                trackingCode = UPDATED_TRACKING_CODE,
                date = UPDATED_DATE,
                details = UPDATED_DETAILS
            )

            // Add required entity
            val invoice: Invoice
            if (em.findAll(Invoice::class).isEmpty()) {
                invoice = InvoiceResourceIT.createUpdatedEntity(em)
                em.persist(invoice)
                em.flush()
            } else {
                invoice = em.findAll(Invoice::class).get(0)
            }
            shipment.invoice = invoice
            return shipment
        }
    }
}
