DROP DATABASE IF EXISTS ttschool;
CREATE DATABASE `ttschool`;
USE `ttschool`;

CREATE TABLE school (
    id INT(11) NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(50) NOT NULL,
    YEAR INT(11) NOT NULL,
    PRIMARY KEY (id),
    KEY NAME (NAME),
    KEY YEAR (YEAR)
)ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `subject` (
   id INT(11) NOT NULL AUTO_INCREMENT,
   NAME VARCHAR(50) NOT NULL,
   PRIMARY KEY (id),
   KEY NAME (NAME)
)ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `group` (
     id INT(11) NOT NULL AUTO_INCREMENT,
     schoolid INT(11) NOT NULL,
     NAME VARCHAR(50) NOT NULL,
     room VARCHAR(50) NOT NULL,
     PRIMARY KEY (id),
     KEY NAME (NAME),
     KEY room (room),
     FOREIGN KEY (schoolid) REFERENCES school (id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE trainee (
     id INT(11) NOT NULL AUTO_INCREMENT,
     groupid INT(11) DEFAULT NULL,
     firstname VARCHAR(50) NOT NULL,
     lastname VARCHAR(50) NOT NULL,
     rating INT(1) NOT NULL,
     PRIMARY KEY (id),
     KEY firstname (firstname),
     KEY lastname (lastname),
     KEY rating (rating),
                         FOREIGN KEY (groupid) REFERENCES `group` (id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE group_subject (
    id INT(11) NOT NULL AUTO_INCREMENT,
    groupid INT(11) NOT NULL,
    subjectid INT(11) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (groupid) REFERENCES `group` (id) ON DELETE CASCADE,
    FOREIGN KEY (subjectid) REFERENCES `subject` (id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8;
