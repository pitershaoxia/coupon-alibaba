/*
 Navicat Premium Data Transfer

 Source Server         : 本地MySQL
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : localhost:3306
 Source Schema         : imooc_coupon_data

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 04/03/2021 15:26:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `template_id` int(11) NOT NULL DEFAULT 0 COMMENT '关联优惠券模板的主键',
  `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '领取用户',
  `coupon_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '优惠券码',
  `assign_time` datetime(0) NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '领取时间',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '优惠券的状态(1-可用，2-已使用，3-过期)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_id`(`template_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '优惠券(用户领取的记录)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon
-- ----------------------------
INSERT INTO `coupon` VALUES (1, 17, 118, '100192000469204207', '2020-09-11 10:29:31', 1);
INSERT INTO `coupon` VALUES (10, 17, 118, '100109204042073816', '2020-09-11 10:54:14', 1);
INSERT INTO `coupon` VALUES (11, 17, 116, '100102040990549920', '2020-09-11 11:24:19', 1);
INSERT INTO `coupon` VALUES (12, 17, 116, '100192000469204207', '2020-09-11 11:27:50', 1);
INSERT INTO `coupon` VALUES (13, 17, 116, '100149200096589597', '2020-09-11 11:38:23', 1);
INSERT INTO `coupon` VALUES (14, 17, 116, '100102004915972990', '2020-09-30 15:30:04', 1);
INSERT INTO `coupon` VALUES (15, 17, 116, '100102090499932339', '2020-09-30 15:31:42', 1);
INSERT INTO `coupon` VALUES (16, 17, 116, '100190002475556452', '2020-09-30 15:35:49', 1);
INSERT INTO `coupon` VALUES (17, 17, 4396, '100140209073263306', '2020-09-30 15:36:44', 1);
INSERT INTO `coupon` VALUES (18, 17, 4396, '100109040255884180', '2020-09-30 16:04:46', 1);

-- ----------------------------
-- Table structure for coupon_path
-- ----------------------------
DROP TABLE IF EXISTS `coupon_path`;
CREATE TABLE `coupon_path`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '路径ID, 自增主键',
  `path_pattern` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '路径模式	',
  `http_method` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'http请求类型',
  `path_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '路径描述',
  `service_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '服务名',
  `op_mode` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '操作类型, READ/WRITE',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_path_pattern`(`path_pattern`) USING BTREE,
  INDEX `idx_servivce_name`(`service_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '路径信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon_path
-- ----------------------------
INSERT INTO `coupon_path` VALUES (10, '/coupon-template/template/build', 'POST', 'buildTemplate', 'eureka-client-coupon-template', 'WRITE');
INSERT INTO `coupon_path` VALUES (11, '/coupon-template/template/info', 'GET', 'info', 'eureka-client-coupon-template', 'READ');

-- ----------------------------
-- Table structure for coupon_role
-- ----------------------------
DROP TABLE IF EXISTS `coupon_role`;
CREATE TABLE `coupon_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID, 自增主键',
  `role_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `role_tag` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '角色TAG标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon_role
-- ----------------------------
INSERT INTO `coupon_role` VALUES (1, '管理员', 'ADMIN');
INSERT INTO `coupon_role` VALUES (2, '超级管理员', 'SUPER_ADMIN');
INSERT INTO `coupon_role` VALUES (3, '普通用户', 'CUSTOMER');

-- ----------------------------
-- Table structure for coupon_role_path_mapping
-- ----------------------------
DROP TABLE IF EXISTS `coupon_role_path_mapping`;
CREATE TABLE `coupon_role_path_mapping`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `role_id` int(11) NOT NULL DEFAULT 0 COMMENT '角色ID',
  `path_id` int(11) NOT NULL DEFAULT 0 COMMENT '路径ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE,
  INDEX `idx_path_id`(`path_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色路径映射表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon_role_path_mapping
-- ----------------------------
INSERT INTO `coupon_role_path_mapping` VALUES (1, 1, 10);
INSERT INTO `coupon_role_path_mapping` VALUES (2, 1, 11);
INSERT INTO `coupon_role_path_mapping` VALUES (3, 3, 11);

-- ----------------------------
-- Table structure for coupon_template
-- ----------------------------
DROP TABLE IF EXISTS `coupon_template`;
CREATE TABLE `coupon_template`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `available` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是可用状态; true: 可用, false: 不可用',
  `expired` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否过期; true: 是, false: 否',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '优惠券名称',
  `logo` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '优惠券 logo',
  `intro` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '优惠券描述',
  `category` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '优惠券分类',
  `product_line` int(11) NOT NULL DEFAULT 0 COMMENT '产品线',
  `coupon_count` int(11) NOT NULL DEFAULT 0 COMMENT '优惠券总数',
  `create_time` datetime(0) NOT NULL DEFAULT '0000-01-01 00:00:00' COMMENT '创建时间',
  `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '创建用户',
  `template_key` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '优惠券模板的编码',
  `target` int(11) NOT NULL DEFAULT 0 COMMENT '目标用户',
  `rule` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '优惠券规则: TemplateRule 的 json 表示',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE,
  INDEX `idx_category`(`category`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '优惠券模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon_template
-- ----------------------------
INSERT INTO `coupon_template` VALUES (12, 1, 1, '优惠券01', 'http://www.imooc.com', '这是第一张优惠券', '001', 1, 1000, '2020-08-19 11:47:06', 10001, '100120200819', 1, '{\"discount\":{\"base\":199,\"quota\":20},\"expiration\":{\"deadline\":1568082612129,\"gap\":1,\"period\":1},\"limitation\":1,\"usage\":{\"city\":\"深圳市\",\"goodsType\":\"[1,3]\",\"province\":\"广东省\"},\"weight\":\"[]\"}');
INSERT INTO `coupon_template` VALUES (13, 1, 1, '优惠券02', 'http://www.imooc.com', '这是第二张优惠券', '002', 1, 1000, '2020-08-19 11:49:31', 10001, '100220200819', 1, '{\"discount\":{\"base\":1,\"quota\":85},\"expiration\":{\"deadline\":1568082612129,\"gap\":1,\"period\":1},\"limitation\":1,\"usage\":{\"city\":\"桐城市\",\"goodsType\":\"[1,3]\",\"province\":\"安徽省\"},\"weight\":\"[\\\"1001201909060001\\\"]\"}');
INSERT INTO `coupon_template` VALUES (14, 1, 1, '优惠券03', 'http://www.imooc.com', '这是第三张优惠券', '003', 1, 1000, '2020-08-19 11:49:53', 10001, '100320200819', 1, '{\"discount\":{\"base\":1,\"quota\":5},\"expiration\":{\"deadline\":1568082612129,\"gap\":1,\"period\":1},\"limitation\":1,\"usage\":{\"city\":\"桐城市\",\"goodsType\":\"[1,3]\",\"province\":\"安徽省\"},\"weight\":\"[]\"}');
INSERT INTO `coupon_template` VALUES (17, 1, 0, 'nike篮球鞋专用券', 'https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1029818041,1523383330&fm=26&gp=0.jpg', 'k8篮球鞋', '001', 1, 1000, '2020-09-04 16:45:40', 9527, '100120200904', 1, '{\"discount\":{\"base\":799,\"quota\":60},\"expiration\":{\"deadline\":1630684800000,\"gap\":0,\"period\":1},\"limitation\":100,\"usage\":{\"city\":\"深圳市\",\"goodsType\":\"[1,4]\",\"province\":\"广东省\"},\"weight\":\"[\\\"\\\"]\"}');

-- ----------------------------
-- Table structure for coupon_user
-- ----------------------------
DROP TABLE IF EXISTS `coupon_user`;
CREATE TABLE `coupon_user`  (
  `id` int(200) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for coupon_user_role_mapping
-- ----------------------------
DROP TABLE IF EXISTS `coupon_user_role_mapping`;
CREATE TABLE `coupon_user_role_mapping`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户ID',
  `role_id` int(11) NOT NULL DEFAULT 0 COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `key_role_id`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色关系映射表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon_user_role_mapping
-- ----------------------------
INSERT INTO `coupon_user_role_mapping` VALUES (1, 15, 1);
INSERT INTO `coupon_user_role_mapping` VALUES (2, 16, 3);

SET FOREIGN_KEY_CHECKS = 1;
