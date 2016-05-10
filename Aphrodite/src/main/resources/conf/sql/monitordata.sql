# MySQL-Front 5.1  (Build 4.2)

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;


# Host: localhost    Database: cmdb
# ------------------------------------------------------
# Server version 5.1.48-community

#
# Source for table monitordata
#

DROP TABLE IF EXISTS `monitordata`;
CREATE TABLE `monitordata` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `taskdataid` int(10) NOT NULL DEFAULT '0' COMMENT '外键，对应时间段任务的id',
  `hostid` varchar(20) NOT NULL DEFAULT '' COMMENT '对应主机ID',
  `monitorType` varchar(10) NOT NULL DEFAULT '0' COMMENT '1-cpu; 2-disk; 3-net; 4-memory',
  `itemName` varchar(20) NOT NULL DEFAULT '' COMMENT '对应键值对',
  `path` varchar(255) DEFAULT NULL COMMENT '数据存放地址',
  PRIMARY KEY (`Id`),
  KEY `taskdataid` (`taskdataid`,`hostid`,`monitorType`)
) ENGINE=InnoDB AUTO_INCREMENT=340 DEFAULT CHARSET=utf8;

#
# Dumping data for table monitordata
#

LOCK TABLES `monitordata` WRITE;
/*!40000 ALTER TABLE `monitordata` DISABLE KEYS */;
INSERT INTO `monitordata` VALUES (356,1,'12','CPU','CPU_system_time','E://monitorData//task_12//host_12_CPU_system_time.data');
INSERT INTO `monitordata` VALUES (357,1,'12','CPU','CPU_user_time','E://monitorData//task_12//host_12_CPU_user_time.data');
INSERT INTO `monitordata` VALUES (358,1,'12','CPU','CPU_idle_time','E://monitorData//task_12//host_12_CPU_idle_time.data');
INSERT INTO `monitordata` VALUES (359,1,'12','CPU','CPU_iowait_time','E://monitorData//task_12//host_12_CPU_iowait_time.data');
INSERT INTO `monitordata` VALUES (360,1,'14','CPU','CPU_idle_time','E://monitorData//task_12//host_14_CPU_idle_time.data');
INSERT INTO `monitordata` VALUES (361,1,'14','CPU','CPU_iowait_time','E://monitorData//task_12//host_14_CPU_iowait_time.data');
INSERT INTO `monitordata` VALUES (362,1,'14','CPU','CPU_system_time','E://monitorData//task_12//host_14_CPU_system_time.data');
INSERT INTO `monitordata` VALUES (363,1,'14','CPU','CPU_user_time','E://monitorData//task_12//host_14_CPU_user_time.data');
INSERT INTO `monitordata` VALUES (364,1,'12','MEMORY','Available_memory','E://monitorData//task_12//host_12_Available_memory.data');
INSERT INTO `monitordata` VALUES (365,1,'12','MEMORY','Free_swap_space','E://monitorData//task_12//host_12_Free_swap_space.data');
INSERT INTO `monitordata` VALUES (366,1,'12','MEMORY','Total_memory','E://monitorData//task_12//host_12_Total_memory.data');
INSERT INTO `monitordata` VALUES (367,1,'12','MEMORY','Total_swap_space','E://monitorData//task_12//host_12_Total_swap_space.data');
INSERT INTO `monitordata` VALUES (368,1,'14','MEMORY','Available_memory','E://monitorData//task_12//host_14_Available_memory.data');
INSERT INTO `monitordata` VALUES (369,1,'14','MEMORY','Free_swap_space','E://monitorData//task_12//host_14_Free_swap_space.data');
INSERT INTO `monitordata` VALUES (370,1,'14','MEMORY','Total_memory','E://monitorData//task_12//host_14_Total_memory.data');
INSERT INTO `monitordata` VALUES (371,1,'14','MEMORY','Total_swap_space','E://monitorData//task_12//host_14_Total_swap_space.data');
/*!40000 ALTER TABLE `monitordata` ENABLE KEYS */;
UNLOCK TABLES;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
