package com.alibaba.coupon.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.coupon.domain.vo.TemplateRuleVo;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description:
 * @Author: LiuPing
 * @Time: 2020/9/17 0017 -- 11:46
 */
@MappedTypes(TemplateRuleVo.class)
public class RuleConverter implements TypeHandler<TemplateRuleVo> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setParameter(PreparedStatement preparedStatement, int index, TemplateRuleVo ruleVo, JdbcType jdbcType) throws SQLException {
        try {
            String value = objectMapper.writeValueAsString(ruleVo);
            preparedStatement.setString(index, value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TemplateRuleVo getResult(ResultSet resultSet, String param) throws SQLException {
        String value = resultSet.getString(param);
        return parseString2TemplateRuleVo(value);
    }

    private TemplateRuleVo parseString2TemplateRuleVo(String value) {
        TemplateRuleVo vo = new TemplateRuleVo();
        if (value == null) {
            return vo;
        }
        try {
            vo = objectMapper.readValue(value, TemplateRuleVo.class);
//            vo = JSON.parseObject(value, TemplateRuleVo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

    @Override
    public TemplateRuleVo getResult(ResultSet resultSet, int index) throws SQLException {
        String value = resultSet.getString(index);
        return parseString2TemplateRuleVo(value);
    }

    @Override
    public TemplateRuleVo getResult(CallableStatement callableStatement, int index) throws SQLException {
        String value = callableStatement.getString(index);
        return parseString2TemplateRuleVo(value);
    }

}
