package com.sendilkumarn.sample.knotification.web.rest

import com.sendilkumarn.sample.knotification.KnotificationApp
import com.sendilkumarn.sample.knotification.domain.Notification
import com.sendilkumarn.sample.knotification.domain.enumeration.NotificationType
import com.sendilkumarn.sample.knotification.repository.NotificationRepository
import com.sendilkumarn.sample.knotification.web.rest.errors.ExceptionTranslator
import java.time.Instant
import java.time.temporal.ChronoUnit
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
import org.springframework.validation.Validator

/**
 * Integration tests for the [NotificationResource] REST controller.
 *
 * @see NotificationResource
 */
@SpringBootTest(classes = [KnotificationApp::class])
@AutoConfigureMockMvc
@WithMockUser
class NotificationResourceIT {

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restNotificationMockMvc: MockMvc

    private lateinit var notification: Notification

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val notificationResource = NotificationResource(notificationRepository)
         this.restNotificationMockMvc = MockMvcBuilders.standaloneSetup(notificationResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        notificationRepository.deleteAll()
        notification = createEntity()
    }

    @Test
    @Throws(Exception::class)
    fun createNotification() {
        val databaseSizeBeforeCreate = notificationRepository.findAll().size

        // Create the Notification
        restNotificationMockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isCreated)

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1)
        val testNotification = notificationList[notificationList.size - 1]
        assertThat(testNotification.date).isEqualTo(DEFAULT_DATE)
        assertThat(testNotification.details).isEqualTo(DEFAULT_DETAILS)
        assertThat(testNotification.sentDate).isEqualTo(DEFAULT_SENT_DATE)
        assertThat(testNotification.format).isEqualTo(DEFAULT_FORMAT)
        assertThat(testNotification.userId).isEqualTo(DEFAULT_USER_ID)
        assertThat(testNotification.productId).isEqualTo(DEFAULT_PRODUCT_ID)
    }

    @Test
    fun createNotificationWithExistingId() {
        val databaseSizeBeforeCreate = notificationRepository.findAll().size

        // Create the Notification with an existing ID
        notification.id = "existing_id"

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationMockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isBadRequest)

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    fun checkDateIsRequired() {
        val databaseSizeBeforeTest = notificationRepository.findAll().size
        // set the field null
        notification.date = null

        // Create the Notification, which fails.

        restNotificationMockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isBadRequest)

        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    fun checkSentDateIsRequired() {
        val databaseSizeBeforeTest = notificationRepository.findAll().size
        // set the field null
        notification.sentDate = null

        // Create the Notification, which fails.

        restNotificationMockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isBadRequest)

        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    fun checkFormatIsRequired() {
        val databaseSizeBeforeTest = notificationRepository.findAll().size
        // set the field null
        notification.format = null

        // Create the Notification, which fails.

        restNotificationMockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isBadRequest)

        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    fun checkUserIdIsRequired() {
        val databaseSizeBeforeTest = notificationRepository.findAll().size
        // set the field null
        notification.userId = null

        // Create the Notification, which fails.

        restNotificationMockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isBadRequest)

        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    fun checkProductIdIsRequired() {
        val databaseSizeBeforeTest = notificationRepository.findAll().size
        // set the field null
        notification.productId = null

        // Create the Notification, which fails.

        restNotificationMockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isBadRequest)

        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Throws(Exception::class)
    fun getAllNotifications() {
        // Initialize the database
        notificationRepository.save(notification)

        // Get all the notificationList
        restNotificationMockMvc.perform(get("/api/notifications?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.id)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
            .andExpect(jsonPath("$.[*].sentDate").value(hasItem(DEFAULT_SENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID?.toInt())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID?.toInt()))) }

    @Test
    @Throws(Exception::class)
    fun getNotification() {
        // Initialize the database
        notificationRepository.save(notification)

        val id = notification.id
        assertNotNull(id)

        // Get the notification
        restNotificationMockMvc.perform(get("/api/notifications/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notification.id))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS))
            .andExpect(jsonPath("$.sentDate").value(DEFAULT_SENT_DATE.toString()))
            .andExpect(jsonPath("$.format").value(DEFAULT_FORMAT.toString()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID?.toInt()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID?.toInt())) }

    @Test
    @Throws(Exception::class)
    fun getNonExistingNotification() {
        // Get the notification
        restNotificationMockMvc.perform(get("/api/notifications/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    fun updateNotification() {
        // Initialize the database
        notificationRepository.save(notification)

        val databaseSizeBeforeUpdate = notificationRepository.findAll().size

        // Update the notification
        val id = notification.id
        assertNotNull(id)
        val updatedNotification = notificationRepository.findById(id).get()
        updatedNotification.date = UPDATED_DATE
        updatedNotification.details = UPDATED_DETAILS
        updatedNotification.sentDate = UPDATED_SENT_DATE
        updatedNotification.format = UPDATED_FORMAT
        updatedNotification.userId = UPDATED_USER_ID
        updatedNotification.productId = UPDATED_PRODUCT_ID

        restNotificationMockMvc.perform(
            put("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedNotification))
        ).andExpect(status().isOk)

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate)
        val testNotification = notificationList[notificationList.size - 1]
        assertThat(testNotification.date).isEqualTo(UPDATED_DATE)
        assertThat(testNotification.details).isEqualTo(UPDATED_DETAILS)
        assertThat(testNotification.sentDate).isEqualTo(UPDATED_SENT_DATE)
        assertThat(testNotification.format).isEqualTo(UPDATED_FORMAT)
        assertThat(testNotification.userId).isEqualTo(UPDATED_USER_ID)
        assertThat(testNotification.productId).isEqualTo(UPDATED_PRODUCT_ID)
    }

    @Test
    fun updateNonExistingNotification() {
        val databaseSizeBeforeUpdate = notificationRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc.perform(
            put("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(notification))
        ).andExpect(status().isBadRequest)

        // Validate the Notification in the database
        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Throws(Exception::class)
    fun deleteNotification() {
        // Initialize the database
        notificationRepository.save(notification)

        val databaseSizeBeforeDelete = notificationRepository.findAll().size

        // Delete the notification
        restNotificationMockMvc.perform(
            delete("/api/notifications/{id}", notification.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val notificationList = notificationRepository.findAll()
        assertThat(notificationList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private val DEFAULT_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_DETAILS = "AAAAAAAAAA"
        private const val UPDATED_DETAILS = "BBBBBBBBBB"

        private val DEFAULT_SENT_DATE: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_SENT_DATE: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_FORMAT: NotificationType = NotificationType.EMAIL
        private val UPDATED_FORMAT: NotificationType = NotificationType.SMS

        private const val DEFAULT_USER_ID: Long = 1L
        private const val UPDATED_USER_ID: Long = 2L

        private const val DEFAULT_PRODUCT_ID: Long = 1L
        private const val UPDATED_PRODUCT_ID: Long = 2L

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(): Notification {
            val notification = Notification(
                date = DEFAULT_DATE,
                details = DEFAULT_DETAILS,
                sentDate = DEFAULT_SENT_DATE,
                format = DEFAULT_FORMAT,
                userId = DEFAULT_USER_ID,
                productId = DEFAULT_PRODUCT_ID
            )

            return notification
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(): Notification {
            val notification = Notification(
                date = UPDATED_DATE,
                details = UPDATED_DETAILS,
                sentDate = UPDATED_SENT_DATE,
                format = UPDATED_FORMAT,
                userId = UPDATED_USER_ID,
                productId = UPDATED_PRODUCT_ID
            )

            return notification
        }
    }
}
