-- -----------------------------------------------------
-- Data for table `access`
-- -----------------------------------------------------
INSERT INTO `access` (`id`, `name`) VALUES (1, 'access.admin');
INSERT INTO `access` (`id`, `name`) VALUES (2, 'access.staff');
INSERT INTO `access` (`id`, `name`) VALUES (3, 'access.patient');

-- -----------------------------------------------------
-- Data for table `field_type`
-- -----------------------------------------------------

INSERT INTO `field_type` (`id`, `name`) VALUES (1, 'string');
INSERT INTO `field_type` (`id`, `name`) VALUES (2, 'int');
INSERT INTO `field_type` (`id`, `name`) VALUES (3, 'float');
INSERT INTO `field_type` (`id`, `name`) VALUES (4, 'boolean');

-- -----------------------------------------------------
-- Data for table `range_type`
-- -----------------------------------------------------

INSERT INTO `range_type` (`id`, `name`) VALUES (1, 'low');
INSERT INTO `range_type` (`id`, `name`) VALUES (2, 'normal');
INSERT INTO `range_type` (`id`, `name`) VALUES (3, 'high');
