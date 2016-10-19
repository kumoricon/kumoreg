UPDATE mysql.user SET Password=PASSWORD('password') WHERE User='root';
DELETE FROM mysql.user WHERE User='';
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
DROP DATABASE IF EXISTS test;
DELETE FROM mysql.db WHERE Db='test' OR Db='test\\_%';
FLUSH PRIVILEGES;

CREATE DATABASE IF NOT EXISTS kumoreg CHARACTER SET UTF8;
CREATE DATABASE IF NOT EXISTS kumoreg_training CHARACTER SET UTF8;

CREATE USER 'kumoreg'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON kumoreg.* to 'kumoreg'@'localhost';
CREATE USER 'kumoregtraining'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON kumoreg_training.* to 'kumoregtraining'@'localhost';

