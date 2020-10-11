
USE `photoalbum` ;
CREATE TABLE IF NOT EXISTS `photoalbum`.`image` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `filename` VARCHAR(255) DEFAULT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `type` VARCHAR(10) DEFAULT NULL,
  `size` INT(11) DEFAULT NULL,
  `uploaded` BIT DEFAULT 1,
  `date_created` DATETIME(6) DEFAULT NULL,
  `last_updated` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) 
ENGINE=InnoDB
AUTO_INCREMENT = 1;

-- -----------------------------------------------------
-- Schema photoalbum
-- -----------------------------------------------------
--DROP SCHEMA IF EXISTS `photoalbum`;

--CREATE SCHEMA `photoalbum`;
--INSERT INTO image (filename, description, `type`, `size`, active, date_created, last_updated) 
--	VALUES('aaa', 'bbb', 'PNG', 333, b'1', current_date(), current_date());

--INSERT INTO image (filename, description, `type`, `size`, active, date_created, last_updated) 
--	VALUES('bbb', 'bbb', 'PNG', 334, b'1', current_date(), current_date());