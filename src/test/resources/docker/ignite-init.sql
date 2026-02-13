DROP SCHEMA IF EXISTS lbcat CASCADE;
CREATE SCHEMA lbcat;

CREATE TABLE lbcat.authors (
  id INT PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL,
  birthdate DATE NOT NULL,
  added TIMESTAMP WITH LOCAL TIME ZONE NOT NULL
);

INSERT INTO lbcat.authors VALUES
  (1,'Eileen','Lubowitz','ppaucek@example.org',date '1991-03-04',timestamp '2004-05-30 02:08:25'),
  (2,'Tamia','Mayert','shansen@example.org',date '2016-03-27',timestamp '2014-03-21 02:52:00'),
  (3,'Cyril','Funk','reynolds.godfrey@example.com', date '1988-04-21',timestamp '2011-06-24 18:17:48'),
  (4,'Nicolas','Buckridge','xhoeger@example.net', date '2017-02-03',timestamp '2019-04-22 02:04:41'),
  (5,'Jayden','Walter','lillian66@example.com', date '2010-02-27',timestamp '1990-02-04 02:32:00');

CREATE TABLE lbcat.posts (
  id INT PRIMARY KEY,
  author_id INT NOT NULL,
  title VARCHAR(255) NOT NULL,
  description VARCHAR(500) NOT NULL,
  content VARCHAR NOT NULL,
  inserted_date DATE
);

INSERT INTO lbcat.posts VALUES
  (1,1,'temporibus','voluptatum','Fugit non et doloribus repudiandae.',date '2015-11-18'),
  (2,2,'ea','aut','Tempora molestias maiores provident molestiae sint possimus quasi.',date '1975-06-08'),
  (3,3,'illum','rerum','Delectus recusandae sit officiis dolor.',date '1975-02-25'),
  (4,4,'itaque','deleniti','Magni nam optio id recusandae.',date '2010-07-28'),
  (5,5,'ad','similique','Rerum tempore quis ut nesciunt qui excepturi est.',date '2006-10-09');