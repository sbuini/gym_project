package com.gym.management.service;

import com.gym.management.dto.UserDto;
import com.gym.management.entity.User;
import com.gym.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        if (!isValidEmail(user.getEmail())) {
            throw new RuntimeException("邮箱格式不正确");
        }
        user.setRole("ROLE_USER");
        return userRepository.save(user);
    }

    public boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getBalance());
    }

    public UserDto recharge(Long userId, Double amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || amount == null || amount <= 0) {
            return null;
        }
        Double currentBalance = user.getBalance();
        if (currentBalance == null) currentBalance = 0.0;
        user.setBalance(currentBalance + amount);
        return toDto(userRepository.save(user));
    }

    public UserDto updateEmail(Long userId, String email) {
        if (!isValidEmail(email)) {
            return null;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        user.setEmail(email);
        return toDto(userRepository.save(user));
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            return false;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        if (user.getPassword() == null || !user.getPassword().equals(oldPassword)) {
            return false;
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }
}
