package com.incapp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.incapp.entity.Book;
import com.incapp.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

}
