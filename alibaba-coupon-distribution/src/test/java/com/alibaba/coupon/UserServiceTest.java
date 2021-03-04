package com.alibaba.coupon;


import com.alibaba.coupon.domain.dto.AcquireTemplateRequestDTO;
import com.alibaba.coupon.module.service.DistributeService;
import com.alibaba.fastjson.JSON;
import com.simple.coupon.domain.dto.CouponTemplateDTO;
import com.simple.coupon.domain.dto.SettlementInfoDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @Author: LiuPing
 * @Time: 2020/9/30 0030 -- 14:28
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    private Long fakeUserId = 4396L;
//    private Long fakeUserId = 116L;

    @Autowired
    private DistributeService distributeService;

    @Test
    public void test() {

//        System.out.println(
//                JSON.toJSONString(distributeService.findCouponByStatus(
//                        fakeUserId,
//                        CouponStatusEnums.USABLE.getCode()
//                ))
//        );


    }

    @Test
    public void testFindAvailableTemplate() {

        System.out.println(JSON.toJSONString(
                distributeService.findAvailableTemplate(fakeUserId)
        ));
    }

    @Test
    public void testGet() {

        System.out.println(
                JSON.toJSONString(
                        distributeService.acquireTemplate(fakeRequestDTO(fakeUserId))
                )
        );

    }

    private AcquireTemplateRequestDTO fakeRequestDTO(Long fakeUserId) {
        String data = "{\"category\":\"满减券\",\"desc\":\"k8篮球鞋\",\"id\":17,\"key\":\"100120200904\",\"logo\":\"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1029818041,1523383330&fm=26&gp=0.jpg\",\"name\":\"nike篮球鞋专用券\",\"productLine\":\"天猫\",\"rule\":{\"discount\":{\"base\":799,\"quota\":60},\"expiration\":{\"deadline\":1630684800000,\"gap\":0,\"period\":1},\"limitation\":100,\"usage\":{\"city\":\"深圳市\",\"goodsType\":\"[1,4]\",\"province\":\"广东省\"},\"weight\":\"[\\\"\\\"]\"},\"target\":\"单个用户\"}";
        CouponTemplateDTO templateDTO = JSON.parseObject(data, CouponTemplateDTO.class);
        return
                AcquireTemplateRequestDTO.builder()
                        .userId(fakeUserId)
                        .templateDTO(templateDTO)
                        .build();
    }

    @Test
    public void testFee() {
        System.out.println(
                JSON.toJSONString(
                        distributeService.settlement(fakeSettlementRequestDTO())
                )
        );
    }

    private SettlementInfoDTO fakeSettlementRequestDTO() {
        String data = "{\n" +
                "\t\"cost\": 1010.56,\n" +
                "\t\"couponAndTemplateInfos\": [{\n" +
                "\t\t\"id\": 18,\n" +
                "\t\t\"template\": {\n" +
                "\t\t\t\"category\": \"1\",\n" +
                "\t\t\t\"desc\": \"k8篮球鞋\",\n" +
                "\t\t\t\"id\": 17,\n" +
                "\t\t\t\"key\": \"100120200904\",\n" +
                "\t\t\t\"logo\": \"https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1029818041,1523383330&fm=26&gp=0.jpg\",\n" +
                "\t\t\t\"name\": \"nike篮球鞋专用券\",\n" +
                "\t\t\t\"productLine\": \"天猫\",\n" +
                "\t\t\t\"rule\": {\n" +
                "\t\t\t\t\"discount\": {\n" +
                "\t\t\t\t\t\"base\": 199,\n" +
                "\t\t\t\t\t\"quota\": 20\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"expiration\": {\n" +
                "\t\t\t\t\t\"deadline\": 1630684800000,\n" +
                "\t\t\t\t\t\"gap\": 0,\n" +
                "\t\t\t\t\t\"period\": 1\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"limitation\": 100,\n" +
                "\t\t\t\t\"usage\": {\n" +
                "\t\t\t\t\t\"city\": \"深圳市\",\n" +
                "\t\t\t\t\t\"goodsType\": \"[1,4]\",\n" +
                "\t\t\t\t\t\"province\": \"广东省\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"weight\": \"[\\\"\\\"]\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"target\": \"单个用户\"\n" +
                "\t\t}\n" +
                "\t}],\n" +
                "\t\"employ\": false,\n" +
                "\t\"goodsInfos\": [{\n" +
                "\t\t\"count\": 2,\n" +
                "\t\t\"price\": 10.88,\n" +
                "\t\t\"type\": 1\n" +
                "\t}, {\n" +
                "\t\t\"count\": 10,\n" +
                "\t\t\"price\": 20.88,\n" +
                "\t\t\"type\": 1\n" +
                "\t}],\n" +
                "\t\"userId\": 4396\n" +
                "}";
        return JSON.parseObject(data, SettlementInfoDTO.class);
    }
}

