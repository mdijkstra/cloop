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
  `datetime_consumption` datetime DEFAULT NULL,
  `datetime_ideal_injection` datetime DEFAULT NULL,
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
  `units_intended` int(11) NOT NULL,
  `units_delivered` int(11) DEFAULT NULL,
  `datetime_intended` datetime NOT NULL,
  `datetime_delivered` datetime DEFAULT NULL,
  PRIMARY KEY (`injection_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='table to store injections intended/delivered';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `injections`
--

LOCK TABLES `injections` WRITE;
/*!40000 ALTER TABLE `injections` DISABLE KEYS */;
/*!40000 ALTER TABLE `injections` ENABLE KEYS */;
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

-- Dump completed on 2014-04-13  0:24:19
