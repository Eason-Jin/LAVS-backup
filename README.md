[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/5pVslkgH)
# SOFTENG 306: Project 1 Starter

Welcome to your first project for SOFTENG 306. 

The project specification is available [here](https://canvas.auckland.ac.nz/courses/105857/files/13471322). The four deliverables for this project are:
* [Design Documentation](https://canvas.auckland.ac.nz/courses/105857/assignments/400876)
* [Demo](https://canvas.auckland.ac.nz/courses/105857/assignments/400878)
* [Code](https://canvas.auckland.ac.nz/courses/105857/assignments/400879)
* [Quality Report](https://canvas.auckland.ac.nz/courses/105857/assignments/404409)

## Important

Update this readme file to contain the names of each team member and your GitHub login.

* Jessica Jiang - jj2947
* Eason Jin - Eason-Jin
* Amanda Lowe - starfluffy
* Liam Parker - LiamParker1
* Victor Qiu - vqiu25

## Initial Repository Code

The repository contains some initial Java code to help your project. These are under the [src/main/java/uoa/lavs](src/main/java/uoa/lavs) folder. The following code is supplied:
* [mainframe](src/main/java/uoa/lavs/mainframe): the interfaces for sending requests to and receiving responses from the mainframe. You will need to use the [Connection](src/main/java/uoa/lavs/mainframe/Connection.java) interface in your code: we have provided two implementations of this interface in the [simulator](src/main/java/uoa/lavs/mainframe/simulator/) folder.
* [Utility](src/main/java/uoa/lavs/utility/): a utility for calculating loan repayments.

  In addition, there are some unit tests in [src/test/java/uoa/lavs/](src/test/java/uoa/lavs/) folder.


## Running Instructions

To run LAVS, perform:

`./mvnw clean javafx:run`

## Debugging Instructions

To debug LAVS, perform:

`./mvnw clean javafx:run@debug`

## Testing Instructions

To run the JUnit tests on VSCode:
1. Find `src/test/java/uoa/lavs`
2. Here you will find `dataoperations` and `models` folder
3. Right click on either folder and click "Run Tests with Coverage", to view code coverage.

To run the JUnit tests on IntelliJ:
1. Find `src/main/java/uoa/lavs`
2. Right click the `lavs` folder and hover over "More Run/Debug", clicking "Run 'Tests in 'uoa.lavs" with Coverage"


## Dependencies

LAVS utilises the following dependencies:
* Nitrite Database
* OkHttp
* Apache Commons Math
* JUnit 
* JavaFX 
* Atlantafx
* SQLite JDBC Driver
* Spring Framework


