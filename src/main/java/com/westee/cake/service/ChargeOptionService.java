package com.westee.cake.service;

import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.ChargeOption;
import com.westee.cake.generate.ChargeOptionExample;
import com.westee.cake.generate.ChargeOptionMapper;
import com.westee.cake.util.Utils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ChargeOptionService {
    private final ChargeOptionMapper chargeOptionMapper;

    @Autowired
    public ChargeOptionService(ChargeOptionMapper chargeOptionMapper) {
        this.chargeOptionMapper = chargeOptionMapper;
    }


    public List<ChargeOption> getChargeOptionList() {
        ChargeOptionExample chargeOptionExample = new ChargeOptionExample();
        chargeOptionExample.createCriteria().andDeletedEqualTo(false);
        return chargeOptionMapper.selectByExample(chargeOptionExample);
    }

    @RequiresRoles("admin")
    public ChargeOption createChargeOption(ChargeOption chargeOption, long shopId, long userId) {
        chargeOption.setOwnerUsrId(userId);
        chargeOption.setShopId(shopId);
        chargeOption.setDeleted(false);
        chargeOption.setUpdatedAt(Utils.getNow());
        chargeOption.setCreatedAt(Utils.getNow());
        chargeOptionMapper.insert(chargeOption);
        return chargeOption;
    }

    @RequiresRoles("admin")
    public ChargeOption deleteChargeOption(int chargeOptionId, long userId) {
        ChargeOption chargeOption = chargeOptionMapper.selectByPrimaryKey(chargeOptionId);
        if (userId == chargeOption.getOwnerUsrId()) {
            ChargeOptionExample chargeOptionExample = new ChargeOptionExample();
            chargeOptionExample.createCriteria().andIdEqualTo(chargeOptionId);
            chargeOption.setDeleted(true);
            chargeOptionMapper.updateByExampleSelective(chargeOption, chargeOptionExample);
        } else {
            throw HttpException.forbidden("没有权限");
        }
        return chargeOption;
    }

    @RequiresRoles("admin")
    public ChargeOption updateChargeOption(ChargeOption chargeOption, Long userId) {
        ChargeOption chargeOptionResult = chargeOptionMapper.selectByPrimaryKey(chargeOption.getId());
        if (Objects.equals(userId, chargeOptionResult.getOwnerUsrId())) {
            ChargeOptionExample chargeOptionExample = new ChargeOptionExample();
            chargeOptionExample.createCriteria().andIdEqualTo(chargeOptionResult.getId());
            chargeOption.setUpdatedAt(Utils.getNow());
            chargeOptionMapper.updateByExampleSelective(chargeOption, chargeOptionExample);
        } else {
            throw HttpException.forbidden("没有权限");
        }
        return chargeOption;
    }
}
