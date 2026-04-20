package group16.comp4442_project.service;

import group16.comp4442_project.dao.OrderDAO;
import group16.comp4442_project.dao.TrackingDAO;
import group16.comp4442_project.model.Order;
import group16.comp4442_project.model.Tracking;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class TrackingService {
    private final TrackingDAO trackingDao;
    private final OrderDAO orderDao;
    private final Random random = new Random();

    public TrackingService(TrackingDAO trackingDao, OrderDAO orderDao) {
        this.trackingDao = trackingDao;
        this.orderDao = orderDao;
    }

    // Generate fake but deterministic coordinates based on order ID
    // The coordinates are around Hong Kong (approx 22.3°N, 114.2°E)
    public Tracking getOrCreateTracking(int orderId) {
        Optional<Tracking> existing = trackingDao.findByOrderId(orderId);
        if (existing.isPresent()) {
            // Simulate real-time movement: update status and location slightly every time
            Tracking t = existing.get();
            simulateMovement(t);
            trackingDao.updateTracking(t.getOrderId(), t.getLocationDescription(), t.getLatitude(), t.getLongitude(), t.getStatus());
            return t;
        } else {
            // Create a new tracking record with fake initial data
            Optional<Order> orderOpt = orderDao.findById(orderId);
            if (orderOpt.isEmpty()) {
                throw new RuntimeException("Order not found");
            }
            Tracking newTrack = new Tracking();
            newTrack.setOrderId(orderId);
            // Deterministic but pseudo-random coordinates based on orderId
            double baseLat = 22.3 + (orderId % 100) * 0.01;
            double baseLng = 114.2 + (orderId % 100) * 0.01;
            newTrack.setLatitude(baseLat);
            newTrack.setLongitude(baseLng);
            newTrack.setLocationDescription("Warehouse - Ready to ship");
            newTrack.setStatus("PENDING");
            trackingDao.create(newTrack);
            return newTrack;
        }
    }

    private void simulateMovement(Tracking t) {
        // Slight random offset to mimic real-time movement
        double deltaLat = (random.nextDouble() - 0.5) * 0.02;
        double deltaLng = (random.nextDouble() - 0.5) * 0.02;
        double newLat = t.getLatitude() + deltaLat;
        double newLng = t.getLongitude() + deltaLng;
        t.setLatitude(newLat);
        t.setLongitude(newLng);

        String[] locations = {"Distribution Center", "In transit - North", "Local depot", "Out for delivery", "Near destination"};
        String[] statuses = {"PENDING", "SHIPPED", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED"};
        int step = (int)(Math.abs(t.getLatitude() * 100) % statuses.length);
        t.setLocationDescription(locations[step % locations.length]);
        t.setStatus(statuses[step % statuses.length]);
    }
}