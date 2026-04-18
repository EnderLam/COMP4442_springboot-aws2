package group16.comp4442_project.controller;

import group16.comp4442_project.config.AuthInterceptor;
import group16.comp4442_project.dao.PaymentDAO;
import group16.comp4442_project.model.User;
import group16.comp4442_project.service.OrderService;
import group16.comp4442_project.service.ProductService;
import group16.comp4442_project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final PaymentDAO paymentDao;

    public AdminController(
            UserService userService,
            ProductService productService,
            OrderService orderService,
            PaymentDAO paymentDao
    ) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
        this.paymentDao = paymentDao;
    }

    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getAttribute(AuthInterceptor.AUTH_USER);
        return user != null && "admin".equalsIgnoreCase(user.getRole());
    }

    @GetMapping("/users")
    public Object users(HttpServletRequest request) {
        if (!isAdmin(request)) return "Admin only";
        return userService.listUsersSafe();
    }

    @PutMapping("/users/{id}/role")
    public String updateUserRole(
            HttpServletRequest request,
            @PathVariable int id,
            @RequestBody Map<String, String> body
    ) {
        if (!isAdmin(request)) return "Admin only";
        return userService.updateRole(id, body.getOrDefault("role", ""));
    }

    @GetMapping("/products")
    public Object products(HttpServletRequest request) {
        if (!isAdmin(request)) return "Admin only";
        return productService.getProducts();
    }

    @GetMapping("/orders")
    public Object orders(HttpServletRequest request) {
        if (!isAdmin(request)) return "Admin only";
        return orderService.getOrders();
    }

    @GetMapping("/payments")
    public Object payments(HttpServletRequest request) {
        if (!isAdmin(request)) return "Admin only";
        return paymentDao.findAll();
    }
}
