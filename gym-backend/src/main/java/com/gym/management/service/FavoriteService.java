package com.gym.management.service;

import com.gym.management.entity.Favorite;
import com.gym.management.entity.Gym;
import com.gym.management.entity.User;
import com.gym.management.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;

    public List<Favorite> getFavoritesByUser(User user) {
        return favoriteRepository.findByUser(user);
    }

    public boolean toggleFavorite(User user, Gym gym) {
        Optional<Favorite> existing = favoriteRepository.findByUserAndGym(user, gym);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false; // removed
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setGym(gym);
            favorite.setCreateTime(LocalDateTime.now());
            favoriteRepository.save(favorite);
            return true; // added
        }
    }

    public boolean isFavorited(User user, Gym gym) {
        return favoriteRepository.existsByUserAndGym(user, gym);
    }
}
