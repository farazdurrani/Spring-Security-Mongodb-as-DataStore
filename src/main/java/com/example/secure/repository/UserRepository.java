package com.example.secure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.secure.bean.User;

public interface UserRepository extends MongoRepository<User, Long> {

	User findByUsername(String username);

}
