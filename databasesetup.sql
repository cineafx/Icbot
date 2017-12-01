/* Create Database */
CREATE SCHEMA `icbot` DEFAULT CHARACTER SET utf8 ;


/* Create table channels*/
CREATE TABLE `channels` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `channelName` varchar(45) NOT NULL,
  `shouldModerate` tinyint(4) NOT NULL DEFAULT '0',
  `useCommands` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ;


/* Create table comments */
CREATE TABLE `commands` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `channel` int(11) DEFAULT NULL,
  `command` varchar(255) NOT NULL,
  `response` text NOT NULL,
  `userlevel` int(11) DEFAULT '0',
  `timeout` int(11) DEFAULT '5',
  `contains` tinyint(4) DEFAULT '0',
  `timesUsed` int(11) DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `ID_idx` (`channel`),
  CONSTRAINT `ID` FOREIGN KEY (`channel`) REFERENCES `channels` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;