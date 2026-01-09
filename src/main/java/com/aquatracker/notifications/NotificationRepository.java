package com.aquatracker.notifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(String userId);
    List<Notification> findByUser_IdAndIsReadOrderByCreatedAtDesc(String userId, Boolean isRead);
    List<Notification> findByUser_IdAndAquarium_IdOrderByCreatedAtDesc(String userId, Long aquariumId);
    Long countByUser_IdAndIsRead(String userId, Boolean isRead);
}

