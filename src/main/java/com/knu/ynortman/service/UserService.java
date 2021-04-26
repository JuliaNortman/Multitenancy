package com.knu.ynortman.service;

import com.knu.ynortman.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();
    UserDto getUser(long id);
    UserDto createUser(UserDto user);
}
