package com.shhy.demo.service;

import com.shhy.demo.bean.WxUser;

import java.util.List;

public interface WxUserService {
    List<WxUser> getAllUser();

    boolean checkUserRegister(String openId);

    boolean insert(WxUser user);

    WxUser returnWxUserByOpenId(String openid);

    boolean update(WxUser user);
}
