package com.gym.management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;
    
    private Integer score; // 1-5
    
    private String comment;
    
    private java.time.LocalDateTime createTime;
}
