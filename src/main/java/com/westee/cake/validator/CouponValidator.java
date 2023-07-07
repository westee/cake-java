package com.westee.cake.validator;

import com.westee.cake.generate.Coupon;

import java.math.BigDecimal;
import java.util.Date;

public class CouponValidator {

    public static boolean validateCouponParameters(Coupon coupon) {
        return validateName(coupon.getName()) && validateDiscountType(coupon.getDiscountType()) &&
                validateDiscountByType(coupon.getDiscountType(), coupon.getDiscountAmount(), coupon.getDiscountPercentage())
                && validateMinimumAmount(coupon.getMinimumAmount()) && validateStartDate(coupon.getStartDate()) && validateEndDate(coupon.getEndDate());
    }

    private static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    private static boolean validateDiscountType(String discountType) {
        return discountType != null && (discountType.equals("AMOUNT") || discountType.equals("PERCENTAGE"));
    }

    private static boolean validateDiscountByType(String discountType, BigDecimal discountAmount, BigDecimal discountPercentage) {
        if(discountType.equals("AMOUNT")) {
            return validateDiscountAmount(discountAmount);
        } else  {
            return validateDiscountPercentage(discountPercentage);
        }
    }
    private static boolean validateDiscountPercentage(BigDecimal discountAmount) {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    private static boolean validateDiscountAmount(BigDecimal discountAmount) {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    private static boolean validateMinimumAmount(BigDecimal minimumAmount) {
        return minimumAmount == null || minimumAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    private static boolean validateStartDate(Date startDate) {
        return startDate != null && startDate.before(new Date());
    }

    private static boolean validateEndDate(Date endDate) {
        return endDate != null && endDate.after(new Date());
    }
}
