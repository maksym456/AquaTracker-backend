package com.aquatracker.contacts;

import com.aquatracker.common.IdMapper;

public class ContactResponseDto {
    private String id;
    private String name;
    private String email;
    private String status;

    public ContactResponseDto() {}

    public ContactResponseDto(Contact contact) {
        this.id = IdMapper.toContactId(contact.getId());
        this.name = contact.getFriendName() != null ? contact.getFriendName() : 
                   (contact.getFriend() != null ? contact.getFriend().getUsername() : "Unknown");
        this.email = contact.getFriendEmail() != null ? contact.getFriendEmail() : 
                    (contact.getFriend() != null ? contact.getFriend().getEmail() : "");
        this.status = contact.getStatus() != null ? contact.getStatus() : "pending";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

