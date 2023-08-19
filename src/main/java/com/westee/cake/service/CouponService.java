package com.westee.cake.service;


import com.github.pagehelper.PageHelper;
import com.westee.cake.entity.PageResponse;
import com.westee.cake.generate.Coupon;
import com.westee.cake.generate.CouponExample;
import com.westee.cake.generate.CouponMapper;
import com.westee.cake.generate.User;
import com.westee.cake.generate.UserCoupon;
import com.westee.cake.generate.UserCouponExample;
import com.westee.cake.generate.UserCouponMapper;
import com.westee.cake.generate.UserExample;
import com.westee.cake.generate.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CouponService {
    private final CouponMapper couponMapper;
    private final UserMapper userMapper;
    private final UserCouponMapper userCouponMapper;

    @Autowired
    public CouponService(CouponMapper couponMapper, UserMapper userMapper, UserCouponMapper userCouponMapper) {
        this.couponMapper = couponMapper;
        this.userMapper = userMapper;
        this.userCouponMapper = userCouponMapper;
    }

    /**
     * 计算更新用户总余额
     * 新建一条充值记录
     *
     * @param coupon  充值金额
     * @return       充值记录
     */
    public Coupon insertCoupon(Coupon coupon) {
        coupon.setCreatedAt(new Date());
        coupon.setUpdatedAt(new Date());
        couponMapper.insert(coupon);
        return coupon;
    }

    public PageResponse<Coupon> getCouponList(Integer pageNum, Integer pageSize) {
        CouponExample couponExample = new CouponExample();
        couponExample.setOrderByClause("`CREATED_AT` DESC");
        long count = couponMapper.countByExample(couponExample);
        long totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        PageHelper.startPage(pageNum, pageSize);
        List<Coupon> couponList = couponMapper.selectByExample(couponExample);

        return PageResponse.pageData(pageNum, pageSize, totalPage, couponList);
    }

    public void insertUserCoupon(long couponId) {
        UserExample userExample = new UserExample();
        List<User> users = userMapper.selectByExample(userExample);
        UserCoupon userCoupon = new UserCoupon();
        users.forEach(user -> {
            userCoupon.setCouponId(couponId);
            userCoupon.setUserId(user.getId());
            userCoupon.setUsed(false);
            userCoupon.setCreatedAt(new Date());
            userCoupon.setUpdatedAt(new Date());
            userCouponMapper.insert(userCoupon);
        });
    }

    public ArrayList<Coupon> getUserCouponList(Long userId, String status) {
        // 查出用户有多少未使用优惠券
        UserCouponExample userCouponExample = new UserCouponExample();
        userCouponExample.setOrderByClause("`CREATED_AT` DESC");
        UserCouponExample.Criteria criteria = userCouponExample.createCriteria();
        criteria.andUserIdEqualTo(userId);

         if(Objects.equals(CouponStatus.UNUSED.getName(), status)) {
            criteria.andUsedEqualTo(false);
        } else if(Objects.equals(CouponStatus.USED.getName(), status)) {
            criteria.andUsedEqualTo(true);
        }
        List<UserCoupon> userCoupons = userCouponMapper.selectByExample(userCouponExample);

        // 根据优惠券id 以及起止时间查询优惠券
        ArrayList<Coupon> coupons = new ArrayList<>();
        userCoupons.forEach(userCoupon -> {
            CouponExample couponExample = new CouponExample();
            couponExample.createCriteria().andStartDateLessThanOrEqualTo(new Date()).andEndDateGreaterThan(new Date())
                    .andIdEqualTo(userCoupon.getCouponId());
            List<Coupon> couponList = couponMapper.selectByExample(couponExample);
            if(!couponList.isEmpty()) {
                coupons.add(couponList.get(0));
            }
        });

        return coupons;
    }

    public long countUserCoupon(long userId) {
        UserCouponExample userCouponExample = new UserCouponExample();
        userCouponExample.createCriteria().andUserIdEqualTo(userId).andUsedEqualTo(false);
        List<UserCoupon> userCoupons = userCouponMapper.selectByExample(userCouponExample);

        List<Long> couponIdList = userCoupons.stream()
                .map(UserCoupon::getCouponId)
                .collect(Collectors.toList());
        if (couponIdList.isEmpty()) {
            return 0;
        }

        CouponExample couponExample = new CouponExample();
        couponExample.createCriteria().andIdIn(couponIdList).andEndDateGreaterThan(new Date());
        return couponMapper.countByExample(couponExample);
    }

    public enum CouponStatus {
        ALL,
        USED,
        UNUSED;

        public Object getName() {
            return name().toLowerCase();
        }
    }
}
