package group16.comp4442_project.controller;

import group16.comp4442_project.model.Tracking;
import group16.comp4442_project.service.TrackingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracking")
public class TrackingController {
    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/{orderId}")
    public Tracking getTracking(@PathVariable int orderId) {
        return trackingService.getOrCreateTracking(orderId);
    }
}