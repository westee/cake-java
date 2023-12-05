package com.westee.cake.realm;

import com.westee.cake.generate.Role;
import com.westee.cake.generate.User;
import com.westee.cake.service.MenuService;
import com.westee.cake.service.RoleService;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.List;

@Slf4j
public class AuthorizationRealm extends AuthorizingRealm {
    @Inject
    private RoleService roleService;
    @Inject
    private MenuService menuService;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object principal = principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (principal instanceof User) {
            User userLogin = (User) principal;
            if(userLogin != null){
                List<Role> roleList = roleService.findByUserid(userLogin.getId());
                if(CollectionUtils.isNotEmpty(roleList)){
                    for(Role role : roleList){
                        info.addRole(role.getName());

//                        List<Menu> menuList = menuService.getAllMenuByRoleId(role.getId());
//                        if(CollectionUtils.isNotEmpty(menuList)){
//                            for (Menu menu : menuList){
//                                if(StringUtils.isNoneBlank(menu.getPermission())){
//                                    info.addStringPermission(menu.getPermission());
//                                }
//                            }
//                        }
                    }
                }
            }
        }
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        return null;
    }
}
