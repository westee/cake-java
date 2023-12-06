package com.westee.cake.service;

import com.github.pagehelper.PageHelper;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.westee.cake.data.ChargeStatus;
import com.westee.cake.data.ChargeType;
import com.westee.cake.entity.OrderType;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.Charge;
import com.westee.cake.generate.ChargeExample;
import com.westee.cake.generate.ChargeMapper;
import com.westee.cake.generate.ChargeOption;
import com.westee.cake.generate.ChargeOptionMapper;
import com.westee.cake.generate.User;
import com.westee.cake.generate.UserMapper;
import com.westee.cake.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class ChargeService {
    private final ChargeMapper chargeMapper;
    private final UserMapper userMapper;
    private final WechatPayService wechatPayService;
    private final ChargeOptionMapper chargeOptionMapper;
    private final UserService userService;

    @Autowired
    public ChargeService(ChargeMapper chargeMapper, UserMapper userMapper, WechatPayService wechatPayService,
                         ChargeOptionMapper chargeOptionMapper, UserService userService) {
        this.chargeMapper = chargeMapper;
        this.userMapper = userMapper;
        this.wechatPayService = wechatPayService;
        this.chargeOptionMapper = chargeOptionMapper;
        this.userService = userService;
    }

    /**
     * 计算更新用户总余额
     * 新建一条充值记录
     *
     * @param chargeId 充值金额
     * @param userId   用户id
     * @return 充值记录
     */
    public PrepayWithRequestPaymentResponse charge(int chargeId, Long userId) throws Exception {
        ChargeOption chargeOption = chargeOptionMapper.selectByPrimaryKey(chargeId);
        BigDecimal amount = chargeOption.getAmount();
        User user = userMapper.selectByPrimaryKey(userId);
        String wxOutTradeNo = new Date().getTime() + "_charge_" + OrderService.generateRandomCode(10);
        String desc = "用户" + userId + "充值:" + amount + "元，赠送" + chargeOption.getGift() + "元";

        Charge charge = new Charge();
        charge.setAmount(amount);
        charge.setUserId(userId);
        charge.setWxOutTradeNo(wxOutTradeNo);
        charge.setRemark(wxOutTradeNo);
        charge.setPresent(chargeOption.getGift());
        charge.setStatus(ChargeStatus.WAIT.getName());
        charge.setChargeType(ChargeType.WECHAT.getName());
        charge.setCreatedAt(Utils.getNow());
        charge.setUpdatedAt(Utils.getNow());
        chargeMapper.insert(charge);
        // 发起微信支付请求
        try {
            return wechatPayService.prepayWithRequestPayment(
                    desc, wxOutTradeNo,
                    amount, user.getWxOpenId(), OrderType.CHARGE);
        } catch (HttpException e) {
            // 发送HTTP请求失败
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务返回状态小于200或大于等于300，例如500
            e.printStackTrace();
        } catch (MalformedMessageException e) {
            // 服务返回成功，返回体类型不合法，或者解析返回体失败
            e.printStackTrace();
        }
        throw HttpException.success("获取支付信息失败");
    }

    public PageResponse<Charge> doGetChargeList(Long userId, Integer pageNum, Integer pageSize, boolean isAdmin) {
        if (isAdmin) {
            userService.checkAdmin(userId);
        }
        ChargeExample chargeExample = new ChargeExample();
        if (!isAdmin) {
            chargeExample.createCriteria().andUserIdEqualTo(userId);
        }
        chargeExample.setOrderByClause("`CREATED_AT` DESC");
        long count = chargeMapper.countByExample(chargeExample);
        long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        PageHelper.startPage(pageNum, pageSize);
        List<Charge> charges = chargeMapper.selectByExample(chargeExample);
        charges.forEach(charge -> {
            User user = userService.getUserById(charge.getUserId());
            charge.setUsername(user.getNickname());
        });

        return PageResponse.pageData(pageNum, pageSize, totalPage, charges);
    }

    public Charge getChargeByOutTradeNo(String no) {
        ChargeExample chargeExample = new ChargeExample();
        chargeExample.createCriteria().andWxOutTradeNoEqualTo(no);
        List<Charge> charges = chargeMapper.selectByExample(chargeExample);
        return charges.get(0);
    }

    public void updateCharge(Charge chargeByOutTradeNo) {
        chargeMapper.updateByPrimaryKey(chargeByOutTradeNo);
    }
}
