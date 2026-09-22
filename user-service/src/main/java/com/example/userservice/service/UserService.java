package com.example.userservice.service;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.exception.UserAlreadyExistsException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.userservice.mapper.UserMapper.toEntity;
import static com.example.userservice.mapper.UserMapper.toResponse;

/**
 * Service с бизнес-логикой приложения и обработкой исключений.
 *
 * @author Yushinova (TATYANA YUSHINOVA)
 */
@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким mail " + request.getEmail() + " уже существует");
        }
        try {
            UserEntity entity = toEntity(request);
            UserEntity saved = repository.save(entity);
            return toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("Пользователь с таким mail " + request.getEmail() + " уже существует");
        }
    }

    @Transactional
    public UserResponse updateUser(Integer id, UserRequest request) {
        UserEntity user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id " + id + " не найден"));
        Optional<UserEntity> byEmail = repository.findByEmail(request.getEmail());
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
        return repository.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id " + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return repository.findAll().stream().map(UserMapper::toResponse).toList();
    }

    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("Пользователь с id=" + id + " не найден");
        }
        repository.deleteById(id);
    }
}