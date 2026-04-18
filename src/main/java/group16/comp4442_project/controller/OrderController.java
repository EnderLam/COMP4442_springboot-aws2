package group16.comp4442_project.controller;

import group16.comp4442_project.config.AuthInterceptor;
import group16.comp4442_project.model.Order;
import group16.comp4442_project.model.Product;
import group16.comp4442_project.model.User;
import group16.comp4442_project.service.OrderService;
import group16.comp4442_project.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;

    public OrderController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @PostMapping
    public String addOrder(
            HttpServletRequest request,
            @RequestBody Order order
    ) {
        User user = (User) request.getAttribute(AuthInterceptor.AUTH_USER);
        String role = user.getRole();
        if (!"buyer".equalsIgnoreCase(role) && !"admin".equalsIgnoreCase(role)) {
            return "Only buyer/admin can create order";
        }
        Product product = productService.getProduct(order.getProductId());
        if (product == null) {
            return "Product not found";
        }
        if (order.getQuantity() <= 0 || order.getQuantity() > product.getStock()) {
            return "Invalid quantity";
        }
        return orderService.addOrder(order, user);
    }

    @GetMapping
    public List<Order> getOrders(HttpServletRequest request) {
        User user = (User) request.getAttribute(AuthInterceptor.AUTH_USER);
        String role = user.getRole();
        if ("buyer".equalsIgnoreCase(role)) {
            return orderService.getOrdersByBuyer(user);
        }
        if ("seller".equalsIgnoreCase(role)) {
            return orderService.getOrdersBySeller(user);
        }
        return orderService.getOrders();
    }

    @DeleteMapping("/{id}")
    public String removeOrder(
            @PathVariable int id,
            HttpServletRequest request
    ) {
        User user = (User) request.getAttribute(AuthInterceptor.AUTH_USER);
        String role = user.getRole();
        if ("buyer".equalsIgnoreCase(role) && !orderService.isBuyerOrder(user, id)) {
            return "Buyer can only remove own order";
        }
        if ("seller".equalsIgnoreCase(role)) {
            Order order = orderService.getOrder(id);
            if (order == null) {
                return "Order not found";
            }
            Product product = productService.getProduct(order.getProductId());
            if (product == null || !user.getUsername().equals(product.getSeller())) {
                return "Seller can only remove own product order";
            }
        }
        return orderService.deleteOrder(id);
    }
}