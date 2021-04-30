package com.knu.ynortman.repository;

import com.knu.ynortman.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
	
	/*@Query("SELECT u FROM User u")
	Iterable<User> findAll();
	
	@Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findById(long id);*/
}
