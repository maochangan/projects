package com.shhy.demo.service.impl;

import com.shhy.demo.bean.WxUser;
import com.shhy.demo.dao.WxUserDao;
import com.shhy.demo.service.WxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class WxUserServiceImpl implements WxUserService {

    @Autowired
    private WxUserDao wxUserDao ;


    @Override
    public List<WxUser> getAllUser() {
        List<WxUser> list = wxUserDao.getAllWxUser();
        if(0 == list.size()){
            return null;
        }else{
            return list;
        }
    }

    @Override
    public boolean checkUserRegister(String openId) {
        return wxUserDao.checkUserRegister(openId);
    }

    @Override
    public boolean insert(WxUser user) {
        int state = wxUserDao.insert(user);
        if(0 == state){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public WxUser returnWxUserByOpenId(String openid) {
        WxUser wxUser = wxUserDao.returnWxUserByOpenId(openid);
        if(null == wxUser){
            return null;
        }else{
            return wxUser;
        }
    }

    @Override
    public boolean update(WxUser user) {
        int  state = wxUserDao.update(user);
        if(state == 0){
            return false;
        }else{
            return true;
        }
    }
}
