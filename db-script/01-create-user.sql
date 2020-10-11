CREATE DATABASE photoalbum;
CREATE USER 'pauser'@'localhost' IDENTIFIED BY 'test1234';
GRANT ALL PRIVILEGES ON photoalbum.* TO 'pauser'@'localhost';
flush privileges;
