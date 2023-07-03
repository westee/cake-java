package com.westee.cake.service;

import com.github.pagehelper.PageHelper;
import com.westee.cake.data.ChargeStatus;
import com.westee.cake.data.ChargeType;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.generate.Charge;
import com.westee.cake.generate.ChargeExample;
import com.westee.cake.generate.ChargeMapper;
import com.westee.cake.generate.User;
import com.westee.cake.generate.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ChargeService {
    private final ChargeMapper chargeMapper;
    private final UserMapper userMapper;

    @Autowired
    public ChargeService(ChargeMapper chargeMapper, UserMapper userMapper) {
        this.chargeMapper = chargeMapper;
        this.userMapper = userMapper;
    }

    /**
     * 计算更新用户总余额
     * 新建一条充值记录
     *
     * @param price  充值金额
     * @param userId 用户id
     * @return       充值记录
     */
    public Charge charge(BigDecimal price, Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        BigDecimal balance =  Objects.isNull(user.getBalance()) ? BigDecimal.ZERO : user.getBalance();
        User user1 = new User();
        user1.setId(userId);
        user1.setBalance(balance.add(price));
        userMapper.updateByPrimaryKeySelective(user1);

        Charge charge = new Charge();
        charge.setAmount(price);
        charge.setUserId(userId);
        charge.setStatus(ChargeStatus.OK.getName());
        charge.setChargeType(ChargeType.WECHAT.getName());
        charge.setCreatedAt(new Date());
        charge.setUpdatedAt(new Date());
        chargeMapper.insert(charge);
        return charge;
    }

    public PageResponse<Charge> getChargeList(Long userId, Integer pageNum, Integer pageSize) {
        ChargeExample chargeExample = new ChargeExample();
        chargeExample.createCriteria().andUserIdEqualTo(userId);
        chargeExample.setOrderByClause("`CREATED_AT` DESC");
        long count = chargeMapper.countByExample(chargeExample);
        long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        PageHelper.startPage(pageNum, pageSize);
        List<Charge> charges = chargeMapper.selectByExample(chargeExample);

        return PageResponse.pageData(pageNum, pageSize, totalPage, charges);
    }
}
