--
-- Table structure for table `airplane`
--

DROP TABLE IF EXISTS `airplane`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `airplane` (
                            `airplane_id` int(11) NOT NULL AUTO_INCREMENT,
                            `airplane_name` varchar(255) DEFAULT NULL,
                            `capacity` float DEFAULT NULL,
                            PRIMARY KEY (`airplane_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `airplane`
--

LOCK TABLES `airplane` WRITE;
/*!40000 ALTER TABLE `airplane` DISABLE KEYS */;
/*!40000 ALTER TABLE `airplane` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `airport_geo`
--

DROP TABLE IF EXISTS `airport_geo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `airport_geo` (
                               `airport_geo_id` int(11) NOT NULL AUTO_INCREMENT,
                               `airport_name` varchar(255) DEFAULT NULL,
                               `description` varchar(255) DEFAULT NULL,
                               `location_id` int(11) NOT NULL,
                               PRIMARY KEY (`airport_geo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `airport_geo`
--

LOCK TABLES `airport_geo` WRITE;
/*!40000 ALTER TABLE `airport_geo` DISABLE KEYS */;
/*!40000 ALTER TABLE `airport_geo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flight`
--

DROP TABLE IF EXISTS `flight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight` (
                          `flight_id` int(11) NOT NULL AUTO_INCREMENT,
                          `airplane_id` int(11) NOT NULL,
                          `flight_no` varchar(255) NOT NULL,
                          `from_airport_id` int(11) NOT NULL,
                          `to_airport_id` int(11) NOT NULL,
                          PRIMARY KEY (`flight_id`),
                          UNIQUE KEY `UK_g9lyjbdea3jbrhy3t85n9bfq2` (`flight_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight`
--

LOCK TABLES `flight` WRITE;
/*!40000 ALTER TABLE `flight` DISABLE KEYS */;
/*!40000 ALTER TABLE `flight` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flight_log`
--

DROP TABLE IF EXISTS `flight_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight_log` (
                              `flight_log_id` int(11) NOT NULL AUTO_INCREMENT,
                              `flight_id` int(11) NOT NULL,
                              `flight_no_new` varchar(255) DEFAULT NULL,
                              `flight_no_old` varchar(255) DEFAULT NULL,
                              `log_date` datetime DEFAULT NULL,
                              `username` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`flight_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight_log`
--

LOCK TABLES `flight_log` WRITE;
/*!40000 ALTER TABLE `flight_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `flight_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flight_schedule`
--

DROP TABLE IF EXISTS `flight_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight_schedule` (
                                   `flight_schedule_id` int(11) NOT NULL AUTO_INCREMENT,
                                   `available_seat` int(11) DEFAULT NULL,
                                   `end_time` datetime DEFAULT NULL,
                                   `flight_no` varchar(255) DEFAULT NULL,
                                   `start_time` datetime DEFAULT NULL,
                                   PRIMARY KEY (`flight_schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight_schedule`
--

LOCK TABLES `flight_schedule` WRITE;
/*!40000 ALTER TABLE `flight_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `flight_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location` (
                            `location_id` int(11) NOT NULL AUTO_INCREMENT,
                            `city_id` int(11) DEFAULT NULL,
                            `city_name` varchar(255) DEFAULT NULL,
                            `country_code` int(11) DEFAULT NULL,
                            `country_name` varchar(255) DEFAULT NULL,
                            `latitude` float DEFAULT NULL,
                            `longitude` bigint(20) DEFAULT NULL,
                            PRIMARY KEY (`location_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `dob` date DEFAULT NULL,
                        `email` varchar(128) NOT NULL,
                        `email_verified` bit(1) DEFAULT NULL,
                        `gender` bit(1) DEFAULT NULL,
                        `password` varchar(64) NOT NULL,
                        `phone` varchar(10) DEFAULT NULL,
                        `role` bit(1) DEFAULT NULL,
                        `uid` varchar(255) DEFAULT NULL,
                        `username` varchar(128) DEFAULT NULL,
                        `verification_code` varchar(64) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `password_reset_token`
--

DROP TABLE IF EXISTS `password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_token` (
                                        `id` int(11) NOT NULL AUTO_INCREMENT,
                                        `expiry_date` datetime DEFAULT NULL,
                                        `token` varchar(255) DEFAULT NULL,
                                        `user_id` int(11) NOT NULL,
                                        PRIMARY KEY (`id`),
                                        KEY `FK5lwtbncug84d4ero33v3cfxvl` (`user_id`),
                                        CONSTRAINT `FK5lwtbncug84d4ero33v3cfxvl` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_token`
--

LOCK TABLES `password_reset_token` WRITE;
/*!40000 ALTER TABLE `password_reset_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket`
--

DROP TABLE IF EXISTS `ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket` (
                          `ticket_id` int(11) NOT NULL AUTO_INCREMENT,
                          `booking_state` varchar(255) DEFAULT NULL,
                          `flight_schedule_id` int(11) DEFAULT NULL,
                          `price` bigint(20) DEFAULT NULL,
                          `seat_number` varchar(255) DEFAULT NULL,
                          `uid` int(11) DEFAULT NULL,
                          PRIMARY KEY (`ticket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket`
--

LOCK TABLES `ticket` WRITE;
/*!40000 ALTER TABLE `ticket` DISABLE KEYS */;
/*!40000 ALTER TABLE `ticket` ENABLE KEYS */;
UNLOCK TABLES;

/* Query for add column to table*/
/* Chạy câu này nếu update lỗi "You are using safe update mode and you tried to update a table without a WHERE that uses a KEY column" */
SET SQL_SAFE_UPDATES = 0;
/* QUERY */
ALTER TABLE flight_schedule ADD(flight_state VARCHAR(10));
UPDATE flight_schedule SET flight_state = 'FLIGHT_ON';