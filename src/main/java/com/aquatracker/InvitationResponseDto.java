package com.aquatracker;

import com.aquatracker.common.IdMapper;

import java.time.LocalDateTime;

public class InvitationResponseDto {
    private String id;
    private String senderId;
    private String senderName;
    private String senderEmail;
    private String recipientEmail;
    private String recipientId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    public InvitationResponseDto() {}

    public InvitationResponseDto(Invitation invitation) {
        this.id = IdMapper.toInvitationId(invitation.getId());
        this.senderId = invitation.getSender() != null ? 
                       IdMapper.toUserId(invitation.getSender().getId()) : null;
        this.senderName = invitation.getSender() != null ? 
                         invitation.getSender().getUsername() : null;
        this.senderEmail = invitation.getSender() != null ? 
                          invitation.getSender().getEmail() : null;
        this.recipientEmail = invitation.getRecipientEmail();
        this.recipientId = invitation.getRecipient() != null ? 
                          IdMapper.toUserId(invitation.getRecipient().getId()) : null;
        this.status = invitation.getStatus() != null ? invitation.getStatus() : "pending";
        this.createdAt = invitation.getCreatedAt();
        this.respondedAt = invitation.getRespondedAt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}

