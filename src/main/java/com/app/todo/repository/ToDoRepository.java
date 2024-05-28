package com.app.todo.repository;

import com.app.todo.model.ToDo;
import com.app.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    List<ToDo> findAllByUserOrderByCreatedAtDesc(User user);

    List<ToDo> findAllByOrderByCreatedAtDesc();

    List<ToDo> findByTitleContainingIgnoreCase(String title);

}
