package com.example.userservice.service;

import com.example.common.dto.UserEvent;
import com.example.common.dto.UserEventType;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.exception.UserAlreadyExistsException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.eventproducer.UserEventProducer;
import com.example.userservice.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.userservice.mapper.UserMapper.toEntity;
import static com.example.userservice.mapper.UserMapper.toResponse;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    public UserService(UserRepository userRepository, UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.userEventProducer = userEventProducer;
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким mail " + request.getEmail() + " уже существует");
        }
        try {
            UserEntity entity = toEntity(request);
            UserEntity saved = userRepository.save(entity);
            UserEvent event = new UserEvent(UUID.randomUUID(), saved.getEmail(), UserEventType.CREATE_USER);
            userEventProducer.sendEvent(event);
            return toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("Пользователь с таким mail " + request.getEmail() + " уже существует");
        }
    }

    @Transactional
    public UserResponse updateUser(Integer id, UserRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id " + id + " не найден"));
        Optional<UserEntity> byEmail = userRepository.findByEmail(request.getEmail());
        if (byEmail.isPresent() && !byEmail.get().getId().equals(id)) {
            throw new UserAlreadyExistsException("Пользователь с таким mail " + request.getEmail() + " уже существует");
        }
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setAge(request.getAge());
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id " + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toResponse).toList();
    }

    @Transactional
    public void delete(Integer id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id " + id + " не найден"));
        userRepository.deleteById(id);
        UserEvent event = new UserEvent(UUID.randomUUID(), user.getEmail(), UserEventType.DELETE_USER);
        userEventProducer.sendEvent(event);
    }
}