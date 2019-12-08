Hybrid Cloud Management System
==

The project allows you to create VMs present across different cloud providers or in-house systems.  
You can then use these VMs to deploy your applications easily. The project has different allocation  
strategies to deploy containers across VMs. It also has optimization and deployment algorithms to
efficiently  use and optimize your existing VMs


# Project Structure

The project is split into two main components: 
## 1. Root project
The main project is present in `container-management` directory. The main project depends on two libraries
which are present in different directories with their own build.gradle  

## 2. Libraries
The libraries are present separately in two different gradle projects under the `libraries` directory
These libraries can be built independently of the main project thus making it possible to be used in 
any other project by publishing them to the maven repository.


# External Frameworks/Libraries used

 - Spring with Spring boot 
 - Hibernate with JPA
 - Lombok
 - Thymeleaf
 - JSCH
 - Google GSON
 - Flyway
 - JUnit with Mockito

# Security features
 
 - JSR 303 validations
 - Custom form validations with Spring validator
 - HTML5 validations
 - CSRF protection with spring security
 - SQL injection protection with JPA / ORM
 - Role based authorization to resources
 - Route protection with spring security
 - Authentication with spring security
 - Encryption of private keys
 - Activity logs for forensics
 
 # Design Patterns
 Following design patterns are used across the project:
 ## Main project
 - Observer/Listener pattern for Hibernate events
 - Converter pattern to convert DTO <-> Model

 ## ConfigureVM library
 - Singleton pattern for single instance of VM connection
 - Strategy pattern for selecting deployment strategies
 
 ## Deployer library
 - Strategy pattern for selecting allocation strategies
 - Template pattern for optimization algorithm
 

 # Future scope
 - Make algorithms compatible with multi-threading.
 - Add ability to optimize VMs with different cores.
 - Improve encryption security
   
 
 # How to run JUnit tests
  - Create a postgres database
  - Update the database access details in application-test.properties
  - Now run using the gradle task to execute all tests in your test db