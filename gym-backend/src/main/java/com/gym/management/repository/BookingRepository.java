package com.gym.management.repository;

import com.gym.management.entity.Booking;
import com.gym.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByStatusOrderByBookingTimeDesc(String status);
    List<Booking> findAllByOrderByBookingTimeDesc();
    List<Booking> findByUserAndStatusAndAttended(User user, String status, Boolean attended);
    long countByStatus(String status);
}
