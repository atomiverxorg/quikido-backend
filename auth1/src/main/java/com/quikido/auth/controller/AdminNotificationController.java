package com.quikido.auth.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.quikido.auth.dto.NotificationRequest;
import com.quikido.auth.entity.User;
import com.quikido.auth.repository.UserRepository;
import com.quikido.auth.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/notifications")
public class AdminNotificationController {

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> sendNotification(@RequestBody NotificationRequest request) {
        // Assume admin has ability to send to specific users or groups
        String deviceToken = getDeviceTokenForUser(request.getUserId());
        try {
            firebaseService.sendPushNotification(deviceToken, request.getTitle(), request.getMessage());
            return ResponseEntity.ok("Notification sent successfully.");
        } catch (FirebaseMessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send notification");
        }
    }

    private String getDeviceTokenForUser(Long userId) {
        // Fetch the user and get their FCM device token
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getDeviceToken();
    }
}