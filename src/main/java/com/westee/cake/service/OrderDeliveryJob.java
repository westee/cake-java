package com.westee.cake.service;

import com.westee.cake.entity.ExpressCreate;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderDeliveryJob implements Job {
    @Autowired
    private WeChatExpressService weChatExpressService;

    public OrderDeliveryJob() {}

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        ExpressCreate expressInfo = (ExpressCreate) jobDataMap.get("info");
        weChatExpressService.doCreateExpress(expressInfo);
    }
}
