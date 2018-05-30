package com.shhy.demo.controller;


import com.shhy.demo.service.ManagerService;
import com.shhy.demo.util.AdminUtil;
import com.shhy.demo.util.AppMd5Util;
import com.shhy.demo.util.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping(value = "managerController")
public class ManagerController {

    Logger logger = LoggerFactory.getLogger(ManagerController.class);

    @Autowired
    private ManagerService managerService;


    @RequestMapping(value = "adminLogin", method = RequestMethod.POST)
    public JsonResult adminLogin(String userName, String password, HttpSession session) {
        if (userName.equals(AdminUtil.USER_NAME) && AppMd5Util.md5Password(password).equals(AdminUtil.PASSWORD)) {
            session.setAttribute("userName", userName);
            return JsonResult.success().add("msg", "登陆成功！");
        } else {
            return JsonResult.fail().add("msg", "用户名密码错误");
        }
    }


    @RequestMapping(value = "analysis", method = RequestMethod.GET)
    public JsonResult analysis(Date beginTime, Date endTime, Integer eventId, HttpSession session) {
        logger.info("事件数据查询API");
        if (null == session.getAttribute("userName")) {
            return JsonResult.fail().add("msg", "请登陆！");
        }
        logger.info("查询使用信息:" + beginTime + endTime + eventId);
        List<Map<String, Object>> data = managerService.analysis(beginTime, endTime, eventId);
        if (null == data) {
            return JsonResult.fail().add("msg", "no date");
        } else {
            return JsonResult.success().add("data", data);
        }
    }

    @RequestMapping(value = "analysis/users", method = RequestMethod.GET)
    public JsonResult analysisUsers(Date beginTime, Date endTime, HttpSession session) {
        logger.info("用户数据查询API");
        if (null == session.getAttribute("userName")) {
            return JsonResult.fail().add("msg", "请登陆！");
        }
        logger.info("查询使用信息:" + beginTime + endTime);
        List<Map<String, Object>> data = managerService.analysisUsers(beginTime, endTime);
        if (null == data) {
            return JsonResult.fail().add("msg", "no data");
        } else {
            return JsonResult.success().add("data", data);
        }
    }


    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String test() {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        Date beginTime = calendar.getTime();
        Integer eventId = null;
        List<Map<String, Object>> list = managerService.analysis(beginTime, endTime, eventId);
        List<Map<String, Object>> data1 = managerService.analysisUsers(beginTime, endTime);
        Map<String, Object> data = new HashMap<>();
        data.put("status", 1);
        data.put("data", list);
        data.put("data1", data1);
        return data.toString();
    }


}
