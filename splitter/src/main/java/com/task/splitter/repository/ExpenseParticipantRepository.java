package com.task.splitter.repository;

import com.task.splitter.model.ExpenseParticipant;
import com.task.splitter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Long> {

    List<ExpenseParticipant> findByUser(User user);
}