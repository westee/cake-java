package com.westee.cake.config;

import com.westee.cake.service.OrderDeliveryScheduler;
import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@Lazy
public class QuartzConfig {
    private final DataSource dataSource;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CustomJobFactory customJobFactory;

    @Bean
    public JobFactory jobFactory() {
        return new SpringBeanJobFactory();
    }

    @Bean
    public Scheduler scheduler() throws Exception {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setStartupDelay(10);
        schedulerFactoryBean.setApplicationContext(applicationContext);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        schedulerFactoryBean.afterPropertiesSet();
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.setJobFactory(customJobFactory);
        scheduler.pauseAll();
        return scheduler;
    }

    @Bean
    public OrderDeliveryScheduler orderDeliveryScheduler(Scheduler scheduler) {
        return new OrderDeliveryScheduler(scheduler);
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        Properties properties = new Properties();
        properties.setProperty("useProperties", "false");
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.setProperty("org.quartz.jobStore.isClustered", "true");
        properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");

        // Datasource
        properties.setProperty("org.quartz.jobStore.dataSource", "love");
        properties.setProperty("org.quartz.dataSource.love.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("org.quartz.dataSource.love.user", "root");
        properties.setProperty("org.quartz.dataSource.love.password", "root");
        properties.setProperty("org.quartz.dataSource.love.URL", "jdbc:mysql://172.20.0.3:3310/test-mysql");

        propertiesFactoryBean.setProperties(properties);
        return propertiesFactoryBean.getObject();
    }
}
