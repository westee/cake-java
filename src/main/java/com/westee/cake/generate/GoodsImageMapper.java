package com.westee.cake.generate;

import com.westee.cake.generate.GoodsImage;
import com.westee.cake.generate.GoodsImageExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GoodsImageMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    long countByExample(GoodsImageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int deleteByExample(GoodsImageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int insert(GoodsImage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int insertSelective(GoodsImage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    List<GoodsImage> selectByExample(GoodsImageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    GoodsImage selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByExampleSelective(@Param("record") GoodsImage record, @Param("example") GoodsImageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByExample(@Param("record") GoodsImage record, @Param("example") GoodsImageExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByPrimaryKeySelective(GoodsImage record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_IMAGE
     *
     * @mbg.generated Wed Dec 06 14:12:31 CST 2023
     */
    int updateByPrimaryKey(GoodsImage record);
}