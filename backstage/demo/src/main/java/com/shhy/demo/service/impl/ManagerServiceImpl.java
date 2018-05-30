package com.shhy.demo.service.impl;

import com.shhy.demo.dao.WxCntDao;
import com.shhy.demo.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private WxCntDao wxCntDao;

    @Override
    public List<Map<String, Object>> analysis(Date beginTime, Date endTime, Integer eventId) {
        List<Map<String, Object>> data = wxCntDao.selectCountWithCondition(beginTime, endTime, eventId);
        if(data.size() == 0){
            return null;
        }
        return data;
    }

    @Override
    public List<Map<String, Object>> analysisUsers(Date beginTime, Date endTime) {
        List<Map<String, Object>> data = wxCntDao.analysisUsers(beginTime, endTime);
        if(data.size() == 0){
            return null;
        }
        return data;
    }
}
