SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `medical-service-manager` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `medical-service-manager` ;

-- -----------------------------------------------------
-- Table `medical-service-manager`.`access`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`access` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`access` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`country`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`country` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`country` (
  `id` INT NOT NULL,
  `country` VARCHAR(255) NOT NULL,
  `last_update` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `country_UNIQUE` (`country` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`city`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`city` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`city` (
  `id` INT NOT NULL,
  `country_id` INT NOT NULL,
  `city` VARCHAR(255) NOT NULL,
  `last_update` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_city_country1_idx` (`country_id` ASC),
  CONSTRAINT `fk_city_country1`
    FOREIGN KEY (`country_id`)
    REFERENCES `medical-service-manager`.`country` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`address`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`address` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`address` (
  `id` INT NOT NULL,
  `city_id` INT NOT NULL,
  `address` VARCHAR(45) NOT NULL,
  `address2` VARCHAR(45) NULL,
  `district` VARCHAR(45) NULL,
  `postal_code` VARCHAR(45) NOT NULL,
  `phone` VARCHAR(45) NULL,
  `last_update` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_address_city1_idx` (`city_id` ASC),
  CONSTRAINT `fk_address_city1`
    FOREIGN KEY (`city_id`)
    REFERENCES `medical-service-manager`.`city` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`person`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`person` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`person` (
  `id` INT NOT NULL,
  `access_id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `lastname` VARCHAR(45) NOT NULL,
  `ssn` VARCHAR(11) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `attempts` INT NOT NULL DEFAULT 0,
  `login` DATETIME NULL,
  `address_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_person_access_idx` (`access_id` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  INDEX `fk_person_address1_idx` (`address_id` ASC),
  CONSTRAINT `fk_person_access`
    FOREIGN KEY (`access_id`)
    REFERENCES `medical-service-manager`.`access` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_person_address1`
    FOREIGN KEY (`address_id`)
    REFERENCES `medical-service-manager`.`address` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`service`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`service` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`service` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`service_instance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`service_instance` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`service_instance` (
  `id` INT NOT NULL,
  `service_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_service_instance_service1_idx` (`service_id` ASC),
  CONSTRAINT `fk_service_instance_service1`
    FOREIGN KEY (`service_id`)
    REFERENCES `medical-service-manager`.`service` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`person_has_service`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`person_has_service` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`person_has_service` (
  `person_id` INT NOT NULL,
  `service_instance_id` INT NOT NULL,
  `date` DATETIME NOT NULL,
  PRIMARY KEY (`person_id`, `service_instance_id`),
  INDEX `fk_person_has_Service_person1_idx` (`person_id` ASC),
  INDEX `fk_person_has_service_service_instance1_idx` (`service_instance_id` ASC),
  CONSTRAINT `fk_person_has_Service_person1`
    FOREIGN KEY (`person_id`)
    REFERENCES `medical-service-manager`.`person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_person_has_service_service_instance1`
    FOREIGN KEY (`service_instance_id`)
    REFERENCES `medical-service-manager`.`service_instance` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`field_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`field_type` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`field_type` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`tmfield`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`tmfield` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`tmfield` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `desc` LONGBLOB NULL,
  `field_type_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC),
  INDEX `fk_field_field_type1_idx` (`field_type_id` ASC),
  CONSTRAINT `fk_field_field_type1`
    FOREIGN KEY (`field_type_id`)
    REFERENCES `medical-service-manager`.`field_type` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`range_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`range_type` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`range_type` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`range`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`range` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`range` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `min` FLOAT NOT NULL,
  `max` FLOAT NOT NULL,
  `range_type_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_range_range_type1_idx` (`range_type_id` ASC),
  CONSTRAINT `fk_range_range_type1`
    FOREIGN KEY (`range_type_id`)
    REFERENCES `medical-service-manager`.`range_type` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`service_has_field`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`service_has_field` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`service_has_field` (
  `service_id` INT NOT NULL,
  `field_id` INT NOT NULL,
  `index` INT NOT NULL,
  PRIMARY KEY (`service_id`, `field_id`),
  INDEX `fk_service_has_field_field1_idx` (`field_id` ASC),
  INDEX `fk_service_has_field_service1_idx` (`service_id` ASC),
  UNIQUE INDEX `index_UNIQUE` (`index` ASC),
  UNIQUE INDEX `service_id_UNIQUE` (`service_id` ASC),
  CONSTRAINT `fk_service_has_field_service1`
    FOREIGN KEY (`service_id`)
    REFERENCES `medical-service-manager`.`service` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_service_has_field_field1`
    FOREIGN KEY (`field_id`)
    REFERENCES `medical-service-manager`.`tmfield` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`instance_field`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`instance_field` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`instance_field` (
  `id` INT NOT NULL,
  `service_instance_id` INT NOT NULL,
  `tmfield_id` INT NOT NULL,
  `int_val` INT NULL,
  `float_val` FLOAT NULL,
  `string_val` BLOB NULL,
  `bool_val` TINYINT(1) NULL,
  `index` INT NULL,
  PRIMARY KEY (`id`, `service_instance_id`),
  INDEX `fk_instance_field_service_instance1_idx` (`service_instance_id` ASC),
  INDEX `fk_instance_field_tmfield1_idx` (`tmfield_id` ASC),
  CONSTRAINT `fk_instance_field_service_instance1`
    FOREIGN KEY (`service_instance_id`)
    REFERENCES `medical-service-manager`.`service_instance` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_instance_field_tmfield1`
    FOREIGN KEY (`tmfield_id`)
    REFERENCES `medical-service-manager`.`tmfield` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `medical-service-manager`.`tmfield_has_range`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `medical-service-manager`.`tmfield_has_range` ;

CREATE TABLE IF NOT EXISTS `medical-service-manager`.`tmfield_has_range` (
  `tmfield_id` INT NOT NULL,
  `range_id` INT NOT NULL,
  PRIMARY KEY (`tmfield_id`, `range_id`),
  INDEX `fk_tmfield_has_range_range1_idx` (`range_id` ASC),
  INDEX `fk_tmfield_has_range_tmfield1_idx` (`tmfield_id` ASC),
  CONSTRAINT `fk_tmfield_has_range_tmfield1`
    FOREIGN KEY (`tmfield_id`)
    REFERENCES `medical-service-manager`.`tmfield` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tmfield_has_range_range1`
    FOREIGN KEY (`range_id`)
    REFERENCES `medical-service-manager`.`range` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `medical-service-manager`.`access`
-- -----------------------------------------------------
START TRANSACTION;
USE `medical-service-manager`;
INSERT INTO `medical-service-manager`.`access` (`id`, `name`) VALUES (1, 'access.admin');
INSERT INTO `medical-service-manager`.`access` (`id`, `name`) VALUES (2, 'access.staff');
INSERT INTO `medical-service-manager`.`access` (`id`, `name`) VALUES (3, 'access.patient');

COMMIT;


-- -----------------------------------------------------
-- Data for table `medical-service-manager`.`country`
-- -----------------------------------------------------
START TRANSACTION;
USE `medical-service-manager`;
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (1, 'Afghanistan', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (2, 'Algeria', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (3, 'American Samoa', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (4, 'Angola', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (5, 'Anguilla', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (6, 'Argentina', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (7, 'Armenia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (8, 'Australia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (9, 'Austria', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (10, 'Azerbaijan', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (11, 'Bahrain', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (12, 'Bangladesh', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (13, 'Belarus', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (14, 'Bolivia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (15, 'Brazil', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (16, 'Brunei', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (17, 'Bulgaria', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (18, 'Cambodia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (19, 'Cameroon', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (20, 'Canada', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (21, 'Chad', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (22, 'Chile', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (23, 'China', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (24, 'Colombia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (25, 'Congo, The Democratic Republic of the', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (26, 'Czech Republic', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (27, 'Dominican Republic', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (28, 'Ecuador', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (29, 'Egypt', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (30, 'Estonia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (31, 'Ethiopia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (32, 'Faroe Islands', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (33, 'Finland', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (34, 'France', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (35, 'French Guiana', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (36, 'French Polynesia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (37, 'Gambia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (38, 'Germany', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (39, 'Greece', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (40, 'Greenland', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (41, 'Holy See (Vatican City State)', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (42, 'Hong Kong', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (43, 'Hungary', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (44, 'India', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (45, 'Indonesia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (46, 'Iran', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (47, 'Iraq', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (48, 'Israel', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (49, 'Italy', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (50, 'Japan', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (51, 'Kazakstan', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (52, 'Kenya', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (53, 'Kuwait', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (54, 'Latvia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (55, 'Liechtenstein', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (56, 'Lithuania', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (57, 'Madagascar', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (58, 'Malawi', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (59, 'Malaysia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (60, 'Mexico', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (61, 'Moldova', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (62, 'Morocco', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (63, 'Mozambique', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (64, 'Myanmar', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (65, 'Nauru', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (66, 'Nepal', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (67, 'Netherlands', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (68, 'New Zealand', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (69, 'Nigeria', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (70, 'North Korea', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (71, 'Oman', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (72, 'Pakistan', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (73, 'Paraguay', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (74, 'Peru', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (75, 'Philippines', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (76, 'Poland', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (77, 'Puerto Rico', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (78, 'Romania', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (79, 'Runion', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (80, 'Russian Federation', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (81, 'Saint Vincent and the Grenadines', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (82, 'Saudi Arabia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (83, 'Senegal', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (84, 'Slovakia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (85, 'South Africa', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (86, 'South Korea', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (87, 'Spain', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (88, 'Sri Lanka', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (89, 'Sudan', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (90, 'Sweden', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (91, 'Switzerland', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (92, 'Taiwan', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (93, 'Tanzania', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (94, 'Thailand', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (95, 'Tonga', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (96, 'Tunisia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (97, 'Turkey', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (98, 'Turkmenistan', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (99, 'Tuvalu', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (100, 'Ukraine', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (101, 'United Arab Emirates', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (102, 'United Kingdom', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (103, 'United States', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (104, 'Venezuela', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (105, 'Vietnam', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (106, 'Virgin Islands, U.S.', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (107, 'Yemen', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (108, 'Yugoslavia', '2015-02-15 04:44:00');
INSERT INTO `medical-service-manager`.`country` (`id`, `country`, `last_update`) VALUES (109, 'Zambia', '2015-02-15 04:44:00');

COMMIT;


-- -----------------------------------------------------
-- Data for table `medical-service-manager`.`city`
-- -----------------------------------------------------
START TRANSACTION;
USE `medical-service-manager`;
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (1, 87, '\'A Corua (La Corua)\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (2, 82, '\'Abha\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (3, 101, '\'Abu Dhabi\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (4, 60, '\'Acua\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (5, 97, '\'Adana\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (6, 31, '\'Addis Abeba\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (7, 107, '\'Aden\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (8, 44, '\'Adoni\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (9, 44, '\'Ahmadnagar\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (10, 50, '\'Akishima\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (11, 103, '\'Akron\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (12, 101, '\'al-Ayn\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (13, 82, '\'al-Hawiya\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (14, 11, '\'al-Manama\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (15, 89, '\'al-Qadarif\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (16, 82, '\'al-Qatif\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (17, 49, '\'Alessandria\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (18, 44, '\'Allappuzha (Alleppey)\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (19, 60, '\'Allende\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (20, 6, '\'Almirante Brown\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (21, 15, '\'Alvorada\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (22, 44, '\'Ambattur\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (23, 67, '\'Amersfoort\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (24, 44, '\'Amroha\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (25, 15, '\'Angra dos Reis\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (26, 15, '\'Anpolis\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (27, 22, '\'Antofagasta\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (28, 15, '\'Aparecida de Goinia\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (29, 67, '\'Apeldoorn\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (30, 15, '\'Araatuba\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (31, 46, '\'Arak\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (32, 77, '\'Arecibo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (33, 103, '\'Arlington\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (34, 48, '\'Ashdod\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (35, 98, '\'Ashgabat\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (36, 48, '\'Ashqelon\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (37, 73, '\'Asuncin\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (38, 39, '\'Athenai\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (39, 80, '\'Atinsk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (40, 60, '\'Atlixco\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (41, 103, '\'Augusta-Richmond County\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (42, 103, '\'Aurora\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (43, 6, '\'Avellaneda\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (44, 15, '\'Bag\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (45, 6, '\'Baha Blanca\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (46, 23, '\'Baicheng\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (47, 23, '\'Baiyin\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (48, 10, '\'Baku\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (49, 80, '\'Balaiha\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (50, 97, '\'Balikesir\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (51, 44, '\'Balurghat\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (52, 19, '\'Bamenda\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (53, 16, '\'Bandar Seri Begawan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (54, 37, '\'Banjul\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (55, 104, '\'Barcelona\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (56, 91, '\'Basel\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (57, 48, '\'Bat Yam\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (58, 97, '\'Batman\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (59, 2, '\'Batna\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (60, 18, '\'Battambang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (61, 75, '\'Baybay\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (62, 75, '\'Bayugan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (63, 2, '\'Bchar\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (64, 63, '\'Beira\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (65, 103, '\'Bellevue\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (66, 15, '\'Belm\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (67, 4, '\'Benguela\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (68, 62, '\'Beni-Mellal\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (69, 69, '\'Benin City\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (70, 49, '\'Bergamo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (71, 44, '\'Berhampore (Baharampur)\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (72, 91, '\'Bern\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (73, 44, '\'Bhavnagar\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (74, 44, '\'Bhilwara\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (75, 44, '\'Bhimavaram\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (76, 44, '\'Bhopal\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (77, 44, '\'Bhusawal\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (78, 44, '\'Bijapur\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (79, 29, '\'Bilbays\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (80, 23, '\'Binzhou\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (81, 66, '\'Birgunj\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (82, 75, '\'Bislig\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (83, 15, '\'Blumenau\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (84, 15, '\'Boa Vista\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (85, 85, '\'Boksburg\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (86, 78, '\'Botosani\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (87, 85, '\'Botshabelo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (88, 102, '\'Bradford\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (89, 15, '\'Braslia\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (90, 84, '\'Bratislava\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (91, 49, '\'Brescia\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (92, 34, '\'Brest\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (93, 49, '\'Brindisi\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (94, 103, '\'Brockton\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (95, 78, '\'Bucuresti\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (96, 24, '\'Buenaventura\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (97, 76, '\'Bydgoszcz\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (98, 75, '\'Cabuyao\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (99, 74, '\'Callao\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (100, 105, '\'Cam Ranh\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (101, 103, '\'Cape Coral\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (102, 104, '\'Caracas\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (103, 60, '\'Carmen\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (104, 75, '\'Cavite\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (105, 35, '\'Cayenne\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (106, 60, '\'Celaya\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (107, 44, '\'Chandrapur\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (108, 92, '\'Changhwa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (109, 23, '\'Changzhou\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (110, 44, '\'Chapra\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (111, 106, '\'Charlotte Amalie\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (112, 85, '\'Chatsworth\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (113, 86, '\'Cheju\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (114, 92, '\'Chiayi\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (115, 61, '\'Chisinau\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (116, 92, '\'Chungho\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (117, 45, '\'Cianjur\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (118, 45, '\'Ciomas\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (119, 45, '\'Ciparay\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (120, 103, '\'Citrus Heights\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (121, 41, '\'Citt del Vaticano\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (122, 73, '\'Ciudad del Este\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (123, 103, '\'Clarksville\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (124, 60, '\'Coacalco de Berriozbal\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (125, 60, '\'Coatzacoalcos\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (126, 103, '\'Compton\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (127, 22, '\'Coquimbo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (128, 6, '\'Crdoba\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (129, 60, '\'Cuauhtmoc\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (130, 60, '\'Cuautla\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (131, 60, '\'Cuernavaca\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (132, 104, '\'Cuman\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (133, 76, '\'Czestochowa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (134, 72, '\'Dadu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (135, 103, '\'Dallas\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (136, 23, '\'Datong\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (137, 54, '\'Daugavpils\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (138, 75, '\'Davao\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (139, 23, '\'Daxian\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (140, 103, '\'Dayton\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (141, 69, '\'Deba Habe\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (142, 97, '\'Denizli\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (143, 12, '\'Dhaka\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (144, 44, '\'Dhule (Dhulia)\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (145, 23, '\'Dongying\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (146, 87, '\'Donostia-San Sebastin\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (147, 24, '\'Dos Quebradas\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (148, 38, '\'Duisburg\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (149, 102, '\'Dundee\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (150, 80, '\'Dzerzinsk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (151, 67, '\'Ede\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (152, 69, '\'Effon-Alaiye\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (153, 14, '\'El Alto\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (154, 60, '\'El Fuerte\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (155, 103, '\'El Monte\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (156, 80, '\'Elista\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (157, 23, '\'Emeishan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (158, 67, '\'Emmen\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (159, 23, '\'Enshi\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (160, 38, '\'Erlangen\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (161, 6, '\'Escobar\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (162, 46, '\'Esfahan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (163, 97, '\'Eskisehir\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (164, 44, '\'Etawah\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (165, 6, '\'Ezeiza\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (166, 23, '\'Ezhou\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (167, 36, '\'Faaa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (168, 92, '\'Fengshan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (169, 44, '\'Firozabad\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (170, 24, '\'Florencia\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (171, 103, '\'Fontana\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (172, 50, '\'Fukuyama\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (173, 99, '\'Funafuti\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (174, 23, '\'Fuyu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (175, 23, '\'Fuzhou\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (176, 44, '\'Gandhinagar\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (177, 103, '\'Garden Grove\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (178, 103, '\'Garland\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (179, 20, '\'Gatineau\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (180, 97, '\'Gaziantep\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (181, 87, '\'Gijn\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (182, 75, '\'Gingoog\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (183, 15, '\'Goinia\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (184, 45, '\'Gorontalo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (185, 103, '\'Grand Prairie\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (186, 9, '\'Graz\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (187, 103, '\'Greensboro\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (188, 60, '\'Guadalajara\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (189, 15, '\'Guaruj\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (190, 15, '\'guas Lindas de Gois\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (191, 44, '\'Gulbarga\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (192, 75, '\'Hagonoy\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (193, 23, '\'Haining\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (194, 105, '\'Haiphong\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (195, 44, '\'Haldia\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (196, 20, '\'Halifax\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (197, 44, '\'Halisahar\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (198, 38, '\'Halle/Saale\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (199, 23, '\'Hami\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (200, 68, '\'Hamilton\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (201, 105, '\'Hanoi\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (202, 60, '\'Hidalgo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (203, 50, '\'Higashiosaka\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (204, 50, '\'Hino\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (205, 50, '\'Hiroshima\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (206, 107, '\'Hodeida\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (207, 23, '\'Hohhot\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (208, 44, '\'Hoshiarpur\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (209, 92, '\'Hsichuh\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (210, 23, '\'Huaian\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (211, 44, '\'Hubli-Dharwad\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (212, 60, '\'Huejutla de Reyes\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (213, 60, '\'Huixquilucan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (214, 74, '\'Hunuco\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (215, 15, '\'Ibirit\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (216, 29, '\'Idfu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (217, 69, '\'Ife\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (218, 69, '\'Ikerre\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (219, 75, '\'Iligan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (220, 69, '\'Ilorin\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (221, 75, '\'Imus\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (222, 97, '\'Inegl\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (223, 59, '\'Ipoh\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (224, 50, '\'Isesaki\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (225, 80, '\'Ivanovo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (226, 50, '\'Iwaki\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (227, 50, '\'Iwakuni\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (228, 50, '\'Iwatsuki\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (229, 50, '\'Izumisano\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (230, 88, '\'Jaffna\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (231, 44, '\'Jaipur\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (232, 45, '\'Jakarta\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (233, 53, '\'Jalib al-Shuyukh\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (234, 12, '\'Jamalpur\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (235, 80, '\'Jaroslavl\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (236, 76, '\'Jastrzebie-Zdrj\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (237, 82, '\'Jedda\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (238, 80, '\'Jelets\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (239, 44, '\'Jhansi\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (240, 23, '\'Jinchang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (241, 23, '\'Jining\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (242, 23, '\'Jinzhou\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (243, 44, '\'Jodhpur\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (244, 85, '\'Johannesburg\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (245, 103, '\'Joliet\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (246, 60, '\'Jos Azueta\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (247, 15, '\'Juazeiro do Norte\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (248, 15, '\'Juiz de Fora\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (249, 23, '\'Junan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (250, 60, '\'Jurez\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (251, 1, '\'Kabul\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (252, 69, '\'Kaduna\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (253, 50, '\'Kakamigahara\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (254, 80, '\'Kaliningrad\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (255, 76, '\'Kalisz\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (256, 50, '\'Kamakura\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (257, 44, '\'Kamarhati\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (258, 100, '\'Kamjanets-Podilskyi\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (259, 80, '\'Kamyin\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (260, 50, '\'Kanazawa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (261, 44, '\'Kanchrapara\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (262, 103, '\'Kansas City\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (263, 44, '\'Karnal\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (264, 44, '\'Katihar\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (265, 46, '\'Kermanshah\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (266, 97, '\'Kilis\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (267, 85, '\'Kimberley\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (268, 86, '\'Kimchon\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (269, 81, '\'Kingstown\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (270, 80, '\'Kirovo-Tepetsk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (271, 52, '\'Kisumu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (272, 109, '\'Kitwe\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (273, 85, '\'Klerksdorp\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (274, 80, '\'Kolpino\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (275, 100, '\'Konotop\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (276, 50, '\'Koriyama\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (277, 23, '\'Korla\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (278, 80, '\'Korolev\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (279, 42, '\'Kowloon and New Kowloon\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (280, 108, '\'Kragujevac\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (281, 97, '\'Ktahya\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (282, 59, '\'Kuching\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (283, 44, '\'Kumbakonam\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (284, 50, '\'Kurashiki\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (285, 80, '\'Kurgan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (286, 80, '\'Kursk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (287, 50, '\'Kuwana\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (288, 60, '\'La Paz\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (289, 6, '\'La Plata\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (290, 27, '\'La Romana\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (291, 23, '\'Laiwu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (292, 103, '\'Lancaster\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (293, 23, '\'Laohekou\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (294, 75, '\'Lapu-Lapu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (295, 103, '\'Laredo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (296, 91, '\'Lausanne\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (297, 34, '\'Le Mans\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (298, 23, '\'Lengshuijiang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (299, 23, '\'Leshan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (300, 20, '\'Lethbridge\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (301, 45, '\'Lhokseumawe\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (302, 23, '\'Liaocheng\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (303, 54, '\'Liepaja\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (304, 58, '\'Lilongwe\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (305, 74, '\'Lima\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (306, 103, '\'Lincoln\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (307, 9, '\'Linz\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (308, 80, '\'Lipetsk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (309, 49, '\'Livorno\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (310, 80, '\'Ljubertsy\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (311, 28, '\'Loja\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (312, 102, '\'London\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (313, 20, '\'London\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (314, 76, '\'Lublin\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (315, 25, '\'Lubumbashi\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (316, 92, '\'Lungtan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (317, 15, '\'Luzinia\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (318, 45, '\'Madiun\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (319, 57, '\'Mahajanga\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (320, 80, '\'Maikop\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (321, 90, '\'Malm\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (322, 103, '\'Manchester\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (323, 75, '\'Mandaluyong\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (324, 72, '\'Mandi Bahauddin\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (325, 38, '\'Mannheim\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (326, 104, '\'Maracabo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (327, 72, '\'Mardan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (328, 15, '\'Maring\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (329, 71, '\'Masqat\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (330, 60, '\'Matamoros\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (331, 50, '\'Matsue\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (332, 23, '\'Meixian\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (333, 103, '\'Memphis\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (334, 6, '\'Merlo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (335, 60, '\'Mexicali\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (336, 44, '\'Miraj\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (337, 29, '\'Mit Ghamr\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (338, 50, '\'Miyakonojo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (339, 13, '\'Mogiljov\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (340, 13, '\'Molodetno\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (341, 60, '\'Monclova\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (342, 64, '\'Monywa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (343, 80, '\'Moscow\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (344, 47, '\'Mosul\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (345, 100, '\'Mukateve\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (346, 44, '\'Munger (Monghyr)\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (347, 93, '\'Mwanza\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (348, 25, '\'Mwene-Ditu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (349, 64, '\'Myingyan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (350, 44, '\'Mysore\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (351, 63, '\'Naala-Porto\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (352, 80, '\'Nabereznyje Telny\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (353, 62, '\'Nador\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (354, 44, '\'Nagaon\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (355, 50, '\'Nagareyama\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (356, 46, '\'Najafabad\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (357, 86, '\'Naju\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (358, 94, '\'Nakhon Sawan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (359, 105, '\'Nam Dinh\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (360, 4, '\'Namibe\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (361, 92, '\'Nantou\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (362, 23, '\'Nanyang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (363, 21, '\'NDjamna\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (364, 85, '\'Newcastle\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (365, 60, '\'Nezahualcyotl\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (366, 105, '\'Nha Trang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (367, 80, '\'Niznekamsk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (368, 108, '\'Novi Sad\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (369, 80, '\'Novoterkassk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (370, 95, '\'Nukualofa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (371, 40, '\'Nuuk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (372, 52, '\'Nyeri\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (373, 104, '\'Ocumare del Tuy\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (374, 69, '\'Ogbomosho\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (375, 72, '\'Okara\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (376, 50, '\'Okayama\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (377, 50, '\'Okinawa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (378, 26, '\'Olomouc\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (379, 89, '\'Omdurman\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (380, 50, '\'Omiya\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (381, 69, '\'Ondo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (382, 50, '\'Onomichi\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (383, 20, '\'Oshawa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (384, 97, '\'Osmaniye\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (385, 100, '\'ostka\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (386, 50, '\'Otsu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (387, 33, '\'Oulu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (388, 87, '\'Ourense (Orense)\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (389, 69, '\'Owo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (390, 69, '\'Oyo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (391, 75, '\'Ozamis\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (392, 85, '\'Paarl\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (393, 60, '\'Pachuca de Soto\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (394, 94, '\'Pak Kret\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (395, 44, '\'Palghat (Palakkad)\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (396, 45, '\'Pangkal Pinang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (397, 36, '\'Papeete\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (398, 44, '\'Parbhani\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (399, 44, '\'Pathankot\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (400, 44, '\'Patiala\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (401, 39, '\'Patras\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (402, 51, '\'Pavlodar\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (403, 45, '\'Pemalang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (404, 103, '\'Peoria\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (405, 24, '\'Pereira\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (406, 18, '\'Phnom Penh\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (407, 23, '\'Pingxiang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (408, 80, '\'Pjatigorsk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (409, 76, '\'Plock\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (410, 15, '\'Po\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (411, 77, '\'Ponce\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (412, 45, '\'Pontianak\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (413, 15, '\'Poos de Caldas\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (414, 28, '\'Portoviejo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (415, 45, '\'Probolinggo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (416, 44, '\'Pudukkottai\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (417, 44, '\'Pune\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (418, 44, '\'Purnea (Purnia)\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (419, 45, '\'Purwakarta\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (420, 70, '\'Pyongyang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (421, 29, '\'Qalyub\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (422, 23, '\'Qinhuangdao\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (423, 46, '\'Qomsheh\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (424, 6, '\'Quilmes\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (425, 44, '\'Rae Bareli\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (426, 44, '\'Rajkot\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (427, 44, '\'Rampur\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (428, 22, '\'Rancagua\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (429, 44, '\'Ranchi\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (430, 20, '\'Richmond Hill\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (431, 15, '\'Rio Claro\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (432, 23, '\'Rizhao\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (433, 103, '\'Roanoke\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (434, 28, '\'Robamba\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (435, 103, '\'Rockford\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (436, 17, '\'Ruse\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (437, 85, '\'Rustenburg\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (438, 67, '\'s-Hertogenbosch\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (439, 38, '\'Saarbrcken\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (440, 50, '\'Sagamihara\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (441, 103, '\'Saint Louis\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (442, 79, '\'Saint-Denis\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (443, 62, '\'Sal\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (444, 71, '\'Salala\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (445, 60, '\'Salamanca\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (446, 103, '\'Salinas\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (447, 9, '\'Salzburg\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (448, 44, '\'Sambhal\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (449, 103, '\'San Bernardino\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (450, 27, '\'San Felipe de Puerto Plata\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (451, 60, '\'San Felipe del Progreso\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (452, 60, '\'San Juan Bautista Tuxtepec\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (453, 73, '\'San Lorenzo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (454, 6, '\'San Miguel de Tucumn\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (455, 107, '\'Sanaa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (456, 15, '\'Santa Brbara dOeste\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (457, 6, '\'Santa F\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (458, 75, '\'Santa Rosa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (459, 87, '\'Santiago de Compostela\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (460, 27, '\'Santiago de los Caballeros\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (461, 15, '\'Santo Andr\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (462, 23, '\'Sanya\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (463, 50, '\'Sasebo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (464, 44, '\'Satna\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (465, 29, '\'Sawhaj\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (466, 80, '\'Serpuhov\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (467, 46, '\'Shahr-e Kord\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (468, 23, '\'Shanwei\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (469, 23, '\'Shaoguan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (470, 101, '\'Sharja\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (471, 23, '\'Shenzhen\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (472, 72, '\'Shikarpur\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (473, 44, '\'Shimoga\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (474, 50, '\'Shimonoseki\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (475, 44, '\'Shivapuri\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (476, 29, '\'Shubra al-Khayma\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (477, 38, '\'Siegen\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (478, 44, '\'Siliguri (Shiliguri)\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (479, 100, '\'Simferopol\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (480, 24, '\'Sincelejo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (481, 46, '\'Sirjan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (482, 97, '\'Sivas\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (483, 2, '\'Skikda\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (484, 80, '\'Smolensk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (485, 15, '\'So Bernardo do Campo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (486, 15, '\'So Leopoldo\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (487, 24, '\'Sogamoso\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (488, 69, '\'Sokoto\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (489, 94, '\'Songkhla\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (490, 15, '\'Sorocaba\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (491, 85, '\'Soshanguve\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (492, 96, '\'Sousse\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (493, 5, '\'South Hill\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (494, 102, '\'Southampton\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (495, 102, '\'Southend-on-Sea\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (496, 102, '\'Southport\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (497, 85, '\'Springs\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (498, 17, '\'Stara Zagora\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (499, 103, '\'Sterling Heights\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (500, 102, '\'Stockport\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (501, 14, '\'Sucre\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (502, 23, '\'Suihua\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (503, 74, '\'Sullana\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (504, 97, '\'Sultanbeyli\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (505, 10, '\'Sumqayit\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (506, 100, '\'Sumy\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (507, 59, '\'Sungai Petani\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (508, 103, '\'Sunnyvale\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (509, 45, '\'Surakarta\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (510, 80, '\'Syktyvkar\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (511, 49, '\'Syrakusa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (512, 43, '\'Szkesfehrvr\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (513, 93, '\'Tabora\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (514, 46, '\'Tabriz\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (515, 82, '\'Tabuk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (516, 3, '\'Tafuna\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (517, 75, '\'Taguig\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (518, 107, '\'Taizz\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (519, 75, '\'Talavera\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (520, 103, '\'Tallahassee\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (521, 50, '\'Tama\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (522, 44, '\'Tambaram\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (523, 75, '\'Tanauan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (524, 6, '\'Tandil\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (525, 12, '\'Tangail\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (526, 92, '\'Tanshui\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (527, 75, '\'Tanza\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (528, 75, '\'Tarlac\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (529, 97, '\'Tarsus\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (530, 30, '\'Tartu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (531, 80, '\'Teboksary\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (532, 45, '\'Tegal\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (533, 48, '\'Tel Aviv-Jaffa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (534, 63, '\'Tete\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (535, 23, '\'Tianjin\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (536, 23, '\'Tiefa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (537, 23, '\'Tieli\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (538, 97, '\'Tokat\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (539, 86, '\'Tonghae\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (540, 23, '\'Tongliao\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (541, 60, '\'Torren\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (542, 92, '\'Touliu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (543, 34, '\'Toulon\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (544, 34, '\'Toulouse\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (545, 32, '\'Trshavn\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (546, 92, '\'Tsaotun\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (547, 50, '\'Tsuyama\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (548, 75, '\'Tuguegarao\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (549, 76, '\'Tychy\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (550, 44, '\'Udaipur\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (551, 49, '\'Udine\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (552, 50, '\'Ueda\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (553, 86, '\'Uijongbu\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (554, 44, '\'Uluberia\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (555, 50, '\'Urawa\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (556, 60, '\'Uruapan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (557, 97, '\'Usak\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (558, 80, '\'Usolje-Sibirskoje\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (559, 44, '\'Uttarpara-Kotrung\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (560, 55, '\'Vaduz\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (561, 104, '\'Valencia\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (562, 104, '\'Valle de la Pascua\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (563, 60, '\'Valle de Santiago\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (564, 44, '\'Valparai\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (565, 20, '\'Vancouver\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (566, 44, '\'Varanasi (Benares)\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (567, 6, '\'Vicente Lpez\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (568, 44, '\'Vijayawada\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (569, 15, '\'Vila Velha\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (570, 56, '\'Vilnius\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (571, 105, '\'Vinh\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (572, 15, '\'Vitria de Santo Anto\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (573, 103, '\'Warren\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (574, 23, '\'Weifang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (575, 38, '\'Witten\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (576, 8, '\'Woodridge\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (577, 76, '\'Wroclaw\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (578, 23, '\'Xiangfan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (579, 23, '\'Xiangtan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (580, 23, '\'Xintai\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (581, 23, '\'Xinxiang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (582, 44, '\'Yamuna Nagar\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (583, 65, '\'Yangor\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (584, 23, '\'Yantai\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (585, 19, '\'Yaound\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (586, 7, '\'Yerevan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (587, 23, '\'Yinchuan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (588, 23, '\'Yingkou\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (589, 102, '\'York\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (590, 23, '\'Yuncheng\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (591, 23, '\'Yuzhou\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (592, 23, '\'Zalantun\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (593, 93, '\'Zanzibar\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (594, 23, '\'Zaoyang\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (595, 60, '\'Zapopan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (596, 69, '\'Zaria\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (597, 80, '\'Zeleznogorsk\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (598, 51, '\'Zhezqazghan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (599, 23, '\'Zhoushan\'', '2/15/2006 4:45');
INSERT INTO `medical-service-manager`.`city` (`id`, `country_id`, `city`, `last_update`) VALUES (600, 83, '\'Ziguinchor\'', '2/15/2006 4:45');

COMMIT;


-- -----------------------------------------------------
-- Data for table `medical-service-manager`.`address`
-- -----------------------------------------------------
START TRANSACTION;
USE `medical-service-manager`;
INSERT INTO `medical-service-manager`.`address` (`id`, `city_id`, `address`, `address2`, `district`, `postal_code`, `phone`, `last_update`) VALUES (1, 1, 'System Address', NULL, 'System', 'XXXXX', 'XXXXXXXXX', NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table `medical-service-manager`.`person`
-- -----------------------------------------------------
START TRANSACTION;
USE `medical-service-manager`;
INSERT INTO `medical-service-manager`.`person` (`id`, `access_id`, `name`, `lastname`, `ssn`, `username`, `password`, `attempts`, `login`, `address_id`) VALUES (1, 1, 'System', 'Admin', '000-00-0000', 'admin', '21232f297a57a5a743894a0e4a801fc3', 0, NULL, 1);

COMMIT;


-- -----------------------------------------------------
-- Data for table `medical-service-manager`.`field_type`
-- -----------------------------------------------------
START TRANSACTION;
USE `medical-service-manager`;
INSERT INTO `medical-service-manager`.`field_type` (`id`, `name`) VALUES (1, 'string');
INSERT INTO `medical-service-manager`.`field_type` (`id`, `name`) VALUES (2, 'int');
INSERT INTO `medical-service-manager`.`field_type` (`id`, `name`) VALUES (3, 'float');
INSERT INTO `medical-service-manager`.`field_type` (`id`, `name`) VALUES (4, 'boolean');

COMMIT;


-- -----------------------------------------------------
-- Data for table `medical-service-manager`.`range_type`
-- -----------------------------------------------------
START TRANSACTION;
USE `medical-service-manager`;
INSERT INTO `medical-service-manager`.`range_type` (`id`, `name`) VALUES (1, 'low');
INSERT INTO `medical-service-manager`.`range_type` (`id`, `name`) VALUES (2, 'normal');
INSERT INTO `medical-service-manager`.`range_type` (`id`, `name`) VALUES (3, 'high');

COMMIT;

