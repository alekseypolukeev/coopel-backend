# About
This is some kind of skeleton to develop Spring Boot 2 mocroservices, secured with Oauth2 JWT faster.
It contains such common things like:
- Separate microservice API modules simplify testing and usage from 3rd party Java applications;
- Strong modules structure allow reuse IoC container components and dependencies providing separate functionality (e.q JPA, search, NoSQL);
- Timezone, Jackson serialization, mapping and separate environment settings;
- Security resource server settings;
- Custom user context handling, integrated with method security;
- Communication between microservices; 
- Oauth2 (JWT) based microservice...
- Deploy...

# Common

# Structure

# Modules
## common-api
## common
## common-jpa

# Microservices
## Auth
### Datasource configuration
1. Start postgres in docker:
```
docker-compose -f docker/postgresql.yml up
```
2. Connect to postgres using password from postgresql.yml:
```
psql -h localhos -U postgres postgres
```
3. Create role:
```
CREATE ROLE auth_role WITH LOGIN PASSWORD 'change_me';
```
4. Create database:
```
create database auth encoding=UTF8;
```
5. Grant database to role:
```
GRANT ALL PRIVILEGES ON DATABASE auth TO auth_role;
```

See `common-jpa` for datasource configuration details.
 
### Notification emails
Need to be configured with frontend URL to build correct links.
```
coopel:
  auth:
    email:
      site-url: http://localhost:8080
      password-recovery-path: /#/set-password
      confirm-email-path: /#/email-confirm
```

### Generate key store
`Note:` Override for different env!
~~~
keytool -genkeypair -alias <ALIAS> -keyalg RSA -keysize 2048 -keypass <PASSWORD> -keystore /home/keystore.jks -storepass <PASSWORD>
~~~
Then change 
```
coopel:
  auth:
    keystore:
      path: <PATH_TO_KEYSTORE>
      secret: <PASSWORD>
      key-alias: <ALIAS>
```
### Get JWT public key
- Execute to extract public key
~~~
keytool -list -rfc --keystore /home/keystore.jks | openssl x509 -inform pem -pubkey
~~~
- Get from API
~~~
curl http://localhost:22022/oauth/token_key
~~~
