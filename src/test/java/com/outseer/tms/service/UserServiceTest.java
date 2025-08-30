package com.outseer.tms.service;

import com.outseer.tms.dto.Response;
import com.outseer.tms.dto.UserRequestDto;
import com.outseer.tms.dto.UserResponseDto;
import com.outseer.tms.entity.UserEntity;
import com.outseer.tms.exception.UserIdAlreadyExistsException;
import com.outseer.tms.exception.UserNotFoundException;
import com.outseer.tms.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        modelMapper = new ModelMapper();
        userService = new UserService(userRepository, modelMapper);
    }

    @Test
    void testSaveUser_Success() {
        UserRequestDto request = new UserRequestDto("sathish1234", "sathishKumar P H", "sathishkph@yahoo.com");
        when(userRepository.existsById("sathish1234")).thenReturn(false);

        Response response = userService.saveUser(request);

        assertTrue(response.isSuccess());
        assertEquals("User created successfully.", response.getMessage());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testSaveUser_UserAlreadyExists() {
        UserRequestDto request = new UserRequestDto("sathish1234", "sathishKumar P H", "sathishkph@yahoo.com");
        when(userRepository.existsById("sathish1234")).thenReturn(true);

        assertThrows(UserIdAlreadyExistsException.class, () -> userService.saveUser(request));
    }

    @Test
    void testFindById_UserFound() {
        UserEntity entity = new UserEntity();
        entity.setUserId("sathish1234");
        entity.setName( "sathishKumar P H");
        entity.setEmail("sathishkph@yahoo.com");
        when(userRepository.findById("sathish1234")).thenReturn(Optional.of(entity));

        UserResponseDto response = userService.findById("sathish1234");

        assertEquals("sathish1234", response.getUserId());
        assertEquals("sathishKumar P H", response.getName());
        assertEquals("sathishkph@yahoo.com",response.getEmail());
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById("sathish1234")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById("sathish1234"));
    }
}
