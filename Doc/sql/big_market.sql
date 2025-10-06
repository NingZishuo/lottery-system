/*
SQLyog Community v13.1.7 (64 bit)
MySQL - 8.0.32 : Database - big_market
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`big_market` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `big_market`;

/*Table structure for table `award` */

DROP TABLE IF EXISTS `award`;

CREATE TABLE `award` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `award_id` int NOT NULL COMMENT '抽奖奖品ID - 内部流转使用',
  `award_key` varchar(32) NOT NULL COMMENT '奖品对接标识 - 每一个都是一个对应的发奖策略',
  `award_config` varchar(32) NOT NULL COMMENT '奖品配置信息',
  `award_desc` varchar(128) NOT NULL COMMENT '奖品内容描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `award` */

insert  into `award`(`id`,`award_id`,`award_key`,`award_config`,`award_desc`,`create_time`,`update_time`) values 
(1,101,'user_credit_random','1,500','用户积分【优先透彻规则范围，如果没有则走配置】','2025-07-01 15:25:19','2025-09-23 00:50:44'),
(2,102,'keyboard','keyboard','keyboard 实物奖励','2025-07-01 15:25:19','2025-09-23 00:35:34'),
(3,103,'chatgpt_use_count','10','ChatGPT-4 增加使用次数','2025-07-01 15:25:19','2025-09-23 00:25:10'),
(4,104,'gemini_use_count','10','Gemini 增加使用次数','2025-07-01 15:25:19','2025-09-23 00:28:02'),
(5,105,'openai_model','chatgpt','ChatGPT 增加模型','2025-07-01 15:25:19','2025-09-25 21:40:58'),
(6,106,'openai_model','gemini','gemini 增加模型','2025-07-01 15:25:19','2025-09-23 00:23:30'),
(7,107,'iphone_17','iPhone_17','iPhone-17 实物奖励','2025-07-01 15:25:19','2025-09-23 01:22:50'),
(8,108,'gpu','RTX_5060Ti','RTX_5060Ti 实物奖励','2025-07-01 15:25:19','2025-09-23 01:22:53'),
(9,109,'car','Car','Car 实物奖励','2025-07-01 15:25:19','2025-09-25 22:00:00');

/*Table structure for table `daily_behavior_rebate` */

DROP TABLE IF EXISTS `daily_behavior_rebate`;

CREATE TABLE `daily_behavior_rebate` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `behavior_type` varchar(16) NOT NULL COMMENT '行为类型（sign 签到、openai_pay 支付）',
  `rebate_desc` varchar(128) NOT NULL COMMENT '返利描述',
  `rebate_type` varchar(16) NOT NULL COMMENT '返利类型（sku 活动库存充值商品、integral 用户活动积分）',
  `rebate_config` varchar(32) NOT NULL COMMENT '返利配置',
  `state` varchar(12) NOT NULL COMMENT '状态（open 开启、close 关闭）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_behavior_type` (`behavior_type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日常行为返利活动配置';

/*Data for the table `daily_behavior_rebate` */

insert  into `daily_behavior_rebate`(`id`,`behavior_type`,`rebate_desc`,`rebate_type`,`rebate_config`,`state`,`create_time`,`update_time`) values 
(1,'sign','sign-sku','sku','1001','open','2025-09-08 20:33:10','2025-09-09 22:59:01'),
(2,'sign','sign-integral','integral','15','open','2025-09-08 20:33:37','2025-09-09 22:59:04');

/*Table structure for table `raffle_activity` */

DROP TABLE IF EXISTS `raffle_activity`;

