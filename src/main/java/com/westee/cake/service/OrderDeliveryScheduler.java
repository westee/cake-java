package com.westee.cake.service;

import com.westee.cake.entity.ExpressCreate;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class OrderDeliveryScheduler {
    private final Scheduler scheduler;

    private static final Logger log = LoggerFactory.getLogger(OrderDeliveryScheduler.class);

    @Autowired
    public OrderDeliveryScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleOrderDelivery(Date cronExpression, ExpressCreate expressInfo) {
        String orderId = expressInfo.getStore_order_id();
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("orderId", orderId);
        jobDataMap.put("date", cronExpression.toString());
        jobDataMap.put("info", expressInfo);
        JobDetail jobDetail = JobBuilder.newJob(OrderDeliveryJob.class)
                .withIdentity("orderDeliveryJob" + orderId)
                .usingJobData(jobDataMap)
                .build();
        Trigger trigger = OrderDeliveryTrigger.createTrigger(jobDetail, cronExpression);
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("延迟任务错误：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void cancelOrderDelivery(String orderId) {
        JobKey jobKey = new JobKey("orderDeliveryJob" + orderId);
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("取消任务错误：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void rescheduleOrderDelivery(String orderId, Date newCronExpression)  {
        JobKey jobKey = new JobKey("orderDeliveryJob" + orderId);
        JobDetail jobDetail = null;
        try {
            jobDetail = scheduler.getJobDetail(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        ExpressCreate info = (ExpressCreate) jobDetail.getJobDataMap().get("info");
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("修改派送时间取消任务错误：{}", e.getMessage());
            throw new RuntimeException(e);
        }
        scheduleOrderDelivery(newCronExpression, info);
    }
}
