package com.westee.cake.realm;

import com.westee.cake.entity.Menu;
import com.westee.cake.generate.Role;
import com.westee.cake.generate.User;
import com.westee.cake.service.MenuService;
import com.westee.cake.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class AuthorizationRealm extends AuthorizingRealm {
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("---------------- 执行 Shiro 权限获取 ---------------------");
        Object principal = principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (principal instanceof User) {
            User userLogin = (User) principal;
            if(userLogin != null){
                List<Role> roleList = roleService.findByUserid(userLogin.getId());
                if(CollectionUtils.isNotEmpty(roleList)){
                    for(Role role : roleList){
                        info.addRole(role.getName());

                        List<Menu> menuList = menuService.getAllMenuByRoleId(role.getId());
                        if(CollectionUtils.isNotEmpty(menuList)){
                            for (Menu menu : menuList){
                                if(StringUtils.isNoneBlank(menu.getPermission())){
                                    info.addStringPermission(menu.getPermission());
                                }
                            }
                        }
                    }
                }
            }
        }
        log.info("---------------- 获取到以下权限 ----------------");
        log.info(info.getStringPermissions().toString());
        log.info("---------------- Shiro 权限获取成功 ----------------------");
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        return null;
    }
}
