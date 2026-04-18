package group16.comp4442_project.service;

import group16.comp4442_project.model.User;
import group16.comp4442_project.dao.UserDAO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserDAO userDao;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(
            UserDAO userDao,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void ensureDefaultAdmin() {
        if (userDao.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("admin");
            userDao.save(admin);
        }
    }

    public String register(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            return "Username and password are required";
        }
        if (!"buyer".equalsIgnoreCase(user.getRole()) && !"seller".equalsIgnoreCase(user.getRole())) {
            return "Role must be buyer or seller";
        }
        if (userDao.findByUsername(user.getUsername()).isPresent()) {
            return "Username already exists";
        }
        user.setRole(user.getRole().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
        return "User registered";
    }

    public Map<String, Object> login(User user){
        Optional<User> optionalUser = userDao.findByUsername(user.getUsername());
        if (optionalUser.isEmpty() || !passwordEncoder.matches(user.getPassword(), optionalUser.get().getPassword())) {
            return Map.of("success", false, "message", "Login failed");
        }
        User stored = optionalUser.get();
        return Map.of("success", true, "user", toSafeUser(stored));
    }

    public Optional<User> getUserById(int userId) {
        return userDao.findById(userId);
    }

    public List<Map<String, Object>> listUsersSafe() {
        return userDao.findAll().stream()
                .map(this::toSafeUser)
                .collect(Collectors.toList());
    }

    public String updateRole(int userId, String role) {
        if (!"buyer".equalsIgnoreCase(role) && !"seller".equalsIgnoreCase(role) && !"admin".equalsIgnoreCase(role)) {
            return "Invalid role";
        }
        userDao.updateRole(userId, role.toLowerCase());
        return "Role updated";
    }

    public Map<String, Object> toSafeUser(User user) {
        return Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
        );
    }
}