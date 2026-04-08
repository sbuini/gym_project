package com.gym.management.service;

import com.gym.management.entity.Booking;
import com.gym.management.entity.Gym;
import com.gym.management.entity.User;
import com.gym.management.repository.BookingRepository;
import com.gym.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    public Booking createBooking(User user, Gym gym) {
        // 检查是否有未签到的已确认预约
        List<Booking> unsignedBookings = bookingRepository.findByUserAndStatusAndAttended(user, "CONFIRMED", false);
        if (!unsignedBookings.isEmpty()) {
            throw new RuntimeException("您还有已确认但未签到的预约，请先完成签到后再预约新场馆");
        }

        if (user.getBalance() == null || user.getBalance() < gym.getPrice()) {
            throw new RuntimeException("余额不足");
        }
        
        // 扣款
        user.setBalance(user.getBalance() - gym.getPrice());
        userRepository.save(user);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setGym(gym);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus("PENDING");
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUser(user);
    }

    public List<Booking> getPendingBookings() {
        return bookingRepository.findByStatusOrderByBookingTimeDesc("PENDING");
    }

    public Booking updateBookingStatus(Long bookingId, String status) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return null;
        }
        Booking booking = bookingOpt.get();
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public long countPendingBookings() {
        return bookingRepository.countByStatus("PENDING");
    }

    public long countAllBookings() {
        return bookingRepository.count();
    }

    public long countConfirmedBookings() {
        return bookingRepository.countByStatus("CONFIRMED");
    }

    public long countCancelledBookings() {
        return bookingRepository.countByStatus("CANCELLED");
    }

    public List<Booking> getAllBookings(String status) {
        if (status == null || status.isBlank()) {
            return bookingRepository.findAllByOrderByBookingTimeDesc();
        }
        return bookingRepository.findByStatusOrderByBookingTimeDesc(status);
    }

    public Booking cancelBookingByUser(Long bookingId, Long userId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return null;
        }
        Booking booking = bookingOpt.get();
        if (booking.getUser() == null || booking.getUser().getId() == null) {
            return null;
        }
        if (!booking.getUser().getId().equals(userId)) {
            return null;
        }
        if (!"PENDING".equals(booking.getStatus())) {
            return null;
        }
        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    public Booking signIn(Long bookingId, Long userId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("预约单不存在");
        }
        Booking booking = bookingOpt.get();
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此预约单");
        }
        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("只有已确认的预约才能签到");
        }
        if (booking.getAttended() != null && booking.getAttended()) {
            throw new RuntimeException("该预约已签到");
        }
        booking.setAttended(true);
        booking.setSignInTime(LocalDateTime.now());
        return bookingRepository.save(booking);
    }
}
