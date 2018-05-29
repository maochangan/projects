package com.shhy.demo.service.impl;

import com.shhy.demo.bean.User;
import com.shhy.demo.dao.UserDao;
import com.shhy.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl  implements UserService {

    @Autowired
    private UserDao userDao ;

    @Override
    public List<User> findAll() {
        List<User> list = userDao.findAll();
        if(0 ==list.size()){
            return null;
        }else{
            return list;
        }
    }

    @Override
    public User getOneById(Integer id) {
        return userDao.getUserById(id);
    }

    @Override
    public List<User> getAllUser() {
        return userDao.getAllUser();
    }
}
