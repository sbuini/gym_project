package com.gym.management.controller;

import com.gym.management.entity.Booking;
import com.gym.management.entity.Gym;
import com.gym.management.entity.User;
import com.gym.management.service.BookingService;
import com.gym.management.service.GymService;
import com.gym.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookingController {
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private GymService gymService;
    
    @Autowired
    private UserService userService;

    @PostMapping("/book")
    public String bookGym(@RequestParam Long gymId, @RequestParam Long userId) {
        User user = userService.findById(userId);
        Gym gym = gymService.getGymById(gymId).orElse(null);
        
        if (user != null && gym != null) {
            bookingService.createBooking(user, gym);
        }
        
        return "redirect:/bookings?userId=" + userId;
    }

    @GetMapping("/bookings")
    public String myBookings(@RequestParam Long userId, org.springframework.ui.Model model) {
        User user = userService.findById(userId);
        if (user != null) {
            model.addAttribute("bookings", bookingService.getBookingsByUser(user));
            model.addAttribute("currentUser", user);
            return "bookings";
        }
        return "redirect:/";
    }
}
