package com.shhy.demo.controller;

import com.shhy.demo.bean.User;
import com.shhy.demo.service.UserService;
import com.shhy.demo.util.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "userController")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService ;

    @RequestMapping(value = "testMvc", method = RequestMethod.GET)
    public JsonResult testMvc(){
        logger.info("测试访问");
        return JsonResult.success().add("msg", "msg");
    }


    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public JsonResult findAll(){
        List<User> list = userService.findAll();
        logger.info("测试mybatis" );
        if(null == list){
            return JsonResult.fail().add("msg", "is null");
        }else{
            return JsonResult.success().add("list", list);
        }
    }

    @RequestMapping(value = "getOneById" , method = RequestMethod.GET)
    public JsonResult getOneById(Integer id){
        logger.info("测试传参");
        User u = userService.getOneById(id);
        if(null == u){
            return JsonResult.fail().add("msg", "is null");
        }else{
            return JsonResult.success().add("u", u);
        }
    }

    @RequestMapping(value = "getALlUser", method = RequestMethod.GET)
    public JsonResult getALlUsers(){
        List<User> list = userService.getAllUser();
        if(null == list){
            return JsonResult.fail().add("msg" , "is null");
        }else{
            return JsonResult.success().add("user", list);
        }
    }

    @RequestMapping(value = "testInsert", method = RequestMethod.GET)
    public JsonResult testInsert(){
        logger.info("测试传参");
        User user = new User();
        user.setName("heloo");
        user.setPsd("123");
        user.setSex('男');
        user.setRealName("test");
        boolean state = userService.testInsert(user);
        if(state){
            return JsonResult.success().add("success" , "11");
        }else{
            return JsonResult.fail().add("msg", "fial");
        }

    }

}
