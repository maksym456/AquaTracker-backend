package com.aquatracker.contacts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUser_Id(String userId);
    Optional<Contact> findByUser_IdAndFriend_Id(String userId, String friendId);
    boolean existsByUser_IdAndFriend_Id(String userId, String friendId);
}

