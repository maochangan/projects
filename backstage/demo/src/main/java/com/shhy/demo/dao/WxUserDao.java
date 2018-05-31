package com.shhy.demo.dao;

import com.shhy.demo.bean.WxUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

public interface WxUserDao {

    int insert(WxUser user);

    int update(WxUser user);

    @Delete("DELETE FORM wx_user WHERE id = #{id , jdbcType=INTEGER} ")
    int delete(Integer id);

    Boolean checkUserRegister(String openId);

    WxUser returnWxUserByOpenId(String openId);

    WxUser returnWxUserById(Integer id);

    List<WxUser> getAllWxUser();


}
