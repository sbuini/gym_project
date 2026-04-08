package com.gym.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;
    
    private LocalDateTime bookingTime;
    
    private String status; // "PENDING", "CONFIRMED", "CANCELLED"
    
    private Boolean attended = false;
    
    private LocalDateTime signInTime;
}
