package com.shhy.demo.service;

import com.shhy.demo.bean.WxCnt;
import com.shhy.demo.bean.WxUser;

import java.util.List;

public interface WxUserService {

    List<WxUser> getAllUser();

    boolean checkUserRegister(String openId);

    boolean insert(WxUser user);

    WxUser returnWxUserByOpenId(String openid);

    boolean update(WxUser user);

    boolean insertCnt(WxCnt cnt);

    Integer getCntByEntity(WxCnt cnt);

    WxCnt getCntById(Integer id);

    boolean updateCnt(WxCnt wxCnt);
}
