package com.gym.management;

import com.gym.management.entity.Gym;
import com.gym.management.entity.Rating;
import com.gym.management.entity.User;
import com.gym.management.repository.GymRepository;
import com.gym.management.repository.RatingRepository;
import com.gym.management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final RatingRepository ratingRepository;

    public DataInitializer(UserRepository userRepository, GymRepository gymRepository, RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.gymRepository = gymRepository;
        this.ratingRepository = ratingRepository;
    }

    @Override
    public void run(String... args) {
        ensureUser("admin", "admin123", "ROLE_ADMIN");
        ensureUser("Alice", "123", "ROLE_USER");
        ensureUser("Bob", "123", "ROLE_USER");
        ensureUser("Charlie", "123", "ROLE_USER");

        if (gymRepository.count() == 0) {
            gymRepository.save(createGym("铁人健身中心", "中心城区", 50.0, "力量巅峰健身中心拥有最先进的举重和力量训练设备。适合所有级别的健身爱好者。", "/images/gyms/power_gym.jpg", "健身", "010-12345678"));
            gymRepository.save(createGym("静心瑜伽馆", "城西新区", 40.0, "在这个宁静的空间里，通过专业的瑜伽课程连接您的身心。", "/images/gyms/yoga_studio.jpg", "瑜伽", "010-87654321"));
            gymRepository.save(createGym("综合体能馆", "高新园区", 60.0, "极速综合体能馆提供高强度的功能性训练。", "/images/gyms/crossfit.jpg", "综合", "010-11223344"));
            gymRepository.save(createGym("蓝海游泳中心", "浦海新区", 35.0, "蓝海游泳中心拥有奥运标准尺寸的恒温泳池。", "/images/gyms/swim_center.jpg", "游泳", "010-55667788"));
            gymRepository.save(createGym("铁拳搏击俱乐部", "工业区", 45.0, "铁拳搏击俱乐部由退役职业选手执教。", "/images/gyms/boxing.jpg", "搏击", "010-99001122"));
            gymRepository.save(createGym("动感单车骑行馆", "中央商务区", 55.0, "动感单车课程将带给您无与伦比的燃脂体验。", "/images/gyms/cycling.jpg", "骑行", "010-33445566"));
            gymRepository.save(createGym("精英网球场", "金水区", 80.0, "精英网球场提供专业级的室内外球场。", "/images/gyms/tennis.jpg", "球类", "010-77889900"));
            gymRepository.save(createGym("全能室内攀岩馆", "中原区", 70.0, "挑战重力，体验攀岩的乐趣。", "/images/gyms/climbing.jpg", "攀岩", "010-22334455"));
            gymRepository.save(createGym("都市普拉提", "郑东新区", 90.0, "普拉提能有效改善体态、增强核心力量并提升柔韧性。", "/images/gyms/pilates.jpg", "瑜伽", "010-66778899"));
            gymRepository.save(createGym("少年篮球学院", "经开区", 30.0, "专注于青少年篮球技能开发和团队协作能力培养。", "/images/gyms/basketball.jpg", "球类", "010-44556677"));
            gymRepository.save(createGym("精英击剑馆", "艺术区", 120.0, "体验击剑这项贵族运动的优雅与智慧。", "/images/gyms/fencing.jpg", "竞技", "010-88990011"));
            gymRepository.save(createGym("足球竞技中心", "航海体育场", 65.0, "足球竞技中心拥有专业草坪、标准灯光系统。", "/images/gyms/football.jpg", "球类", "010-12344321"));
        }

        if (ratingRepository.count() == 0) {
            Optional<User> user1Opt = userRepository.findByUsername("Alice");
            Optional<User> user2Opt = userRepository.findByUsername("Bob");
            Optional<User> user3Opt = userRepository.findByUsername("Charlie");

            List<Gym> gyms = gymRepository.findAll();
            if (user1Opt.isPresent() && user2Opt.isPresent() && user3Opt.isPresent() && gyms.size() >= 10) {
                User user1 = user1Opt.get();
                User user2 = user2Opt.get();
                User user3 = user3Opt.get();

                // 评分数据，让推荐系统更有趣
                createRating(user1, gyms.get(0), 5); // 喜欢力量
                createRating(user1, gyms.get(2), 4); // 喜欢CrossFit
                createRating(user1, gyms.get(4), 5); // 喜欢搏击
                createRating(user1, gyms.get(6), 2);

                createRating(user2, gyms.get(0), 4); // 也喜欢力量
                createRating(user2, gyms.get(1), 5); // 喜欢瑜伽
                createRating(user2, gyms.get(8), 5); // 喜欢普拉提
                createRating(user2, gyms.get(3), 3);

                createRating(user3, gyms.get(3), 5); // 喜欢游泳
                createRating(user3, gyms.get(9), 5); // 喜欢篮球
                createRating(user3, gyms.get(7), 4); // 喜欢攀岩
                createRating(user3, gyms.get(1), 1);
            }
        }
    }

    private Gym createGym(String name, String location, Double price, String description, String imageUrl, String category, String phone) {
        Gym gym = new Gym();
        gym.setName(name);
        gym.setLocation(location);
        gym.setPrice(price);
        gym.setDescription(description);
        gym.setImageUrl(imageUrl);
        gym.setCategory(category);
        gym.setPhone(phone);
        return gym;
    }

    private void ensureUser(String username, String password, String role) {
        Optional<User> existingOpt = userRepository.findByUsername(username);
        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();
            boolean changed = false;
            if (existing.getRole() == null || !existing.getRole().equals(role)) {
                existing.setRole(role);
                changed = true;
            }
            if (existing.getPassword() == null || existing.getPassword().isBlank()) {
                existing.setPassword(password);
                changed = true;
            }
            if (changed) {
                userRepository.save(existing);
            }
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        userRepository.save(user);
    }

    private void createRating(User user, Gym gym, int score) {
        Rating rating = new Rating();
        rating.setUser(user);
        rating.setGym(gym);
        rating.setScore(score);
        ratingRepository.save(rating);
    }
}
