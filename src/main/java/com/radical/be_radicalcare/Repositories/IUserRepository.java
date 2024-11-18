package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
