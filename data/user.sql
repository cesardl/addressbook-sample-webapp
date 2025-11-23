CREATE USER 'travis'@'%' IDENTIFIED BY '';

GRANT ALL PRIVILEGES ON *.* TO 'travis'@'%';

FLUSH PRIVILEGES;

SELECT now(), @@system_time_zone, @@global.time_zone, @@session.time_zone;
