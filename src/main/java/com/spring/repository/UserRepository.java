package com.spring.repository;

import com.spring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);


    @Query(value = "SELECT users.* FROM users WHERE  user_id > 1  ORDER BY user_id ASC", nativeQuery = true)
    List<User> findAllUser();


}
