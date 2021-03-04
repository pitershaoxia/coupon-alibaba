package com.alibaba.coupon.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.coupon.domain.enums.CouponCategoryEnums;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description: 优惠券分类枚举属性转换器
 * @Author: LiuPing
 * @Time: 2020/9/17 0017 -- 10:02
 */
@MappedTypes(CouponCategoryEnums.class)
public class CouponCategoryConverter implements TypeHandler<CouponCategoryEnums> {

    ObjectMapper objectMapper = new ObjectMapper();
    /*插入数据 由javabean转换为数据库接收的类型*/


    @Override
    public void setParameter(PreparedStatement preparedStatement, int index, CouponCategoryEnums categoryEnums, JdbcType jdbcType) throws SQLException {
        try {
            String value = objectMapper.writeValueAsString(categoryEnums.getCode());
            switch (value) {
                case "1":
                    preparedStatement.setString(index, "001");
                    break;
                case "2":
                    preparedStatement.setString(index, "002");
                    break;
                case "3":
                    preparedStatement.setString(index, "003");
                    break;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CouponCategoryEnums getResult(ResultSet resultSet, String parameterName) throws SQLException {
        int code = resultSet.getInt(parameterName);
        return parseString2CouponCategoryEnums(code);
    }

    private CouponCategoryEnums parseString2CouponCategoryEnums(Integer code) {
        CouponCategoryEnums couponCategoryEnums = null;
        if (code == null) {
            return couponCategoryEnums;
        }
        return CouponCategoryEnums.getCouponByCode(code);
    }

    @Override
    public CouponCategoryEnums getResult(ResultSet resultSet, int index) throws SQLException {
        int code = resultSet.getInt(index);
        return parseString2CouponCategoryEnums(code);
    }

    @Override
    public CouponCategoryEnums getResult(CallableStatement callableStatement, int index) throws SQLException {
        int code = callableStatement.getInt(index);
        return parseString2CouponCategoryEnums(code);
    }

}
