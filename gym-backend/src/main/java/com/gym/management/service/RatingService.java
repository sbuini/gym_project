package com.gym.management.service;

import com.gym.management.entity.Gym;
import com.gym.management.entity.Rating;
import com.gym.management.entity.User;
import com.gym.management.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    public List<Rating> getRatingsByGym(Gym gym) {
        return ratingRepository.findByGym(gym);
    }

    public Rating saveRating(User user, Gym gym, Integer score, String comment) {
        Rating rating = ratingRepository.findByUserAndGym(user, gym).orElse(new Rating());
        rating.setUser(user);
        rating.setGym(gym);
        rating.setScore(score);
        rating.setComment(comment);
        rating.setCreateTime(LocalDateTime.now());
        return ratingRepository.save(rating);
    }
}
