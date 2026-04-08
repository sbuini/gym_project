package com.gym.management.controller;

import com.gym.management.entity.Gym;
import com.gym.management.entity.User;
import com.gym.management.service.GymService;
import com.gym.management.service.RecommendationService;
import com.gym.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class GymController {
    @Autowired
    private GymService gymService;
    
    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) Long userId) {
        List<Gym> allGyms = gymService.getAllGyms();
        model.addAttribute("gyms", allGyms);
        
        if (userId != null) {
            List<Gym> recommended = recommendationService.recommendGyms(userId);
            model.addAttribute("recommendations", recommended);
            model.addAttribute("currentUser", userService.findById(userId));
        }
        
        return "index";
    }

    @GetMapping("/gym/{id}")
    public String gymDetail(@PathVariable Long id, Model model) {
        gymService.getGymById(id).ifPresent(gym -> model.addAttribute("gym", gym));
        return "gym_detail";
    }
}