CREATE TABLE `raffle_activity` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `activity_name` varchar(64) NOT NULL COMMENT '活动名称',
  `activity_desc` varchar(128) NOT NULL COMMENT '活动描述',
  `begin_date_time` datetime NOT NULL COMMENT '开始时间',
  `end_date_time` datetime NOT NULL COMMENT '结束时间',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `state` varchar(8) NOT NULL COMMENT '活动状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_activity_id` (`activity_id`),
  KEY `idx_begin_date_time` (`begin_date_time`),
  KEY `idx_end_date_time` (`end_date_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='抽奖活动表';

/*Data for the table `raffle_activity` */

insert  into `raffle_activity`(`id`,`activity_id`,`activity_name`,`activity_desc`,`begin_date_time`,`end_date_time`,`strategy_id`,`state`,`create_time`,`update_time`) values 
(1,101,'activity_1','1号activity','2025-08-01 17:54:55','2027-10-26 17:55:05',100001,'open','2025-08-31 17:56:02','2025-09-23 00:05:22');

/*Table structure for table `raffle_activity_count` */

DROP TABLE IF EXISTS `raffle_activity_count`;

CREATE TABLE `raffle_activity_count` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `activity_count_id` bigint NOT NULL COMMENT '活动次数编号',
  `total_count` int NOT NULL COMMENT '总次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_activity_count_id` (`activity_count_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='抽奖活动次数配置表';

/*Data for the table `raffle_activity_count` */

insert  into `raffle_activity_count`(`id`,`activity_count_id`,`total_count`,`create_time`,`update_time`) values 
(1,11,1,'2025-08-31 17:57:07','2025-08-31 17:57:07'),
(2,12,5,'2025-09-21 16:11:33','2025-09-21 16:11:33'),
(3,13,10,'2025-09-21 16:12:13','2025-09-21 16:12:13');

/*Table structure for table `raffle_activity_sku` */

DROP TABLE IF EXISTS `raffle_activity_sku`;

CREATE TABLE `raffle_activity_sku` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `sku` bigint NOT NULL COMMENT '商品sku - 把每一个组合当做一个商品',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `activity_count_id` bigint NOT NULL COMMENT '活动个人参与次数ID',
  `stock_count` int NOT NULL COMMENT '商品库存',
  `stock_count_surplus` int NOT NULL COMMENT '剩余库存',
  `product_price` decimal(10,2) NOT NULL COMMENT '商品金额【积分】',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_sku` (`sku`),
  KEY `idx_activity_id_activity_count_id` (`activity_id`,`activity_count_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `raffle_activity_sku` */

insert  into `raffle_activity_sku`(`id`,`sku`,`activity_id`,`activity_count_id`,`stock_count`,`stock_count_surplus`,`product_price`,`create_time`,`update_time`) values 
(1,1001,101,11,100,91,5.11,'2025-08-31 17:57:49','2025-10-01 07:46:05'),
(2,1002,101,12,80,71,10.56,'2025-09-21 16:11:23','2025-09-28 19:05:37'),
(3,1003,101,13,50,13,15.78,'2025-09-21 16:11:51','2025-10-01 08:37:35');

/*Table structure for table `rule_tree` */

DROP TABLE IF EXISTS `rule_tree`;

CREATE TABLE `rule_tree` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `strategy_id` int DEFAULT NULL COMMENT '策略ID',
  `tree_id` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规则树ID',
  `tree_name` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规则树名',
  `tree_desc` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规则树描述',
  `tree_root_rule_node` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规则树根节点',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `rule_tree` */

insert  into `rule_tree`(`id`,`strategy_id`,`tree_id`,`tree_name`,`tree_desc`,`tree_root_rule_node`,`create_time`,`update_time`) values 
(1,100001,'tree_lock','规则树','规则树','rule_lock','2025-08-21 22:47:53','2025-08-21 22:47:53');

/*Table structure for table `rule_tree_node` */

DROP TABLE IF EXISTS `rule_tree_node`;

CREATE TABLE `rule_tree_node` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `tree_id` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自增树ID',
  `rule_model` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点模型',
  `rule_desc` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点描述',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `rule_tree_node` */

insert  into `rule_tree_node`(`id`,`tree_id`,`rule_model`,`rule_desc`,`create_time`,`update_time`) values 
(1,'tree_lock','rule_lock','抽奖N次后解锁','2025-08-21 22:58:38','2025-08-21 22:58:38'),
(2,'tree_lock','rule_luck_award','兜底奖品随机积分','2025-08-21 22:58:44','2025-08-21 22:58:44'),
(3,'tree_lock','rule_stock','库存扣减原则','2025-08-21 22:59:23','2025-08-21 22:59:23');

/*Table structure for table `rule_tree_node_line` */

DROP TABLE IF EXISTS `rule_tree_node_line`;

CREATE TABLE `rule_tree_node_line` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `tree_id` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自增树ID',
  `rule_node_from` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自身节点',
  `rule_node_to` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目标节点',
  `rule_limit_type` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '限定值要求',
  `rule_logic_check_type` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '限定值',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `rule_tree_node_line` */

insert  into `rule_tree_node_line`(`id`,`tree_id`,`rule_node_from`,`rule_node_to`,`rule_limit_type`,`rule_logic_check_type`,`create_time`,`update_time`) values 
(1,'tree_lock','rule_lock','rule_stock','EQUAL','ALLOW','2025-08-21 23:02:57','2025-08-21 23:02:57'),
(2,'tree_lock','rule_lock','rule_luck_award','EQUAL','TAKE_OVER','2025-08-21 23:02:58','2025-08-21 23:02:58'),
(3,'tree_lock','rule_stock','rule_luck_award','EQUAL','TAKE_OVER','2025-08-21 23:02:59','2025-08-21 23:02:59');

/*Table structure for table `strategy` */

DROP TABLE IF EXISTS `strategy`;

CREATE TABLE `strategy` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `strategy_id` int NOT NULL COMMENT '抽奖策略ID',
  `strategy_desc` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '抽奖策略描述',
  `rule_models` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '策略模型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `strategy` */

insert  into `strategy`(`id`,`strategy_id`,`strategy_desc`,`rule_models`,`create_time`,`update_time`) values 
(1,100001,'抽奖策略A','rule_black_list,rule_white_list,rule_weight','2025-06-30 16:54:29','2025-06-30 16:54:29');

/*Table structure for table `strategy_award` */

DROP TABLE IF EXISTS `strategy_award`;

CREATE TABLE `strategy_award` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `strategy_id` int NOT NULL COMMENT '抽奖策略ID',
  `award_id` int NOT NULL COMMENT '抽奖奖品ID',
  `award_title` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '抽奖奖品标题',
  `award_subtitle` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '抽奖奖品副标题',
  `award_count` int NOT NULL COMMENT '奖品库存总量',
  `award_count_surplus` int NOT NULL COMMENT '奖品库存剩余',
  `award_rate` decimal(6,4) NOT NULL COMMENT '奖品中奖概率',
  `sort` int unsigned NOT NULL COMMENT '排序',
  `rule_models` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规则模型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `strategy_award` */

insert  into `strategy_award`(`id`,`strategy_id`,`award_id`,`award_title`,`award_subtitle`,`award_count`,`award_count_surplus`,`award_rate`,`sort`,`rule_models`,`create_time`,`update_time`) values 
(1,100001,101,'Random credit',NULL,80000,79928,0.6000,1,'rule_luck_award','2025-06-30 19:59:19','2025-10-01 08:06:40'),
(2,100001,102,'keyBoard',NULL,10000,9982,0.2000,2,'rule_luck_award','2025-06-30 19:59:19','2025-10-01 08:06:25'),
(3,100001,103,'10 uses of ChatGPT',NULL,5000,4983,0.1000,3,'rule_luck_award','2025-06-30 19:59:19','2025-09-28 19:15:16'),
(4,100001,104,'10 uses of Gemini',NULL,4000,3993,0.0600,4,'rule_luck_award','2025-06-30 19:59:19','2025-10-01 08:05:55'),
(5,100001,105,'Add ChatGPT model',NULL,600,600,0.0300,5,'rule_luck_award','2025-06-30 19:59:19','2025-06-30 19:59:19'),
(6,100001,106,'Add Gemini model',NULL,200,199,0.0050,6,'rule_luck_award','2025-06-30 19:59:19','2025-09-28 19:14:01'),
(7,100001,107,'iPhone 17','抽奖1次后解锁',200,199,0.0020,7,'rule_lock,rule_luck_award','2025-06-30 19:59:19','2025-06-30 19:59:19'),
(8,100001,108,'RTX 5060Ti','抽奖2次后解锁',199,198,0.0020,8,'rule_lock,rule_luck_award','2025-06-30 19:59:19','2025-09-28 19:08:59'),
(9,100001,109,'Car','抽奖6次后解锁',1,1,0.0010,9,'rule_lock,rule_luck_award','2025-06-30 19:59:19','2025-06-30 19:59:19');

/*Table structure for table `strategy_black_list` */

DROP TABLE IF EXISTS `strategy_black_list`;

CREATE TABLE `strategy_black_list` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `strategy_id` int NOT NULL COMMENT '抽奖策略ID',
  `award_id` int NOT NULL COMMENT '固定奖品ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `strategy_black_list` */

insert  into `strategy_black_list`(`id`,`strategy_id`,`award_id`,`user_id`,`create_time`,`update_time`) values 
(1,100001,101,1,'2025-08-21 22:36:24','2025-08-21 22:36:24'),
(2,100001,101,2,'2025-08-21 22:36:24','2025-08-21 22:36:24'),
(3,100001,101,3,'2025-08-21 22:36:24','2025-08-21 22:36:24');

/*Table structure for table `strategy_rule` */

DROP TABLE IF EXISTS `strategy_rule`;

CREATE TABLE `strategy_rule` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `strategy_id` int NOT NULL COMMENT '抽奖策略ID',
  `award_id` int DEFAULT NULL COMMENT '抽奖奖品ID',
  `rule_type` int NOT NULL DEFAULT '0' COMMENT '抽奖规则类型[1-策略规则 2-奖品规则]',
  `rule_model` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '抽奖规则类型[rule_lock]',
  `rule_value` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '抽奖规则比值',
  `rule_desc` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '抽奖规则描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `strategy_rule` */

insert  into `strategy_rule`(`id`,`strategy_id`,`award_id`,`rule_type`,`rule_model`,`rule_value`,`rule_desc`,`create_time`,`update_time`) values 
(16,100001,101,2,'rule_luck_award','1,10','兜底奖品10以内随机积分','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(17,100001,102,2,'rule_luck_award','1,20','兜底奖品20以内随机积分','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(18,100001,103,2,'rule_luck_award','1,30','兜底奖品30以内随机积分','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(19,100001,104,2,'rule_luck_award','1,40','兜底奖品40以内随机积分','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(20,100001,105,2,'rule_luck_award','1,50','兜底奖品50以内随机积分','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(21,100001,106,2,'rule_luck_award','1,60','兜底奖品60以内随机积分','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(22,100001,107,2,'rule_luck_award','1,100','兜底奖品100以内随机积分','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(23,100001,108,2,'rule_luck_award','1,100','兜底奖品100以内随机积分','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(24,100001,107,2,'rule_lock','1','抽奖1次后解锁','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(25,100001,108,2,'rule_lock','2','抽奖2次后解锁','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(26,100001,109,2,'rule_lock','6','抽奖6次后解锁','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(27,100001,NULL,1,'rule_weight','30:103,104,105,106,107,108,109 40:105,106,107,108,109 50:107,108,109','保底系统','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(28,100001,NULL,1,'rule_black_list','1,5','黑名单抽奖，积分兜底','2025-06-30 20:09:36','2025-06-30 20:09:36'),
(29,100001,NULL,1,'rule_white_list','','白名单,最低保底范围','2025-08-17 22:43:03','2025-08-17 22:43:03'),
(31,100001,109,2,'rule_luck_award','1,100','兜底奖品100以内随机积分','2025-09-23 00:49:21','2025-09-23 00:49:21');

/*Table structure for table `strategy_white_list` */

DROP TABLE IF EXISTS `strategy_white_list`;

CREATE TABLE `strategy_white_list` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `strategy_id` int NOT NULL COMMENT '抽奖策略ID',
  `rule_weight` int NOT NULL COMMENT '最低保底门槛奖励',
  `user_id` int NOT NULL COMMENT '用户ID',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `strategy_white_list` */

insert  into `strategy_white_list`(`id`,`strategy_id`,`rule_weight`,`user_id`,`create_time`,`update_time`) values 
(1,100001,50,10,'2025-08-21 22:37:13','2025-08-21 22:37:13');

/*Table structure for table `task` */

DROP TABLE IF EXISTS `task`;

CREATE TABLE `task` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `queue` varchar(32) NOT NULL COMMENT '消息主题',
  `message_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '消息编号',
  `message` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息主体',
  `state` varchar(16) NOT NULL DEFAULT 'create' COMMENT '任务状态；create-创建、completed-完成、fail-失败',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_message_id` (`message_id`),
  KEY `idx_state` (`state`),
  KEY `idx_create_time` (`update_time`)
) ENGINE=InnoDB AUTO_INCREMENT=270 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务表，发送MQ';

/*Data for the table `task` */

insert  into `task`(`id`,`queue`,`message_id`,`message`,`state`,`create_time`,`update_time`) values 
(54,'credit_adjust_success','86850222701','{\"data\":{\"orderId\":\"319008686635\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-27 18:44:02\",\"userId\":\"zishuoning\"},\"messageId\":\"86850222701\",\"timestamp\":1758969846638}','completed','2025-09-27 18:44:10','2025-09-27 18:44:12'),
(55,'credit_adjust_success','69917253675','{\"data\":{\"orderId\":\"534139279618\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-27 18:44:18\",\"userId\":\"zishuoning\"},\"messageId\":\"69917253675\",\"timestamp\":1758969861957}','completed','2025-09-27 18:44:23','2025-09-27 18:44:25'),
(56,'credit_adjust_success','57280957293','{\"data\":{\"orderId\":\"801211710635\",\"outBusinessNo\":\"zishuoning_creditPay-1001_2025-09-27 18:46:06\",\"userId\":\"zishuoning\"},\"messageId\":\"57280957293\",\"timestamp\":1758969969625}','completed','2025-09-27 18:46:13','2025-09-27 18:46:13'),
(57,'send_rebate','72570343869','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"1001\",\"rebateDesc\":\"sign-sku\",\"rebateTypeVO\":\"SKU\"},\"orderId\":\"249154148764\",\"outBusinessNo\":\"zishuoning_sign-sku_2025-09-27\",\"userId\":\"zishuoning\"},\"messageId\":\"72570343869\",\"timestamp\":1758969996005}','completed','2025-09-27 18:46:37','2025-09-27 18:46:38'),
(58,'send_rebate','22337074568','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"15\",\"rebateDesc\":\"sign-integral\",\"rebateTypeVO\":\"INTEGRAL\"},\"orderId\":\"169764221616\",\"outBusinessNo\":\"zishuoning_sign-integral_2025-09-27\",\"userId\":\"zishuoning\"},\"messageId\":\"22337074568\",\"timestamp\":1758969996010}','completed','2025-09-27 18:46:41','2025-09-27 18:46:42'),
(59,'send_rebate','52146735167','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"1001\",\"rebateDesc\":\"sign-sku\",\"rebateTypeVO\":\"SKU\"},\"orderId\":\"924207658097\",\"outBusinessNo\":\"zishuoning_sign-sku_2025-09-27\",\"userId\":\"zishuoning\"},\"messageId\":\"52146735167\",\"timestamp\":1758970688801}','completed','2025-09-27 18:58:09','2025-09-27 18:58:10'),
(60,'send_rebate','72058246736','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"15\",\"rebateDesc\":\"sign-integral\",\"rebateTypeVO\":\"INTEGRAL\"},\"orderId\":\"713843108086\",\"outBusinessNo\":\"zishuoning_sign-integral_2025-09-27\",\"userId\":\"zishuoning\"},\"messageId\":\"72058246736\",\"timestamp\":1758970688803}','completed','2025-09-27 18:58:11','2025-09-27 18:58:11'),
(61,'credit_adjust_success','45044912445','{\"data\":{\"orderId\":\"713030040556\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-27 19:16:37\",\"userId\":\"zishuoning\"},\"messageId\":\"45044912445\",\"timestamp\":1758971801288}','completed','2025-09-27 19:16:42','2025-09-27 19:16:44'),
(62,'send_award','21015009410','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"017524033213_0\",\"userId\":\"zishuoning\"},\"messageId\":\"21015009410\",\"timestamp\":1758972734284}','completed','2025-09-27 19:32:16','2025-09-27 19:32:16'),
(63,'send_award','73618287164','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"017524033213_1\",\"userId\":\"zishuoning\"},\"messageId\":\"73618287164\",\"timestamp\":1758972734285}','completed','2025-09-27 19:32:16','2025-09-27 19:32:16'),
(64,'send_award','10383168444','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"017524033213_2\",\"userId\":\"zishuoning\"},\"messageId\":\"10383168444\",\"timestamp\":1758972734285}','completed','2025-09-27 19:32:17','2025-09-27 19:32:18'),
(65,'send_award','86606091522','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"017524033213_3\",\"userId\":\"zishuoning\"},\"messageId\":\"86606091522\",\"timestamp\":1758972734285}','completed','2025-09-27 19:32:18','2025-09-27 19:32:19'),
(66,'send_award','89091418360','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"017524033213_4\",\"userId\":\"zishuoning\"},\"messageId\":\"89091418360\",\"timestamp\":1758972734285}','completed','2025-09-27 19:32:19','2025-09-27 19:32:19'),
(67,'send_award','27862166355','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"017524033213_5\",\"userId\":\"zishuoning\"},\"messageId\":\"27862166355\",\"timestamp\":1758972734285}','completed','2025-09-27 19:32:21','2025-09-27 19:32:21'),
(68,'send_award','43657326693','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"017524033213_6\",\"userId\":\"zishuoning\"},\"messageId\":\"43657326693\",\"timestamp\":1758972734285}','completed','2025-09-27 19:32:21','2025-09-27 19:32:22'),
(69,'send_award','12306092433','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"017524033213_7\",\"userId\":\"zishuoning\"},\"messageId\":\"12306092433\",\"timestamp\":1758972734285}','completed','2025-09-27 19:32:22','2025-09-27 19:32:22'),
(70,'send_award','25002804096','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"017524033213_8\",\"userId\":\"zishuoning\"},\"messageId\":\"25002804096\",\"timestamp\":1758972734285}','completed','2025-09-27 19:32:23','2025-09-27 19:32:23'),
(71,'send_award','09079702396','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"017524033213_9\",\"userId\":\"zishuoning\"},\"messageId\":\"09079702396\",\"timestamp\":1758972734285}','completed','2025-09-27 19:32:24','2025-09-27 19:32:24'),
(72,'send_award','63966218908','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"276985762139_0\",\"userId\":\"zishuoning\"},\"messageId\":\"63966218908\",\"timestamp\":1758973943880}','completed','2025-09-27 19:52:27','2025-09-27 19:52:27'),
(73,'send_award','62116618557','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"276985762139_1\",\"userId\":\"zishuoning\"},\"messageId\":\"62116618557\",\"timestamp\":1758973943882}','completed','2025-09-27 19:52:28','2025-09-27 19:52:28'),
(74,'send_award','73759117803','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"276985762139_2\",\"userId\":\"zishuoning\"},\"messageId\":\"73759117803\",\"timestamp\":1758973943882}','completed','2025-09-27 19:52:28','2025-09-27 19:52:29'),
(75,'send_award','88712330424','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"276985762139_3\",\"userId\":\"zishuoning\"},\"messageId\":\"88712330424\",\"timestamp\":1758973943882}','completed','2025-09-27 19:52:30','2025-09-27 19:52:30'),
(76,'send_award','57436851082','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"276985762139_4\",\"userId\":\"zishuoning\"},\"messageId\":\"57436851082\",\"timestamp\":1758973943882}','completed','2025-09-27 19:52:30','2025-09-27 19:52:31'),
(77,'send_award','11391304754','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"276985762139_5\",\"userId\":\"zishuoning\"},\"messageId\":\"11391304754\",\"timestamp\":1758973943882}','completed','2025-09-27 19:52:32','2025-09-27 19:52:32'),
(78,'send_award','19809015874','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"276985762139_6\",\"userId\":\"zishuoning\"},\"messageId\":\"19809015874\",\"timestamp\":1758973943882}','completed','2025-09-27 19:52:32','2025-09-27 19:52:32'),
(79,'send_award','71408524223','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"276985762139_7\",\"userId\":\"zishuoning\"},\"messageId\":\"71408524223\",\"timestamp\":1758973943882}','completed','2025-09-27 19:52:33','2025-09-27 19:52:33'),
(80,'send_award','94501038964','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"276985762139_8\",\"userId\":\"zishuoning\"},\"messageId\":\"94501038964\",\"timestamp\":1758973943882}','completed','2025-09-27 19:52:33','2025-09-27 19:52:34'),
(81,'send_award','12347522092','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"276985762139_9\",\"userId\":\"zishuoning\"},\"messageId\":\"12347522092\",\"timestamp\":1758973943882}','completed','2025-09-27 19:52:34','2025-09-27 19:52:35'),
(82,'send_award','33523384109','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"732518524175\",\"userId\":\"zishuoning\"},\"messageId\":\"33523384109\",\"timestamp\":1758997077428}','completed','2025-09-28 02:17:59','2025-09-28 02:18:00'),
(83,'send_award','06561979762','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"432960633256\",\"userId\":\"zishuoning\"},\"messageId\":\"06561979762\",\"timestamp\":1758997097326}','completed','2025-09-28 02:18:18','2025-09-28 02:18:19'),
(84,'send_award','93779335866','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"920030401163_0\",\"userId\":\"zishuoning\"},\"messageId\":\"93779335866\",\"timestamp\":1758997114582}','completed','2025-09-28 02:18:36','2025-09-28 02:18:36'),
(85,'send_award','11292660365','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"920030401163_1\",\"userId\":\"zishuoning\"},\"messageId\":\"11292660365\",\"timestamp\":1758997114582}','completed','2025-09-28 02:18:36','2025-09-28 02:18:36'),
(86,'send_award','44667552142','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"920030401163_2\",\"userId\":\"zishuoning\"},\"messageId\":\"44667552142\",\"timestamp\":1758997114582}','completed','2025-09-28 02:18:37','2025-09-28 02:18:37'),
(87,'send_award','39667049712','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"920030401163_3\",\"userId\":\"zishuoning\"},\"messageId\":\"39667049712\",\"timestamp\":1758997114582}','completed','2025-09-28 02:18:37','2025-09-28 02:18:37'),
(88,'send_award','76709608878','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"920030401163_4\",\"userId\":\"zishuoning\"},\"messageId\":\"76709608878\",\"timestamp\":1758997114582}','completed','2025-09-28 02:18:38','2025-09-28 02:18:38'),
(89,'send_award','43935399725','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"920030401163_5\",\"userId\":\"zishuoning\"},\"messageId\":\"43935399725\",\"timestamp\":1758997114582}','completed','2025-09-28 02:18:38','2025-09-28 02:18:38'),
(90,'send_award','14036070607','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"920030401163_6\",\"userId\":\"zishuoning\"},\"messageId\":\"14036070607\",\"timestamp\":1758997114582}','completed','2025-09-28 02:18:39','2025-09-28 02:18:39'),
(91,'send_award','20310832211','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"920030401163_7\",\"userId\":\"zishuoning\"},\"messageId\":\"20310832211\",\"timestamp\":1758997114582}','completed','2025-09-28 02:18:39','2025-09-28 02:18:39'),
(92,'send_award','88387517577','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"920030401163_8\",\"userId\":\"zishuoning\"},\"messageId\":\"88387517577\",\"timestamp\":1758997114582}','completed','2025-09-28 02:18:40','2025-09-28 02:18:40'),
(93,'send_award','47016680540','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"920030401163_9\",\"userId\":\"zishuoning\"},\"messageId\":\"47016680540\",\"timestamp\":1758997114582}','completed','2025-09-28 02:18:40','2025-09-28 02:18:40'),
(94,'send_award','90765569873','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"438319087867\",\"userId\":\"zishuoning\"},\"messageId\":\"90765569873\",\"timestamp\":1758997480232}','completed','2025-09-28 02:24:42','2025-09-28 02:24:43'),
(95,'send_award','33050777993','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"307544522662\",\"userId\":\"zishuoning\"},\"messageId\":\"33050777993\",\"timestamp\":1758997515636}','completed','2025-09-28 02:25:17','2025-09-28 02:25:17'),
(96,'send_award','61820879608','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"041626659402\",\"userId\":\"zishuoning\"},\"messageId\":\"61820879608\",\"timestamp\":1758997528670}','completed','2025-09-28 02:25:30','2025-09-28 02:25:31'),
(97,'send_award','61279280667','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"476366798148\",\"userId\":\"zishuoning\"},\"messageId\":\"61279280667\",\"timestamp\":1758997705603}','completed','2025-09-28 02:28:27','2025-09-28 02:28:28'),
(98,'send_award','29407812924','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"706280473231\",\"userId\":\"zishuoning\"},\"messageId\":\"29407812924\",\"timestamp\":1758997785368}','completed','2025-09-28 02:29:47','2025-09-28 02:29:47'),
(99,'send_award','22297963120','{\"data\":{\"awardId\":105,\"awardTitle\":\"Add ChatGPT model\",\"orderId\":\"126639322161\",\"userId\":\"zishuoning\"},\"messageId\":\"22297963120\",\"timestamp\":1758997929126}','completed','2025-09-28 02:32:11','2025-09-28 02:32:11'),
(100,'send_award','85711336784','{\"data\":{\"awardId\":105,\"awardTitle\":\"Add ChatGPT model\",\"orderId\":\"413984167802\",\"userId\":\"zishuoning\"},\"messageId\":\"85711336784\",\"timestamp\":1758998721337}','completed','2025-09-28 02:45:23','2025-09-28 02:45:23'),
(101,'send_rebate','81265708743','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"1001\",\"rebateDesc\":\"sign-sku\",\"rebateTypeVO\":\"SKU\"},\"orderId\":\"871066867200\",\"outBusinessNo\":\"zishuoning_sign-sku_2025-09-28\",\"userId\":\"zishuoning\"},\"messageId\":\"81265708743\",\"timestamp\":1758998743633}','completed','2025-09-28 02:45:43','2025-09-28 02:45:44'),
(102,'send_rebate','31776862391','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"15\",\"rebateDesc\":\"sign-integral\",\"rebateTypeVO\":\"INTEGRAL\"},\"orderId\":\"832388495232\",\"outBusinessNo\":\"zishuoning_sign-integral_2025-09-28\",\"userId\":\"zishuoning\"},\"messageId\":\"31776862391\",\"timestamp\":1758998743638}','completed','2025-09-28 02:45:45','2025-09-28 02:45:45'),
(103,'credit_adjust_success','17311155877','{\"data\":{\"orderId\":\"030171750765\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 02:45:46\",\"userId\":\"zishuoning\"},\"messageId\":\"17311155877\",\"timestamp\":1758998747815}','completed','2025-09-28 02:45:48','2025-09-28 02:45:49'),
(104,'send_award','09249505885','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"017604746736_0\",\"userId\":\"zishuoning\"},\"messageId\":\"09249505885\",\"timestamp\":1758998770215}','completed','2025-09-28 02:46:11','2025-09-28 02:46:12'),
(105,'send_award','10324964734','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"017604746736_1\",\"userId\":\"zishuoning\"},\"messageId\":\"10324964734\",\"timestamp\":1758998770215}','completed','2025-09-28 02:46:12','2025-09-28 02:46:12'),
(106,'send_award','79537220980','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"017604746736_2\",\"userId\":\"zishuoning\"},\"messageId\":\"79537220980\",\"timestamp\":1758998770215}','completed','2025-09-28 02:46:12','2025-09-28 02:46:13'),
(107,'send_award','87028983548','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"017604746736_3\",\"userId\":\"zishuoning\"},\"messageId\":\"87028983548\",\"timestamp\":1758998770215}','completed','2025-09-28 02:46:13','2025-09-28 02:46:13'),
(108,'send_award','56837557893','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"017604746736_4\",\"userId\":\"zishuoning\"},\"messageId\":\"56837557893\",\"timestamp\":1758998770215}','completed','2025-09-28 02:46:14','2025-09-28 02:46:14'),
(109,'send_award','77545942393','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"017604746736_5\",\"userId\":\"zishuoning\"},\"messageId\":\"77545942393\",\"timestamp\":1758998770216}','completed','2025-09-28 02:46:14','2025-09-28 02:46:14'),
(110,'send_award','37527436570','{\"data\":{\"awardId\":107,\"awardTitle\":\"iPhone 17\",\"orderId\":\"017604746736_6\",\"userId\":\"zishuoning\"},\"messageId\":\"37527436570\",\"timestamp\":1758998770216}','completed','2025-09-28 02:46:15','2025-09-28 02:46:15'),
(111,'send_award','56126405490','{\"data\":{\"awardId\":105,\"awardTitle\":\"Add ChatGPT model\",\"orderId\":\"017604746736_7\",\"userId\":\"zishuoning\"},\"messageId\":\"56126405490\",\"timestamp\":1758998770216}','completed','2025-09-28 02:46:15','2025-09-28 02:46:15'),
(112,'send_award','54394808327','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"017604746736_8\",\"userId\":\"zishuoning\"},\"messageId\":\"54394808327\",\"timestamp\":1758998770216}','completed','2025-09-28 02:46:16','2025-09-28 02:46:16'),
(113,'send_award','58268126189','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"017604746736_9\",\"userId\":\"zishuoning\"},\"messageId\":\"58268126189\",\"timestamp\":1758998770216}','completed','2025-09-28 02:46:16','2025-09-28 02:46:16'),
(114,'credit_adjust_success','95105420021','{\"data\":{\"orderId\":\"165281093637\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 02:51:33\",\"userId\":\"zishuoning\"},\"messageId\":\"95105420021\",\"timestamp\":1758999095513}','completed','2025-09-28 02:51:36','2025-09-28 02:51:37'),
(115,'credit_adjust_success','69421710260','{\"data\":{\"orderId\":\"959536813429\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 02:51:34\",\"userId\":\"zishuoning\"},\"messageId\":\"69421710260\",\"timestamp\":1758999096526}','completed','2025-09-28 02:51:38','2025-09-28 02:51:38'),
(116,'credit_adjust_success','58851305073','{\"data\":{\"orderId\":\"959822070547\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 02:52:13\",\"userId\":\"zishuoning\"},\"messageId\":\"58851305073\",\"timestamp\":1758999135076}','completed','2025-09-28 02:52:15','2025-09-28 02:52:16'),
(117,'credit_adjust_success','43080990613','{\"data\":{\"orderId\":\"042847026766\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 02:52:14\",\"userId\":\"zishuoning\"},\"messageId\":\"43080990613\",\"timestamp\":1758999135749}','completed','2025-09-28 02:52:17','2025-09-28 02:52:18'),
(118,'credit_adjust_success','38670443473','{\"data\":{\"orderId\":\"047416900180\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 02:52:15\",\"userId\":\"zishuoning\"},\"messageId\":\"38670443473\",\"timestamp\":1758999137076}','completed','2025-09-28 02:52:18','2025-09-28 02:52:19'),
(119,'credit_adjust_success','04631946731','{\"data\":{\"orderId\":\"287149569885\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 02:53:43\",\"userId\":\"zishuoning\"},\"messageId\":\"04631946731\",\"timestamp\":1758999225564}','completed','2025-09-28 02:53:46','2025-09-28 02:53:47'),
(120,'credit_adjust_success','18823609713','{\"data\":{\"orderId\":\"667078270878\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 02:53:44\",\"userId\":\"zishuoning\"},\"messageId\":\"18823609713\",\"timestamp\":1758999226337}','completed','2025-09-28 02:53:47','2025-09-28 02:53:48'),
(121,'credit_adjust_success','95887438795','{\"data\":{\"orderId\":\"866059999407\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 02:54:01\",\"userId\":\"zishuoning\"},\"messageId\":\"95887438795\",\"timestamp\":1758999243614}','completed','2025-09-28 02:54:04','2025-09-28 02:54:05'),
(122,'credit_adjust_success','14102561259','{\"data\":{\"orderId\":\"523632831458\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 02:54:02\",\"userId\":\"zishuoning\"},\"messageId\":\"14102561259\",\"timestamp\":1758999243751}','completed','2025-09-28 02:54:05','2025-09-28 02:54:05'),
(123,'credit_adjust_success','52583158586','{\"data\":{\"orderId\":\"868261585085\",\"outBusinessNo\":\"zishuoning_creditPay-1002_2025-09-28 02:55:04\",\"userId\":\"zishuoning\"},\"messageId\":\"52583158586\",\"timestamp\":1758999306565}','completed','2025-09-28 02:55:07','2025-09-28 02:55:08'),
(124,'credit_adjust_success','62979995671','{\"data\":{\"orderId\":\"362574032870\",\"outBusinessNo\":\"zishuoning_creditPay-1002_2025-09-28 02:55:05\",\"userId\":\"zishuoning\"},\"messageId\":\"62979995671\",\"timestamp\":1758999307055}','completed','2025-09-28 02:55:08','2025-09-28 02:55:09'),
(125,'send_award','58417517027','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"298170819052\",\"userId\":\"zishuoning\"},\"messageId\":\"58417517027\",\"timestamp\":1759055758577}','completed','2025-09-28 18:36:02','2025-09-28 18:36:03'),
(126,'send_award','99036818741','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"797055598514\",\"userId\":\"zishuoning\"},\"messageId\":\"99036818741\",\"timestamp\":1759056887146}','completed','2025-09-28 18:54:50','2025-09-28 18:54:52'),
(127,'send_rebate','96333831635','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"1001\",\"rebateDesc\":\"sign-sku\",\"rebateTypeVO\":\"SKU\"},\"orderId\":\"554965739003\",\"outBusinessNo\":\"zishuoning_sign-sku_2025-09-28\",\"userId\":\"zishuoning\"},\"messageId\":\"96333831635\",\"timestamp\":1759057381614}','completed','2025-09-28 19:03:03','2025-09-28 19:03:03'),
(128,'send_rebate','29729065364','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"15\",\"rebateDesc\":\"sign-integral\",\"rebateTypeVO\":\"INTEGRAL\"},\"orderId\":\"787499541536\",\"outBusinessNo\":\"zishuoning_sign-integral_2025-09-28\",\"userId\":\"zishuoning\"},\"messageId\":\"29729065364\",\"timestamp\":1759057381616}','completed','2025-09-28 19:03:04','2025-09-28 19:03:05'),
(129,'credit_adjust_success','74258929520','{\"data\":{\"orderId\":\"670032552281\",\"outBusinessNo\":\"zishuoning_creditPay-1002_2025-09-28 19:05:28\",\"userId\":\"zishuoning\"},\"messageId\":\"74258929520\",\"timestamp\":1759057532176}','completed','2025-09-28 19:05:34','2025-09-28 19:05:36'),
(130,'send_award','68955860680','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"048302991251\",\"userId\":\"zishuoning\"},\"messageId\":\"68955860680\",\"timestamp\":1759057554593}','completed','2025-09-28 19:05:59','2025-09-28 19:06:00'),
(131,'credit_adjust_success','64189735303','{\"data\":{\"orderId\":\"106515638074\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 19:06:05\",\"userId\":\"zishuoning\"},\"messageId\":\"64189735303\",\"timestamp\":1759057568462}','completed','2025-09-28 19:06:10','2025-09-28 19:06:11'),
(132,'send_award','55684750433','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"464048829393_0\",\"userId\":\"zishuoning\"},\"messageId\":\"55684750433\",\"timestamp\":1759057584666}','completed','2025-09-28 19:06:27','2025-09-28 19:06:28'),
(133,'send_award','67386059058','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"464048829393_1\",\"userId\":\"zishuoning\"},\"messageId\":\"67386059058\",\"timestamp\":1759057584666}','completed','2025-09-28 19:06:28','2025-09-28 19:06:29'),
(134,'send_award','07644737944','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"464048829393_2\",\"userId\":\"zishuoning\"},\"messageId\":\"07644737944\",\"timestamp\":1759057584666}','completed','2025-09-28 19:06:29','2025-09-28 19:06:29'),
(135,'send_award','85436809985','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"464048829393_3\",\"userId\":\"zishuoning\"},\"messageId\":\"85436809985\",\"timestamp\":1759057584666}','completed','2025-09-28 19:06:30','2025-09-28 19:06:30'),
(136,'send_award','07453079517','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"464048829393_4\",\"userId\":\"zishuoning\"},\"messageId\":\"07453079517\",\"timestamp\":1759057584666}','completed','2025-09-28 19:06:30','2025-09-28 19:06:31'),
(137,'send_award','11453772385','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"464048829393_5\",\"userId\":\"zishuoning\"},\"messageId\":\"11453772385\",\"timestamp\":1759057584666}','completed','2025-09-28 19:06:31','2025-09-28 19:06:31'),
(138,'send_award','55943594222','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"464048829393_6\",\"userId\":\"zishuoning\"},\"messageId\":\"55943594222\",\"timestamp\":1759057584666}','completed','2025-09-28 19:06:32','2025-09-28 19:06:32'),
(139,'send_award','12560745212','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"464048829393_7\",\"userId\":\"zishuoning\"},\"messageId\":\"12560745212\",\"timestamp\":1759057584666}','completed','2025-09-28 19:06:33','2025-09-28 19:06:33'),
(140,'send_award','69215414865','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"464048829393_8\",\"userId\":\"zishuoning\"},\"messageId\":\"69215414865\",\"timestamp\":1759057584666}','completed','2025-09-28 19:06:33','2025-09-28 19:06:33'),
(141,'send_award','51458943346','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"464048829393_9\",\"userId\":\"zishuoning\"},\"messageId\":\"51458943346\",\"timestamp\":1759057584667}','completed','2025-09-28 19:06:34','2025-09-28 19:06:34'),
(142,'credit_adjust_success','24482725800','{\"data\":{\"orderId\":\"516493497698\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 19:07:02\",\"userId\":\"zishuoning\"},\"messageId\":\"24482725800\",\"timestamp\":1759057623779}','completed','2025-09-28 19:07:05','2025-09-28 19:07:06'),
(143,'credit_adjust_success','68104574925','{\"data\":{\"orderId\":\"057317595502\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 19:07:09\",\"userId\":\"zishuoning\"},\"messageId\":\"68104574925\",\"timestamp\":1759057632534}','completed','2025-09-28 19:07:15','2025-09-28 19:07:16'),
(144,'credit_adjust_success','48691809569','{\"data\":{\"orderId\":\"976614830414\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 19:07:24\",\"userId\":\"zishuoning\"},\"messageId\":\"48691809569\",\"timestamp\":1759057646283}','completed','2025-09-28 19:07:28','2025-09-28 19:07:29'),
(145,'credit_adjust_success','11252834110','{\"data\":{\"orderId\":\"782821363209\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-09-28 19:07:35\",\"userId\":\"zishuoning\"},\"messageId\":\"11252834110\",\"timestamp\":1759057657569}','completed','2025-09-28 19:07:39','2025-09-28 19:07:40'),
(146,'send_award','17280137316','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"047903597207_0\",\"userId\":\"zishuoning\"},\"messageId\":\"17280137316\",\"timestamp\":1759057672051}','completed','2025-09-28 19:07:55','2025-09-28 19:07:55'),
(147,'send_award','99004836227','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"047903597207_1\",\"userId\":\"zishuoning\"},\"messageId\":\"99004836227\",\"timestamp\":1759057672051}','completed','2025-09-28 19:07:55','2025-09-28 19:07:56'),
(148,'send_award','21982441065','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"047903597207_2\",\"userId\":\"zishuoning\"},\"messageId\":\"21982441065\",\"timestamp\":1759057672051}','completed','2025-09-28 19:07:56','2025-09-28 19:07:56'),
(149,'send_award','96222461572','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"047903597207_3\",\"userId\":\"zishuoning\"},\"messageId\":\"96222461572\",\"timestamp\":1759057672051}','completed','2025-09-28 19:07:56','2025-09-28 19:07:57'),
(150,'send_award','38555002790','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"047903597207_4\",\"userId\":\"zishuoning\"},\"messageId\":\"38555002790\",\"timestamp\":1759057672051}','completed','2025-09-28 19:07:57','2025-09-28 19:07:57'),
(151,'send_award','20756803198','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"047903597207_5\",\"userId\":\"zishuoning\"},\"messageId\":\"20756803198\",\"timestamp\":1759057672051}','completed','2025-09-28 19:07:57','2025-09-28 19:07:58'),
(152,'send_award','16355124193','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"047903597207_6\",\"userId\":\"zishuoning\"},\"messageId\":\"16355124193\",\"timestamp\":1759057672051}','completed','2025-09-28 19:07:58','2025-09-28 19:07:58'),
(153,'send_award','12242191890','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"047903597207_7\",\"userId\":\"zishuoning\"},\"messageId\":\"12242191890\",\"timestamp\":1759057672051}','completed','2025-09-28 19:07:59','2025-09-28 19:07:59'),
(154,'send_award','07395204971','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"047903597207_8\",\"userId\":\"zishuoning\"},\"messageId\":\"07395204971\",\"timestamp\":1759057672051}','completed','2025-09-28 19:08:00','2025-09-28 19:08:00'),
(155,'send_award','31267868568','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"047903597207_9\",\"userId\":\"zishuoning\"},\"messageId\":\"31267868568\",\"timestamp\":1759057672051}','completed','2025-09-28 19:08:00','2025-09-28 19:08:00'),
(156,'send_award','28295183062','{\"data\":{\"awardId\":105,\"awardTitle\":\"Add ChatGPT model\",\"orderId\":\"665673982795_0\",\"userId\":\"zishuoning\"},\"messageId\":\"28295183062\",\"timestamp\":1759057695648}','completed','2025-09-28 19:08:22','2025-09-28 19:08:22'),
(157,'send_award','74521925527','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"665673982795_1\",\"userId\":\"zishuoning\"},\"messageId\":\"74521925527\",\"timestamp\":1759057695648}','completed','2025-09-28 19:08:22','2025-09-28 19:08:22'),
(158,'send_award','84549509041','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"665673982795_2\",\"userId\":\"zishuoning\"},\"messageId\":\"84549509041\",\"timestamp\":1759057695648}','completed','2025-09-28 19:08:24','2025-09-28 19:08:25'),
(159,'send_award','65903558504','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"665673982795_3\",\"userId\":\"zishuoning\"},\"messageId\":\"65903558504\",\"timestamp\":1759057695648}','completed','2025-09-28 19:08:26','2025-09-28 19:08:26'),
(160,'send_award','20370330626','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"665673982795_4\",\"userId\":\"zishuoning\"},\"messageId\":\"20370330626\",\"timestamp\":1759057695648}','completed','2025-09-28 19:08:26','2025-09-28 19:08:27'),
(161,'send_award','82113846601','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"665673982795_5\",\"userId\":\"zishuoning\"},\"messageId\":\"82113846601\",\"timestamp\":1759057695648}','completed','2025-09-28 19:08:27','2025-09-28 19:08:28'),
(162,'send_award','21759726705','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"665673982795_6\",\"userId\":\"zishuoning\"},\"messageId\":\"21759726705\",\"timestamp\":1759057695648}','completed','2025-09-28 19:08:28','2025-09-28 19:08:28'),
(163,'send_award','58031005614','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"665673982795_7\",\"userId\":\"zishuoning\"},\"messageId\":\"58031005614\",\"timestamp\":1759057695648}','completed','2025-09-28 19:08:28','2025-09-28 19:08:29'),
(164,'send_award','08869978257','{\"data\":{\"awardId\":107,\"awardTitle\":\"iPhone 17\",\"orderId\":\"665673982795_8\",\"userId\":\"zishuoning\"},\"messageId\":\"08869978257\",\"timestamp\":1759057695648}','completed','2025-09-28 19:08:29','2025-09-28 19:08:30'),
(165,'send_award','37777440010','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"665673982795_9\",\"userId\":\"zishuoning\"},\"messageId\":\"37777440010\",\"timestamp\":1759057695648}','completed','2025-09-28 19:08:30','2025-09-28 19:08:30'),
(166,'send_award','47347847956','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"836383070383_0\",\"userId\":\"zishuoning\"},\"messageId\":\"47347847956\",\"timestamp\":1759057724885}','completed','2025-09-28 19:08:49','2025-09-28 19:08:49'),
(167,'send_award','78798934236','{\"data\":{\"awardId\":108,\"awardTitle\":\"RTX 5060Ti\",\"orderId\":\"836383070383_1\",\"userId\":\"zishuoning\"},\"messageId\":\"78798934236\",\"timestamp\":1759057724885}','completed','2025-09-28 19:08:50','2025-09-28 19:08:51'),
(168,'send_award','77644425526','{\"data\":{\"awardId\":105,\"awardTitle\":\"Add ChatGPT model\",\"orderId\":\"836383070383_2\",\"userId\":\"zishuoning\"},\"messageId\":\"77644425526\",\"timestamp\":1759057724885}','completed','2025-09-28 19:08:52','2025-09-28 19:08:52'),
(169,'send_award','41807909724','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"836383070383_3\",\"userId\":\"zishuoning\"},\"messageId\":\"41807909724\",\"timestamp\":1759057724885}','completed','2025-09-28 19:08:53','2025-09-28 19:08:54'),
(170,'send_award','95675892199','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"836383070383_4\",\"userId\":\"zishuoning\"},\"messageId\":\"95675892199\",\"timestamp\":1759057724885}','completed','2025-09-28 19:08:54','2025-09-28 19:08:55'),
(171,'send_award','62557269842','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"836383070383_5\",\"userId\":\"zishuoning\"},\"messageId\":\"62557269842\",\"timestamp\":1759057724885}','completed','2025-09-28 19:08:55','2025-09-28 19:08:55'),
(172,'send_award','60559884884','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"836383070383_6\",\"userId\":\"zishuoning\"},\"messageId\":\"60559884884\",\"timestamp\":1759057724885}','completed','2025-09-28 19:08:55','2025-09-28 19:08:56'),
(173,'send_award','42303386963','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"836383070383_7\",\"userId\":\"zishuoning\"},\"messageId\":\"42303386963\",\"timestamp\":1759057724885}','completed','2025-09-28 19:08:56','2025-09-28 19:08:57'),
(174,'send_award','68667612957','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"836383070383_8\",\"userId\":\"zishuoning\"},\"messageId\":\"68667612957\",\"timestamp\":1759057724885}','completed','2025-09-28 19:08:57','2025-09-28 19:08:57'),
(175,'send_award','98602096444','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"836383070383_9\",\"userId\":\"zishuoning\"},\"messageId\":\"98602096444\",\"timestamp\":1759057724885}','completed','2025-09-28 19:08:58','2025-09-28 19:08:58'),
(176,'send_award','11584982582','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"288449600760_0\",\"userId\":\"zishuoning\"},\"messageId\":\"11584982582\",\"timestamp\":1759057885428}','completed','2025-09-28 19:11:28','2025-09-28 19:11:28'),
(177,'send_award','67360332382','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"288449600760_1\",\"userId\":\"zishuoning\"},\"messageId\":\"67360332382\",\"timestamp\":1759057885428}','completed','2025-09-28 19:11:28','2025-09-28 19:11:28'),
(178,'send_award','29753721189','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"288449600760_2\",\"userId\":\"zishuoning\"},\"messageId\":\"29753721189\",\"timestamp\":1759057885428}','completed','2025-09-28 19:11:29','2025-09-28 19:11:30'),
(179,'send_award','60746707674','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"288449600760_3\",\"userId\":\"zishuoning\"},\"messageId\":\"60746707674\",\"timestamp\":1759057885428}','completed','2025-09-28 19:11:30','2025-09-28 19:11:30'),
(180,'send_award','53028659261','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"288449600760_4\",\"userId\":\"zishuoning\"},\"messageId\":\"53028659261\",\"timestamp\":1759057885428}','completed','2025-09-28 19:11:30','2025-09-28 19:11:31'),
(181,'send_award','74558583526','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"288449600760_5\",\"userId\":\"zishuoning\"},\"messageId\":\"74558583526\",\"timestamp\":1759057885428}','completed','2025-09-28 19:11:31','2025-09-28 19:11:31'),
(182,'send_award','25060406385','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"288449600760_6\",\"userId\":\"zishuoning\"},\"messageId\":\"25060406385\",\"timestamp\":1759057885428}','completed','2025-09-28 19:11:31','2025-09-28 19:11:32'),
(183,'send_award','42129244505','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"288449600760_7\",\"userId\":\"zishuoning\"},\"messageId\":\"42129244505\",\"timestamp\":1759057885428}','completed','2025-09-28 19:11:32','2025-09-28 19:11:32'),
(184,'send_award','11691607057','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"288449600760_8\",\"userId\":\"zishuoning\"},\"messageId\":\"11691607057\",\"timestamp\":1759057885428}','completed','2025-09-28 19:11:32','2025-09-28 19:11:33'),
(185,'send_award','74062625875','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"288449600760_9\",\"userId\":\"zishuoning\"},\"messageId\":\"74062625875\",\"timestamp\":1759057885428}','completed','2025-09-28 19:11:33','2025-09-28 19:11:33'),
(186,'send_award','90285463365','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"127631625992_0\",\"userId\":\"zishuoning\"},\"messageId\":\"90285463365\",\"timestamp\":1759057991366}','completed','2025-09-28 19:13:14','2025-09-28 19:13:15'),
(187,'send_award','18098498904','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"127631625992_1\",\"userId\":\"zishuoning\"},\"messageId\":\"18098498904\",\"timestamp\":1759057991366}','completed','2025-09-28 19:13:15','2025-09-28 19:13:15'),
(188,'send_award','16753235094','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"127631625992_2\",\"userId\":\"zishuoning\"},\"messageId\":\"16753235094\",\"timestamp\":1759057991366}','completed','2025-09-28 19:13:16','2025-09-28 19:13:16'),
(189,'send_award','45325350374','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"127631625992_3\",\"userId\":\"zishuoning\"},\"messageId\":\"45325350374\",\"timestamp\":1759057991366}','completed','2025-09-28 19:13:16','2025-09-28 19:13:16'),
(190,'send_award','24795889008','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"127631625992_4\",\"userId\":\"zishuoning\"},\"messageId\":\"24795889008\",\"timestamp\":1759057991366}','completed','2025-09-28 19:13:17','2025-09-28 19:13:17'),
(191,'send_award','62464332507','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"127631625992_5\",\"userId\":\"zishuoning\"},\"messageId\":\"62464332507\",\"timestamp\":1759057991366}','completed','2025-09-28 19:13:18','2025-09-28 19:13:18'),
(192,'send_award','20890273891','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"127631625992_6\",\"userId\":\"zishuoning\"},\"messageId\":\"20890273891\",\"timestamp\":1759057991366}','completed','2025-09-28 19:13:18','2025-09-28 19:13:18'),
(193,'send_award','40668307937','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"127631625992_7\",\"userId\":\"zishuoning\"},\"messageId\":\"40668307937\",\"timestamp\":1759057991366}','completed','2025-09-28 19:13:19','2025-09-28 19:13:19'),
(194,'send_award','35103627898','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"127631625992_8\",\"userId\":\"zishuoning\"},\"messageId\":\"35103627898\",\"timestamp\":1759057991366}','completed','2025-09-28 19:13:19','2025-09-28 19:13:19'),
(195,'send_award','31029571764','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"127631625992_9\",\"userId\":\"zishuoning\"},\"messageId\":\"31029571764\",\"timestamp\":1759057991366}','completed','2025-09-28 19:13:20','2025-09-28 19:13:20'),
(196,'send_award','94182915129','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"445228452015_0\",\"userId\":\"zishuoning\"},\"messageId\":\"94182915129\",\"timestamp\":1759058010321}','completed','2025-09-28 19:13:33','2025-09-28 19:13:34'),
(197,'send_award','58519183854','{\"data\":{\"awardId\":106,\"awardTitle\":\"Add Gemini model\",\"orderId\":\"445228452015_1\",\"userId\":\"zishuoning\"},\"messageId\":\"58519183854\",\"timestamp\":1759058010321}','completed','2025-09-28 19:13:34','2025-09-28 19:13:34'),
(198,'send_award','00260495823','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"445228452015_2\",\"userId\":\"zishuoning\"},\"messageId\":\"00260495823\",\"timestamp\":1759058010321}','completed','2025-09-28 19:13:35','2025-09-28 19:13:35'),
(199,'send_award','02264664359','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"445228452015_3\",\"userId\":\"zishuoning\"},\"messageId\":\"02264664359\",\"timestamp\":1759058010321}','completed','2025-09-28 19:13:35','2025-09-28 19:13:35'),
(200,'send_award','84803160754','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"445228452015_4\",\"userId\":\"zishuoning\"},\"messageId\":\"84803160754\",\"timestamp\":1759058010321}','completed','2025-09-28 19:13:36','2025-09-28 19:13:36'),
(201,'send_award','60858889646','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"445228452015_5\",\"userId\":\"zishuoning\"},\"messageId\":\"60858889646\",\"timestamp\":1759058010321}','completed','2025-09-28 19:13:37','2025-09-28 19:13:37'),
(202,'send_award','21014049615','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"445228452015_6\",\"userId\":\"zishuoning\"},\"messageId\":\"21014049615\",\"timestamp\":1759058010321}','completed','2025-09-28 19:13:37','2025-09-28 19:13:38'),
(203,'send_award','52103109328','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"445228452015_7\",\"userId\":\"zishuoning\"},\"messageId\":\"52103109328\",\"timestamp\":1759058010321}','completed','2025-09-28 19:13:38','2025-09-28 19:13:38'),
(204,'send_award','56153929038','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"445228452015_8\",\"userId\":\"zishuoning\"},\"messageId\":\"56153929038\",\"timestamp\":1759058010321}','completed','2025-09-28 19:13:39','2025-09-28 19:13:39'),
(205,'send_award','73356367544','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"445228452015_9\",\"userId\":\"zishuoning\"},\"messageId\":\"73356367544\",\"timestamp\":1759058010321}','completed','2025-09-28 19:13:40','2025-09-28 19:13:40'),
(206,'send_award','36711378900','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"567184804661_0\",\"userId\":\"zishuoning\"},\"messageId\":\"36711378900\",\"timestamp\":1759058030773}','completed','2025-09-28 19:13:54','2025-09-28 19:13:54'),
(207,'send_award','03089779383','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"567184804661_1\",\"userId\":\"zishuoning\"},\"messageId\":\"03089779383\",\"timestamp\":1759058030773}','completed','2025-09-28 19:13:54','2025-09-28 19:13:55'),
(208,'send_award','00503036002','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"567184804661_2\",\"userId\":\"zishuoning\"},\"messageId\":\"00503036002\",\"timestamp\":1759058030773}','completed','2025-09-28 19:13:55','2025-09-28 19:13:56'),
(209,'send_award','44402997966','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"567184804661_3\",\"userId\":\"zishuoning\"},\"messageId\":\"44402997966\",\"timestamp\":1759058030773}','completed','2025-09-28 19:13:57','2025-09-28 19:13:57'),
(210,'send_award','52118370160','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"567184804661_4\",\"userId\":\"zishuoning\"},\"messageId\":\"52118370160\",\"timestamp\":1759058030773}','completed','2025-09-28 19:13:57','2025-09-28 19:13:58'),
(211,'send_award','40153430814','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"567184804661_5\",\"userId\":\"zishuoning\"},\"messageId\":\"40153430814\",\"timestamp\":1759058030773}','completed','2025-09-28 19:13:58','2025-09-28 19:13:58'),
(212,'send_award','35647090245','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"567184804661_6\",\"userId\":\"zishuoning\"},\"messageId\":\"35647090245\",\"timestamp\":1759058030773}','completed','2025-09-28 19:13:58','2025-09-28 19:13:59'),
(213,'send_award','55734529768','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"567184804661_7\",\"userId\":\"zishuoning\"},\"messageId\":\"55734529768\",\"timestamp\":1759058030773}','completed','2025-09-28 19:13:59','2025-09-28 19:13:59'),
(214,'send_award','51327858431','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"567184804661_8\",\"userId\":\"zishuoning\"},\"messageId\":\"51327858431\",\"timestamp\":1759058030773}','completed','2025-09-28 19:13:59','2025-09-28 19:14:00'),
(215,'send_award','39340286336','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"567184804661_9\",\"userId\":\"zishuoning\"},\"messageId\":\"39340286336\",\"timestamp\":1759058030773}','completed','2025-09-28 19:14:00','2025-09-28 19:14:00'),
(216,'send_award','71878127366','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"671968304558_0\",\"userId\":\"zishuoning\"},\"messageId\":\"71878127366\",\"timestamp\":1759058053046}','completed','2025-09-28 19:14:16','2025-09-28 19:14:16'),
(217,'send_award','54590053888','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"671968304558_1\",\"userId\":\"zishuoning\"},\"messageId\":\"54590053888\",\"timestamp\":1759058053046}','completed','2025-09-28 19:14:16','2025-09-28 19:14:16'),
(218,'send_award','59269805970','{\"data\":{\"awardId\":106,\"awardTitle\":\"Add Gemini model\",\"orderId\":\"671968304558_2\",\"userId\":\"zishuoning\"},\"messageId\":\"59269805970\",\"timestamp\":1759058053046}','completed','2025-09-28 19:14:17','2025-09-28 19:14:17'),
(219,'send_award','23835071638','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"671968304558_3\",\"userId\":\"zishuoning\"},\"messageId\":\"23835071638\",\"timestamp\":1759058053046}','completed','2025-09-28 19:14:17','2025-09-28 19:14:17'),
(220,'send_award','36402788439','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"671968304558_4\",\"userId\":\"zishuoning\"},\"messageId\":\"36402788439\",\"timestamp\":1759058053046}','completed','2025-09-28 19:14:18','2025-09-28 19:14:18'),
(221,'send_award','92083537940','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"671968304558_5\",\"userId\":\"zishuoning\"},\"messageId\":\"92083537940\",\"timestamp\":1759058053046}','completed','2025-09-28 19:14:18','2025-09-28 19:14:18'),
(222,'send_award','90806283411','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"671968304558_6\",\"userId\":\"zishuoning\"},\"messageId\":\"90806283411\",\"timestamp\":1759058053046}','completed','2025-09-28 19:14:19','2025-09-28 19:14:19'),
(223,'send_award','23999510357','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"671968304558_7\",\"userId\":\"zishuoning\"},\"messageId\":\"23999510357\",\"timestamp\":1759058053046}','completed','2025-09-28 19:14:19','2025-09-28 19:14:20'),
(224,'send_award','91953371825','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"671968304558_8\",\"userId\":\"zishuoning\"},\"messageId\":\"91953371825\",\"timestamp\":1759058053046}','completed','2025-09-28 19:14:20','2025-09-28 19:14:20'),
(225,'send_award','77309167940','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"671968304558_9\",\"userId\":\"zishuoning\"},\"messageId\":\"77309167940\",\"timestamp\":1759058053046}','completed','2025-09-28 19:14:20','2025-09-28 19:14:21'),
(226,'send_award','15414220660','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"093849903794_0\",\"userId\":\"zishuoning\"},\"messageId\":\"15414220660\",\"timestamp\":1759058073283}','completed','2025-09-28 19:14:36','2025-09-28 19:14:36'),
(227,'send_award','26840092645','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"093849903794_1\",\"userId\":\"zishuoning\"},\"messageId\":\"26840092645\",\"timestamp\":1759058073283}','completed','2025-09-28 19:14:36','2025-09-28 19:14:37'),
(228,'send_award','35988771970','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"093849903794_2\",\"userId\":\"zishuoning\"},\"messageId\":\"35988771970\",\"timestamp\":1759058073283}','completed','2025-09-28 19:14:37','2025-09-28 19:14:37'),
(229,'send_award','11725741783','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"093849903794_3\",\"userId\":\"zishuoning\"},\"messageId\":\"11725741783\",\"timestamp\":1759058073283}','completed','2025-09-28 19:14:37','2025-09-28 19:14:38'),
(230,'send_award','62302812157','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"093849903794_4\",\"userId\":\"zishuoning\"},\"messageId\":\"62302812157\",\"timestamp\":1759058073283}','completed','2025-09-28 19:14:38','2025-09-28 19:14:38'),
(231,'send_award','39703750142','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"093849903794_5\",\"userId\":\"zishuoning\"},\"messageId\":\"39703750142\",\"timestamp\":1759058073283}','completed','2025-09-28 19:14:38','2025-09-28 19:14:39'),
(232,'send_award','57665296750','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"093849903794_6\",\"userId\":\"zishuoning\"},\"messageId\":\"57665296750\",\"timestamp\":1759058073283}','completed','2025-09-28 19:14:39','2025-09-28 19:14:39'),
(233,'send_award','19482956963','{\"data\":{\"awardId\":107,\"awardTitle\":\"iPhone 17\",\"orderId\":\"093849903794_7\",\"userId\":\"zishuoning\"},\"messageId\":\"19482956963\",\"timestamp\":1759058073283}','completed','2025-09-28 19:14:40','2025-09-28 19:14:40'),
(234,'send_award','79028124953','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"093849903794_8\",\"userId\":\"zishuoning\"},\"messageId\":\"79028124953\",\"timestamp\":1759058073283}','completed','2025-09-28 19:14:41','2025-09-28 19:14:41'),
(235,'send_award','11889527184','{\"data\":{\"awardId\":103,\"awardTitle\":\"10 uses of ChatGPT\",\"orderId\":\"093849903794_9\",\"userId\":\"zishuoning\"},\"messageId\":\"11889527184\",\"timestamp\":1759058073283}','completed','2025-09-28 19:14:41','2025-09-28 19:14:41'),
(236,'send_rebate','75632805025','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"1001\",\"rebateDesc\":\"sign-sku\",\"rebateTypeVO\":\"SKU\"},\"orderId\":\"691477340673\",\"outBusinessNo\":\"1_sign-sku_2025-09-28\",\"userId\":\"1\"},\"messageId\":\"75632805025\",\"timestamp\":1759058149624}','completed','2025-09-28 19:15:51','2025-09-28 19:15:51'),
(237,'send_rebate','70581408126','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"15\",\"rebateDesc\":\"sign-integral\",\"rebateTypeVO\":\"INTEGRAL\"},\"orderId\":\"577617277488\",\"outBusinessNo\":\"1_sign-integral_2025-09-28\",\"userId\":\"1\"},\"messageId\":\"70581408126\",\"timestamp\":1759058149624}','completed','2025-09-28 19:15:52','2025-09-28 19:15:53'),
(238,'send_award','05025329359','{\"data\":{\"awardConfig\":\"1,5\",\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"937520776243\",\"userId\":\"1\"},\"messageId\":\"05025329359\",\"timestamp\":1759058162693}','completed','2025-09-28 19:16:06','2025-09-28 19:16:07'),
(239,'credit_adjust_success','94814210997','{\"data\":{\"orderId\":\"330021029900\",\"outBusinessNo\":\"1_creditPay-1003_2025-09-28 19:16:10\",\"userId\":\"1\"},\"messageId\":\"94814210997\",\"timestamp\":1759058172743}','completed','2025-09-28 19:16:15','2025-09-28 19:16:16'),
(240,'send_award','60172941784','{\"data\":{\"awardConfig\":\"1,5\",\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"612926320081_0\",\"userId\":\"1\"},\"messageId\":\"60172941784\",\"timestamp\":1759058183024}','completed','2025-09-28 19:16:26','2025-09-28 19:16:26'),
(241,'send_award','45121090036','{\"data\":{\"awardConfig\":\"1,5\",\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"612926320081_1\",\"userId\":\"1\"},\"messageId\":\"45121090036\",\"timestamp\":1759058183024}','completed','2025-09-28 19:16:27','2025-09-28 19:16:28'),
(242,'send_award','43459019105','{\"data\":{\"awardConfig\":\"1,5\",\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"612926320081_2\",\"userId\":\"1\"},\"messageId\":\"43459019105\",\"timestamp\":1759058183024}','completed','2025-09-28 19:16:28','2025-09-28 19:16:28'),
(243,'send_award','84089374224','{\"data\":{\"awardConfig\":\"1,5\",\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"612926320081_3\",\"userId\":\"1\"},\"messageId\":\"84089374224\",\"timestamp\":1759058183024}','completed','2025-09-28 19:16:28','2025-09-28 19:16:29'),
(244,'send_award','96894755255','{\"data\":{\"awardConfig\":\"1,5\",\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"612926320081_4\",\"userId\":\"1\"},\"messageId\":\"96894755255\",\"timestamp\":1759058183024}','completed','2025-09-28 19:16:29','2025-09-28 19:16:29'),
(245,'send_award','98262174078','{\"data\":{\"awardConfig\":\"1,5\",\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"612926320081_5\",\"userId\":\"1\"},\"messageId\":\"98262174078\",\"timestamp\":1759058183024}','completed','2025-09-28 19:16:29','2025-09-28 19:16:30'),
(246,'send_award','08560325034','{\"data\":{\"awardConfig\":\"1,5\",\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"612926320081_6\",\"userId\":\"1\"},\"messageId\":\"08560325034\",\"timestamp\":1759058183024}','completed','2025-09-28 19:16:30','2025-09-28 19:16:30'),
(247,'send_award','63184930488','{\"data\":{\"awardConfig\":\"1,5\",\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"612926320081_7\",\"userId\":\"1\"},\"messageId\":\"63184930488\",\"timestamp\":1759058183024}','completed','2025-09-28 19:16:30','2025-09-28 19:16:31'),
(248,'send_award','73706254531','{\"data\":{\"awardConfig\":\"1,5\",\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"612926320081_8\",\"userId\":\"1\"},\"messageId\":\"73706254531\",\"timestamp\":1759058183024}','completed','2025-09-28 19:16:31','2025-09-28 19:16:32'),
(249,'send_award','80798353261','{\"data\":{\"awardConfig\":\"1,5\",\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"612926320081_9\",\"userId\":\"1\"},\"messageId\":\"80798353261\",\"timestamp\":1759058183024}','completed','2025-09-28 19:16:32','2025-09-28 19:16:32'),
(250,'send_rebate','45339815798','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"1001\",\"rebateDesc\":\"sign-sku\",\"rebateTypeVO\":\"SKU\"},\"orderId\":\"822117402011\",\"outBusinessNo\":\"10_sign-sku_2025-09-28\",\"userId\":\"10\"},\"messageId\":\"45339815798\",\"timestamp\":1759058358773}','completed','2025-09-28 19:19:20','2025-09-28 19:19:20'),
(251,'send_rebate','55564974535','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"15\",\"rebateDesc\":\"sign-integral\",\"rebateTypeVO\":\"INTEGRAL\"},\"orderId\":\"069878424126\",\"outBusinessNo\":\"10_sign-integral_2025-09-28\",\"userId\":\"10\"},\"messageId\":\"55564974535\",\"timestamp\":1759058358773}','completed','2025-09-28 19:19:21','2025-09-28 19:19:22'),
(252,'send_award','47875762013','{\"data\":{\"awardConfig\":\"1,100\",\"awardId\":109,\"awardTitle\":\"Car\",\"orderId\":\"037861116661\",\"userId\":\"10\"},\"messageId\":\"47875762013\",\"timestamp\":1759058370860}','completed','2025-09-28 19:19:33','2025-09-28 19:19:34'),
(253,'send_award','52567015916','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"818536963916\",\"userId\":\"zishuoning\"},\"messageId\":\"52567015916\",\"timestamp\":1759077076024}','completed','2025-09-29 00:31:16','2025-09-29 00:31:16'),
(254,'send_rebate','44276523880','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"1001\",\"rebateDesc\":\"sign-sku\",\"rebateTypeVO\":\"SKU\"},\"orderId\":\"162208133614\",\"outBusinessNo\":\"zishuoning_sign-sku_2025-10-01\",\"userId\":\"zishuoning\"},\"messageId\":\"44276523880\",\"timestamp\":1759275957046}','completed','2025-10-01 07:45:57','2025-10-01 07:45:57'),
(255,'send_rebate','06622073408','{\"data\":{\"dailyBehaviorRebateVO\":{\"behaviorTypeVO\":\"SIGN\",\"rebateConfig\":\"15\",\"rebateDesc\":\"sign-integral\",\"rebateTypeVO\":\"INTEGRAL\"},\"orderId\":\"093147367038\",\"outBusinessNo\":\"zishuoning_sign-integral_2025-10-01\",\"userId\":\"zishuoning\"},\"messageId\":\"06622073408\",\"timestamp\":1759275957292}','completed','2025-10-01 07:45:57','2025-10-01 07:45:58'),
(256,'send_award','60457500674','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"722320976868\",\"userId\":\"zishuoning\"},\"messageId\":\"60457500674\",\"timestamp\":1759275961873}','completed','2025-10-01 07:46:01','2025-10-01 07:46:02'),
(257,'credit_adjust_success','93682513199','{\"data\":{\"orderId\":\"800591500658\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-10-01 08:05:39\",\"userId\":\"zishuoning\"},\"messageId\":\"93682513199\",\"timestamp\":1759277140176}','completed','2025-10-01 08:05:40','2025-10-01 08:05:41'),
(258,'send_award','46927315356','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"703212223759_0\",\"userId\":\"zishuoning\"},\"messageId\":\"46927315356\",\"timestamp\":1759277148916}','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(259,'send_award','61098965782','{\"data\":{\"awardId\":104,\"awardTitle\":\"10 uses of Gemini\",\"orderId\":\"703212223759_1\",\"userId\":\"zishuoning\"},\"messageId\":\"61098965782\",\"timestamp\":1759277148920}','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(260,'send_award','63434421781','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"703212223759_2\",\"userId\":\"zishuoning\"},\"messageId\":\"63434421781\",\"timestamp\":1759277148920}','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(261,'send_award','98325513181','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"703212223759_3\",\"userId\":\"zishuoning\"},\"messageId\":\"98325513181\",\"timestamp\":1759277148920}','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(262,'send_award','91977525975','{\"data\":{\"awardId\":102,\"awardTitle\":\"keyBoard\",\"orderId\":\"703212223759_4\",\"userId\":\"zishuoning\"},\"messageId\":\"91977525975\",\"timestamp\":1759277148920}','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(263,'send_award','21898272686','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"703212223759_5\",\"userId\":\"zishuoning\"},\"messageId\":\"21898272686\",\"timestamp\":1759277148920}','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(264,'send_award','68013115973','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"703212223759_6\",\"userId\":\"zishuoning\"},\"messageId\":\"68013115973\",\"timestamp\":1759277148920}','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(265,'send_award','49285341270','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"703212223759_7\",\"userId\":\"zishuoning\"},\"messageId\":\"49285341270\",\"timestamp\":1759277148920}','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(266,'send_award','51893922161','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"703212223759_8\",\"userId\":\"zishuoning\"},\"messageId\":\"51893922161\",\"timestamp\":1759277148920}','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(267,'send_award','91528269762','{\"data\":{\"awardId\":101,\"awardTitle\":\"Random credit\",\"orderId\":\"703212223759_9\",\"userId\":\"zishuoning\"},\"messageId\":\"91528269762\",\"timestamp\":1759277148920}','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(268,'credit_adjust_success','56730322082','{\"data\":{\"orderId\":\"708605732355\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-10-01 08:36:22\",\"userId\":\"zishuoning\"},\"messageId\":\"56730322082\",\"timestamp\":1759278982749}','completed','2025-10-01 08:36:22','2025-10-01 08:36:22'),
(269,'credit_adjust_success','05283968708','{\"data\":{\"orderId\":\"451629053416\",\"outBusinessNo\":\"zishuoning_creditPay-1003_2025-10-01 08:37:27\",\"userId\":\"zishuoning\"},\"messageId\":\"05283968708\",\"timestamp\":1759279047731}','completed','2025-10-01 08:37:27','2025-10-01 08:37:27');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
