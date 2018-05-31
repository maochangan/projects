package com.shhy.demo;

import com.shhy.demo.bean.WxUser;
import com.shhy.demo.dao.WxCntDao;
import com.shhy.demo.dao.WxUserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired

    @Test
    public void contextLoads() {

        WxUser user = new WxUser();

        for (int i = 0 ; i < 50 ; i ++){
            user.setOpenId("openid"+ (i+Math.random()*i));
            user.setNickName("nickName" + (i+Math.random()*i));
            user.setProvince("province"+ (i+Math.random()*i));
            user.setCity("city"+(i+Math.random()*i));
            user.setCountry("country" + (i+Math.random()*i));
            user.setHeadImgPath("path" + (i+Math.random()*i));

        }

    }

}
