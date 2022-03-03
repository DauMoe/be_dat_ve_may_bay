-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 03, 2022 at 06:00 PM
-- Server version: 10.4.13-MariaDB
-- PHP Version: 7.2.32

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `flight_manager`
--

-- --------------------------------------------------------

--
-- Table structure for table `airplane`
--

CREATE TABLE `airplane` (
  `AIRPLANE_ID` int(10) UNSIGNED NOT NULL,
  `AIRPLANE_NAME` varchar(255) NOT NULL,
  `CAPACITY` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `airport_geo`
--

CREATE TABLE `airport_geo` (
  `AIRPORT_GEO_ID` int(10) UNSIGNED NOT NULL,
  `LOCATION_ID` int(10) UNSIGNED DEFAULT NULL,
  `AIRPORT_NAME` varchar(255) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `flight`
--

CREATE TABLE `flight` (
  `FLIGHT_ID` int(10) UNSIGNED NOT NULL,
  `FLIGHT_NO` varchar(255) DEFAULT NULL,
  `FROM_AIRPORT_ID` int(10) UNSIGNED DEFAULT NULL,
  `TO_AIRPORT_ID` int(10) UNSIGNED DEFAULT NULL,
  `AIRPLANE_ID` int(10) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `flight_log`
--

CREATE TABLE `flight_log` (
  `FLIGHT_LOG_ID` int(10) UNSIGNED NOT NULL,
  `LOG_DATE` datetime(6) DEFAULT NULL ON UPDATE current_timestamp(6),
  `USERNAME` varchar(255) DEFAULT NULL,
  `FLIGHT_ID` int(10) UNSIGNED DEFAULT NULL,
  `FLIGHT_NO_OLD` varchar(255) DEFAULT NULL,
  `FLIGHT_NO_NEW` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `flight_schedule`
--

CREATE TABLE `flight_schedule` (
  `FLIGHT_SCHEDULE_ID` int(10) UNSIGNED NOT NULL,
  `START_TIME` datetime(6) NOT NULL,
  `END_TIME` datetime(6) NOT NULL,
  `FLIGHT_NO` varchar(255) NOT NULL,
  `AVAILABLE_SEAT` bigint(20) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `location`
--

CREATE TABLE `location` (
  `LOCATION_ID` int(10) UNSIGNED NOT NULL,
  `COUNTRY_CODE` int(10) UNSIGNED NOT NULL,
  `CITY_CODE` int(10) UNSIGNED NOT NULL,
  `COUNTRY_NAME` varchar(255) DEFAULT NULL,
  `LONGITUDE` varchar(255) DEFAULT NULL,
  `LATITUDE` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `ticket`
--

CREATE TABLE `ticket` (
  `TICKET_ID` int(10) UNSIGNED NOT NULL,
  `FLIGHT_SCHEDULE_ID` int(10) UNSIGNED DEFAULT NULL,
  `SEAT_NUMBER` varchar(255) DEFAULT NULL,
  `PRICE` bigint(20) DEFAULT NULL,
  `UID` int(10) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `UID` int(10) UNSIGNED NOT NULL,
  `EMAIL` varchar(255) NOT NULL,
  `USERNAME` varchar(255) NOT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  `EMAIL_VERIFIED` tinyint(1) UNSIGNED DEFAULT 0,
  `DOB` date DEFAULT NULL,
  `GENDER` varchar(255) DEFAULT NULL,
  `PHONE` varchar(255) DEFAULT NULL,
  `ROLE` tinyint(1) UNSIGNED DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `airplane`
--
ALTER TABLE `airplane`
  ADD PRIMARY KEY (`AIRPLANE_ID`);

--
-- Indexes for table `airport_geo`
--
ALTER TABLE `airport_geo`
  ADD PRIMARY KEY (`AIRPORT_GEO_ID`),
  ADD KEY `LOCATION_ID` (`LOCATION_ID`);

--
-- Indexes for table `flight`
--
ALTER TABLE `flight`
  ADD PRIMARY KEY (`FLIGHT_ID`),
  ADD KEY `FROM_AIRPORT_ID` (`FROM_AIRPORT_ID`),
  ADD KEY `TO_AIRPORT_ID` (`TO_AIRPORT_ID`),
  ADD KEY `AIRPLANE_ID` (`AIRPLANE_ID`),
  ADD KEY `FLIGHT_NO` (`FLIGHT_NO`);

--
-- Indexes for table `flight_log`
--
ALTER TABLE `flight_log`
  ADD PRIMARY KEY (`FLIGHT_LOG_ID`),
  ADD KEY `USERNAME` (`USERNAME`),
  ADD KEY `FLIGHT_ID` (`FLIGHT_ID`),
  ADD KEY `FLIGHT_NO_OLD` (`FLIGHT_NO_OLD`),
  ADD KEY `FLIGHT_NO_NEW` (`FLIGHT_NO_NEW`);

--
-- Indexes for table `flight_schedule`
--
ALTER TABLE `flight_schedule`
  ADD PRIMARY KEY (`FLIGHT_SCHEDULE_ID`) USING BTREE,
  ADD KEY `flight_no` (`FLIGHT_NO`),
  ADD KEY `FLIGHT_SCHEDULE_ID` (`FLIGHT_SCHEDULE_ID`);

--
-- Indexes for table `location`
--
ALTER TABLE `location`
  ADD PRIMARY KEY (`LOCATION_ID`);

--
-- Indexes for table `ticket`
--
ALTER TABLE `ticket`
  ADD PRIMARY KEY (`TICKET_ID`),
  ADD KEY `FLIGHT_SCHEDULE_ID` (`FLIGHT_SCHEDULE_ID`),
  ADD KEY `UID` (`UID`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`UID`),
  ADD UNIQUE KEY `EMAIL` (`EMAIL`) USING BTREE,
  ADD UNIQUE KEY `USERNAME_2` (`USERNAME`) USING BTREE,
  ADD KEY `USERNAME` (`USERNAME`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `airplane`
--
ALTER TABLE `airplane`
  MODIFY `AIRPLANE_ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `airport_geo`
--
ALTER TABLE `airport_geo`
  MODIFY `AIRPORT_GEO_ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `flight`
--
ALTER TABLE `flight`
  MODIFY `FLIGHT_ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `flight_log`
--
ALTER TABLE `flight_log`
  MODIFY `FLIGHT_LOG_ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `flight_schedule`
--
ALTER TABLE `flight_schedule`
  MODIFY `FLIGHT_SCHEDULE_ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `location`
--
ALTER TABLE `location`
  MODIFY `LOCATION_ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ticket`
--
ALTER TABLE `ticket`
  MODIFY `TICKET_ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `UID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `airport_geo`
--
ALTER TABLE `airport_geo`
  ADD CONSTRAINT `airport_geo_ibfk_1` FOREIGN KEY (`LOCATION_ID`) REFERENCES `location` (`LOCATION_ID`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `flight`
--
ALTER TABLE `flight`
  ADD CONSTRAINT `flight_ibfk_1` FOREIGN KEY (`FROM_AIRPORT_ID`) REFERENCES `airport_geo` (`AIRPORT_GEO_ID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `flight_ibfk_2` FOREIGN KEY (`TO_AIRPORT_ID`) REFERENCES `airport_geo` (`AIRPORT_GEO_ID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `flight_ibfk_3` FOREIGN KEY (`AIRPLANE_ID`) REFERENCES `airplane` (`AIRPLANE_ID`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `flight_log`
--
ALTER TABLE `flight_log`
  ADD CONSTRAINT `flight_log_ibfk_1` FOREIGN KEY (`USERNAME`) REFERENCES `user` (`USERNAME`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `flight_log_ibfk_2` FOREIGN KEY (`FLIGHT_ID`) REFERENCES `flight` (`FLIGHT_ID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `flight_log_ibfk_3` FOREIGN KEY (`FLIGHT_NO_OLD`) REFERENCES `flight` (`FLIGHT_NO`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `flight_log_ibfk_4` FOREIGN KEY (`FLIGHT_NO_NEW`) REFERENCES `flight` (`FLIGHT_NO`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `flight_schedule`
--
ALTER TABLE `flight_schedule`
  ADD CONSTRAINT `flight_schedule_ibfk_1` FOREIGN KEY (`flight_no`) REFERENCES `flight` (`FLIGHT_NO`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `ticket`
--
ALTER TABLE `ticket`
  ADD CONSTRAINT `ticket_ibfk_1` FOREIGN KEY (`FLIGHT_SCHEDULE_ID`) REFERENCES `flight_schedule` (`flight_schedule_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `ticket_ibfk_2` FOREIGN KEY (`UID`) REFERENCES `user` (`UID`) ON DELETE CASCADE ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
