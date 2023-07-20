package com.westee.cake.generate;

import com.westee.cake.generate.ChargeOption;
import com.westee.cake.generate.ChargeOptionExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChargeOptionMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHARGE_OPTION
     *
     * @mbg.generated Thu Jul 20 15:47:23 CST 2023
     */
    long countByExample(ChargeOptionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHARGE_OPTION
     *
     * @mbg.generated Thu Jul 20 15:47:23 CST 2023
     */
    int deleteByExample(ChargeOptionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHARGE_OPTION
     *
     * @mbg.generated Thu Jul 20 15:47:23 CST 2023
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHARGE_OPTION
     *
     * @mbg.generated Thu Jul 20 15:47:23 CST 2023
     */
    int insert(ChargeOption record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHARGE_OPTION
     *
     * @mbg.generated Thu Jul 20 15:47:23 CST 2023
     */
    int insertSelective(ChargeOption record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHARGE_OPTION
     *
     * @mbg.generated Thu Jul 20 15:47:23 CST 2023
     */
    List<ChargeOption> selectByExample(ChargeOptionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHARGE_OPTION
     *
     * @mbg.generated Thu Jul 20 15:47:23 CST 2023
     */
    ChargeOption selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHARGE_OPTION
     *
     * @mbg.generated Thu Jul 20 15:47:23 CST 2023
     */
    int updateByExampleSelective(@Param("record") ChargeOption record, @Param("example") ChargeOptionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHARGE_OPTION
     *
     * @mbg.generated Thu Jul 20 15:47:23 CST 2023
     */
    int updateByExample(@Param("record") ChargeOption record, @Param("example") ChargeOptionExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHARGE_OPTION
     *
     * @mbg.generated Thu Jul 20 15:47:23 CST 2023
     */
    int updateByPrimaryKeySelective(ChargeOption record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CHARGE_OPTION
     *
     * @mbg.generated Thu Jul 20 15:47:23 CST 2023
     */
    int updateByPrimaryKey(ChargeOption record);
}