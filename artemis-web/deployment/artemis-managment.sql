CREATE TABLE `instance` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'the primary key of the instance',
  `INSTANCE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the id of the instance',
  `SERVICE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the service id of the instance',
  `REGION_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the region id of the instance',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of instance',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `INSTANCE_ID` (`INSTANCE_ID`,`SERVICE_ID`,`REGION_ID`,`OPERATION`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store instance status for manangement';

CREATE TABLE `instance_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'the primary key of log',
  `INSTANCE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the id of the instance',
  `SERVICE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the service id of the instance',
  `REGION_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the region id of the instance',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of instance',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `COMPLETE` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'true indicates that operation is not complete, otherwise operation is complete',
  `EXTENSIONS` varchar(2048) NOT NULL DEFAULT '{}' COMMENT 'the extension data for log',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  KEY `INSTANCE_ID` (`INSTANCE_ID`,`SERVICE_ID`,`REGION_ID`,`OPERATION`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store log for instance';

CREATE TABLE `server` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'the primary key of the machine',
  `SERVER_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the id of the server',
  `REGION_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the region id of the server',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of instance',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SERVER_ID` (`SERVER_ID`,`REGION_ID`,`OPERATION`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store machine status for instances';

CREATE TABLE `server_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'the primary key of the machine',
  `SERVER_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the id of the server',
  `REGION_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the region id of the server',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of instance',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `COMPLETE` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'true indicates that operation is not complete, otherwise operation is complete',
  `EXTENSIONS` varchar(2048) NOT NULL DEFAULT '{}' COMMENT 'the extension data for log',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  KEY `SERVER_ID` (`SERVER_ID`,`REGION_ID`,`OPERATION`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store log for server';

CREATE TABLE `service_group` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `SERVICE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'group serviceId',
  `REGION_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'group regionId',
  `ZONE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'group zoneId',
  `NAME` varchar(255) NOT NULL DEFAULT '' COMMENT 'group name',
  `APP_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'group appId',
  `DESCRIPTION` varchar(1024) DEFAULT NULL COMMENT 'group description',
  `STATUS` varchar(255) NOT NULL DEFAULT '' COMMENT 'group status',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  `DELETED` tinyint(1) DEFAULT '0' COMMENT 'whether group is deleted or not',
  `type` varchar(64) NOT NULL DEFAULT 'physical' COMMENT 'group type',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SERVICE_REGION_ZONE_GROUP` (`SERVICE_ID`,`REGION_ID`,`ZONE_ID`,`NAME`),
  KEY `SERVICE` (`SERVICE_ID`),
  KEY `STATUS` (`STATUS`),
  KEY `DELETED` (`DELETED`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service groups for manangement';

CREATE TABLE `service_group_instance` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `GROUP_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group_id',
  `INSTANCE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'instance_id',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `GROUP_INSTANCE` (`GROUP_ID`,`INSTANCE_ID`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service group instances for manangement';

CREATE TABLE `service_group_instance_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `GROUP_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group_id',
  `INSTANCE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'instance_id',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of group',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `REASON` varchar(128) DEFAULT '' COMMENT 'operator reason',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  KEY `GROUP_INSTANCE` (`GROUP_ID`,`INSTANCE_ID`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service group instance operator log';

CREATE TABLE `service_group_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `GROUP_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group_id',
  `PARENT_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group parent id',
  `NAME` varchar(255) NOT NULL DEFAULT '' COMMENT 'group name',
  `STATUS` varchar(255) NOT NULL DEFAULT '' COMMENT 'group status',
  `WEIGHT` int(11) DEFAULT NULL COMMENT 'group weight',
  `TYPE` varchar(255) NOT NULL DEFAULT '' COMMENT 'group type',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of service_group',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `DESCRIPTION` varchar(1024) DEFAULT NULL COMMENT 'group description',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `EXTENSIONS` varchar(2048) NOT NULL DEFAULT '{}' COMMENT 'the extension data for log',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  `SERVICE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'group serviceId',
  `REGION_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'group regionId',
  `ZONE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'group zoneId',
  `APP_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'group appId',
  `REASON` varchar(128) DEFAULT '' COMMENT 'operator reason',
  PRIMARY KEY (`ID`),
  KEY `NAME_PARENT_TYPE` (`NAME`,`PARENT_ID`),
  KEY `STATUS` (`STATUS`),
  KEY `TYPE` (`TYPE`),
  KEY `OPERATOR` (`OPERATOR_ID`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service groups log for manangement';

CREATE TABLE `service_group_operation` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'the primary key of the group operation',
  `GROUP_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group id',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of group',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `GROUP_OPEATION` (`GROUP_ID`,`OPERATION`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store group operation for manangement';

CREATE TABLE `service_group_operation_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'the primary key of the group operation',
  `GROUP_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group id',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of group',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `COMPLETE` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'true indicates that operation is not complete, otherwise operation is complete',
  `EXTENSIONS` varchar(2048) NOT NULL DEFAULT '{}' COMMENT 'the extension data for log',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  `reason` varchar(128) DEFAULT '' COMMENT 'operator reason',
  PRIMARY KEY (`ID`),
  KEY `GROUP_OPEATION` (`GROUP_ID`,`OPERATION`),
  KEY `OPERATOR` (`OPERATOR_ID`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store group operation log for manangement';

CREATE TABLE `service_group_tag` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'the primary key of the group tag',
  `GROUP_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group id',
  `TAG` varchar(255) NOT NULL DEFAULT '' COMMENT 'the tag of group',
  `VALUE` varchar(255) NOT NULL DEFAULT '' COMMENT 'the tag value of group',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `GROUP_TAG` (`GROUP_ID`,`TAG`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store group tag for manangement';

CREATE TABLE `service_group_tag_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'the primary key of the group tag',
  `GROUP_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group id',
  `TAG` varchar(255) NOT NULL DEFAULT '' COMMENT 'the tag of group',
  `VALUE` varchar(255) NOT NULL DEFAULT '' COMMENT 'the tag value of group',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of service_group_tag',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `EXTENSIONS` varchar(2048) NOT NULL DEFAULT '{}' COMMENT 'the extension data for log',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  KEY `GROUP_TAG` (`GROUP_ID`,`TAG`),
  KEY `OPERATOR` (`OPERATOR_ID`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store group tag log for manangement';

CREATE TABLE `service_instance` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `SERVICE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'instance serviceId',
  `INSTANCE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'instanceId',
  `IP` varchar(255) NOT NULL DEFAULT '' COMMENT 'ip',
  `MACHINE_NAME` varchar(255) NOT NULL DEFAULT '' COMMENT 'machine name',
  `METADATA` varchar(1024) NOT NULL DEFAULT '' COMMENT 'metadata',
  `PORT` int(11) NOT NULL DEFAULT '80' COMMENT 'port',
  `PROTOCOL` varchar(16) NOT NULL DEFAULT 'http' COMMENT 'protocol',
  `REGION_ID` varchar(16) NOT NULL DEFAULT '' COMMENT 'region_id',
  `ZONE_ID` varchar(128) NOT NULL DEFAULT '' COMMENT 'zone id',
  `GROUP_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'group_id',
  `HEALTHY_CHECK_URL` varchar(512) NOT NULL DEFAULT '' COMMENT 'healthy check url',
  `URL` varchar(512) NOT NULL DEFAULT '' COMMENT 'instance url',
  `DESCRIPTION` varchar(1024) DEFAULT NULL COMMENT 'description',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SERVICE_INSTANCE` (`SERVICE_ID`,`INSTANCE_ID`),
  KEY `SERVICE` (`SERVICE_ID`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service logic instances for manangement';

CREATE TABLE `service_instance_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `SERVICE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'instance serviceId',
  `INSTANCE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'instanceId',
  `IP` varchar(255) NOT NULL DEFAULT '' COMMENT 'ip',
  `MACHINE_NAME` varchar(255) NOT NULL DEFAULT '' COMMENT 'machine name',
  `METADATA` varchar(1024) NOT NULL DEFAULT '' COMMENT 'metadata',
  `PORT` int(11) NOT NULL DEFAULT '80' COMMENT 'port',
  `PROTOCOL` varchar(16) NOT NULL DEFAULT 'http' COMMENT 'protocol',
  `REGION_ID` varchar(16) NOT NULL DEFAULT '' COMMENT 'region_id',
  `ZONE_ID` varchar(128) NOT NULL DEFAULT '' COMMENT 'zone id',
  `GROUP_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'group_id',
  `HEALTHY_CHECK_URL` varchar(512) NOT NULL DEFAULT '' COMMENT 'healthy check url',
  `URL` varchar(512) NOT NULL DEFAULT '' COMMENT 'instance url',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of service instance',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  KEY `SERVICE_INSTANCE` (`SERVICE_ID`,`INSTANCE_ID`),
  KEY `SERVICE` (`SERVICE_ID`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service logic instances log for manangement';

CREATE TABLE `service_route_rule` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `SERVICE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'route-rule serviceId',
  `NAME` varchar(255) NOT NULL DEFAULT '' COMMENT 'route-rule name',
  `DESCRIPTION` varchar(1024) DEFAULT NULL COMMENT 'route-rule description',
  `STATUS` varchar(255) NOT NULL DEFAULT '' COMMENT 'route-rule status',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  `DELETED` tinyint(1) DEFAULT '0' COMMENT 'whether route-rule is deleted or not',
  `strategy` varchar(64) NOT NULL DEFAULT 'weighted-round-robin' COMMENT 'route rule strategy',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SERVICE_ROUTE_RULE_NAME` (`SERVICE_ID`,`NAME`),
  KEY `SERVICE` (`SERVICE_ID`),
  KEY `STATUS` (`STATUS`),
  KEY `DELETED` (`DELETED`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service route-rules for manangement';

CREATE TABLE `service_route_rule_group` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `ROUTE_RULE_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'service-route-rule id',
  `GROUP_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group id',
  `WEIGHT` int(11) DEFAULT NULL COMMENT 'service-route-rule-group released weight',
  `UNRELEASED_WEIGHT` int(11) DEFAULT NULL COMMENT 'service-route-rule-group unreleased weight',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ROUTE_RULE_GROUP` (`ROUTE_RULE_ID`,`GROUP_ID`),
  KEY `ROUTE_RULE` (`ROUTE_RULE_ID`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service-route-rule-group for management';

CREATE TABLE `service_route_rule_group_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `ROUTE_RULE_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'service-route-rule id',
  `GROUP_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT 'group id',
  `WEIGHT` int(11) DEFAULT NULL COMMENT 'service-route-rule-group released weight',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of route rule group',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `EXTENSIONS` varchar(2048) NOT NULL DEFAULT '{}' COMMENT 'the extension data for log',
  `REASON` varchar(128) DEFAULT '' COMMENT 'operator reason',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  KEY `ROUTE_RULE_GROUP` (`ROUTE_RULE_ID`,`GROUP_ID`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service route rule group log for manangement';

CREATE TABLE `service_route_rule_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `SERVICE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'route-rule serviceId',
  `NAME` varchar(255) NOT NULL DEFAULT '' COMMENT 'route-rule name',
  `STATUS` varchar(255) NOT NULL DEFAULT '' COMMENT 'route-rule status',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of route rule',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `EXTENSIONS` varchar(2048) NOT NULL DEFAULT '{}' COMMENT 'the extension data for log',
  `REASON` varchar(128) DEFAULT '' COMMENT 'operator reason',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  KEY `SERVICE_NAME` (`NAME`,`SERVICE_ID`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service route rule log for manangement';

CREATE TABLE `service_zone` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'the primary key',
  `SERVICE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the service id of the service zone',
  `ZONE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the zoneId of the service zone',
  `REGION_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the region id of the service zone',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of service zone',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SERVICE_REGION_ZONE` (`SERVICE_ID`,`REGION_ID`,`ZONE_ID`,`OPERATION`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service zone status for manangement';

CREATE TABLE `service_zone_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'the primary key',
  `SERVICE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the service id of the service zone',
  `ZONE_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the zoneId of the service zone',
  `REGION_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'the region id of the service zone',
  `OPERATION` varchar(255) NOT NULL DEFAULT '' COMMENT 'the operation of service zone',
  `OPERATOR_ID` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator id',
  `TOKEN` varchar(255) NOT NULL DEFAULT '' COMMENT 'operator token',
  `REASON` varchar(128) DEFAULT '' COMMENT 'operator reason',
  `COMPLETE` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'true indicates that operation is not complete, otherwise operation is complete',
  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
  `DataChange_LastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'last update time',
  PRIMARY KEY (`ID`),
  KEY `SERVICE_REGION_ZONE` (`SERVICE_ID`,`REGION_ID`,`ZONE_ID`,`OPERATION`),
  KEY `DataChange_LastTime` (`DataChange_LastTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='store service zone status for manangement';