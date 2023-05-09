package com.westee.cake.generate;

import com.westee.cake.generate.GoodsTypes;
import com.westee.cake.generate.GoodsTypesExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GoodsTypesMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_TYPES
     *
     * @mbg.generated Tue May 09 10:08:12 CST 2023
     */
    long countByExample(GoodsTypesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_TYPES
     *
     * @mbg.generated Tue May 09 10:08:12 CST 2023
     */
    int deleteByExample(GoodsTypesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_TYPES
     *
     * @mbg.generated Tue May 09 10:08:12 CST 2023
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_TYPES
     *
     * @mbg.generated Tue May 09 10:08:12 CST 2023
     */
    int insert(GoodsTypes record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_TYPES
     *
     * @mbg.generated Tue May 09 10:08:12 CST 2023
     */
    int insertSelective(GoodsTypes record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_TYPES
     *
     * @mbg.generated Tue May 09 10:08:12 CST 2023
     */
    List<GoodsTypes> selectByExample(GoodsTypesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_TYPES
     *
     * @mbg.generated Tue May 09 10:08:12 CST 2023
     */
    GoodsTypes selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_TYPES
     *
     * @mbg.generated Tue May 09 10:08:12 CST 2023
     */
    int updateByExampleSelective(@Param("record") GoodsTypes record, @Param("example") GoodsTypesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_TYPES
     *
     * @mbg.generated Tue May 09 10:08:12 CST 2023
     */
    int updateByExample(@Param("record") GoodsTypes record, @Param("example") GoodsTypesExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_TYPES
     *
     * @mbg.generated Tue May 09 10:08:12 CST 2023
     */
    int updateByPrimaryKeySelective(GoodsTypes record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table GOODS_TYPES
     *
     * @mbg.generated Tue May 09 10:08:12 CST 2023
     */
    int updateByPrimaryKey(GoodsTypes record);
}