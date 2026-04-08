package com.gym.management.controller;

import com.gym.management.dto.UserDto;
import com.gym.management.entity.*;
import com.gym.management.repository.GymRepository;
import com.gym.management.repository.UserRepository;
import com.gym.management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ApiController {

    @Autowired
    private GymService gymService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private UserRepository userRepository;

    // --- Authentication ---
    @PostMapping("/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        Optional<User> userOpt = userService.findByUsername(username);
        Map<String, Object> response = new HashMap<>();
        
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
            response.put("success", true);
            response.put("user", userService.toDto(user));
            response.put("token", "mock-jwt-token-" + user.getId());
            return response;
        }
        
        response.put("success", false);
        response.put("message", "Invalid username or password");
        return response;
    }

    @PostMapping("/auth/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return response;
        }
        
        try {
            User savedUser = userService.registerUser(user);
            response.put("success", true);
            response.put("user", userService.toDto(savedUser));
            response.put("message", "注册成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/auth/admin/login")
    public Map<String, Object> adminLogin(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = login(credentials);
        Object success = response.get("success");
        if (!(success instanceof Boolean) || !((Boolean) success)) {
            return response;
        }
        Object userObj = response.get("user");
        if (userObj instanceof UserDto) {
            UserDto user = (UserDto) userObj;
            if (!"ROLE_ADMIN".equals(user.getRole())) {
                Map<String, Object> denied = new HashMap<>();
                denied.put("success", false);
                denied.put("message", "Not an admin account");
                return denied;
            }
        }
        return response;
    }

    // --- Gyms ---
    @GetMapping("/gyms")
    public List<Gym> getAllGyms() {
        return gymService.getAllGyms();
    }

    @GetMapping("/gyms/{id}")
    public Optional<Gym> getGymById(@PathVariable Long id) {
        return gymService.getGymById(id);
    }

    @PostMapping("/admin/gyms")
    public Gym saveGym(@RequestBody Gym gym) {
        return gymService.saveGym(gym);
    }

    @DeleteMapping("/admin/gyms/{id}")
    public void deleteGym(@PathVariable Long id) {
        // Implement delete in GymService if not exists
        // gymService.deleteGym(id);
    }

    // --- Recommendations ---
    @GetMapping("/recommendations/{userId}")
    public List<Gym> getRecommendations(@PathVariable Long userId) {
        return recommendationService.recommendGyms(userId);
    }

    // --- User Management (Admin) ---
    @GetMapping("/admin/users")
    public List<UserDto> getAllUsers() {
        return userService.findAll().stream()
                .map(userService::toDto)
                .toList();
    }

    @DeleteMapping("/admin/users/{userId}")
    public Map<String, Object> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return res;
    }

    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userService.toDto(userService.findById(userId));
    }

    @PutMapping("/users/{userId}/email")
    public Map<String, Object> updateEmail(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String email = body.get("email");
        UserDto updated = userService.updateEmail(userId, email);
        Map<String, Object> res = new HashMap<>();
        res.put("success", updated != null);
        res.put("user", updated);
        return res;
    }

    @PostMapping("/users/{userId}/password")
    public Map<String, Object> changePassword(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        boolean ok = userService.changePassword(userId, oldPassword, newPassword);
        Map<String, Object> res = new HashMap<>();
        res.put("success", ok);
        if (!ok) {
            res.put("message", "旧密码错误或新密码无效");
        }
        return res;
    }

    @PostMapping("/users/{userId}/recharge")
    public Map<String, Object> recharge(@PathVariable Long userId, @RequestBody Map<String, Double> body) {
        Double amount = body.get("amount");
        UserDto updated = userService.recharge(userId, amount);
        Map<String, Object> res = new HashMap<>();
        res.put("success", updated != null);
        res.put("user", updated);
        return res;
    }

    // --- Bookings ---
    @PostMapping("/book")
    public Map<String, Object> bookGym(@RequestParam Long gymId, @RequestParam Long userId) {
        Map<String, Object> res = new HashMap<>();
        try {
            User user = userService.findById(userId);
            Gym gym = gymService.getGymById(gymId).orElse(null);
            if (user != null && gym != null) {
                Booking booking = bookingService.createBooking(user, gym);
                res.put("success", true);
                res.put("booking", booking);
                return res;
            }
            res.put("success", false);
            res.put("message", "用户或场馆不存在");
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
        }
        return res;
    }

    @GetMapping("/bookings/{userId}")
    public List<Booking> getBookingsByUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user != null) {
            return bookingService.getBookingsByUser(user);
        }
        return List.of();
    }

    @GetMapping("/admin/bookings/pending")
    public List<Booking> getPendingBookings() {
        return bookingService.getPendingBookings();
    }

    @PostMapping("/admin/bookings/{bookingId}/approve")
    public Booking approveBooking(@PathVariable Long bookingId) {
        return bookingService.updateBookingStatus(bookingId, "CONFIRMED");
    }

    @PostMapping("/admin/bookings/{bookingId}/reject")
    public Booking rejectBooking(@PathVariable Long bookingId) {
        return bookingService.updateBookingStatus(bookingId, "CANCELLED");
    }

    @GetMapping("/admin/stats")
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("gyms", gymRepository.count());
        stats.put("users", userRepository.count());
        stats.put("bookingsTotal", bookingService.countAllBookings());
        stats.put("bookingsPending", bookingService.countPendingBookings());
        stats.put("bookingsConfirmed", bookingService.countConfirmedBookings());
        stats.put("bookingsCancelled", bookingService.countCancelledBookings());
        return stats;
    }

    @GetMapping("/admin/bookings")
    public List<Booking> getAllBookings(@RequestParam(required = false) String status) {
        return bookingService.getAllBookings(status);
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public Booking cancelBooking(@PathVariable Long bookingId, @RequestParam Long userId) {
        return bookingService.cancelBookingByUser(bookingId, userId);
    }

    @PostMapping("/bookings/{bookingId}/sign-in")
    public Map<String, Object> signIn(@PathVariable Long bookingId, @RequestParam Long userId) {
        Map<String, Object> res = new HashMap<>();
        try {
            Booking booking = bookingService.signIn(bookingId, userId);
            res.put("success", true);
            res.put("booking", booking);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
        }
        return res;
    }

    // --- Favorites ---
    @GetMapping("/users/{userId}/favorites")
    public List<Gym> getFavorites(@PathVariable Long userId) {
        User user = userService.findById(userId);
        return favoriteService.getFavoritesByUser(user).stream().map(Favorite::getGym).toList();
    }

    @PostMapping("/gyms/{gymId}/favorite")
    public Map<String, Object> toggleFavorite(@PathVariable Long gymId, @RequestParam Long userId) {
        User user = userService.findById(userId);
        Gym gym = gymService.getGymById(gymId).orElse(null);
        boolean favorited = favoriteService.toggleFavorite(user, gym);
        Map<String, Object> res = new HashMap<>();
        res.put("favorited", favorited);
        return res;
    }

    @GetMapping("/gyms/{gymId}/favorite-status")
    public Map<String, Object> getFavoriteStatus(@PathVariable Long gymId, @RequestParam Long userId) {
        User user = userService.findById(userId);
        Gym gym = gymService.getGymById(gymId).orElse(null);
        Map<String, Object> res = new HashMap<>();
        res.put("favorited", favoriteService.isFavorited(user, gym));
        return res;
    }

    // --- Ratings ---
    @GetMapping("/gyms/{gymId}/ratings")
    public List<Rating> getRatings(@PathVariable Long gymId) {
        Gym gym = gymService.getGymById(gymId).orElse(null);
        return ratingService.getRatingsByGym(gym);
    }

    @PostMapping("/gyms/{gymId}/rate")
    public Rating rateGym(@PathVariable Long gymId, @RequestBody Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        Integer score = (Integer) payload.get("score");
        String comment = (String) payload.get("comment");
        User user = userService.findById(userId);
        Gym gym = gymService.getGymById(gymId).orElse(null);
        return ratingService.saveRating(user, gym, score, comment);
    }

    // --- User Stats ---
    @GetMapping("/users/{userId}/stats")
    public Map<String, Object> getUserStats(@PathVariable Long userId) {
        User user = userService.findById(userId);
        List<Booking> userBookings = bookingService.getBookingsByUser(user);
        
        long totalBookings = userBookings.size();
        long completedBookings = userBookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();
        double totalSpent = userBookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .mapToDouble(b -> b.getGym().getPrice())
                .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBookings", totalBookings);
        stats.put("completedBookings", completedBookings);
        stats.put("totalSpent", totalSpent);
        stats.put("balance", user.getBalance() != null ? user.getBalance() : 0.0);
        return stats;
    }
}
