package group16.comp4442_project.service;

import group16.comp4442_project.dao.OrderDAO;
import group16.comp4442_project.model.Order;
import group16.comp4442_project.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderDAO orderDao;

    public OrderService(OrderDAO orderDao) {
        this.orderDao = orderDao;
    }

    public String addOrder(Order order, User buyer) {
        if (order.getQuantity() <= 0) {
            return "Invalid quantity";
        }
        order.setUserId(buyer.getId());
        order.setStatus("CREATED");
        boolean ok = orderDao.createOrderAndDecreaseStock(order);
        if (!ok) {
            return "Not enough stock";
        }
        return "Order added";
    }

    public List<Order> getOrders() {
        return orderDao.findAll();
    }

    public List<Order> getOrdersByBuyer(User buyer) {
        return orderDao.findByUserId(buyer.getId());
    }

    public List<Order> getOrdersBySeller(User seller) {
        return orderDao.findBySeller(seller.getUsername());
    }

    public String deleteOrder(int orderId) {
        if (orderDao.findById(orderId).isEmpty()) {
            return "Order not found";
        }
        orderDao.delete(orderId);
        return "Order removed";
    }

    public Order getOrder(int orderId) {
        return orderDao.findById(orderId).orElse(null);
    }

    public boolean isBuyerOrder(User buyer, int orderId) {
        Order order = getOrder(orderId);
        return order != null && order.getUserId() == buyer.getId();
    }
}