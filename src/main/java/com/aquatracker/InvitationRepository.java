package com.aquatracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findBySenderId(Long senderId);
    List<Invitation> findByRecipientId(Long recipientId);
    Optional<Invitation> findBySenderIdAndRecipientEmail(Long senderId, String recipientEmail);
    List<Invitation> findByRecipientIdAndStatus(Long recipientId, String status);
}

