package com.westee.cake.generate;

import com.westee.cake.generate.ExpressGoodsType;
import com.westee.cake.generate.ExpressGoodsTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExpressGoodsTypeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPRESS_GOODS_TYPE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    long countByExample(ExpressGoodsTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPRESS_GOODS_TYPE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int deleteByExample(ExpressGoodsTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPRESS_GOODS_TYPE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPRESS_GOODS_TYPE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int insert(ExpressGoodsType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPRESS_GOODS_TYPE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int insertSelective(ExpressGoodsType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPRESS_GOODS_TYPE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    List<ExpressGoodsType> selectByExample(ExpressGoodsTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPRESS_GOODS_TYPE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    ExpressGoodsType selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPRESS_GOODS_TYPE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByExampleSelective(@Param("record") ExpressGoodsType record, @Param("example") ExpressGoodsTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPRESS_GOODS_TYPE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByExample(@Param("record") ExpressGoodsType record, @Param("example") ExpressGoodsTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPRESS_GOODS_TYPE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByPrimaryKeySelective(ExpressGoodsType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table EXPRESS_GOODS_TYPE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByPrimaryKey(ExpressGoodsType record);
}