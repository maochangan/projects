package com.shhy.demo.controller;


import com.shhy.demo.bean.WxCnt;
import com.shhy.demo.bean.WxUser;
import com.shhy.demo.service.WxUserService;
import com.shhy.demo.util.JsonResult;
import com.shhy.demo.util.WxAuthUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.Date;

@RestController
@RequestMapping(value = "wxLoginController")
public class WxLoginController {

    Logger logger = LoggerFactory.getLogger(WxLoginController.class);

    @Autowired
    private WxUserService wxUserService;


    @RequestMapping(value = "wxLogin" , method = RequestMethod.GET)
    public void wxLogin(HttpServletResponse response) {
        logger.info("登录接口");
        String backUrl = "https://wutian.zijimedia.cc/api/wxLoginController/wxCallBack";
        String url = null;
        try {
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WxAuthUtil.APP_ID
                    + "&redirect_uri=" + URLEncoder.encode(backUrl , "UTF-8")
                    + "&response_type=code"
                    + "&scope=snsapi_userinfo"
                    + "&state=STATE#wechat_redirect";
            logger.info("forward重定向地址{" + url + "}");
            response.sendRedirect(url);
        } catch (Exception e) {
            logger.error(e.getMessage() , e );
        }

    }

    @RequestMapping(value = "wxCallBack" , method = RequestMethod.GET)
    public JsonResult wxCallBack(HttpServletRequest request , HttpSession session){
        try {
            logger.info("微信回调函数");
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
            WxUser user = new WxUser();
            user.setOpenId(userInfo.getString("openid"));
            user.setNickName(userInfo.getString("nickname"));
            user.setSex(userInfo.getString("sex").charAt(0));
            user.setProvince(userInfo.getString("province"));
            user.setCity(userInfo.getString("city"));
            user.setCountry(userInfo.getString("country"));
            user.setHeadImgPath(userInfo.getString("headimgurl"));
            boolean flag = wxUserService.checkUserRegister(user.getOpenId());
            if(!flag){
                boolean state = wxUserService.insert(user);
                logger.info("is or not insert:"+state);
                session.setAttribute("wxUser" , user);
            }else{
                WxUser wxUser = wxUserService.returnWxUserByOpenId(openid);
                user.setId(wxUser.getId());
                boolean state = wxUserService.update(user);
                logger.info("is  or not update "+ state);
                session.setAttribute("wxUser" , user);
            }
            return JsonResult.success().add("msg", "授权登陆成功");
        }catch (Exception e){
            logger.error(e.getMessage() , e);
            return JsonResult.fail().add("msg", "err");
        }
    }


    @RequestMapping(value = "analysis", method = RequestMethod.POST)
    public JsonResult analysis(Integer event_id , HttpSession session , HttpServletResponse response){
        WxUser user = (WxUser) session.getAttribute("wxUser");
        if(null == user){
            logger.info("重新授权登陆");
            wxLogin(response);
            return JsonResult.fail().add("msg", "toLogin");
        }else{
            session.removeAttribute("cntId");
            WxCnt cnt = new WxCnt();
            cnt.setUserId(user.getId());
            cnt.setEventId(event_id);
            cnt.setUseDate(new Date());
            cnt.setUseTime(0);
            boolean state = wxUserService.insertCnt(cnt);
            if(state){
                Integer cntId = wxUserService.getCntByEntity(cnt);
                if(0 == cntId){}
                session.setAttribute("cntId" , cntId);
            }
            logger.info("is or not insert");
            return JsonResult.success().add("msg", state);
        }
    }


    @RequestMapping(value = "analysis/online", method = RequestMethod.GET)
    public void online(HttpSession session){
        Integer id = (Integer) session.getAttribute("cntId");
        if(0 == id){logger.info("nothing");}
        WxCnt wxCnt = wxUserService.getCntById(id);
        if(null == wxCnt){logger.info("nothing");}
        wxCnt.setUseTime(wxCnt.getUseTime()+1);
        boolean state = wxUserService.updateCnt(wxCnt);
        logger.info("is update:" + state);
    }

}
