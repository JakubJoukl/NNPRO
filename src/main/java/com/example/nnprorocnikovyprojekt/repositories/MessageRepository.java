package com.example.nnprorocnikovyprojekt.repositories;

import com.example.nnprorocnikovyprojekt.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {

}
