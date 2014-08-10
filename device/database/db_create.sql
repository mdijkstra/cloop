CREATE DATABASE  IF NOT EXISTS `cloop` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `cloop`;
-- MySQL dump 10.13  Distrib 5.6.13, for Win32 (x86)
--
-- Host: localhost    Database: cloop
-- ------------------------------------------------------
-- Server version	5.6.17-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alerts`
--

DROP TABLE IF EXISTS `alerts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alerts` (
  `alert_id` int(11) NOT NULL AUTO_INCREMENT,
  `datetime_recorded` datetime NOT NULL,
  `datetime_to_alert` datetime NOT NULL,
  `src` varchar(45) NOT NULL,
  `code` varchar(45) NOT NULL,
  `type` varchar(45) NOT NULL,
  `message` varchar(500) NOT NULL,
  `value` varchar(45) DEFAULT NULL,
  `option1` varchar(500) DEFAULT NULL,
  `option2` varchar(500) DEFAULT NULL,
  `transferred` varchar(45) NOT NULL DEFAULT 'NO',
  `datetime_dismissed` datetime DEFAULT NULL,
  `src_dismissed` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`alert_id`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alerts`
--

LOCK TABLES `alerts` WRITE;
/*!40000 ALTER TABLE `alerts` DISABLE KEYS */;
/*!40000 ALTER TABLE `alerts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `automode_switch`
--

DROP TABLE IF EXISTS `automode_switch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `automode_switch` (
  `automode_switch_id` int(11) NOT NULL,
  `datetime_recorded` datetime NOT NULL,
  `is_on` varchar(45) NOT NULL,
  PRIMARY KEY (`automode_switch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='table to record switching on and off the automatic mode. is_on = yes means the system will deliver insulin; is_on = no the device will take no actions. populated on the app and transferred over to the device';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `automode_switch`
--

LOCK TABLES `automode_switch` WRITE;
/*!40000 ALTER TABLE `automode_switch` DISABLE KEYS */;
INSERT INTO `automode_switch` VALUES (1,'2014-08-10 10:40:32','yes');
/*!40000 ALTER TABLE `automode_switch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `courses` (
  `course_id` int(11) NOT NULL COMMENT 'Not AI b/c it is AI on the app',
  `food_id` int(11) DEFAULT NULL,
  `serv_quantity` float DEFAULT NULL,
  `carbs` int(11) NOT NULL,
  `datetime_consumption` datetime NOT NULL,
  `datetime_ideal_injection` datetime DEFAULT NULL,
  `comment` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='table to store individual items eaten';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses_to_injections`
--

DROP TABLE IF EXISTS `courses_to_injections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `courses_to_injections` (
  `course_id` int(11) NOT NULL,
  `injection_id` int(11) NOT NULL,
  `transferred` varchar(45) NOT NULL DEFAULT 'NO',
  PRIMARY KEY (`course_id`,`injection_id`),
  KEY `FK_injection_id_idx` (`injection_id`),
  CONSTRAINT `FK_course_id` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_injection_id` FOREIGN KEY (`injection_id`) REFERENCES `injections` (`injection_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='A table to link courses to injections';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses_to_injections`
--

LOCK TABLES `courses_to_injections` WRITE;
/*!40000 ALTER TABLE `courses_to_injections` DISABLE KEYS */;
/*!40000 ALTER TABLE `courses_to_injections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `injections`
--

DROP TABLE IF EXISTS `injections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `injections` (
  `injection_id` int(11) NOT NULL AUTO_INCREMENT,
  `injection_type` varchar(45) NOT NULL,
  `units_intended` float NOT NULL,
  `units_delivered` float DEFAULT NULL,
  `temp_rate` float DEFAULT NULL,
  `datetime_intended` datetime NOT NULL,
  `datetime_delivered` datetime DEFAULT NULL,
  `cur_iob_units` float NOT NULL,
  `cur_bg_units` float DEFAULT NULL,
  `cur_bg` int(11) DEFAULT NULL,
  `correction_units` float NOT NULL,
  `carbs_to_cover` int(11) NOT NULL,
  `carbs_units` float NOT NULL,
  `cur_basal_units` float NOT NULL,
  `all_meal_carbs_absorbed` varchar(45) DEFAULT NULL,
  `status` varchar(45) NOT NULL,
  `transferred` varchar(45) NOT NULL DEFAULT 'NO',
  PRIMARY KEY (`injection_id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8 COMMENT='table to store injections intended/delivered';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `injections`
--

LOCK TABLES `injections` WRITE;
/*!40000 ALTER TABLE `injections` DISABLE KEYS */;
/*!40000 ALTER TABLE `injections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iob`
--

DROP TABLE IF EXISTS `iob`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iob` (
  `datetime_iob` datetime NOT NULL,
  `iob` float NOT NULL,
  `transferred` varchar(45) DEFAULT 'no',
  PRIMARY KEY (`datetime_iob`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iob`
--

LOCK TABLES `iob` WRITE;
/*!40000 ALTER TABLE `iob` DISABLE KEYS */;
/*!40000 ALTER TABLE `iob` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iob_dist`
--

DROP TABLE IF EXISTS `iob_dist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iob_dist` (
  `interval` int(11) NOT NULL,
  `iob_dist_pct` float NOT NULL,
  `injection_type` varchar(45) NOT NULL,
  PRIMARY KEY (`interval`,`injection_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iob_dist`
--

LOCK TABLES `iob_dist` WRITE;
/*!40000 ALTER TABLE `iob_dist` DISABLE KEYS */;
INSERT INTO `iob_dist` VALUES (0,100,'bolus'),(0,100,'square'),(5,100,'bolus'),(5,100,'square'),(10,100,'bolus'),(10,100,'square'),(15,100,'bolus'),(15,100,'square'),(20,100,'bolus'),(20,100,'square'),(25,100,'bolus'),(25,100,'square'),(30,100,'bolus'),(30,100,'square'),(35,99.8265,'bolus'),(35,99.8566,'square'),(40,99.3072,'bolus'),(40,99.4272,'square'),(45,98.4456,'bolus'),(45,98.7142,'square'),(50,97.2478,'bolus'),(50,97.7218,'square'),(55,95.7222,'bolus'),(55,96.4557,'square'),(60,93.8791,'bolus'),(60,94.923,'square'),(65,91.7316,'bolus'),(65,93.1327,'square'),(70,89.2944,'bolus'),(70,91.0949,'square'),(75,86.5844,'bolus'),(75,88.8214,'square'),(80,83.6206,'bolus'),(80,86.3252,'square'),(85,80.4234,'bolus'),(85,83.6206,'square'),(90,77.0151,'bolus'),(90,80.7232,'square'),(95,73.4193,'bolus'),(95,77.6495,'square'),(100,69.6609,'bolus'),(100,74.4172,'square'),(105,65.7661,'bolus'),(105,71.0448,'square'),(110,61.7619,'bolus'),(110,67.5517,'square'),(115,57.676,'bolus'),(115,63.9579,'square'),(120,53.5369,'bolus'),(120,60.2841,'square'),(125,49.3732,'bolus'),(125,56.5512,'square'),(130,45.2138,'bolus'),(130,52.7808,'square'),(135,41.0877,'bolus'),(135,48.9944,'square'),(140,37.0234,'bolus'),(140,45.2138,'square'),(145,33.0492,'bolus'),(145,41.4607,'square'),(150,29.1927,'bolus'),(150,37.7565,'square'),(155,25.4805,'bolus'),(155,34.1226,'square'),(160,21.9385,'bolus'),(160,30.5797,'square'),(165,18.5913,'bolus'),(165,27.1483,'square'),(170,15.4621,'bolus'),(170,23.8479,'square'),(175,12.5726,'bolus'),(175,20.6976,'square'),(180,9.94282,'bolus'),(180,17.7153,'square'),(185,7.59108,'bolus'),(185,14.9183,'square'),(190,5.53367,'bolus'),(190,12.3225,'square'),(195,3.78488,'bolus'),(195,9.94282,'square'),(200,2.35684,'bolus'),(200,7.79294,'square'),(205,1.25947,'bolus'),(205,5.88518,'square'),(210,0.500375,'bolus'),(210,4.23048,'square'),(215,0.0848297,'bolus'),(215,2.83834,'square'),(220,0.0157168,'bolus'),(220,1.71674,'square'),(225,0.872119,'square'),(230,0.309315,'square'),(235,0.0315598,'square');
/*!40000 ALTER TABLE `iob_dist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logs`
--

DROP TABLE IF EXISTS `logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logs` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT,
  `src_device` varchar(45) NOT NULL,
  `datetime_logged` datetime NOT NULL,
  `code` varchar(45) NOT NULL,
  `type` varchar(45) NOT NULL,
  `message` varchar(500) NOT NULL,
  `option1` varchar(500) DEFAULT NULL,
  `option2` varchar(500) DEFAULT NULL,
  `transferred` varchar(45) NOT NULL DEFAULT 'no',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8 COMMENT='table to record logs';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logs`
--

LOCK TABLES `logs` WRITE;
/*!40000 ALTER TABLE `logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sgvs`
--

DROP TABLE IF EXISTS `sgvs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sgvs` (
  `sgv_id` int(11) NOT NULL AUTO_INCREMENT,
  `device_id` int(11) NOT NULL COMMENT 'the id of the pump to differentiate if wearing two cgms',
  `datetime_recorded` datetime NOT NULL,
  `sgv` int(11) DEFAULT NULL COMMENT 'blood glucose recorded',
  `transferred` varchar(45) NOT NULL DEFAULT 'no',
  PRIMARY KEY (`sgv_id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8 COMMENT='table to store cgm data that is read off the pump';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sgvs`
--

LOCK TABLES `sgvs` WRITE;
/*!40000 ALTER TABLE `sgvs` DISABLE KEYS */;
/*!40000 ALTER TABLE `sgvs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'cloop'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-08-10 10:40:46
