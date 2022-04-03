/*
 Navicat Premium Data Transfer

 Source Server         : MariaDB
 Source Server Type    : MySQL
 Source Server Version : 100413
 Source Host           : localhost:3306
 Source Schema         : filght_manager

 Target Server Type    : MySQL
 Target Server Version : 100413
 File Encoding         : 65001

 Date: 26/03/2022 11:48:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for airplane
-- ----------------------------
DROP TABLE IF EXISTS `airplane`;
CREATE TABLE `airplane`  (
  `AIRPLANE_ID` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `AIRPLANE_NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `CAPACITY` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`AIRPLANE_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of airplane
-- ----------------------------
INSERT INTO `airplane` VALUES (1, 'BOEING-747', '300');
INSERT INTO `airplane` VALUES (2, 'AIRBUS-A350', '150');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
     `id` int NOT NULL AUTO_INCREMENT,
     `dob` date NULL DEFAULT NULL,
     `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
     `email_verified` bit(1) NULL DEFAULT NULL,
     `gender` bit(1) NULL DEFAULT NULL,
     `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
     `phone` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
     `role` bit(1) NULL DEFAULT NULL,
     `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
     `username` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
     `verification_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
     PRIMARY KEY (`id`) USING BTREE,
     UNIQUE INDEX `UK_ob8kqyqqgmefl0aco34akdtpe`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for location
-- ----------------------------
DROP TABLE IF EXISTS `location`;
CREATE TABLE `location`  (
     `LOCATION_ID` int UNSIGNED NOT NULL AUTO_INCREMENT,
     `COUNTRY_CODE` int UNSIGNED NOT NULL,
     `CITY_CODE` int UNSIGNED NOT NULL,
     `COUNTRY_NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
     `LONGITUDE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
     `LATITUDE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
     `city_id` int NULL DEFAULT NULL,
     `city_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
     PRIMARY KEY (`LOCATION_ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of location
-- ----------------------------
INSERT INTO `location` VALUES (1, 10, 10, 'VIETNAM', '10', '11', NULL, NULL);
INSERT INTO `location` VALUES (2, 221, 22, 'RUSSIA', '12', '23', NULL, NULL);

-- ----------------------------
-- Table structure for airport_geo
-- ----------------------------
DROP TABLE IF EXISTS `airport_geo`;
CREATE TABLE `airport_geo`  (
    `AIRPORT_GEO_ID` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `LOCATION_ID` int UNSIGNED NULL DEFAULT NULL,
    `AIRPORT_NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `DESCRIPTION` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    PRIMARY KEY (`AIRPORT_GEO_ID`) USING BTREE,
    INDEX `LOCATION_ID`(`LOCATION_ID`) USING BTREE,
    CONSTRAINT `airport_geo_ibfk_1` FOREIGN KEY (`LOCATION_ID`) REFERENCES `location` (`LOCATION_ID`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of airport_geo
-- ----------------------------
INSERT INTO `airport_geo` VALUES (1, 1, 'NOIBAI', 'beautiful');
INSERT INTO `airport_geo` VALUES (2, 1, 'DANANG', 'good');

-- ----------------------------
-- Table structure for flight
-- ----------------------------
DROP TABLE IF EXISTS `flight`;
CREATE TABLE `flight`  (
  `FLIGHT_ID` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `FLIGHT_NO` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `FROM_AIRPORT_ID` int UNSIGNED NULL DEFAULT NULL,
  `TO_AIRPORT_ID` int UNSIGNED NULL DEFAULT NULL,
  `AIRPLANE_ID` int UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`FLIGHT_ID`) USING BTREE,
  INDEX `FROM_AIRPORT_ID`(`FROM_AIRPORT_ID`) USING BTREE,
  INDEX `TO_AIRPORT_ID`(`TO_AIRPORT_ID`) USING BTREE,
  INDEX `AIRPLANE_ID`(`AIRPLANE_ID`) USING BTREE,
  INDEX `FLIGHT_NO`(`FLIGHT_NO`) USING BTREE,
  CONSTRAINT `flight_ibfk_1` FOREIGN KEY (`FROM_AIRPORT_ID`) REFERENCES `airport_geo` (`AIRPORT_GEO_ID`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `flight_ibfk_2` FOREIGN KEY (`TO_AIRPORT_ID`) REFERENCES `airport_geo` (`AIRPORT_GEO_ID`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `flight_ibfk_3` FOREIGN KEY (`AIRPLANE_ID`) REFERENCES `airplane` (`AIRPLANE_ID`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flight
-- ----------------------------
INSERT INTO `flight` VALUES (1, 'DN1234', 1, 2, 2);

-- ----------------------------
-- Table structure for flight_log
-- ----------------------------
DROP TABLE IF EXISTS `flight_log`;
CREATE TABLE `flight_log`  (
  `FLIGHT_LOG_ID` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `LOG_DATE` datetime(6) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `USERNAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `FLIGHT_ID` int UNSIGNED NULL DEFAULT NULL,
  `FLIGHT_NO_OLD` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `FLIGHT_NO_NEW` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`FLIGHT_LOG_ID`) USING BTREE,
  INDEX `USERNAME`(`USERNAME`) USING BTREE,
  INDEX `FLIGHT_ID`(`FLIGHT_ID`) USING BTREE,
  INDEX `FLIGHT_NO_OLD`(`FLIGHT_NO_OLD`) USING BTREE,
  INDEX `FLIGHT_NO_NEW`(`FLIGHT_NO_NEW`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flight_log
-- ----------------------------

-- ----------------------------
-- Table structure for flight_schedule
-- ----------------------------
DROP TABLE IF EXISTS `flight_schedule`;
CREATE TABLE `flight_schedule`  (
  `FLIGHT_SCHEDULE_ID` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `START_TIME` datetime(6) NOT NULL,
  `END_TIME` datetime(6) NOT NULL,
  `FLIGHT_NO` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `AVAILABLE_SEAT` bigint UNSIGNED NOT NULL,
  PRIMARY KEY (`FLIGHT_SCHEDULE_ID`) USING BTREE,
  INDEX `flight_no`(`FLIGHT_NO`) USING BTREE,
  INDEX `FLIGHT_SCHEDULE_ID`(`FLIGHT_SCHEDULE_ID`) USING BTREE,
  CONSTRAINT `flight_schedule_ibfk_1` FOREIGN KEY (`FLIGHT_NO`) REFERENCES `flight` (`FLIGHT_NO`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flight_schedule
-- ----------------------------
INSERT INTO `flight_schedule` VALUES (1, '2022-03-08 21:56:54.000000', '2022-03-08 23:56:54.000000', 'DN1234', 146);

-- ----------------------------
-- Table structure for password_reset_token
-- ----------------------------
DROP TABLE IF EXISTS `password_reset_token`;
CREATE TABLE `password_reset_token`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(0) NULL DEFAULT NULL,
  `token` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK5lwtbncug84d4ero33v3cfxvl`(`user_id`) USING BTREE,
  CONSTRAINT `FK5lwtbncug84d4ero33v3cfxvl` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of password_reset_token
-- ----------------------------

-- ----------------------------
-- Table structure for ticket
-- ----------------------------
DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket`  (
  `ticket_id` int NOT NULL AUTO_INCREMENT,
  `booking_state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `flight_schedule_id` int NULL DEFAULT NULL,
  `price` bigint NULL DEFAULT NULL,
  `seat_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `uid` int NULL DEFAULT NULL,
  PRIMARY KEY (`ticket_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ticket
-- ----------------------------
INSERT INTO `ticket` VALUES (9, '1', 0, 1500000, '2', 0);

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '0000-00-00', 'long', b'1', b'1', '', NULL, NULL, '0', NULL, '');
INSERT INTO `user` VALUES (2, '0000-00-00', 'longpp', b'1', b'1', '', NULL, NULL, '0', NULL, 'ff278dd4-53fd-4450-950c-45a07233c119');
INSERT INTO `user` VALUES (3, NULL, 'user1@gmail.com', b'1', NULL, '$2a$10$sYScRrUe6Tg4544AayFKk.igYE0zsfn2Xm8VH5nCSouDr81HsY8ai', NULL, b'0', 'c16ce9ec-a365-4217-af8c-b1eeee6da99a', 'Nguyen Van A', '09Jt0vFrcRLalqZrqz4mr4pBzmR7BMlJd9r9VM0PBGS06efUzjgbbFeZCxiy5EHa');


SET FOREIGN_KEY_CHECKS = 1;
