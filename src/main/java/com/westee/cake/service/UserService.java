package com.westee.cake.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westee.cake.entity.UsernameAndPassword;
import com.westee.cake.entity.WeChatSession;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.User;
import com.westee.cake.generate.UserExample;
import com.westee.cake.generate.UserMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class UserService {
    public static String salt = "salt"; // 可以从配置文件中读取或者生成一个随机的字符串
    private static final String APPID = "wx9xxxxxxxxxxx9b4";
    private static final String SECRET = "685742***************84xs859";

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User createUserIfNotExist(String openid) {
        User user = new User();
        user.setWxOpenId(openid);
        userMapper.insert(user);
        UserExample userExample = new UserExample();
        userExample.createCriteria().andWxOpenIdEqualTo(openid);
        return userMapper.selectByExample(userExample).get(0);
    }

    public User getUserByName(String name) {
        UserExample user = new UserExample();
        user.createCriteria().andNameEqualTo(name);
        return userMapper.selectByExample(user).get(0);
    }

    public User getByOpenid(String openid) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andWxOpenIdEqualTo(openid);
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() == 0) {
            createUserIfNotExist(openid);
        }
        return users.get(0);
    }

    public void createUserIfNotExist(WeChatSession weChatSession) {

    }

    public void createUserIfNotExist(UsernameAndPassword usernameAndPassword) {
        User user = new User();
        user.setName(usernameAndPassword.getUsername());
        user.setPassword(new Sha256Hash(usernameAndPassword.getPassword(), salt).toString());
        Date date = new Date();
        user.setCreatedAt(date);
        user.setUpdatedAt(date);
        userMapper.insert(user);
    }

    public WeChatSession getWeChatSession(String wxcode) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceURL = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APPID +
                "&secret=" + SECRET + "&js_code=" + wxcode + "&grant_type=authorization_code";

        ResponseEntity<String> responseEntity = restTemplate.exchange(resourceURL, HttpMethod.GET, null, String.class);
        System.out.println(responseEntity);
        WeChatSession weChatSession = null;
        if (responseEntity.getStatusCode() == HttpStatus.OK) {

            String sessionData = responseEntity.getBody();
            //解析从微信服务器获得的openid和session_key;
            ObjectMapper objectMapper = new ObjectMapper();
            weChatSession = objectMapper.readValue(sessionData, WeChatSession.class);

            //获取用户的唯一标识
            String openid = weChatSession.getOpenid();

            //获取会话秘钥
            String session_key = weChatSession.getSession_key();
            String unionid = weChatSession.getUnionid();

            return weChatSession;
        }
        return weChatSession;
    }

    public static User getSessionUser() {
        // 获取当前 Subject 对象
        Subject subject = SecurityUtils.getSubject();
        // 判断当前用户是否已经登录
        if (subject.isAuthenticated()) {
            // 获取当前用户的 principal（身份/凭证）
            Object principal = subject.getPrincipal();
            // 判断 principal 是否为 null，如果为 null 则表示当前用户没有登录或者登录已经过期
            if (principal != null) {
                // 对 principal 进行类型转换，通常需要根据具体的情况进行转换
                User user = (User) principal;
                // 输出当前登录的用户名信息
                System.out.println("当前登录用户名：" + user.getName());
                return user;
            }
        }
       throw HttpException.notAuthorized("未登录");
    }

    public User updateUser(User user) {
        userMapper.updateByPrimaryKeySelective(user);
        return user;
    }
}
