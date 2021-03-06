# PDPTW

System solves the transportation problem, mainly PDPTW.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisities

What things you need to install the software and how to install them

```
* Maven
* IntellIJ/Eclipse with an installed LombokPlugin
```

### Installing

A step by step series of examples that tell you have to get a development env running

Clone or download repository

```
    $ git clone https://gitee.com/bupt_htl/pdptw.git
```

Install maven dependency

```
    $ mvn clean install
install dependency and skip tests:
   $ mvn clean install -DskipTests
```

Import project to IntelIJ/Eclipse

```
Import as maven project.
```

Run

```
Run Main.java via IDE
```

## Running the tests

Test can be run via Maven, or IDE

```
    $ mvn test
```

## Visualization via visJs

VisJs home page:
```
http://visjs.org/docs/network/
```
Steps to run visualization:
```
1. run Main, then you get a json file in /PDPTW/src/visualization/results
2. open visualization.html
3. import file
```

![image](http://106.12.212.42/upload/2020/11/image-42abba33aeca48c295fcac270818ee19.png)

## Built With

* Maven


