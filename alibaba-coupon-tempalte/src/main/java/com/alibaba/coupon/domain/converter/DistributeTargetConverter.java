package com.alibaba.coupon.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.coupon.domain.enums.DistributionEnums;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description: 分发目标枚举属性转换器
 * @Author: LiuPing
 * @Time: 2020/9/17 0017 -- 10:44
 */
@MappedTypes(DistributionEnums.class)
public class DistributeTargetConverter implements TypeHandler<DistributionEnums> {

    ObjectMapper objectMapper = new ObjectMapper();
    /*插入数据 由javabean转换为数据库接收的类型*/

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, DistributionEnums distributionEnums, JdbcType jdbcType) throws SQLException {
        try {
            String code = objectMapper.writeValueAsString(distributionEnums.getCode());
            preparedStatement.setInt(i, Integer.parseInt(code));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public DistributionEnums getResult(ResultSet resultSet, String parameterName) throws SQLException {
        int code = resultSet.getInt(parameterName);
        return parseInteger2DistributionEnums(code);
    }

    private DistributionEnums parseInteger2DistributionEnums(Integer code) {
        DistributionEnums couponCategoryEnums = null;
        if (code == null) {
            return couponCategoryEnums;
        }
        return DistributionEnums.getDistributionByCode(code);
    }

    @Override
    public DistributionEnums getResult(ResultSet resultSet, int index) throws SQLException {
        int code = resultSet.getInt(index);
        return parseInteger2DistributionEnums(code);
    }

    @Override
    public DistributionEnums getResult(CallableStatement callableStatement, int index) throws SQLException {
        int code = callableStatement.getInt(index);
        return parseInteger2DistributionEnums(code);
    }
}
