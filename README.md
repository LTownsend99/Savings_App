# Savings App for Young People 


## Database 

The database script can be found within the DatabaseScript file within the project
this must be set up on a local Database (such as MySQL).

Once this has been set up /main/resources/application.properties needs to be 
updated with the relevant url, username and password.


```
spring.datasource.url=jdbc:mysql://localhost:3306/mydb

spring.datasource.username=root

spring.datasource.password=Liam2467

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

This must be set up prior to running the application.

## Running the project

To run the project use SavingsAppApplication class

This must be running to allow the flutter application to communicate with it.