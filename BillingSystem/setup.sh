#!/bin/bash

javac -cp ".:sqlite-jdbc-3.36.0.3.jar" 	LoginFrame.java
java -cp ".:sqlite-jdbc-3.36.0.3.jar" LoginFrame

