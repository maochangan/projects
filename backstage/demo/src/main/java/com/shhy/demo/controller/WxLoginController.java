package com.shhy.demo.controller;


import com.shhy.demo.util.JsonResult;
import com.shhy.demo.util.WxAuthUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

@RestController
@RequestMapping(value = "wxLoginController")
public class WxLoginController {

    Logger logger = LoggerFactory.getLogger(WxLoginController.class);

    @RequestMapping(value = "wxLogin" , method = RequestMethod.GET)
    public void wxLogin(HttpServletResponse response) throws IOException {
        logger.info("登录接口");
        String backUrl = "微信开发平授权回调域名+ /api/wxLoginController/wxCallBack";
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WxAuthUtil.APP_ID
                + "&redirect_uri=" + URLEncoder.encode(backUrl , "UTF-8")
                + "&response_type=code"
                + "&scope=snsapi_userinfo"
                + "&state=STATE#wechat_redirect";
        logger.info("forward重定向地址{" + url + "}");
        response.sendRedirect(url);
    }

    @RequestMapping(value = "wxCallBack" , method = RequestMethod.GET)
    public JsonResult wxCallBack(HttpServletRequest request , HttpSession session) throws IOException {
        logger.info("回调函数");
        String code = request.getParameter("code");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+WxAuthUtil.APP_ID
                + "&secret="+WxAuthUtil.APPSECRET
                + "&code="+code
                + "&grant_type=authorization_code";
        logger.info("地址：" + url);
        JSONObject jsonObject = WxAuthUtil.doGet(url);
        String openid = jsonObject.getString("openid");
        String access_token = jsonObject.getString("access_token");
        String refresh_token = jsonObject.getString("refresh_token");
        logger.info("验证tokens是否失效");
        String chickUrl="https://api.weixin.qq.com/sns/auth?access_token="+access_token+"&openid="+openid;
        JSONObject chickuserInfo = WxAuthUtil.doGet(chickUrl);
        if(!"0".equals(chickuserInfo.getString("errcode"))){
            String refreshTokenUrl="https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+openid+"&grant_type=refresh_token&refresh_token="+refresh_token;
            JSONObject refreshInfo = WxAuthUtil.doGet(chickUrl);
            access_token=refreshInfo.getString("access_token");
            return JsonResult.fail().add("msg", "登陆超时，请稍后再试");
        }
        String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token="+access_token
                + "&openid="+openid
                + "&lang=zh_CN";
        JSONObject userInfo = WxAuthUtil.doGet(infoUrl);
        String openidInFinalResult = userInfo.getString("openid");
        String nickName = userInfo.getString("nickname");
        String sex = userInfo.getString("sex");
        String province = userInfo.getString("province");
        String city = userInfo.getString("city");
        String country = userInfo.getString("country");
        String handImgPath = userInfo.getString("headimgurl");
        String privilege = userInfo.getString("privilege");
        String unionid = userInfo.getString("unionid");

        //TODO insert into datatable check user is or not in table


        session.setAttribute("user" , "value");
        return JsonResult.success().add("msg", "授权登陆成功");
    }

}
