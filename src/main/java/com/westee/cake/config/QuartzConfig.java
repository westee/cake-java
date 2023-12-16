package com.westee.cake.config;

import com.westee.cake.service.OrderDeliveryScheduler;
import com.zaxxer.hikari.HikariDataSource;
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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        System.out.println("-------------dataSource-------------");
        HikariDataSource source = (HikariDataSource) dataSource;
        System.out.println(source.getPassword());
        System.out.println(source.getUsername());
        System.out.println(source.getJdbcUrl());
        xxx(source);

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
        properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");

        propertiesFactoryBean.setProperties(properties);
        return propertiesFactoryBean.getObject();
    }


    public void xxx(HikariDataSource source) {
        try (Connection connection = source.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet resultSet = metadata.getTables(null, null, "%", null);
            while (resultSet.next()) {
                System.out.println(resultSet.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
