package group16.comp4442_project.service;

import group16.comp4442_project.dao.OrderDAO;
import group16.comp4442_project.dao.PaymentDAO;
import group16.comp4442_project.dao.ProductDAO;
import group16.comp4442_project.model.Order;
import group16.comp4442_project.model.Payment;
import group16.comp4442_project.model.Product;
import group16.comp4442_project.model.User;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentDAO paymentDao;
    private final OrderDAO orderDao;
    private final ProductDAO productDao;

    public PaymentService(
            PaymentDAO paymentDao,
            OrderDAO orderDao,
            ProductDAO productDao
    ) {
        this.paymentDao = paymentDao;
        this.orderDao = orderDao;
        this.productDao = productDao;
    }

    public String processPayment(Payment payment, User currentUser){
        if (payment.getCardNumber() == null || payment.getCardNumber().length() < 12) {
            return "Payment failed: invalid card number";
        }
        Order order = orderDao.findById(payment.getOrderId()).orElse(null);
        if (order == null) {
            return "Payment failed: order not found";
        }
        if (!"admin".equalsIgnoreCase(currentUser.getRole()) && order.getUserId() != currentUser.getId()) {
            return "Payment failed: unauthorized";
        }
        Product product = productDao.findById(order.getProductId()).orElse(null);
        if (product == null) {
            return "Payment failed: product not found";
        }
        payment.setUserId(currentUser.getId());
        payment.setAmount(product.getPrice() * order.getQuantity());
        payment.setStatus("SUCCESS");
        String cardNo = payment.getCardNumber();
        String cardLast4 = cardNo.substring(cardNo.length() - 4);
        paymentDao.save(payment, cardLast4);
        return "Payment successful for Order " + payment.getOrderId();
    }
}