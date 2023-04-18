/*insert into users(username, password, enabled) values
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
('user','ROLE_ADMIN');*/


insert into evergreen_users(id, username, `name`, mobile, password_hash, enabled, account_non_expired, account_non_locked, credentials_non_expired, email,using_mfa,mfa_key)
values (1, 'user', 'JackieChan', '18600000001', '{bcrypt}$2a$10$mNKRQQ5GTW8ndJcSSAVX8OxLoDOJG088RwdZ8cmMqfWjBQqwY8YTG', 1, 1, 1, 1, 'JackieChan@qq.com',true, '8Uy+OZUaZur9WwcP0z+YxNy+QdsWbtfqA70GQMxMfLeisTd8Na6C7DkjhJWLrGyEyBsnEmmkza6iorytQRh7OQ=='),
       (2, 'sec_user', 'EricLi', '15800000001', '{SHA-1}{zE7LED7/JzjyOIAjnbTgUU+F2ryReSEr3SlCxmAHINs=}5c998a9f7abb066dba3c1bbdcbd9f1d529bc9c65', 1, 1, 1, 1, 'EricLi@qq.com',false, '8Uy+OZUaZur9WwcP0z+YxNy+QdsWbtfqA70GQMxMfLeisTd8Na6C7DkjhJWLrGyEyBsnEmmkza6iorytQRh7OQ==');

insert into evergreen_roles(id, role_name)
values (1, 'ROLE_USER'), (2, 'ROLE_ADMIN');

insert into evergreen_users_roles(user_id, role_id)
values (1, 1), (1, 2), (2, 1);
