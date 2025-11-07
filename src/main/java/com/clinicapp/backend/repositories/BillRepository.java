package com.clinicapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinicapp.backend.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
