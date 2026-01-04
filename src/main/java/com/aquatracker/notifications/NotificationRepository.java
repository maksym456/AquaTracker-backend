package com.aquatracker.notifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUser_IdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead);
    List<Notification> findByUser_IdAndAquarium_IdOrderByCreatedAtDesc(Long userId, Long aquariumId);
    Long countByUser_IdAndIsRead(Long userId, Boolean isRead);
}

