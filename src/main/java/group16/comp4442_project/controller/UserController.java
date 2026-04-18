package group16.comp4442_project.controller;

import group16.comp4442_project.config.AuthInterceptor;
import group16.comp4442_project.model.User;
import group16.comp4442_project.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user){
        return service.register(user);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user, HttpServletResponse response){
        Map<String, Object> result = service.login(user);
        if (Boolean.TRUE.equals(result.get("success"))) {
            @SuppressWarnings("unchecked")
            Map<String, Object> safeUser = (Map<String, Object>) result.get("user");
            Cookie cookie = new Cookie("user_id", String.valueOf(safeUser.get("id")));
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 3600);
            response.addCookie(cookie);
        }
        return result;
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response){
        Cookie cookie = new Cookie("user_id", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "Logged out";
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest request) {
        User user = (User) request.getAttribute(AuthInterceptor.AUTH_USER);
        return service.toSafeUser(user);
    }
}