package com.sendilkumarn.sample.kstore.repository

import com.sendilkumarn.sample.kstore.domain.Authority
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring Data JPA repository for the [Authority] entity.
 */

interface AuthorityRepository : JpaRepository<Authority, String>
