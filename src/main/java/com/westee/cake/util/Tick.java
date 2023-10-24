package com.westee.cake.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westee.cake.config.WxPayConfig;
import com.westee.cake.global.GlobalVariable;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class Tick {
    @Autowired
    WxPayConfig wxPayConfig;

    private final Scheduler scheduler;

    @Autowired
    public Tick(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    private static final Logger log = LoggerFactory.getLogger(Tick.class);
    Logger monitorLogger = LoggerFactory.getLogger("message");

    @Lazy(false)
    @Scheduled(fixedDelay=90, timeUnit = TimeUnit.MINUTES)   //每90分钟执行一次
    public void getWeChatAccessToken() throws IOException {
        HashMap<String, String> hashMapParams = new HashMap<>();
        hashMapParams.put("grant_type", "client_credential");
        hashMapParams.put("appid", wxPayConfig.getAPPID());
        hashMapParams.put("secret", wxPayConfig.getSECRET());

        System.out.println("定时任务启动了");
        //发送GET请求
        String sendGet = RequestUtil.doGetRequest("https://api.weixin.qq.com/cgi-bin/token", hashMapParams);
        // 解析相应内容（转换成json对象）
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> stringStringMap = mapper.readValue(sendGet, new TypeReference<>() {
        });
        //拿到access_token
        String accessToken = stringStringMap.get("access_token");
        GlobalVariable.INSTANCE.setAccessToken(accessToken);
        log.info("获得accessToken {}", accessToken);
        monitorLogger.info("获得accessToken {}", accessToken);
        try {
            if(!scheduler.isStarted()) {
                scheduler.start();
                scheduler.resumeAll();
            }
        } catch (SchedulerException e) {
            log.error("定时任务启动错误 in util Tick：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
