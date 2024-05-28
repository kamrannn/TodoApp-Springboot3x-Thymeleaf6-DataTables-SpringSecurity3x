package com.app.todo;


import com.app.todo.model.ToDo;
import com.app.todo.repository.ToDoRepository;
import com.app.todo.service.ToDoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToDoServiceTest {

    @Mock
    private ToDoRepository toDoRepository;

    @InjectMocks
    private ToDoService toDoService;

    @Test
    void getAllToDoItems_ReturnsEmptyList() {
        when(toDoRepository.findAll()).thenReturn(new ArrayList<>());

        List<ToDo> result = toDoService.getAllToDoItems();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllToDoItems_ReturnsNonEmptyList() {
        List<ToDo> mockToDoList = new ArrayList<>();
        mockToDoList.add(new ToDo(1L, "Task 1", new Date(), "Incomplete"));
        mockToDoList.add(new ToDo(2L, "Task 2", new Date(), "Incomplete"));

        when(toDoRepository.findAll()).thenReturn(mockToDoList);

        List<ToDo> result = toDoService.getAllToDoItems();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void getToDoItemById_ExistingId_ReturnsToDoItem() {
        Long id = 1L;
        ToDo mockToDo = new ToDo(id, "Task 1", new Date(), "Incomplete");

        when(toDoRepository.findById(id)).thenReturn(Optional.of(mockToDo));

        ToDo result = toDoService.getToDoItemById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getToDoItemById_NonExistingId_ReturnsNull() {
        Long id = 1L;

        when(toDoRepository.findById(id)).thenReturn(Optional.empty());

        ToDo result = toDoService.getToDoItemById(id);

        assertNull(result);
    }

    @Test
    void updateStatus_ExistingId_ReturnsTrue() {
        Long id = 1L;
        ToDo mockToDo = new ToDo(id, "Task 1", new Date(), "Incomplete");

        when(toDoRepository.findById(id)).thenReturn(Optional.of(mockToDo));
        when(toDoRepository.save(any())).thenReturn(mockToDo);

        boolean result = toDoService.updateStatus(id);

        assertTrue(result);
        assertEquals("Completed", mockToDo.getStatus());
    }

    @Test
    void updateStatus_NonExistingId_ReturnsFalse() {
        Long id = 1L;

        when(toDoRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = toDoService.updateStatus(id);

        assertFalse(result);
    }

    @Test
    void saveOrUpdateToDoItem_ValidToDo_ReturnsTrue() {
        ToDo mockToDo = new ToDo(1L, "Task 1", new Date(), "Incomplete");

        when(toDoRepository.save(mockToDo)).thenReturn(mockToDo);
        when(toDoRepository.findById(mockToDo.getId())).thenReturn(Optional.of(mockToDo));
        boolean result = toDoService.saveOrUpdateToDoItem(mockToDo);

        assertTrue(result);
    }
    

    @Test
    void deleteToDoItem_ExistingId_ReturnsTrue() {
        Long id = 1L;

        doNothing().when(toDoRepository).deleteById(id);
        when(toDoRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = toDoService.deleteToDoItem(id);

        assertTrue(result);
    }

    @Test
    void deleteToDoItem_NonExistingId_ReturnsFalse() {
        Long id = 1L;

        when(toDoRepository.findById(id)).thenReturn(Optional.of(new ToDo()));

        boolean result = toDoService.deleteToDoItem(id);

        assertFalse(result);
    }
}
