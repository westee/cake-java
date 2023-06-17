package com.westee.cake.generate;

import com.westee.cake.generate.OrderTable;
import com.westee.cake.generate.OrderTableExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderTableMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sat Jun 17 22:59:18 CST 2023
     */
    long countByExample(OrderTableExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sat Jun 17 22:59:18 CST 2023
     */
    int deleteByExample(OrderTableExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sat Jun 17 22:59:18 CST 2023
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sat Jun 17 22:59:18 CST 2023
     */
    int insert(OrderTable record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sat Jun 17 22:59:18 CST 2023
     */
    int insertSelective(OrderTable record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sat Jun 17 22:59:18 CST 2023
     */
    List<OrderTable> selectByExample(OrderTableExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sat Jun 17 22:59:18 CST 2023
     */
    OrderTable selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sat Jun 17 22:59:18 CST 2023
     */
    int updateByExampleSelective(@Param("record") OrderTable record, @Param("example") OrderTableExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sat Jun 17 22:59:18 CST 2023
     */
    int updateByExample(@Param("record") OrderTable record, @Param("example") OrderTableExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sat Jun 17 22:59:18 CST 2023
     */
    int updateByPrimaryKeySelective(OrderTable record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Sat Jun 17 22:59:18 CST 2023
     */
    int updateByPrimaryKey(OrderTable record);
}