package com.knu.ynortman.repository;

import com.knu.ynortman.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
