hybrid-cloud-container-mgmt
==

# Project Structure

## Root project
The main project is present in `container-management` directory. The main project depends on two libraries
which are present in a different directories with their own build.gradle

## Libraries
The libraries are present separately in two different gradle projects under the `libraries` directory
These libraries can be built independently of the main project thus making it possible to be used in 
any other project by publishing them to the maven repository.


# Encryption (DRAFT??)

All private key files are stored in home directory defined in application.proprties
These files are encrypted using AES encryption with the secret key(password) which is not
stored in the server. Every time an user makes a deployment he will be asked for the password
which will be used to decrypt the stored private keys.

In case an attacker hacks into the server, he will still not have access to the secret key hence
the attack can be delayed until any kind of brute force attack takes place.

# How to add SB Admin pages

SB admin is loading all its assets using webjars, so you can fill all absolute paths to the assets by searching
for sb-admin in https://www.webjars.org/

All html pages are loaded using thymeleaf template engine and are present in resources/templates directory.
 -> To add a new page
 1. Download the sb admin zip file from https://startbootstrap.com/themes/sb-admin-2/
 2. Copy paste the html file which you require in templates directory. 
 3. Replace all css, js and img paths in that html file with webjars path.
 4. Add a new controller method for the view
 5. Configure Spring security for this new path in WebSecurityConfig [OPTIONAL]

TODO

 - Add container_id in container_deployments er diagram
 - Add extra fields from VM.java
 
 
 -->> Security features
 
  - JSR 303 validations
  - Custom form validations
  - HTML5 validations
  - CSRF protection with spring security
  - SQL injection protection with JPA / ORM
  - Role based authorization to resources
  - Route protection with spring security
  - Authentication with spring security
  - Encryption of private keys
 
 

 
 # Design Pattern -> Converter Pattern, Strategy Pattern
 
 
 # Parallel Computing Problem -> When an deployment is under process another deployment can have
 #  incorrect allocations due to wrong memory fetches from VMs
 
 
 # How to run JUnit tests
 
  - Create a postgres database
  - Update the database access details in application-test.properties
  - Now run using the gradle task to execute all tests in your test db