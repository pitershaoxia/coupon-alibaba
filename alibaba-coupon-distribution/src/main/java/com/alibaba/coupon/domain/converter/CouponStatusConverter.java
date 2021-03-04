package com.alibaba.coupon.domain.converter;

import com.alibaba.coupon.domain.enums.CouponStatusEnums;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description: 优惠券状态类型转换器
 * @Author: LiuPing
 * @Time: 2020/9/23 0023 -- 14:23
 */
@MappedTypes(CouponStatusEnums.class)
public class CouponStatusConverter implements TypeHandler<CouponStatusEnums> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setParameter(PreparedStatement preparedStatement, int index, CouponStatusEnums couponStatusEnums, JdbcType jdbcType) throws SQLException {
        try {
            String value = objectMapper.writeValueAsString(couponStatusEnums.getCode());
            preparedStatement.setInt(index, Integer.parseInt(value));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CouponStatusEnums getResult(ResultSet resultSet, String param) throws SQLException {
        int code = resultSet.getInt(param);
        return parseInteger2CouponStatusEnums(code);
    }

    private CouponStatusEnums parseInteger2CouponStatusEnums(Integer code) {
        if (code == null) {
            return null;
        }
        return CouponStatusEnums.getCouponStatusByCode(code);
    }

    @Override
    public CouponStatusEnums getResult(ResultSet resultSet, int index) throws SQLException {
        int code = resultSet.getInt(index);
        return parseInteger2CouponStatusEnums(code);
    }

    @Override
    public CouponStatusEnums getResult(CallableStatement callableStatement, int index) throws SQLException {
        int code = callableStatement.getInt(index);
        return parseInteger2CouponStatusEnums(code);
    }

}
