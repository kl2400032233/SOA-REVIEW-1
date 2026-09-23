package com.klu.springmvc.repository;

import com.klu.springmvc.model.AvailabilityStatus;
import com.klu.springmvc.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByAvailabilityStatus(AvailabilityStatus status);
}
