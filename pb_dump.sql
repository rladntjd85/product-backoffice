-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: localhost    Database: pb
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `actor_user_id` bigint DEFAULT NULL,
  `action_type` varchar(50) NOT NULL,
  `target_type` varchar(30) NOT NULL,
  `target_id` bigint DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `user_agent` varchar(500) DEFAULT NULL,
  `diff_json` json DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_audit_created_at` (`created_at`),
  KEY `idx_audit_actor` (`actor_user_id`,`created_at`),
  KEY `idx_audit_target` (`target_type`,`target_id`,`created_at`),
  KEY `idx_audit_action` (`action_type`,`created_at`),
  CONSTRAINT `fk_audit_actor_user` FOREIGN KEY (`actor_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (1,NULL,'LOGIN_FAIL','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"reason\": \"BadCredentialsException\"}','2026-01-28 13:05:51'),(2,NULL,'LOGIN_FAIL','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"reason\": \"BadCredentialsException\"}','2026-01-28 16:02:34'),(3,NULL,'LOGIN_FAIL','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"reason\": \"BadCredentialsException\"}','2026-01-28 16:21:00'),(4,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',NULL,'2026-01-28 16:25:55'),(5,NULL,'LOGIN_FAIL','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"reason\": \"BadCredentialsException\"}','2026-01-28 16:26:21'),(6,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',NULL,'2026-01-28 16:26:37'),(7,NULL,'LOGIN_FAIL','USER',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@admin.com\", \"reason\": \"BadCredentialsException\"}','2026-02-09 22:35:45'),(8,NULL,'LOGIN_FAIL','USER',1,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\", \"locked\": false, \"reason\": \"BadCredentialsException\", \"failedCount\": 1}','2026-02-09 22:35:56'),(9,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 15:54:20'),(10,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 16:39:59'),(11,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 16:44:38'),(12,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 16:51:06'),(13,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 16:58:12'),(14,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 17:21:39'),(15,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 17:26:11'),(16,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 17:31:24'),(17,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 17:39:59'),(18,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 17:40:38'),(19,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 22:22:33'),(20,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 22:34:54'),(21,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 22:39:20'),(22,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 22:39:34'),(23,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 23:01:29'),(24,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 23:05:04'),(25,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 23:08:47'),(26,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 23:16:13'),(27,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 23:19:33'),(28,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 23:24:04'),(29,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 23:26:08'),(30,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 23:29:49'),(31,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-12 23:39:15'),(32,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-13 11:46:47'),(33,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-13 11:51:25'),(34,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-13 14:21:11'),(35,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-13 14:27:10'),(36,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-13 14:32:57'),(37,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-13 14:38:45'),(38,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-13 14:45:34'),(39,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-13 15:36:39'),(40,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-14 20:25:29'),(41,1,'USER_RESET_PASSWORD','USER',2,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"action\": \"RESET_PASSWORD\", \"mustChangePassword\": true}','2026-02-14 20:25:53'),(42,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-14 20:26:17'),(43,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 20:26:33'),(44,NULL,'LOGIN_FAIL','USER',2,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\", \"locked\": false, \"reason\": \"BadCredentialsException\", \"failedCount\": 1}','2026-02-14 20:26:56'),(45,NULL,'LOGIN_FAIL','USER',2,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\", \"locked\": false, \"reason\": \"BadCredentialsException\", \"failedCount\": 2}','2026-02-14 20:27:02'),(46,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 20:27:19'),(47,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 21:04:46'),(48,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 21:26:13'),(49,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 21:26:28'),(50,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 21:39:41'),(51,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 21:45:27'),(52,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 21:54:58'),(53,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 21:55:02'),(54,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 22:02:59'),(55,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 22:16:35'),(56,NULL,'LOGIN_FAIL','USER',2,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\", \"locked\": false, \"reason\": \"BadCredentialsException\", \"failedCount\": 1}','2026-02-14 22:27:29'),(57,NULL,'LOGIN_FAIL','USER',2,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\", \"locked\": false, \"reason\": \"BadCredentialsException\", \"failedCount\": 2}','2026-02-14 22:27:37'),(58,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 22:27:43'),(59,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 22:33:05'),(60,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 22:33:27'),(61,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 22:52:21'),(62,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 22:53:03'),(63,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 22:53:51'),(64,2,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"md@domain.com\"}','2026-02-14 23:05:54'),(65,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-14 23:06:03'),(66,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-14 23:09:44'),(67,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-14 23:18:02'),(68,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-14 23:41:26'),(69,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-14 23:54:34'),(70,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-14 23:58:00'),(71,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-14 23:59:18'),(72,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-15 00:10:11'),(73,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-15 00:23:35'),(74,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-15 00:27:56'),(75,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-15 00:39:58'),(76,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-15 00:45:26'),(77,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-15 00:48:26'),(78,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-15 11:05:54'),(79,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-15 11:22:00'),(80,1,'LOGIN_SUCCESS','AUTH',NULL,'0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','{\"email\": \"admin@local.com\"}','2026-02-15 11:47:41');
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_categories_name` (`name`),
  KEY `idx_categories_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `code` varchar(50) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL,
  `name` varchar(200) NOT NULL,
  `price` int NOT NULL DEFAULT '0',
  `stock` int NOT NULL DEFAULT '0',
  `status` enum('ACTIVE','HIDDEN','SOLD_OUT','DELETED') NOT NULL DEFAULT 'ACTIVE',
  `thumbnail_url` varchar(500) DEFAULT NULL,
  `detail_image_url` varchar(500) DEFAULT NULL,
  `thumbnail_original_name` varchar(255) DEFAULT NULL,
  `detail_original_name` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_products_category` (`category_id`),
  KEY `idx_products_status` (`status`),
  KEY `idx_products_name` (`name`),
  CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_permission`
--

DROP TABLE IF EXISTS `user_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_permission` (
  `user_id` bigint NOT NULL,
  `permission_code` varchar(50) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`permission_code`),
  KEY `idx_up_permission_code` (`permission_code`),
  CONSTRAINT `fk_up_permission` FOREIGN KEY (`permission_code`) REFERENCES `permissions` (`code`),
  CONSTRAINT `fk_up_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_permission`
--

LOCK TABLES `user_permission` WRITE;
/*!40000 ALTER TABLE `user_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `role` enum('ADMIN','MD','VIEWER') NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `locked` tinyint(1) NOT NULL DEFAULT '0',
  `failed_login_count` int NOT NULL DEFAULT '0',
  `last_login_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `must_change_password` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  KEY `idx_users_role` (`role`),
  KEY `idx_users_enabled_locked` (`enabled`,`locked`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin@local.com','$2a$10$JxzPtyH9tO/6ZztTzWJa6OkI283kZ1J33V5ZMbJThkVpzOmEDMRV2','관리자','ADMIN',1,0,0,'2026-02-15 11:47:41','2026-01-28 13:02:53','2026-02-15 11:47:41',0),(2,'md@domain.com','$2a$10$qMx2ag96gOTa3085oVStO.TjbyfOIAGlKUTjN8abfNPNn8wbSOzz2','엠디','MD',1,0,0,'2026-02-14 23:05:54','2026-02-13 11:47:31','2026-02-14 23:05:54',0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-15 15:56:50
