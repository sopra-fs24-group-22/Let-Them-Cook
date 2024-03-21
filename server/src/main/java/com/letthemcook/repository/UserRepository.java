package com.letthemcook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.letthemcook.entity.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
}
