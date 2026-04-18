package group16.comp4442_project.controller;

import group16.comp4442_project.config.AuthInterceptor;
import group16.comp4442_project.model.Payment;
import group16.comp4442_project.model.User;
import group16.comp4442_project.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @PostMapping
    public String pay(
            HttpServletRequest request,
            @RequestBody Payment payment
    ){
        User user = (User) request.getAttribute(AuthInterceptor.AUTH_USER);
        String role = user.getRole();
        if (!"buyer".equalsIgnoreCase(role) && !"admin".equalsIgnoreCase(role)) {
            return "Only buyer/admin can make payment";
        }
        return paymentService.processPayment(payment, user);
    }
}