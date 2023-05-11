package com.westee.cake.service;

import com.westee.cake.entity.UsernameAndPassword;
import com.westee.cake.generate.User;
import com.westee.cake.generate.UserExample;
import com.westee.cake.generate.UserMapper;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {
    public static String salt = "salt"; // 可以从配置文件中读取或者生成一个随机的字符串

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User createUserIfNotExist(String tel) {
        return null;
    }

    public User getUserByName(String name) {
        UserExample user = new UserExample();
        user.createCriteria().andNameEqualTo(name);
        return userMapper.selectByExample(user).get(0);
    }

    public User getByOpenid(String openid) {
        return null;
    }

    public void createUserIfNotExist(UsernameAndPassword usernameAndPassword) {
        User user = new User();
        user.setName(usernameAndPassword.getUsername());
        user.setPassword(new Sha256Hash(usernameAndPassword.getPassword(), salt).toString() );
        Date date = new Date();
        user.setCreatedAt(date);
        user.setUpdatedAt(date);
        userMapper.insert(user);
    }
}
