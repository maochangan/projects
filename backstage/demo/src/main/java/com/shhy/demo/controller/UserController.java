package com.shhy.demo.controller;

import com.shhy.demo.bean.User;
import com.shhy.demo.service.UserService;
import com.shhy.demo.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "userController")
public class UserController {


    @Autowired
    private UserService userService ;

    @RequestMapping(value = "testMvc", method = RequestMethod.GET)
    public JsonResult testMvc(){

        return JsonResult.success().add("msg", "msg");
    }


    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public JsonResult findAll(){
        List<User> list = userService.findAll();
        if(null == list){
            return JsonResult.fail().add("msg", "is null");
        }else{
            return JsonResult.success().add("list", list);
        }
    }

    @RequestMapping(value = "getOneById" , method = RequestMethod.GET)
    public JsonResult getOneById(Integer id){
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
