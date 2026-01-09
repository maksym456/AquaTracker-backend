package com.aquatracker.contacts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUser_Id(String userId);
    List<Contact> findByUser_IdAndStatus(String userId, String status);
    Optional<Contact> findByUser_IdAndFriend_Id(String userId, String friendId);
    Optional<Contact> findByUser_IdAndFriend_IdAndStatus(String userId, String friendId, String status);
    boolean existsByUser_IdAndFriend_Id(String userId, String friendId);
    void deleteByUser_IdAndFriend_Id(String userId, String friendId);
}

