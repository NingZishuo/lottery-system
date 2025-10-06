/*
SQLyog Community v13.1.7 (64 bit)
MySQL - 8.0.32 : Database - big_market_01
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`big_market_01` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `big_market_01`;

/*Table structure for table `raffle_activity_account` */

DROP TABLE IF EXISTS `raffle_activity_account`;

CREATE TABLE `raffle_activity_account` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `total_count` int NOT NULL COMMENT '总次数',
  `total_count_surplus` int NOT NULL COMMENT '总次数-剩余',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_id_activity_id` (`user_id`,`activity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='抽奖活动账户表';

/*Data for the table `raffle_activity_account` */

insert  into `raffle_activity_account`(`id`,`user_id`,`activity_id`,`total_count`,`total_count_surplus`,`create_time`,`update_time`) values 
(4,'zishuoning',101,31,20,'2025-10-01 07:45:58','2025-10-01 08:37:27');

/*Table structure for table `raffle_activity_account_day` */

DROP TABLE IF EXISTS `raffle_activity_account_day`;

CREATE TABLE `raffle_activity_account_day` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `day` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '日期（yyyy-mm-dd）',
  `day_count` int NOT NULL COMMENT '日次数',
  `day_count_surplus` int NOT NULL COMMENT '日次数-剩余',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_id_activity_id_day` (`user_id`,`activity_id`,`day`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='抽奖活动账户表-日次数';

/*Data for the table `raffle_activity_account_day` */

insert  into `raffle_activity_account_day`(`id`,`user_id`,`activity_id`,`day`,`day_count`,`day_count_surplus`,`create_time`,`update_time`) values 
(12,'zishuoning',101,'2025-10-01',31,20,'2025-10-01 07:46:01','2025-10-01 08:37:27');

/*Table structure for table `raffle_activity_account_month` */

DROP TABLE IF EXISTS `raffle_activity_account_month`;

CREATE TABLE `raffle_activity_account_month` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `month` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '月（yyyy-mm）',
  `month_count` int NOT NULL COMMENT '月次数',
  `month_count_surplus` int NOT NULL COMMENT '月次数-剩余',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_id_activity_id_month` (`user_id`,`activity_id`,`month`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='抽奖活动账户表-月次数';

/*Data for the table `raffle_activity_account_month` */

insert  into `raffle_activity_account_month`(`id`,`user_id`,`activity_id`,`month`,`month_count`,`month_count_surplus`,`create_time`,`update_time`) values 
(9,'zishuoning',101,'2025-10',31,20,'2025-10-01 07:46:01','2025-10-01 08:37:27');

/*Table structure for table `raffle_activity_order_000` */

DROP TABLE IF EXISTS `raffle_activity_order_000`;

CREATE TABLE `raffle_activity_order_000` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `sku` bigint NOT NULL COMMENT '商品sku',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `activity_name` varchar(64) NOT NULL COMMENT '活动名称',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `total_count` int NOT NULL COMMENT '总次数',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额【积分】',
  `state` varchar(16) NOT NULL DEFAULT 'complete' COMMENT '订单状态（complete,wait_pay）',
  `out_business_no` varchar(64) NOT NULL COMMENT '业务仿重ID - 外部透传的，确保幂等',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_out_business_no` (`out_business_no`),
  KEY `idx_user_id_activity_id` (`user_id`,`activity_id`,`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='抽奖活动单';

/*Data for the table `raffle_activity_order_000` */

/*Table structure for table `raffle_activity_order_001` */

DROP TABLE IF EXISTS `raffle_activity_order_001`;

CREATE TABLE `raffle_activity_order_001` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `sku` bigint NOT NULL COMMENT '商品sku',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `activity_name` varchar(64) NOT NULL COMMENT '活动名称',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `total_count` int NOT NULL COMMENT '总次数',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额【积分】',
  `state` varchar(16) NOT NULL DEFAULT 'complete' COMMENT '订单状态（complete,wait_pay）',
  `out_business_no` varchar(64) NOT NULL COMMENT '业务仿重ID - 外部透传的，确保幂等',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_out_business_no` (`out_business_no`),
  KEY `idx_user_id_activity_id` (`user_id`,`activity_id`,`state`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='抽奖活动单';

/*Data for the table `raffle_activity_order_001` */

insert  into `raffle_activity_order_001`(`id`,`user_id`,`sku`,`activity_id`,`activity_name`,`strategy_id`,`order_id`,`order_time`,`total_count`,`pay_amount`,`state`,`out_business_no`,`create_time`,`update_time`) values 
(42,'zishuoning',1001,101,'activity_1',100001,'554965739003','2025-09-28 20:03:04',1,0.00,'completed','zishuoning_sign-sku_2025-09-28','2025-09-28 19:03:05','2025-09-28 19:03:05'),
(43,'zishuoning',1003,101,'activity_1',100001,'307419076949','2025-09-28 20:03:34',10,15.78,'wait_pay','zishuoning_creditPay-1003_2025-09-28 19:03:33','2025-09-28 19:03:35','2025-09-28 19:03:35'),
(44,'zishuoning',1002,101,'activity_1',100001,'670032552281','2025-09-28 20:05:30',5,10.56,'completed','zishuoning_creditPay-1002_2025-09-28 19:05:28','2025-09-28 19:05:32','2025-09-28 19:05:37'),
(45,'zishuoning',1003,101,'activity_1',100001,'106515638074','2025-09-28 20:06:07',10,15.78,'completed','zishuoning_creditPay-1003_2025-09-28 19:06:05','2025-09-28 19:06:08','2025-09-28 19:06:12'),
(46,'zishuoning',1003,101,'activity_1',100001,'516493497698','2025-09-28 20:07:03',10,15.78,'completed','zishuoning_creditPay-1003_2025-09-28 19:07:02','2025-09-28 19:07:04','2025-09-28 19:07:07'),
(47,'zishuoning',1003,101,'activity_1',100001,'057317595502','2025-09-28 20:07:10',10,15.78,'completed','zishuoning_creditPay-1003_2025-09-28 19:07:09','2025-09-28 19:07:12','2025-09-28 19:07:17'),
(48,'zishuoning',1003,101,'activity_1',100001,'976614830414','2025-09-28 20:07:25',10,15.78,'completed','zishuoning_creditPay-1003_2025-09-28 19:07:24','2025-09-28 19:07:26','2025-09-28 19:07:29'),
(49,'zishuoning',1003,101,'activity_1',100001,'782821363209','2025-09-28 20:07:36',10,15.78,'completed','zishuoning_creditPay-1003_2025-09-28 19:07:35','2025-09-28 19:07:37','2025-09-28 19:07:41'),
(50,'zishuoning',1001,101,'activity_1',100001,'162208133614','2025-10-01 07:45:58',1,0.00,'completed','zishuoning_sign-sku_2025-10-01','2025-10-01 07:45:58','2025-10-01 07:45:58'),
(51,'zishuoning',1003,101,'activity_1',100001,'537489380007','2025-10-01 07:46:08',10,15.78,'wait_pay','zishuoning_creditPay-1003_2025-10-01 07:46:08','2025-10-01 07:46:08','2025-10-01 07:46:08'),
(52,'zishuoning',1003,101,'activity_1',100001,'573268248633','2025-10-01 07:46:17',10,15.78,'wait_pay','zishuoning_creditPay-1003_2025-10-01 07:46:17','2025-10-01 07:46:17','2025-10-01 07:46:17'),
(53,'zishuoning',1003,101,'activity_1',100001,'529688996518','2025-10-01 07:46:27',10,15.78,'wait_pay','zishuoning_creditPay-1003_2025-10-01 07:46:27','2025-10-01 07:46:27','2025-10-01 07:46:27'),
(54,'zishuoning',1003,101,'activity_1',100001,'820157129068','2025-10-01 07:46:56',10,15.78,'wait_pay','zishuoning_creditPay-1003_2025-10-01 07:46:55','2025-10-01 07:46:55','2025-10-01 07:46:55'),
(55,'zishuoning',1003,101,'activity_1',100001,'355676585187','2025-10-01 07:47:05',10,15.78,'wait_pay','zishuoning_creditPay-1003_2025-10-01 07:47:04','2025-10-01 07:47:04','2025-10-01 07:47:04'),
(56,'zishuoning',1003,101,'activity_1',100001,'982971000722','2025-10-01 07:47:54',10,15.78,'wait_pay','zishuoning_creditPay-1003_2025-10-01 07:47:54','2025-10-01 07:47:54','2025-10-01 07:47:54'),
(57,'zishuoning',1003,101,'activity_1',100001,'800591500658','2025-10-01 08:05:40',10,15.78,'completed','zishuoning_creditPay-1003_2025-10-01 08:05:39','2025-10-01 08:05:40','2025-10-01 08:05:41'),
(58,'zishuoning',1003,101,'activity_1',100001,'708605732355','2025-10-01 08:36:23',10,15.78,'completed','zishuoning_creditPay-1003_2025-10-01 08:36:22','2025-10-01 08:36:22','2025-10-01 08:36:22'),
(59,'zishuoning',1003,101,'activity_1',100001,'451629053416','2025-10-01 08:37:28',10,15.78,'completed','zishuoning_creditPay-1003_2025-10-01 08:37:27','2025-10-01 08:37:27','2025-10-01 08:37:27');

/*Table structure for table `raffle_activity_order_002` */

DROP TABLE IF EXISTS `raffle_activity_order_002`;

CREATE TABLE `raffle_activity_order_002` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `sku` bigint NOT NULL COMMENT '商品sku',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `activity_name` varchar(64) NOT NULL COMMENT '活动名称',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `total_count` int NOT NULL COMMENT '总次数',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额【积分】',
  `state` varchar(16) NOT NULL DEFAULT 'complete' COMMENT '订单状态（complete,wait_pay）',
  `out_business_no` varchar(64) NOT NULL COMMENT '业务仿重ID - 外部透传的，确保幂等',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_out_business_no` (`out_business_no`),
  KEY `idx_user_id_activity_id` (`user_id`,`activity_id`,`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='抽奖活动单';

/*Data for the table `raffle_activity_order_002` */

/*Table structure for table `raffle_activity_order_003` */

DROP TABLE IF EXISTS `raffle_activity_order_003`;

CREATE TABLE `raffle_activity_order_003` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `sku` bigint NOT NULL COMMENT '商品sku',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `activity_name` varchar(64) NOT NULL COMMENT '活动名称',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `total_count` int NOT NULL COMMENT '总次数',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额【积分】',
  `state` varchar(16) NOT NULL DEFAULT 'complete' COMMENT '订单状态（complete,wait_pay）',
  `out_business_no` varchar(64) NOT NULL COMMENT '业务仿重ID - 外部透传的，确保幂等',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_out_business_no` (`out_business_no`),
  KEY `idx_user_id_activity_id` (`user_id`,`activity_id`,`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='抽奖活动单';

/*Data for the table `raffle_activity_order_003` */

/*Table structure for table `user_award_record_000` */

DROP TABLE IF EXISTS `user_award_record_000`;

CREATE TABLE `user_award_record_000` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `order_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '抽奖订单ID【作为幂等使用】',
  `award_id` int NOT NULL COMMENT '奖品ID',
  `award_title` varchar(128) NOT NULL COMMENT '奖品标题（名称）',
  `award_config` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '奖品配置信息',
  `award_time` datetime NOT NULL COMMENT '中奖时间',
  `award_state` varchar(16) NOT NULL DEFAULT 'create' COMMENT '奖品状态；create-创建、completed-发奖完成',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_award_id` (`strategy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户中奖记录表';

/*Data for the table `user_award_record_000` */

/*Table structure for table `user_award_record_001` */

DROP TABLE IF EXISTS `user_award_record_001`;

CREATE TABLE `user_award_record_001` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `order_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '抽奖订单ID【作为幂等使用】',
  `award_id` int NOT NULL COMMENT '奖品ID',
  `award_title` varchar(128) NOT NULL COMMENT '奖品标题（名称）',
  `award_config` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '奖品配置信息',
  `award_time` datetime NOT NULL COMMENT '中奖时间',
  `award_state` varchar(16) NOT NULL DEFAULT 'create' COMMENT '奖品状态；create-创建、completed-发奖完成',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_award_id` (`strategy_id`)
) ENGINE=InnoDB AUTO_INCREMENT=182 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户中奖记录表';

/*Data for the table `user_award_record_001` */

insert  into `user_award_record_001`(`id`,`user_id`,`activity_id`,`strategy_id`,`order_id`,`award_id`,`award_title`,`award_config`,`award_time`,`award_state`,`create_time`,`update_time`) values 
(171,'zishuoning',101,100001,'722320976868',101,'Random credit',NULL,'2025-10-01 07:46:02','completed','2025-10-01 07:46:01','2025-10-01 07:46:02'),
(172,'zishuoning',101,100001,'703212223759_0',101,'Random credit',NULL,'2025-10-01 08:05:48','completed','2025-10-01 08:05:48','2025-10-01 08:05:49'),
(173,'zishuoning',101,100001,'703212223759_1',104,'10 uses of Gemini',NULL,'2025-10-01 08:05:48','create','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(174,'zishuoning',101,100001,'703212223759_2',101,'Random credit',NULL,'2025-10-01 08:05:48','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(175,'zishuoning',101,100001,'703212223759_3',102,'keyBoard',NULL,'2025-10-01 08:05:48','create','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(176,'zishuoning',101,100001,'703212223759_4',102,'keyBoard',NULL,'2025-10-01 08:05:48','create','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(177,'zishuoning',101,100001,'703212223759_5',101,'Random credit',NULL,'2025-10-01 08:05:48','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(178,'zishuoning',101,100001,'703212223759_6',101,'Random credit',NULL,'2025-10-01 08:05:48','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(179,'zishuoning',101,100001,'703212223759_7',101,'Random credit',NULL,'2025-10-01 08:05:48','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(180,'zishuoning',101,100001,'703212223759_8',101,'Random credit',NULL,'2025-10-01 08:05:48','completed','2025-10-01 08:05:49','2025-10-01 08:05:49'),
(181,'zishuoning',101,100001,'703212223759_9',101,'Random credit',NULL,'2025-10-01 08:05:48','completed','2025-10-01 08:05:49','2025-10-01 08:05:49');

/*Table structure for table `user_award_record_002` */

DROP TABLE IF EXISTS `user_award_record_002`;

CREATE TABLE `user_award_record_002` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `order_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '抽奖订单ID【作为幂等使用】',
  `award_id` int NOT NULL COMMENT '奖品ID',
  `award_title` varchar(128) NOT NULL COMMENT '奖品标题（名称）',
  `award_config` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '奖品配置信息',
  `award_time` datetime NOT NULL COMMENT '中奖时间',
  `award_state` varchar(16) NOT NULL DEFAULT 'create' COMMENT '奖品状态；create-创建、completed-发奖完成',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_award_id` (`strategy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户中奖记录表';

/*Data for the table `user_award_record_002` */

/*Table structure for table `user_award_record_003` */

DROP TABLE IF EXISTS `user_award_record_003`;

CREATE TABLE `user_award_record_003` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `order_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '抽奖订单ID【作为幂等使用】',
  `award_id` int NOT NULL COMMENT '奖品ID',
  `award_title` varchar(128) NOT NULL COMMENT '奖品标题（名称）',
  `award_config` varchar(32) DEFAULT NULL COMMENT '奖品配置信息',
  `award_time` datetime NOT NULL COMMENT '中奖时间',
  `award_state` varchar(16) NOT NULL DEFAULT 'create' COMMENT '奖品状态；create-创建、completed-发奖完成',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_award_id` (`strategy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户中奖记录表';

/*Data for the table `user_award_record_003` */

/*Table structure for table `user_behavior_rebate_order_000` */

DROP TABLE IF EXISTS `user_behavior_rebate_order_000`;

CREATE TABLE `user_behavior_rebate_order_000` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `behavior_type` varchar(16) NOT NULL COMMENT '行为类型（sign 签到、openai_pay 支付）',
  `rebate_desc` varchar(128) NOT NULL COMMENT '返利描述',
  `rebate_type` varchar(16) NOT NULL COMMENT '返利类型（sku 活动库存充值商品、integral 用户活动积分）',
  `rebate_config` varchar(32) NOT NULL COMMENT '返利配置【sku值，积分值】',
  `out_business_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务ID - 拼接的唯一值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_biz_id` (`out_business_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户行为返利流水订单表';

/*Data for the table `user_behavior_rebate_order_000` */

/*Table structure for table `user_behavior_rebate_order_001` */

DROP TABLE IF EXISTS `user_behavior_rebate_order_001`;

CREATE TABLE `user_behavior_rebate_order_001` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `behavior_type` varchar(16) NOT NULL COMMENT '行为类型（sign 签到、openai_pay 支付）',
  `rebate_desc` varchar(128) NOT NULL COMMENT '返利描述',
  `rebate_type` varchar(16) NOT NULL COMMENT '返利类型（sku 活动库存充值商品、integral 用户活动积分）',
  `rebate_config` varchar(32) NOT NULL COMMENT '返利配置【sku值，积分值】',
  `out_business_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务ID - 拼接的唯一值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_biz_id` (`out_business_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户行为返利流水订单表';

/*Data for the table `user_behavior_rebate_order_001` */

insert  into `user_behavior_rebate_order_001`(`id`,`user_id`,`order_id`,`behavior_type`,`rebate_desc`,`rebate_type`,`rebate_config`,`out_business_no`,`create_time`,`update_time`) values 
(47,'zishuoning','554965739003','sign','sign-sku','sku','1001','zishuoning_sign-sku_2025-09-28','2025-09-28 19:03:03','2025-09-28 19:03:03'),
(48,'zishuoning','787499541536','sign','sign-integral','integral','15','zishuoning_sign-integral_2025-09-28','2025-09-28 19:03:04','2025-09-28 19:03:04'),
(49,'zishuoning','162208133614','sign','sign-sku','sku','1001','zishuoning_sign-sku_2025-10-01','2025-10-01 07:45:57','2025-10-01 07:45:57'),
(50,'zishuoning','093147367038','sign','sign-integral','integral','15','zishuoning_sign-integral_2025-10-01','2025-10-01 07:45:57','2025-10-01 07:45:57');

/*Table structure for table `user_behavior_rebate_order_002` */

DROP TABLE IF EXISTS `user_behavior_rebate_order_002`;

CREATE TABLE `user_behavior_rebate_order_002` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `behavior_type` varchar(16) NOT NULL COMMENT '行为类型（sign 签到、openai_pay 支付）',
  `rebate_desc` varchar(128) NOT NULL COMMENT '返利描述',
  `rebate_type` varchar(16) NOT NULL COMMENT '返利类型（sku 活动库存充值商品、integral 用户活动积分）',
  `rebate_config` varchar(32) NOT NULL COMMENT '返利配置【sku值，积分值】',
  `out_business_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务ID - 拼接的唯一值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_biz_id` (`out_business_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户行为返利流水订单表';

/*Data for the table `user_behavior_rebate_order_002` */

/*Table structure for table `user_behavior_rebate_order_003` */

DROP TABLE IF EXISTS `user_behavior_rebate_order_003`;

CREATE TABLE `user_behavior_rebate_order_003` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `behavior_type` varchar(16) NOT NULL COMMENT '行为类型（sign 签到、openai_pay 支付）',
  `rebate_desc` varchar(128) NOT NULL COMMENT '返利描述',
  `rebate_type` varchar(16) NOT NULL COMMENT '返利类型（sku 活动库存充值商品、integral 用户活动积分）',
  `rebate_config` varchar(32) NOT NULL COMMENT '返利配置【sku值，积分值】',
  `out_business_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务ID - 拼接的唯一值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_biz_id` (`out_business_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户行为返利流水订单表';

/*Data for the table `user_behavior_rebate_order_003` */

/*Table structure for table `user_credit_account` */

DROP TABLE IF EXISTS `user_credit_account`;

CREATE TABLE `user_credit_account` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总积分，显示总账户值，记得一个人获得的总积分',
  `available_amount` decimal(10,2) NOT NULL COMMENT '可用积分，每次扣减的值',
  `account_status` varchar(8) NOT NULL COMMENT '账户状态【open - 可用，close - 冻结】',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户积分账户';

/*Data for the table `user_credit_account` */

insert  into `user_credit_account`(`id`,`user_id`,`total_amount`,`available_amount`,`account_status`,`create_time`,`update_time`) values 
(3,'zishuoning',1758.47,1711.13,'open','2025-10-01 07:45:58','2025-10-01 08:37:27');

/*Table structure for table `user_credit_order_000` */

DROP TABLE IF EXISTS `user_credit_order_000`;

CREATE TABLE `user_credit_order_000` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `trade_name` varchar(32) NOT NULL COMMENT '交易名称',
  `trade_type` varchar(8) NOT NULL DEFAULT 'forward' COMMENT '交易类型；forward-正向、reverse-逆向',
  `trade_amount` decimal(10,2) NOT NULL COMMENT '交易金额',
  `out_business_no` varchar(64) NOT NULL COMMENT '业务仿重ID - 外部透传。返利、行为等唯一标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_out_business_no` (`out_business_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户积分订单记录';

/*Data for the table `user_credit_order_000` */

/*Table structure for table `user_credit_order_001` */

DROP TABLE IF EXISTS `user_credit_order_001`;

CREATE TABLE `user_credit_order_001` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `trade_name` varchar(32) NOT NULL COMMENT '交易名称',
  `trade_type` varchar(8) NOT NULL DEFAULT 'forward' COMMENT '交易类型；forward-正向、reverse-逆向',
  `trade_amount` decimal(10,2) NOT NULL COMMENT '交易金额',
  `out_business_no` varchar(64) NOT NULL COMMENT '业务仿重ID - 外部透传。返利、行为等唯一标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_out_business_no` (`out_business_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户积分订单记录';

/*Data for the table `user_credit_order_001` */

insert  into `user_credit_order_001`(`id`,`user_id`,`order_id`,`trade_name`,`trade_type`,`trade_amount`,`out_business_no`,`create_time`,`update_time`) values 
(47,'zishuoning','787499541536','行为返利','forward',15.00,'zishuoning_sign-integral_2025-09-28','2025-09-28 19:03:08','2025-09-28 19:03:08'),
(48,'zishuoning','670032552281','兑换抽奖','reverse',-10.56,'zishuoning_creditPay-1002_2025-09-28 19:05:28','2025-09-28 19:05:34','2025-09-28 19:05:34'),
(49,'zishuoning','106515638074','兑换抽奖','reverse',-15.78,'zishuoning_creditPay-1003_2025-09-28 19:06:05','2025-09-28 19:06:10','2025-09-28 19:06:10'),
(50,'zishuoning','516493497698','兑换抽奖','reverse',-15.78,'zishuoning_creditPay-1003_2025-09-28 19:07:02','2025-09-28 19:07:05','2025-09-28 19:07:05'),
(51,'zishuoning','057317595502','兑换抽奖','reverse',-15.78,'zishuoning_creditPay-1003_2025-09-28 19:07:09','2025-09-28 19:07:14','2025-09-28 19:07:14'),
(52,'zishuoning','976614830414','兑换抽奖','reverse',-15.78,'zishuoning_creditPay-1003_2025-09-28 19:07:24','2025-09-28 19:07:28','2025-09-28 19:07:28'),
(53,'zishuoning','782821363209','兑换抽奖','reverse',-15.78,'zishuoning_creditPay-1003_2025-09-28 19:07:35','2025-09-28 19:07:39','2025-09-28 19:07:39'),
(54,'zishuoning','093147367038','行为返利','forward',15.00,'zishuoning_sign-integral_2025-10-01','2025-10-01 07:45:58','2025-10-01 07:45:58'),
(55,'zishuoning','800591500658','兑换抽奖','reverse',-15.78,'zishuoning_creditPay-1003_2025-10-01 08:05:39','2025-10-01 08:05:40','2025-10-01 08:05:40'),
(56,'zishuoning','708605732355','兑换抽奖','reverse',-15.78,'zishuoning_creditPay-1003_2025-10-01 08:36:22','2025-10-01 08:36:22','2025-10-01 08:36:22'),
(57,'zishuoning','451629053416','兑换抽奖','reverse',-15.78,'zishuoning_creditPay-1003_2025-10-01 08:37:27','2025-10-01 08:37:27','2025-10-01 08:37:27');

/*Table structure for table `user_credit_order_002` */

DROP TABLE IF EXISTS `user_credit_order_002`;

CREATE TABLE `user_credit_order_002` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `trade_name` varchar(32) NOT NULL COMMENT '交易名称',
  `trade_type` varchar(8) NOT NULL DEFAULT 'forward' COMMENT '交易类型；forward-正向、reverse-逆向',
  `trade_amount` decimal(10,2) NOT NULL COMMENT '交易金额',
  `out_business_no` varchar(64) NOT NULL COMMENT '业务仿重ID - 外部透传。返利、行为等唯一标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_out_business_no` (`out_business_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户积分订单记录';

/*Data for the table `user_credit_order_002` */

/*Table structure for table `user_credit_order_003` */

DROP TABLE IF EXISTS `user_credit_order_003`;

CREATE TABLE `user_credit_order_003` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `trade_name` varchar(32) NOT NULL COMMENT '交易名称',
  `trade_type` varchar(8) NOT NULL DEFAULT 'forward' COMMENT '交易类型；forward-正向、reverse-逆向',
  `trade_amount` decimal(10,2) NOT NULL COMMENT '交易金额',
  `out_business_no` varchar(64) NOT NULL COMMENT '业务仿重ID - 外部透传。返利、行为等唯一标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  UNIQUE KEY `uq_out_business_no` (`out_business_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户积分订单记录';

/*Data for the table `user_credit_order_003` */

/*Table structure for table `user_raffle_order_000` */

DROP TABLE IF EXISTS `user_raffle_order_000`;

CREATE TABLE `user_raffle_order_000` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `activity_name` varchar(64) NOT NULL COMMENT '活动名称',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `raffle_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '种类:single-单抽、ten-十连抽',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `order_state` varchar(16) NOT NULL DEFAULT 'create' COMMENT '订单状态；create-创建、used-已使用、cancel-已作废',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id_activity_id` (`user_id`,`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户抽奖订单表';

/*Data for the table `user_raffle_order_000` */

/*Table structure for table `user_raffle_order_001` */

DROP TABLE IF EXISTS `user_raffle_order_001`;

CREATE TABLE `user_raffle_order_001` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `activity_name` varchar(64) NOT NULL COMMENT '活动名称',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `raffle_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '种类:single-单抽、ten-十连抽',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `order_state` varchar(16) NOT NULL DEFAULT 'create' COMMENT '订单状态；create-创建、used-已使用、cancel-已作废',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id_activity_id` (`user_id`,`activity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户抽奖订单表';

/*Data for the table `user_raffle_order_001` */

insert  into `user_raffle_order_001`(`id`,`user_id`,`activity_id`,`activity_name`,`strategy_id`,`raffle_type`,`order_id`,`order_time`,`order_state`,`create_time`,`update_time`) values 
(35,'zishuoning',101,'activity_1',100001,'single','048302991251','2025-09-28 19:05:44','used','2025-09-28 19:05:47','2025-09-28 19:05:56'),
(36,'zishuoning',101,'activity_1',100001,'ten','464048829393','2025-09-28 19:06:18','used','2025-09-28 19:06:20','2025-09-28 19:06:26'),
(37,'zishuoning',101,'activity_1',100001,'ten','047903597207','2025-09-28 19:07:46','used','2025-09-28 19:07:48','2025-09-28 19:07:54'),
(38,'zishuoning',101,'activity_1',100001,'ten','665673982795','2025-09-28 19:08:01','used','2025-09-28 19:08:06','2025-09-28 19:08:20'),
(39,'zishuoning',101,'activity_1',100001,'ten','836383070383','2025-09-28 19:08:31','used','2025-09-28 19:08:35','2025-09-28 19:08:46'),
(40,'zishuoning',101,'activity_1',100001,'ten','288449600760','2025-09-28 19:11:19','used','2025-09-28 19:11:21','2025-09-28 19:11:26'),
(41,'zishuoning',101,'activity_1',100001,'ten','127631625992','2025-09-28 19:13:03','used','2025-09-28 19:13:06','2025-09-28 19:13:14'),
(42,'zishuoning',101,'activity_1',100001,'ten','445228452015','2025-09-28 19:13:22','used','2025-09-28 19:13:24','2025-09-28 19:13:32'),
(43,'zishuoning',101,'activity_1',100001,'ten','567184804661','2025-09-28 19:13:42','used','2025-09-28 19:13:45','2025-09-28 19:13:53'),
(44,'zishuoning',101,'activity_1',100001,'ten','671968304558','2025-09-28 19:14:06','used','2025-09-28 19:14:08','2025-09-28 19:14:14'),
(45,'zishuoning',101,'activity_1',100001,'ten','093849903794','2025-09-28 19:14:27','used','2025-09-28 19:14:29','2025-09-28 19:14:34'),
(46,'zishuoning',101,'activity_1',100001,'single','818536963916','2025-09-29 00:31:14','used','2025-09-29 00:31:15','2025-09-29 00:31:16'),
(47,'zishuoning',101,'activity_1',100001,'single','722320976868','2025-10-01 07:46:01','used','2025-10-01 07:46:01','2025-10-01 07:46:01'),
(48,'zishuoning',101,'activity_1',100001,'ten','703212223759','2025-10-01 08:05:48','used','2025-10-01 08:05:48','2025-10-01 08:05:48');

/*Table structure for table `user_raffle_order_002` */

DROP TABLE IF EXISTS `user_raffle_order_002`;

CREATE TABLE `user_raffle_order_002` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `activity_name` varchar(64) NOT NULL COMMENT '活动名称',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `raffle_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '种类:single-单抽、ten-十连抽',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `order_state` varchar(16) NOT NULL DEFAULT 'create' COMMENT '订单状态；create-创建、used-已使用、cancel-已作废',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id_activity_id` (`user_id`,`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户抽奖订单表';

/*Data for the table `user_raffle_order_002` */

/*Table structure for table `user_raffle_order_003` */

DROP TABLE IF EXISTS `user_raffle_order_003`;

CREATE TABLE `user_raffle_order_003` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(32) NOT NULL COMMENT '用户ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `activity_name` varchar(64) NOT NULL COMMENT '活动名称',
  `strategy_id` bigint NOT NULL COMMENT '抽奖策略ID',
  `raffle_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '种类:single-单抽、ten-十连抽',
  `order_id` varchar(12) NOT NULL COMMENT '订单ID',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `order_state` varchar(16) NOT NULL DEFAULT 'create' COMMENT '订单状态；create-创建、used-已使用、cancel-已作废',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_order_id` (`order_id`),
  KEY `idx_user_id_activity_id` (`user_id`,`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户抽奖订单表';

/*Data for the table `user_raffle_order_003` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
