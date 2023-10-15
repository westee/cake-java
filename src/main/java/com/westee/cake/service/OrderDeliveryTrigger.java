package com.westee.cake.service;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import java.util.Date;

public class OrderDeliveryTrigger {
    public static Trigger createTrigger(JobDetail jobDetail, String cronExpression) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .withIdentity("orderDeliveryTrigger")
                .withDescription("Trigger for order delivery")
                .build();
    }

    public static Trigger createTrigger(JobDetail jobDetail, Date startTime) {
        System.out.println("trigger");
        System.out.println(startTime);
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .withIdentity("orderDeliveryTrigger")
                .withDescription("Trigger for order delivery")
                .startAt(startTime)
                .build();
    }
}
