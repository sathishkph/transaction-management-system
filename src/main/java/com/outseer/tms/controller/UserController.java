package com.outseer.tms.controller;

import com.outseer.tms.dto.Response;
import com.outseer.tms.dto.UserRequestDto;
import com.outseer.tms.dto.UserResponseDto;
import com.outseer.tms.entity.UserEntity;
import com.outseer.tms.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {
     private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<Response> saveUser(@Valid @RequestBody UserRequestDto userRequestDto){
            return ResponseEntity.ok(userService.saveUser(userRequestDto));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String userId){
        return ResponseEntity.ok(userService.findById(userId));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Response> deleteUser(@PathVariable String userId){
        return ResponseEntity.ok(userService.delete(userId));
    }
}
