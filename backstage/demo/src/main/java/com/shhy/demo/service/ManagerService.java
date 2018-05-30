package com.shhy.demo.service;

import com.shhy.demo.bean.WxCnt;

import java.util.Date;
import java.util.List;

public interface ManagerService {
    List<WxCnt> analysis(Date beginTime, Date endTime, Integer eventId);
}
