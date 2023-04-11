DROP TABLE IF EXISTS users;
CREATE TABLE users (
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled  TINYINT NOT NULL DEFAULT 1,
    PRIMARY KEY (username)
) ENGINE = INNODB;

DROP TABLE IF EXISTS authorities;
CREATE TABLE authorities (
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users(username)
) ENGINE=InnoDB;

CREATE UNIQUE INDEX ix_auth_username on authorities(username,authority);


DROP TABLE IF EXISTS evergreen_users;
CREATE TABLE evergreen_users (
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled  TINYINT NOT NULL DEFAULT 1,
    name VARCHAR(50) NULL,
    PRIMARY KEY (username)
) ENGINE = INNODB;

DROP TABLE IF EXISTS evergreen_authorities;
CREATE TABLE evergreen_authorities (
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_evergreen_authorities_users FOREIGN KEY (username) REFERENCES evergreen_users(username)
) ENGINE=InnoDB;

CREATE UNIQUE INDEX ix_evergreen_auth_username on evergreen_authorities(username,authority);