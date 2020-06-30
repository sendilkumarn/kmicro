package com.sendilkumarn.sample.kstore.web.rest

import com.sendilkumarn.sample.kstore.KstoreApp
import com.sendilkumarn.sample.kstore.domain.Customer
import com.sendilkumarn.sample.kstore.domain.enumeration.Gender
import com.sendilkumarn.sample.kstore.repository.CustomerRepository
import com.sendilkumarn.sample.kstore.service.CustomerService
import com.sendilkumarn.sample.kstore.web.rest.errors.ExceptionTranslator
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
 * Integration tests for the [CustomerResource] REST controller.
 *
 * @see CustomerResource
 */
@SpringBootTest(classes = [KstoreApp::class])
@AutoConfigureMockMvc
@WithMockUser
class CustomerResourceIT {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var customerService: CustomerService

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

    private lateinit var restCustomerMockMvc: MockMvc

    private lateinit var customer: Customer

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val customerResource = CustomerResource(customerService)
         this.restCustomerMockMvc = MockMvcBuilders.standaloneSetup(customerResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        customer = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createCustomer() {
        val databaseSizeBeforeCreate = customerRepository.findAll().size

        // Create the Customer
        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(customer))
        ).andExpect(status().isCreated)

        // Validate the Customer in the database
        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeCreate + 1)
        val testCustomer = customerList[customerList.size - 1]
        assertThat(testCustomer.firstName).isEqualTo(DEFAULT_FIRST_NAME)
        assertThat(testCustomer.lastName).isEqualTo(DEFAULT_LAST_NAME)
        assertThat(testCustomer.gender).isEqualTo(DEFAULT_GENDER)
        assertThat(testCustomer.email).isEqualTo(DEFAULT_EMAIL)
        assertThat(testCustomer.phone).isEqualTo(DEFAULT_PHONE)
        assertThat(testCustomer.addressLine1).isEqualTo(DEFAULT_ADDRESS_LINE_1)
        assertThat(testCustomer.addressLine2).isEqualTo(DEFAULT_ADDRESS_LINE_2)
        assertThat(testCustomer.city).isEqualTo(DEFAULT_CITY)
        assertThat(testCustomer.country).isEqualTo(DEFAULT_COUNTRY)
    }

    @Test
    @Transactional
    fun createCustomerWithExistingId() {
        val databaseSizeBeforeCreate = customerRepository.findAll().size

        // Create the Customer with an existing ID
        customer.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(customer))
        ).andExpect(status().isBadRequest)

        // Validate the Customer in the database
        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkFirstNameIsRequired() {
        val databaseSizeBeforeTest = customerRepository.findAll().size
        // set the field null
        customer.firstName = null

        // Create the Customer, which fails.

        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(customer))
        ).andExpect(status().isBadRequest)

        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkLastNameIsRequired() {
        val databaseSizeBeforeTest = customerRepository.findAll().size
        // set the field null
        customer.lastName = null

        // Create the Customer, which fails.

        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(customer))
        ).andExpect(status().isBadRequest)

        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkGenderIsRequired() {
        val databaseSizeBeforeTest = customerRepository.findAll().size
        // set the field null
        customer.gender = null

        // Create the Customer, which fails.

        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(customer))
        ).andExpect(status().isBadRequest)

        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkEmailIsRequired() {
        val databaseSizeBeforeTest = customerRepository.findAll().size
        // set the field null
        customer.email = null

        // Create the Customer, which fails.

        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(customer))
        ).andExpect(status().isBadRequest)

        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkPhoneIsRequired() {
        val databaseSizeBeforeTest = customerRepository.findAll().size
        // set the field null
        customer.phone = null

        // Create the Customer, which fails.

        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(customer))
        ).andExpect(status().isBadRequest)

        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkAddressLine1IsRequired() {
        val databaseSizeBeforeTest = customerRepository.findAll().size
        // set the field null
        customer.addressLine1 = null

        // Create the Customer, which fails.

        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(customer))
        ).andExpect(status().isBadRequest)

        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkCityIsRequired() {
        val databaseSizeBeforeTest = customerRepository.findAll().size
        // set the field null
        customer.city = null

        // Create the Customer, which fails.

        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(customer))
        ).andExpect(status().isBadRequest)

        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkCountryIsRequired() {
        val databaseSizeBeforeTest = customerRepository.findAll().size
        // set the field null
        customer.country = null

        // Create the Customer, which fails.

        restCustomerMockMvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(customer))
        ).andExpect(status().isBadRequest)

        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCustomers() {
        // Initialize the database
        customerRepository.saveAndFlush(customer)

        // Get all the customerList
        restCustomerMockMvc.perform(get("/api/customers?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customer.id?.toInt())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getCustomer() {
        // Initialize the database
        customerRepository.saveAndFlush(customer)

        val id = customer.id
        assertNotNull(id)

        // Get the customer
        restCustomerMockMvc.perform(get("/api/customers/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customer.id?.toInt()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.addressLine1").value(DEFAULT_ADDRESS_LINE_1))
            .andExpect(jsonPath("$.addressLine2").value(DEFAULT_ADDRESS_LINE_2))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY)) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingCustomer() {
        // Get the customer
        restCustomerMockMvc.perform(get("/api/customers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateCustomer() {
        // Initialize the database
        customerService.save(customer)

        val databaseSizeBeforeUpdate = customerRepository.findAll().size

        // Update the customer
        val id = customer.id
        assertNotNull(id)
        val updatedCustomer = customerRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedCustomer are not directly saved in db
        em.detach(updatedCustomer)
        updatedCustomer.firstName = UPDATED_FIRST_NAME
        updatedCustomer.lastName = UPDATED_LAST_NAME
        updatedCustomer.gender = UPDATED_GENDER
        updatedCustomer.email = UPDATED_EMAIL
        updatedCustomer.phone = UPDATED_PHONE
        updatedCustomer.addressLine1 = UPDATED_ADDRESS_LINE_1
        updatedCustomer.addressLine2 = UPDATED_ADDRESS_LINE_2
        updatedCustomer.city = UPDATED_CITY
        updatedCustomer.country = UPDATED_COUNTRY

        restCustomerMockMvc.perform(
            put("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedCustomer))
        ).andExpect(status().isOk)

        // Validate the Customer in the database
        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate)
        val testCustomer = customerList[customerList.size - 1]
        assertThat(testCustomer.firstName).isEqualTo(UPDATED_FIRST_NAME)
        assertThat(testCustomer.lastName).isEqualTo(UPDATED_LAST_NAME)
        assertThat(testCustomer.gender).isEqualTo(UPDATED_GENDER)
        assertThat(testCustomer.email).isEqualTo(UPDATED_EMAIL)
        assertThat(testCustomer.phone).isEqualTo(UPDATED_PHONE)
        assertThat(testCustomer.addressLine1).isEqualTo(UPDATED_ADDRESS_LINE_1)
        assertThat(testCustomer.addressLine2).isEqualTo(UPDATED_ADDRESS_LINE_2)
        assertThat(testCustomer.city).isEqualTo(UPDATED_CITY)
        assertThat(testCustomer.country).isEqualTo(UPDATED_COUNTRY)
    }

    @Test
    @Transactional
    fun updateNonExistingCustomer() {
        val databaseSizeBeforeUpdate = customerRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerMockMvc.perform(
            put("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(customer))
        ).andExpect(status().isBadRequest)

        // Validate the Customer in the database
        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteCustomer() {
        // Initialize the database
        customerService.save(customer)

        val databaseSizeBeforeDelete = customerRepository.findAll().size

        // Delete the customer
        restCustomerMockMvc.perform(
            delete("/api/customers/{id}", customer.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val customerList = customerRepository.findAll()
        assertThat(customerList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_FIRST_NAME = "AAAAAAAAAA"
        private const val UPDATED_FIRST_NAME = "BBBBBBBBBB"

        private const val DEFAULT_LAST_NAME = "AAAAAAAAAA"
        private const val UPDATED_LAST_NAME = "BBBBBBBBBB"

        private val DEFAULT_GENDER: Gender = Gender.MALE
        private val UPDATED_GENDER: Gender = Gender.FEMALE

        private const val DEFAULT_EMAIL = "Uv\"D'@}!>M*.5TL^"
        private const val UPDATED_EMAIL = "[8LRX%@1U!wH.{~cxyH"

        private const val DEFAULT_PHONE = "AAAAAAAAAA"
        private const val UPDATED_PHONE = "BBBBBBBBBB"

        private const val DEFAULT_ADDRESS_LINE_1 = "AAAAAAAAAA"
        private const val UPDATED_ADDRESS_LINE_1 = "BBBBBBBBBB"

        private const val DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA"
        private const val UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB"

        private const val DEFAULT_CITY = "AAAAAAAAAA"
        private const val UPDATED_CITY = "BBBBBBBBBB"

        private const val DEFAULT_COUNTRY = "AAAAAAAAAA"
        private const val UPDATED_COUNTRY = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Customer {
            val customer = Customer(
                firstName = DEFAULT_FIRST_NAME,
                lastName = DEFAULT_LAST_NAME,
                gender = DEFAULT_GENDER,
                email = DEFAULT_EMAIL,
                phone = DEFAULT_PHONE,
                addressLine1 = DEFAULT_ADDRESS_LINE_1,
                addressLine2 = DEFAULT_ADDRESS_LINE_2,
                city = DEFAULT_CITY,
                country = DEFAULT_COUNTRY
            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            customer.user = user
            return customer
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Customer {
            val customer = Customer(
                firstName = UPDATED_FIRST_NAME,
                lastName = UPDATED_LAST_NAME,
                gender = UPDATED_GENDER,
                email = UPDATED_EMAIL,
                phone = UPDATED_PHONE,
                addressLine1 = UPDATED_ADDRESS_LINE_1,
                addressLine2 = UPDATED_ADDRESS_LINE_2,
                city = UPDATED_CITY,
                country = UPDATED_COUNTRY
            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            customer.user = user
            return customer
        }
    }
}
