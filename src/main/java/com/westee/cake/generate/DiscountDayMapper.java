package com.westee.cake.generate;

import com.westee.cake.generate.DiscountDay;
import com.westee.cake.generate.DiscountDayExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DiscountDayMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    long countByExample(DiscountDayExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int deleteByExample(DiscountDayExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int insert(DiscountDay record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int insertSelective(DiscountDay record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    List<DiscountDay> selectByExample(DiscountDayExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    DiscountDay selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByExampleSelective(@Param("record") DiscountDay record, @Param("example") DiscountDayExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByExample(@Param("record") DiscountDay record, @Param("example") DiscountDayExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByPrimaryKeySelective(DiscountDay record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DISCOUNT_DAY
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByPrimaryKey(DiscountDay record);
}