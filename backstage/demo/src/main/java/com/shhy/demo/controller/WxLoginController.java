package com.shhy.demo.controller;


import com.shhy.demo.bean.GroupUser;
import com.shhy.demo.bean.WxCnt;
import com.shhy.demo.bean.WxUser;
import com.shhy.demo.service.WxUserService;
import com.shhy.demo.util.JsonResult;
import com.shhy.demo.util.MapResult;
import com.shhy.demo.util.WxAuthUtil;
import org.apache.ibatis.annotations.Param;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.Date;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "")
public class WxLoginController {

    Logger logger = LoggerFactory.getLogger(WxLoginController.class);

    @Autowired
    private WxUserService wxUserService;


    /**
     * 方案1  需要微信开放平台相关密钥
     */
    @RequestMapping(value = "wxLogin", method = RequestMethod.GET)
    public void wxLogin(HttpServletResponse response) {
        logger.info("登录接口");
        String backUrl = "https://wutian.zijimedia.cc/wxid";
        String url = null;
        try {
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WxAuthUtil.APP_ID
                    + "&redirect_uri=" + URLEncoder.encode(backUrl, "UTF-8")
                    + "&response_type=code"
                    + "&scope=snsapi_userinfo"
                    + "&state=STATE#wechat_redirect";
            logger.info("forward重定向地址{" + url + "}");
            response.sendRedirect(url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 方案1 回调接口
     */
    @RequestMapping(value = "wxid", method = RequestMethod.GET)
    public MapResult wxCallBack(HttpServletRequest request, HttpSession session, String code) {
        try {
            logger.info("微信回调函数");
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WxAuthUtil.APP_ID
                    + "&secret=" + WxAuthUtil.APPSECRET
                    + "&code=" + code
                    + "&grant_type=authorization_code";
            logger.info("地址：" + url);
            JSONObject jsonObject = WxAuthUtil.doGet(url);
            logger.info("json" + jsonObject.toString());
            String openid = jsonObject.getString("openid");
            String access_token = jsonObject.getString("access_token");
            String refresh_token = jsonObject.getString("refresh_token");
            logger.info("验证tokens是否失效");
            String chickUrl = "https://api.weixin.qq.com/sns/auth?access_token=" + access_token + "&openid=" + openid;
            JSONObject chickuserInfo = WxAuthUtil.doGet(chickUrl);
            if (!"0".equals(chickuserInfo.getString("errcode"))) {
                String refreshTokenUrl = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + openid + "&grant_type=refresh_token&refresh_token=" + refresh_token;
                JSONObject refreshInfo = WxAuthUtil.doGet(chickUrl);
                access_token = refreshInfo.getString("access_token");
                return MapResult.fail().add("登陆超时，请稍后再试");
            }
            String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token
                    + "&openid=" + openid
                    + "&lang=zh_CN";
            JSONObject userInfo = WxAuthUtil.doGet(infoUrl);
            WxUser user = new WxUser();
            user.setOpenId(userInfo.getString("openid"));
            user.setNickName(userInfo.getString("nickname"));
            user.setSex(userInfo.getString("sex"));
            user.setProvince(userInfo.getString("province"));
            user.setCity(userInfo.getString("city"));
            user.setCountry(userInfo.getString("country"));
            user.setHeadImgPath(userInfo.getString("headimgurl"));
            boolean flag = wxUserService.checkUserRegister(user.getOpenId());
            if (!flag) {
                boolean state = wxUserService.insert(user);
                logger.info("is or not insert:" + state);
                session.setAttribute("wxUser", user);
            } else {
                WxUser wxUser = wxUserService.returnWxUserByOpenId(openid);
                user.setId(wxUser.getId());
                boolean state = wxUserService.update(user);
                logger.info("is  or not update " + state);
            }
            return MapResult.success().addOpenid(user.getOpenId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return MapResult.fail().add("err");
        }
    }

    @RequestMapping(value = "analysisCase1", method = RequestMethod.POST)
    public JsonResult analysis(Integer event_id, HttpSession session, HttpServletResponse response) {
        logger.info("参数检查：" + event_id);
        WxUser user = (WxUser) session.getAttribute("wxUser");
        if (null == user && event_id == null) {
            logger.info("重新授权登陆");
            wxLogin(response);
            return JsonResult.fail().add("msg", "toLogin or check event_id");
        } else {
            session.removeAttribute("cntId");
            WxCnt cnt = new WxCnt();
            cnt.setUserId(user.getId());
            cnt.setEventId(event_id);
            cnt.setUseDate(new Date());
            cnt.setUseTime(0);
            String onlyId = user.getOpenId() + "." + cnt.getUserId() + "." + Math.random();
            cnt.setOwnerId(onlyId);
            boolean state = wxUserService.insertCnt(cnt);
            if (state) {
                Integer cntId = wxUserService.getCntByOwnerId(cnt);
                if (0 == cntId) {
                    logger.info("insert err");
                }
                session.setAttribute("cntId", cntId);
            }
            return JsonResult.success().add("msg", state);
        }
    }

    @RequestMapping(value = "analysis", method = RequestMethod.POST)
    public MapResult toLogin(@Param("ds") GroupUser ds, HttpSession session) {
        logger.info("确认参数" + ds.getOpenid() + ds.getCompany_no() + ds.getStart_time());
        session.removeAttribute("cntId");
        if (null == ds.getOpenid()) {
            return MapResult.fail();
        }
        if (null == session.getAttribute("wxUser")) {
            boolean flag = wxUserService.checkUserRegister(ds.getOpenid());
            if (!flag) {
                return MapResult.fail().add("请先授权");
            } else {
                WxUser wxUser = wxUserService.returnWxUserByOpenId(ds.getOpenid());
                wxUser.setCompanyNo(ds.getCompany_no());
                boolean state = wxUserService.update(wxUser);
                logger.info("update:" + state);
                session.setAttribute("wxUser", wxUser);
                WxCnt cnt = new WxCnt();
                cnt.setUserId(wxUser.getId());
                cnt.setEventId(ds.getEvent_id());
                cnt.setUseTime(0);
                cnt.setUseDate(new Date());
                String onlyId = wxUser.getOpenId() + "." + cnt.getUserId() + "." + Math.random();
                cnt.setOwnerId(onlyId);
                boolean cntInsert = wxUserService.insertCnt(cnt);
                if (cntInsert) {
                    Integer cntId = wxUserService.getCntByOwnerId(cnt);
                    session.setAttribute("cntId", cntId);
                }
            }
            return MapResult.success();
        } else {
            logger.info("已经登陆过 ， 切换event_id");
            session.removeAttribute("cntId");
            WxUser wxUser = (WxUser) session.getAttribute("wxUser");
            WxCnt cnt = new WxCnt();
            cnt.setUserId(wxUser.getId());
            cnt.setEventId(ds.getEvent_id());
            cnt.setUseTime(0);
            cnt.setUseDate(new Date());
            String onlyId = wxUser.getOpenId() + "." + cnt.getUserId() + "." + Math.random();
            cnt.setOwnerId(onlyId);
            boolean cntInsert = wxUserService.insertCnt(cnt);
            if (cntInsert) {
                Integer cntId = wxUserService.getCntByOwnerId(cnt);
                session.setAttribute("cntId", cntId);
            }
            return MapResult.success();
        }
    }

    @RequestMapping(value = "analysis/online", method = RequestMethod.GET)
    public void online(HttpSession session) {
        Integer id = (Integer) session.getAttribute("cntId");
        if (null == id) {
            logger.info("nothing");
        }
        WxCnt wxCnt = wxUserService.getCntById(id);
        if (null == wxCnt) {
            logger.info("nothing");
        }
        wxCnt.setUseTime(wxCnt.getUseTime() + 1);
        boolean state = wxUserService.updateCnt(wxCnt);
        logger.info("is update:" + state);
    }


    @RequestMapping(value = "cs", method = RequestMethod.GET)
    public MapResult cs(HttpSession session) {
        WxUser wxUser = (WxUser) session.getAttribute("wxUser");
        logger.info(":" + session.getAttribute("cntId"));
        return MapResult.success().add(wxUser);
    }
    /*----------------------------test--------------------*/
    @RequestMapping(value = "testA", method = RequestMethod.GET)
    public MapResult testA(){
        WxUser wxUser = new WxUser();
        wxUser.setOpenId("123456789");
        wxUser.setCompanyNo("1234564456");
        boolean state = wxUserService.insert(wxUser);
        logger.info(":" + state);
        return MapResult.success().addOpenid("111");
    }

    @RequestMapping(value = "testB", method = RequestMethod.GET)
    public MapResult testB(){
        WxUser wxUser = wxUserService.returnWxUserByOpenId("123456789");
        boolean check = wxUserService.checkUserRegister("123456789");
        System.out.println("check"+ check);
        wxUser.setCity("hangz");
        wxUser.setCountry("zh1");
        wxUser.setNickName("22");
        wxUser.setCompanyNo("222");
        boolean state = wxUserService.update(wxUser);
        logger.info(":" + state);
        return MapResult.success().add("111");
    }

    @RequestMapping(value = "testC", method = RequestMethod.GET)
    public MapResult testC(){
        WxCnt wxCnt = new WxCnt();
        wxCnt.setUserId(1);
        wxCnt.setEventId(0);
        wxCnt.setUseTime(0);
        wxCnt.setUseDate(new Date());
        wxCnt.setOwnerId("11223344");
        boolean state = wxUserService.insertCnt(wxCnt);
        logger.info(":" + state);
        Integer id = wxUserService.getCntByOwnerId(wxCnt);
        return MapResult.success().add("111");
    }


}
