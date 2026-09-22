package com.example.userservice.service;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.exception.UserAlreadyExistsException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    /**
     * @author FlameFlow21 (Shundev Kirill)
     */
    @Test
    void createUser_successfully() {
        UserRequest userRequest = new UserRequest("Dave", "dave@example.com", 40);
        UserEntity savedUser = new UserEntity("Dave", "dave@example.com", 40);
        savedUser.setId(1);

        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        UserResponse userResponse = userService.createUser(userRequest);
        assertNotNull(userResponse);
        assertEquals(userRequest.getName(), userResponse.getName());
        assertEquals(userRequest.getEmail(), userResponse.getEmail());
        assertEquals(userRequest.getAge(), userResponse.getAge());
        verify(userRepository).existsByEmail(userRequest.getEmail());
        verify(userRepository).save(any(UserEntity.class));
    }

    /**
     * @author FlameFlow21 (Shundev Kirill)
     */
    @Test
    void createUser_ifEmailIsAlreadyOccupied() {
        UserRequest userRequest = new UserRequest("Dave", "bob@example.com", 40);

        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(userRequest)
        );
        assertEquals("Пользователь с таким mail bob@example.com уже существует", exception.getMessage());
        verify(userRepository).existsByEmail(userRequest.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    /**
     * @author FlameFlow21 (Shundev Kirill)
     */
    @Test
    void updateUser_ifUserExists() {
        Integer id = 1;
        UserRequest userRequest = new UserRequest("Alice Updated", "alice.new@example.com", 25);
        UserEntity existingUser = new UserEntity("Alice", "alice@example.com", 24);
        existingUser.setId(id);
        UserEntity updatedUser = new UserEntity("Alice Updated", "alice.new@example.com", 25);
        updatedUser.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());

        UserResponse userResponse = userService.updateUser(id, userRequest);
        assertNotNull(userResponse);
        assertEquals(id, userResponse.getId());
        assertEquals(userRequest.getName(), userResponse.getName());
        assertEquals(userRequest.getEmail(), userResponse.getEmail());
        assertEquals(userRequest.getAge(), userResponse.getAge());
        verify(userRepository).findById(id);
        verify(userRepository).findByEmail(userRequest.getEmail());
    }

    /**
     * @author FlameFlow21 (Shundev Kirill)
     */
    @Test
    void updateUser_ifUserNotExists() {
        Integer id = 99;
        UserRequest userRequest = new UserRequest("Ghost", "ghost@example.com", 30);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(id, userRequest)
        );
        assertEquals("Пользователь с таким id 99 не найден", exception.getMessage());
        verify(userRepository).findById(id);
        verify(userRepository, never()).findByEmail(userRequest.getEmail());
    }

    /**
     * @author FlameFlow21 (Shundev Kirill)
     */
    @Test
    void updateUser_ifEmailIsAlreadyOccupied() {
        Integer id = 1;
        UserRequest userRequest = new UserRequest("Alice", "bob@example.com", 25);
        UserEntity foundByIdUser = new UserEntity("Alice", "alice@example.com", 24);
        foundByIdUser.setId(id);
        UserEntity foundByEmailUser = new UserEntity("Bob", "bob@example.com", 37);
        foundByEmailUser.setId(2);

        when(userRepository.findById(id)).thenReturn(Optional.of(foundByIdUser));
        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.of(foundByEmailUser));

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.updateUser(id, userRequest)
        );
        assertEquals(
                "Пользователь с таким mail bob@example.com уже существует",
                exception.getMessage()
        );
        verify(userRepository).findById(id);
        verify(userRepository).findByEmail(userRequest.getEmail());
    }

    /**
     * @author Simon35845
     */
    @Test
    void getUserById_ifUserExists() {
        Integer id = 1;
        UserEntity user = new UserEntity("Alice", "alice@example.com", 24);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserResponse userResponse = userService.getUserById(id);
        assertEquals(user.getName(), userResponse.getName());
        assertEquals(user.getEmail(), userResponse.getEmail());
        assertEquals(user.getAge(), userResponse.getAge());
        verify(userRepository).findById(id);
    }

    /**
     * @author Simon35845
     */
    @Test
    void getUserById_ifUserNotExists() {
        Integer id = 99;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(id)
        );
        assertEquals("Пользователь с таким id 99 не найден", exception.getMessage());
        verify(userRepository).findById(id);
    }

    /**
     * @author Simon35845
     */
    @Test
    void getAll_ifUsersExists() {
        UserEntity user1 = new UserEntity("Alice", "alice@example.com", 24);
        UserEntity user2 = new UserEntity("Bob", "bob@example.com", 31);
        List<UserEntity> userEntities = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(userEntities);

        List<UserResponse> userResponses = userService.getAll();
        assertEquals(2, userResponses.size());
        verify(userRepository).findAll();
    }

    /**
     * @author Simon35845
     */
    @Test
    void getAll_ifUsersNotExists() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponse> userResponses = userService.getAll();
        assertEquals(0, userResponses.size());
        verify(userRepository).findAll();
    }

    /**
     * @author Yushinova
     */
    @Test
    void deleteUser_ifUsersExists() {
        Integer id = 1;

        when(userRepository.existsById(id)).thenReturn(true);

        userService.delete(id);
        verify(userRepository).existsById(id);
        verify(userRepository).deleteById(id);
    }

    /**
     * @author Yushinova
     */
    @Test
    void deleteUser_ifUserNotFoundTest() {
        Integer id = 99;

        when(userRepository.existsById(id)).thenReturn(false);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> userService.delete(id)
        );
        assertEquals("Пользователь с id=99 не найден", exception.getMessage());
        verify(userRepository).existsById(id);
        verify(userRepository, never()).deleteById(id);
    }
}