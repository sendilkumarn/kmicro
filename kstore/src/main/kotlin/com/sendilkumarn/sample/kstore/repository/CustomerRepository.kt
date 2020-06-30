package com.sendilkumarn.sample.kstore.repository

import com.sendilkumarn.sample.kstore.domain.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Customer] entity.
 */
@Suppress("unused")
@Repository
interface CustomerRepository : JpaRepository<Customer, Long>
