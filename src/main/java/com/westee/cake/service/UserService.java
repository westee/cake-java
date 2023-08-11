package com.westee.cake.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.westee.cake.config.WxPayConfig;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.entity.UsernameAndPassword;
import com.westee.cake.entity.WeChatSession;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Charge;
import com.westee.cake.generate.Role;
import com.westee.cake.generate.RoleMapper;
import com.westee.cake.generate.User;
import com.westee.cake.generate.UserExample;
import com.westee.cake.generate.UserMapper;
import com.westee.cake.generate.UserRole;
import com.westee.cake.generate.UserRoleExample;
import com.westee.cake.generate.UserRoleMapper;
import com.westee.cake.realm.JWTUtil;
import com.westee.cake.realm.LoginType;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserService {
    private final WxPayConfig wxPayConfig;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    @Autowired
    public UserService(UserMapper userMapper, WxPayConfig wxPayConfig, RoleMapper roleMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.wxPayConfig = wxPayConfig;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    public User createUserIfNotExist(String openid) {
        User user = new User();
        user.setWxOpenId(openid);
        user.setUpdatedAt(new Date());
        user.setCreatedAt(new Date());
        userMapper.insert(user);
        UserExample userExample = new UserExample();
        userExample.createCriteria().andWxOpenIdEqualTo(openid);
        return userMapper.selectByExample(userExample).get(0);
    }

    /**
     * 根据用户名查询User，没找到抛出错误
     * 不能帮助用户直接创建用户，因为没有密码
     *
     * @param name 用户名
     * @return User
     */
    public User getUserByName(String name) {
        UserExample user = new UserExample();
        user.createCriteria().andNameEqualTo(name);
        if (userMapper.selectByExample(user).isEmpty()) {
            throw HttpException.notAuthorized("用户名或密码不正确");
        }
        return userMapper.selectByExample(user).get(0);
    }

    public User getByOpenid(String openid) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andWxOpenIdEqualTo(openid);
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() == 0) {
            throw HttpException.notAuthorized("用户未授权");
        }
        return users.get(0);
    }

    public User getUserByToken(String token) {
        if ("".equals(token)) throw HttpException.notAuthorized("用户未登录");
        String username = JWTUtil.getUsername(token);
        String tokenType = JWTUtil.getTokenType(token);
        User user;
        if (Objects.equals(tokenType, LoginType.WECHAT_LOGIN.getType())) {
            user = getByOpenid(username);
        } else {
            user = getUserByName(username);
        }
        return user;
    }

    public void createUserIfNotExist(WeChatSession weChatSession, String avatar, String name) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andWxOpenIdEqualTo(weChatSession.getOpenid());
        if (userMapper.selectByExample(userExample).isEmpty()) {
            User user = new User();
            user.setNickname(name);
            user.setAvatarUrl(avatar);
            user.setWxOpenId(weChatSession.getOpenid());
            user.setWxSessionKey(weChatSession.getSession_key());
            Date date = new Date();
            user.setRoleId(2L);
            user.setCreatedAt(date);
            user.setUpdatedAt(date);
            userMapper.insert(user);
            insertUserRole(user.getId(), 2L);
        }
    }

    public void createUserIfNotExist(UsernameAndPassword usernameAndPassword) {
        User user = new User();
        user.setName(usernameAndPassword.getUsername());
        user.setPassword(new Sha256Hash(usernameAndPassword.getPassword(), wxPayConfig.getSALT()).toString());
        Date date = new Date();
        user.setCreatedAt(date);
        user.setUpdatedAt(date);
        userMapper.insert(user);
        insertUserRole(user.getId(), 2L);
    }

    public WeChatSession getWeChatSession(Map<String, String> body) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String resourceURL = "https://api.weixin.qq.com/sns/jscode2session?appid=" + wxPayConfig.getAPPID() +
                "&secret=" + wxPayConfig.getSECRET() + "&js_code=" + body.get("wxcode") + "&grant_type=authorization_code";

        ResponseEntity<String> responseEntity = restTemplate.exchange(resourceURL, HttpMethod.GET, null, String.class);
        System.out.println(responseEntity);
        WeChatSession weChatSession = null;
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String sessionData = responseEntity.getBody();
            //解析从微信服务器获得的openid和session_key;
            ObjectMapper objectMapper = new ObjectMapper();
            weChatSession = objectMapper.readValue(sessionData, WeChatSession.class);

            if (Objects.nonNull(weChatSession.getOpenid())) {
                createUserIfNotExist(weChatSession, body.get("avatar") == null ? "" : body.get("avatar"),
                        body.get("name") == null ? "" : body.get("name"));
            }

            //获取用户的唯一标识
            String openid = weChatSession.getOpenid();

            //获取会话秘钥
            String session_key = weChatSession.getSession_key();
            String unionid = weChatSession.getUnionid();

            return weChatSession;
        }
        throw HttpException.badRequest(responseEntity.getBody());
    }

    public void updateUserBalance(Charge charge) {
        Long userId = charge.getUserId();
        User user = userMapper.selectByPrimaryKey(userId);
        BigDecimal balance = Objects.isNull(user.getBalance()) ? BigDecimal.ZERO : user.getBalance();
        User user1 = new User();
        user1.setId(userId);
        user1.setBalance(balance.add(charge.getAmount()).add(charge.getPresent()));
        userMapper.updateByPrimaryKeySelective(user1);
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
        checkAdmin(user.getId());
        userMapper.updateByPrimaryKeySelective(user);
        return user;
    }

    public User insertUser(User user) {
        userMapper.insert(user);
        return user;
    }

    public void insertUserRole(long userId, long roleId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);
    }

    public Role getUserRole(long userId) {
        UserRoleExample userRoleExample = new UserRoleExample();
        userRoleExample.createCriteria().andUserIdEqualTo(userId);
        List<UserRole> userRoles = null;
        try {
            userRoles = userRoleMapper.selectByExample(userRoleExample);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (Objects.nonNull(userRoles) && userRoles.isEmpty()) {
            Role role = new Role();
            role.setName("normal");
            return role;
        }
        return roleMapper.selectByPrimaryKey(userRoles.get(0).getRoleId());
    }

    public boolean checkAdmin(long userId) {
        Role userRole = getUserRole(userId);
        if ("admin".equals(userRole.getName())) {
            return true;
        }
        throw HttpException.forbidden("没有权限");
    }

    public User getUserById(Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (Objects.isNull(user)) {
            throw HttpException.notFound("用户不存在");
        }
        return user;
    }

    public User deleteUserById(Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (Objects.isNull(user)) {
            throw HttpException.notFound("用户不存在");
        } else {
            userMapper.deleteByPrimaryKey(userId);
        }
        return user;
    }

    public PageResponse<User> getUserList(Long userId, Integer pageNum, Integer pageSize) {
        checkAdmin(userId);
        UserExample userExample = new UserExample();
        userExample.setOrderByClause("`CREATED_AT` DESC");
        long count = userMapper.countByExample(userExample);
        long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.selectByExample(userExample);

        return PageResponse.pageData(pageNum, pageSize, totalPage, users);
    }

    /**
     *  管理员搜索用户
     * @param keyword   关键字
     * @param type      id tel nickname
     * @return
     */
    public PageResponse<User> searchUser(String keyword, String type) {
        UserExample userExample = new UserExample();
        if(Objects.equals(type, "id")) {
            userExample.createCriteria().andIdEqualTo(Long.valueOf(keyword));
        }
        if(Objects.equals(type, "tel")) {
            userExample.createCriteria().andTelEqualTo(keyword);
        }
        if(Objects.equals(type, "nickname")) {
            userExample.createCriteria().andNicknameEqualTo(keyword);
        }

        List<User> users = userMapper.selectByExample(userExample);
        return PageResponse.pageData(1,1, users.size(), users);
    }
}
