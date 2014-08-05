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
  `datetime_recorded` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

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
-- Table structure for table `injections`
--

DROP TABLE IF EXISTS `injections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `injections` (
  `injection_id` int(11) NOT NULL AUTO_INCREMENT,
  `units_intended` float NOT NULL,
  `units_delivered` float DEFAULT NULL,
  `temp_rate` float DEFAULT NULL,
  `datetime_intended` datetime NOT NULL,
  `datetime_delivered` datetime DEFAULT NULL,
  `cur_iob_units` float NOT NULL,
  `cur_bg_units` float DEFAULT NULL,
  `correction_units` float NOT NULL,
  `carbs_to_cover` int(11) NOT NULL,
  `carbs_units` float NOT NULL,
  `cur_basal_units` float NOT NULL,
  `all_meal_carbs_absorbed` varchar(45) DEFAULT NULL,
  `status` varchar(45) NOT NULL,
  `transferred` varchar(45) NOT NULL DEFAULT 'NO',
  PRIMARY KEY (`injection_id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8 COMMENT='table to store injections intended/delivered';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iob`
--

DROP TABLE IF EXISTS `iob`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iob` (
  `datetime_iob` datetime NOT NULL,
  `iob` float NOT NULL,
  PRIMARY KEY (`datetime_iob`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iob_dist`
--

DROP TABLE IF EXISTS `iob_dist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iob_dist` (
  `interval` int(11) NOT NULL,
  `iob_dist_pct` float NOT NULL,
  PRIMARY KEY (`interval`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `transferred` varchar(45) NOT NULL DEFAULT 'NO',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='table to record logs';
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `transfered` varchar(45) NOT NULL DEFAULT 'NO',
  PRIMARY KEY (`sgv_id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8 COMMENT='table to store cgm data that is read off the pump';
/*!40101 SET character_set_client = @saved_cs_client */;

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

-- Dump completed on 2014-08-04 20:54:42
