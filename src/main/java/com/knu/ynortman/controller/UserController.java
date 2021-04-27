package com.knu.ynortman.controller;

import com.knu.ynortman.dto.UserDto;
import com.knu.ynortman.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        //log.info(TenantContext.getTenantId());
        List<UserDto> userValues = userService.getUsers();
        return new ResponseEntity<>(userValues, HttpStatus.OK);
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserDto> getProduct(@PathVariable("userId") long userId) {
        try {
            UserDto branch = userService.getUser(userId);
            return new ResponseEntity<>(branch, HttpStatus.OK);
        } catch (Exception e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> createProduct(@RequestBody UserDto userValue) {
        UserDto user = userService.createUser(userValue);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
