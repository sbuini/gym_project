package com.gym.management.repository;

import com.gym.management.entity.Rating;
import com.gym.management.entity.User;
import com.gym.management.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByUser(User user);
    List<Rating> findByGym(Gym gym);
    Optional<Rating> findByUserAndGym(User user, Gym gym);
}
