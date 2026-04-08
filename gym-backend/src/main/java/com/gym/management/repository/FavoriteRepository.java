package com.gym.management.repository;

import com.gym.management.entity.Favorite;
import com.gym.management.entity.Gym;
import com.gym.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    Optional<Favorite> findByUserAndGym(User user, Gym gym);
    boolean existsByUserAndGym(User user, Gym gym);
}
