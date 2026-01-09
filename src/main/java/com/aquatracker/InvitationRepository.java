package com.aquatracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findBySender_Id(String senderId);
    List<Invitation> findByRecipient_Id(String recipientId);
    Optional<Invitation> findBySender_IdAndRecipientEmail(String senderId, String recipientEmail);
    List<Invitation> findByRecipient_IdAndStatus(String recipientId, String status);
    Optional<Invitation> findById(Long id);
    List<Invitation> findByRecipientEmailAndStatus(String recipientEmail, String status);
}

