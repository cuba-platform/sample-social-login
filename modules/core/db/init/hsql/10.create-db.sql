-- begin SEC_USER
alter table SEC_USER add column FACEBOOK_ID varchar(255) ^
alter table SEC_USER add column DTYPE varchar(100) ^
update SEC_USER set DTYPE = 'demo$SocialUser' where DTYPE is null ^
-- end SEC_USER
