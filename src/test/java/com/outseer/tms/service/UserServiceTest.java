package com.outseer.tms.service;

import com.outseer.tms.dto.Response;
import com.outseer.tms.dto.UserRequestDto;
import com.outseer.tms.dto.UserResponseDto;
import com.outseer.tms.entity.UserEntity;
import com.outseer.tms.exception.UserIdAlreadyExistsException;
import com.outseer.tms.exception.UserNotFoundException;
import com.outseer.tms.repo.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
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
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        entityManager = mock(EntityManager.class);
        modelMapper = new ModelMapper();

        userService = new UserService(userRepository, modelMapper,entityManager);
    }

    @Test
    void testSaveUser_Success() {
        UserRequestDto request = new UserRequestDto("sathish1234", "sathishKumar P H", "sathishkph@yahoo.com");
        Response response = userService.saveUser(request);

        assertTrue(response.isSuccess());
        assertEquals("User created successfully.", response.getMessage());
        verify(entityManager, times(1)).persist(any(UserEntity.class));
        verify(entityManager, times(1)).flush();
    }

    @Test
    void testSaveUser_UserAlreadyExists() {
        UserRequestDto request = new UserRequestDto("sathish1234", "sathishKumar P H", "sathishkph@yahoo.com");
        doThrow(new EntityExistsException()).when(entityManager).persist(any(UserEntity.class));

        userService = new UserService(null, modelMapper, entityManager);

        assertThrows(UserIdAlreadyExistsException.class, () -> userService.saveUser(request));

        verify(entityManager, times(1)).persist(any(UserEntity.class));
        verify(entityManager, never()).flush();
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
