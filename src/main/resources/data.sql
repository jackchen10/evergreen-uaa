insert into users(username, password, enabled) values
('user','{bcrypt}$2a$10$mNKRQQ5GTW8ndJcSSAVX8OxLoDOJG088RwdZ8cmMqfWjBQqwY8YTG',1),
('sec_user','{SHA-1}{zE7LED7/JzjyOIAjnbTgUU+F2ryReSEr3SlCxmAHINs=}5c998a9f7abb066dba3c1bbdcbd9f1d529bc9c65',1);

insert into authorities(username, authority) values
('sec_user', 'ROLE_USER'),
('user','ROLE_USER'),
('user','ROLE_ADMIN');

insert into evergreen_users(username, password, enabled) values
('user','{bcrypt}$2a$10$mNKRQQ5GTW8ndJcSSAVX8OxLoDOJG088RwdZ8cmMqfWjBQqwY8YTG',1),
('sec_user','{SHA-1}{zE7LED7/JzjyOIAjnbTgUU+F2ryReSEr3SlCxmAHINs=}5c998a9f7abb066dba3c1bbdcbd9f1d529bc9c65',1);

insert into evergreen_authorities(username, authority) values
('sec_user', 'ROLE_USER'),
('user','ROLE_USER'),
('user','ROLE_ADMIN');


/*insert into mooc_users(id, username, `name`, mobile, password_hash, enabled, account_non_expired, account_non_locked, credentials_non_expired, email)
            values (1, 'user', 'Zhang San', '13012341234', '{bcrypt}$2a$10$jhS817qUHgOR4uQSoEBRxO58.rZ1dBCmCTjG8PeuQAX4eISf.zowm', 1, 1, 1, 1, 'zhangsan@local.dev'),
                   (2, 'old_user', 'Li Si', '13812341234', '{SHA-1}7ce0359f12857f2a90c7de465f40a95f01cb5da9', 1, 1, 1, 1, 'lisi@local.dev');
insert into mooc_roles(id, role_name) values (1, 'ROLE_USER'), (2, 'ROLE_ADMIN');
insert into mooc_users_roles(user_id, role_id) values (1, 1), (1, 2), (2, 1);
*/