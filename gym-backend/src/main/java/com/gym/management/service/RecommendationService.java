package com.gym.management.service;

import com.gym.management.entity.Gym;
import com.gym.management.entity.Rating;
import com.gym.management.entity.User;
import com.gym.management.repository.GymRepository;
import com.gym.management.repository.RatingRepository;
import com.gym.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private RatingRepository ratingRepository;
    
    @Autowired
    private GymRepository gymRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<Gym> recommendGyms(Long userId) {
        User currentUser = userRepository.findById(userId).orElse(null);
        if (currentUser == null) return Collections.emptyList();

        List<Rating> allRatings = ratingRepository.findAll();
        List<User> allUsers = userRepository.findAll();
        
        // Map of userId -> Map of gymId -> score
        Map<Long, Map<Long, Integer>> userRatingsMap = new HashMap<>();
        for (Rating rating : allRatings) {
            userRatingsMap.computeIfAbsent(rating.getUser().getId(), k -> new HashMap<>())
                          .put(rating.getGym().getId(), rating.getScore());
        }

        Map<Long, Integer> currentUserRatings = userRatingsMap.getOrDefault(userId, Collections.emptyMap());
        
        // Calculate similarities
        Map<Long, Double> userSimilarities = new HashMap<>();
        for (User user : allUsers) {
            if (user.getId().equals(userId)) continue;
            
            Map<Long, Integer> otherUserRatings = userRatingsMap.getOrDefault(user.getId(), Collections.emptyMap());
            double similarity = calculateCosineSimilarity(currentUserRatings, otherUserRatings);
            if (similarity > 0) {
                userSimilarities.put(user.getId(), similarity);
            }
        }

        // Recommend gyms
        Map<Long, Double> gymRecommendations = new HashMap<>();
        for (Map.Entry<Long, Double> entry : userSimilarities.entrySet()) {
            Long otherUserId = entry.getKey();
            double similarity = entry.getValue();
            Map<Long, Integer> otherUserRatings = userRatingsMap.get(otherUserId);

            for (Map.Entry<Long, Integer> ratingEntry : otherUserRatings.entrySet()) {
                Long gymId = ratingEntry.getKey();
                int score = ratingEntry.getValue();

                // If current user hasn't rated this gym
                if (!currentUserRatings.containsKey(gymId)) {
                    gymRecommendations.put(gymId, gymRecommendations.getOrDefault(gymId, 0.0) + score * similarity);
                }
            }
        }

        return gymRecommendations.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(5)
                .map(e -> gymRepository.findById(e.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private double calculateCosineSimilarity(Map<Long, Integer> ratings1, Map<Long, Integer> ratings2) {
        Set<Long> commonGyms = new HashSet<>(ratings1.keySet());
        commonGyms.retainAll(ratings2.keySet());

        if (commonGyms.isEmpty()) return 0.0;

        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        for (Integer score : ratings1.values()) {
            norm1 += Math.pow(score, 2);
        }
        for (Integer score : ratings2.values()) {
            norm2 += Math.pow(score, 2);
        }

        for (Long gymId : commonGyms) {
            dotProduct += ratings1.get(gymId) * ratings2.get(gymId);
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
