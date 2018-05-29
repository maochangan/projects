package com.shhy.demo.service;

import com.shhy.demo.bean.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User getOneById(Integer id);

    List<User> getAllUser();

    boolean testInsert(User user);
}
