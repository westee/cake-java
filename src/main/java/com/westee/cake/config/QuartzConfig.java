package com.westee.cake.config;

import com.westee.cake.service.OrderDeliveryScheduler;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.Properties;

@Configuration
public class QuartzConfig {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CustomJobFactory customJobFactory;

    @Bean
    public JobFactory jobFactory() {
        return new SpringBeanJobFactory();
    }

    @Bean
    public Scheduler scheduler( ) throws Exception {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setStartupDelay(10);
        schedulerFactoryBean.setApplicationContext(applicationContext);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        schedulerFactoryBean.afterPropertiesSet();
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.setJobFactory(customJobFactory);
        scheduler.start();
        return scheduler;
    }

    @Bean
    public OrderDeliveryScheduler orderDeliveryScheduler(Scheduler scheduler) {
        return new OrderDeliveryScheduler(scheduler);
    }

    private Properties quartzProperties() {
        Properties properties = new Properties();
        properties.setProperty("org.quartz.scheduler.instanceName", "MyScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        return properties;
    }
}
