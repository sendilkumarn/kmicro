package com.sendilkumarn.sample.knotification.repository

import com.sendilkumarn.sample.knotification.domain.Notification
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data MongoDB repository for the [Notification] entity.
 */
@Suppress("unused")
@Repository
interface NotificationRepository : MongoRepository<Notification, String>
