package com.shhy.demo.dao;

import com.shhy.demo.bean.User;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface UserDao {

    List<User> getAllUser();

    @Select("SELECT * FROM test_tb")
    @Results({
            @Result(id = true , column = "id" , property = "id"),
            @Result(column = "name" , property = "name"),
            @Result(column = "psd" , property = "psd"),
            @Result(column = "sex" , property = "sex"),
            @Result(column = "real_name" , property = "realName")
    })
    List<User> findAll();


    @Select("SELECT * FROM test_tb WHERE id = #{id}")
    @Results({
            @Result(id = true , column = "id" , property = "id"),
            @Result(column = "name" , property = "name"),
            @Result(column = "psd" , property = "psd"),
            @Result(column = "sex" , property = "sex"),
            @Result(column = "real_name" , property = "realName")
    })
    User getUserById(Integer id);



}
