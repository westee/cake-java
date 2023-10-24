package com.westee.cake.service;

import com.westee.cake.entity.ExpressCreate;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OrderDeliveryJob implements Job {
    @Autowired
    private WeChatExpressService weChatExpressService;

    private static final Logger log = LoggerFactory.getLogger(OrderDeliveryJob.class);

    public OrderDeliveryJob() {
    }

    @Override
    public void execute(JobExecutionContext context) {
        JobKey jobKey = context.getJobDetail().getKey();
        if (context.isRecovering()) {
            log.info("SimpleRecoveryJob: " + jobKey + " RECOVERING at " + new Date());
        } else {
            log.info("SimpleRecoveryJob: " + jobKey + " STARTING at " + new Date());
        }

        JobDataMap jobDataMap = context.getMergedJobDataMap();
        ExpressCreate expressInfo = (ExpressCreate) jobDataMap.get("info");
        weChatExpressService.doCreateExpress(expressInfo);
    }
}
