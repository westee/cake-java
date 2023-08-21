package com.westee.cake.service;

import com.westee.cake.generate.Role;
import com.westee.cake.generate.UserRoleMapper;
import com.westee.cake.generate.RoleMapper;
import com.westee.cake.generate.UserRoleExample;
import com.westee.cake.generate.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleService(UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    public List<Role> findByUserid(Long userId){
        UserRoleExample userRoleExample = new UserRoleExample();
        userRoleExample.createCriteria().andUserIdEqualTo(userId);
        List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
        ArrayList<Role> objects = new ArrayList<>();
        userRoles.forEach(userRole -> objects.add(roleMapper.selectByPrimaryKey(userRole.getRoleId())));
        return objects;
    }

    public Role getUserRoleById(Long roleId) {
        return roleMapper.selectByPrimaryKey(roleId);
    }
}
