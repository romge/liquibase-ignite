ALTER TABLE LBCAT.authors ADD varcharColumn VARCHAR(25)
ALTER TABLE LBCAT.authors ADD intColumn INT
ALTER TABLE LBCAT.authors ADD dateColumn date
UPDATE LBCAT.authors SET varcharColumn = 'INITIAL_VALUE'
UPDATE LBCAT.authors SET intColumn = 5
UPDATE LBCAT.authors SET dateColumn = DATE'2020-09-21'