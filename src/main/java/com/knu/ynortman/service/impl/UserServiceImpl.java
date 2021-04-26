package com.knu.ynortman.service.impl;

import com.knu.ynortman.dto.UserDto;
import com.knu.ynortman.entity.User;
import com.knu.ynortman.repository.UserRepository;
import com.knu.ynortman.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;

    @Autowired
    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public List<UserDto> getUsers() {
        return StreamSupport.stream(userRepo.findAll().spliterator(), false)
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long id) {
        return userRepo.findById(id)
                .map(UserDto::fromUser)
                .orElseThrow();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepo.save(UserDto.toUser(userDto));
        return UserDto.fromUser(user);
    }
}
