CREATE TABLE `user`
(
    `user_id`  BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name`     VARCHAR(100) NOT NULL,
    `password` VARCHAR(100) NULL DEFAULT NULL,
    `provider` VARCHAR(100) NULL DEFAULT NULL,
    `uid`      VARCHAR(100) NOT NULL,
    `email`    VARCHAR(100) NOT NULL,
    UNIQUE INDEX `UK_tbl_user_uid` (`uid`)
);

CREATE TABLE `user_roles`
(
    `role_sn` BIGINT(20) NOT NULL,
    `roles`   VARCHAR(255) NULL DEFAULT NULL,
    UNIQUE INDEX `FK7ie1lfmnysdogxy1g91ernbkv` (`role_sn`),
    CONSTRAINT `FK_tbl_user_msrl` FOREIGN KEY (`role_sn`) REFERENCES `user` (`user_id`)
);