package com.westee.cake.service;

import com.westee.cake.generate.Config;
import com.westee.cake.generate.ConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ConfigService {
    private final ConfigMapper configMapper;

    @Autowired
    public ConfigService(ConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    public Config getConfig() {
        return configMapper.selectByPrimaryKey(1);
    }

    public Config updateConfig(Config config) {
        config.setId(1);
        config.setUpdatedAt(new Date());
        configMapper.updateByPrimaryKeySelective(config);
        return config;
    }
}
