package com.alibaba.coupon.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.coupon.domain.enums.ProductLineEnums;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description: 产品线枚举属性转换器
 * @Author: LiuPing
 * @Time: 2020/9/17 0017 -- 11:16
 */
@MappedTypes(ProductLineEnums.class)
public class ProductLineConverter implements TypeHandler<ProductLineEnums> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setParameter(PreparedStatement preparedStatement, int index, ProductLineEnums productLineEnums, JdbcType jdbcType) throws SQLException {
        try {
            String code = objectMapper.writeValueAsString(productLineEnums.getCode());
            preparedStatement.setInt(index, Integer.parseInt(code));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProductLineEnums getResult(ResultSet resultSet, String parameterName) throws SQLException {
        int code = resultSet.getInt(parameterName);
        return parseInteger2ProductLineEnums(code);
    }

    private ProductLineEnums parseInteger2ProductLineEnums(Integer code) {
        if (code == null) {
            return null;
        }
        return ProductLineEnums.getProductLineEnumsByCode(code);
    }

    @Override
    public ProductLineEnums getResult(ResultSet resultSet, int index) throws SQLException {
        int code = resultSet.getInt(index);
        return parseInteger2ProductLineEnums(code);
    }

    @Override
    public ProductLineEnums getResult(CallableStatement callableStatement, int index) throws SQLException {
        int coded = callableStatement.getInt(index);
        return parseInteger2ProductLineEnums(coded);
    }
}
