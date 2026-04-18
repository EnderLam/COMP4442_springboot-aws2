package group16.comp4442_project.config;

import group16.comp4442_project.model.User;
import group16.comp4442_project.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    public static final String AUTH_USER = "authUser";
    private final UserService userService;

    public AuthInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)
                || uri.equals("/")
                || uri.startsWith("/users/login")
                || uri.startsWith("/users/register")
                || uri.startsWith("/uploads/")
                || ("GET".equalsIgnoreCase(method) && uri.equals("/products"))
                || uri.contains(".")) {
            return true;
        }

        String userId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("user_id".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }
        if (userId == null || userId.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Login required");
            return false;
        }
        Optional<User> user;
        try {
            user = userService.getUserById(Integer.parseInt(userId));
        } catch (NumberFormatException e) {
            user = Optional.empty();
        }
        if (user.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid session");
            return false;
        }
        request.setAttribute(AUTH_USER, user.get());
        return true;
    }
}
