CREATE TABLE `drivers` (
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
  `lat` FLOAT( 10, 6 ) NOT NULL ,
  `lng` FLOAT( 10, 6 ) NOT NULL,
  `accuracy` FLOAT( 10, 6 ) NOT NULL 
) ENGINE = MYISAM ;

INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.386339','-122.085823','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.38714','-122.083235','0.8');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.393885','-122.078916','0.9');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.402653','-122.079354','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.394011','-122.095528','0.5');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.401724','-122.114646','0.2');



INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.386339','-122.085823','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.38714','-122.083235','0.8');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.393885','-122.078916','0.9');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.402653','-122.079354','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.394011','-122.095528','0.5');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.401724','-122.114646','0.2');

INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.386339','-122.085823','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.38714','-122.083235','0.8');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.393885','-122.078916','0.9');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.402653','-122.079354','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.394011','-122.095528','0.5');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('37.401724','-122.114646','0.2');

INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('13.00284484','77.69171025','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('13.0080657','77.68522542','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('13.00457758','77.68862821','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('13.00184727','77.68935139','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('13.00484893','77.69039444','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('13.00992296','77.68548844','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('13.00932393','77.68660802','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('13.00094414','77.68102383','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('13.00977172','77.68426527','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('12.99884928','77.69254203','0.7');

INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('13.10884928','77.69264203','0.7');
INSERT INTO `drivers` (`lat`, `lng`,`accuracy`) VALUES ('13.18884928','77.69864203','0.7');

select 6371*acos (cos ( radians(13.0040167) )* cos( radians( lat ) ) * cos( radians( lng ) - radians(77.68777649999993) ) + sin ( radians(13.0040167) ) * sin( radians( lat ) )) as distance  where distance < 0.5 from drivers ORDER BY distance LIMIT 0 , 10;