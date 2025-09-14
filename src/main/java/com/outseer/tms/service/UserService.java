package com.outseer.tms.service;

import com.outseer.tms.dto.Response;
import com.outseer.tms.dto.UserRequestDto;
import com.outseer.tms.dto.UserResponseDto;
import com.outseer.tms.entity.UserEntity;
import com.outseer.tms.exception.UserIdAlreadyExistsException;
import com.outseer.tms.exception.UserNotFoundException;
import com.outseer.tms.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Retryable(
            value = {DataAccessException.class}, // db failures
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public Response saveUser(UserRequestDto userRequestDto) {
        Response response;
        String userId = userRequestDto.getUserId();
        try {
            if (userRepository.existsById(userId)) {
                throw new UserIdAlreadyExistsException("User ID already exists for this id:" + userId);
            }
            UserEntity userEntity = modelMapper.map(userRequestDto, UserEntity.class);
            LocalDateTime now = LocalDateTime.now();
            userEntity.setCreatedAt(now);
            userRepository.save(userEntity);
            response = new Response(true, "User created successfully.");
            return response;
        } catch (UserIdAlreadyExistsException e) {
            log.info("User already present for this userId:{}", userId);
            throw e;
        } catch (Exception e) {
            log.info("User creation Exception for this userId:{},Exception:{}", userId, e.getMessage());
           throw e;
        }

    }

    @Retryable(
            value = {DataAccessException.class}, // only retry db failures
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public UserResponseDto findById(String userId) {
        UserEntity userEntity;
        try {
            userEntity = userRepository.findById(userId).
                    orElseThrow(() -> new UserNotFoundException("User not found for this id: " + userId));
            UserResponseDto userResponseDto = new UserResponseDto();
            userResponseDto.setUserId(userEntity.getUserId());
            userResponseDto.setName(userEntity.getName());
            userResponseDto.setEmail(userEntity.getEmail());
            userResponseDto.setCreatedAt(String.valueOf(userEntity.getCreatedAt()));
            return userResponseDto;
        } catch (UserNotFoundException e) {
            log.info("User not found for this userId:{}", userId);
            throw e;
        } catch (Exception e) {
            log.info("User retrieval failed for this userId:{},Exception:{}", userId, e.getMessage());
            throw e;
        }
    }

    @Retryable(
            value = {DataAccessException.class}, // only retry db failures
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public Response delete(String userId) {
        Response response;
        try {
            UserEntity userEntity = userRepository.findById(userId).
                    orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
            userRepository.delete(userEntity);
            response = new Response(true, "User Deleted successfully.");
            return response;
        } catch (UserNotFoundException e) {
            log.info("User not found for this userId:{}", userId);
            throw e;
        } catch (Exception e) {
            log.info("User deletion Exception for this userId:{},Exception:{}", userId, e.getMessage());
           throw e;
        }
    }
}
