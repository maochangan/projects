package com.shhy.demo.service.impl;

import com.shhy.demo.bean.WxCnt;
import com.shhy.demo.dao.WxCntDao;
import com.shhy.demo.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private WxCntDao wxCntDao;

    @Override
    public List<WxCnt> analysis(Date beginTime, Date endTime, Integer eventId) {
        List<Map<String, Object>> data = wxCntDao.selectCountWithCondition(beginTime, endTime, eventId);


        return null;
    }
}
