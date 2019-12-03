# ToDo App for managing your own ToDo List 

##### Including our recent DLC- ToDo Subtasks!

About
---
This service is open for anyone trying to cope with forgetting the keys, leaving the trash inside and not doing the dishes. 
Create your own todo list and add subtasks to it. It really is that simple.

How to start the application
---
Just provide the IP of your favorite database and add it to the config in the jdbc url so the service can connect to Postgres.
Execute ``mvn clean install`` to create a .jar File. Afterwards you can start up the service with the provided
Dockerfile by typing ``docker build -t todo .`` in the project directory and run it with ``docker run -d todo:latest``.

If you take a closer look, there is also a docker-compose file in the project root directory. You can start it up via ``docker-compose up -d`` and there will be a local postgres database available to work with this service.
   

API
---
Check the /swagger path while running the service for a more detailed explanation of the api.

Execute Tests
---
Just go ahead and use `mvn clean test` to execute all unit tests and the integration test 
##

@Author Dennis Berthold 2019