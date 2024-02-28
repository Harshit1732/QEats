# Overview

QEats is a popular food ordering app that allows users to browse and order their favorite dishes from nearby restaurants.

    1.  I had build different parts of the QEats backend which is a Spring Boot application.
    2.  Several REST API endpoints were implemented to query restaurant information and place food orders.
    3.  To give a sense of real-world problems, production issues were investigated using Scientific Debugging methods.
    4.  Along with this, I improved the app performance under large load scenarios as well as included an advanced search feature in the app. 

![Qeats](https://github.com/Harshit1732/QEats/assets/90718298/92483699-2b2f-44c0-a949-e4abbafffd24)


# Replicate performance issues and solve them using caching strategies
    1. Employed JMeter or load testing to expose performance issues.
    2. Identified DB queries contributing to degradation in performance.
    3. Used a Redis cache to alleviate read performance.
    4. Skills used:  Redis, JMeter
 ![Qeats2](https://github.com/Harshit1732/QEats/assets/90718298/58946019-9510-4d7a-8cf6-8fe00395683e)
 ![Qeats3](https://github.com/Harshit1732/QEats/assets/90718298/79adf78c-60ef-43fd-833d-c52e3c3cca73)

# Resolve production issues using Scientific Debugging
 1. Debug QEats app crashes from backend leveraging log messages and structured debugging techniques.
 2. Use IDE features (breakpoints) and assert statements to identify the root cause.
 3. Skills used : Scientific Debugging

# Retrieve restaurant data for a given user location
 1. Implemented GET /API/v1/restaurants and the corresponding request handler and response methods.
 2. Used Mockito to enable the development of the relevant MVCS layers independently.
 3. Retrieved a list of restaurants from MongoDB based on a user location.
 4. Skills used :Spring Boot, Spring Data, REST API, Jackson, Mockito, JUnit, MongoDB
![Qeats4](https://github.com/Harshit1732/QEats/assets/90718298/fe80bfdc-afa8-4ede-ad3d-360337f59067)

