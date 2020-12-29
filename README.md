# k-center-solution-plotter
This software provides a graphic tool to visualize solutions for the K Center Problem.

### Requirements
* To have Java 11 installed and added to the environment path
* To have Maven installed and added to the environment path

# Compile

```
$ mvn clean compile package

```

# Run

```
$ java -jar shade/KCenterPlotter.jar 
```

# Using on Windows
Select the button "Load instance" and choose a .json file with the following format and properties:
```
{ "instance": "D:\\Users\\my_user\\Documents\\instances\\kroa100.tsp", 
 "outliers": [32,53,51,75,44], 
  "centers": [ { "center": 20, "nodes": [5,9,10,14,16,17,20,23,31,35,37,48,58,62,71,73,78,83,89,98]},
{ "center": 49, "nodes": [1,4,12,29,32,36,38,43,49,50,51,67,72,75,77,81,84,86,94,95]},
{ "center": 74, "nodes": [3,7,15,18,21,25,30,41,52,55,64,65,69,74,79,87,88,91,93,96]},
{ "center": 33, "nodes": [2,6,8,11,13,19,26,28,33,34,40,42,45,47,54,61,70,82,85,99]},
{ "center": 57, "nodes": [0,22,24,27,39,44,46,53,56,57,59,60,63,66,68,76,80,90,92,97]}
]}
```

The "instance" property must contain the path to a file with a valid traditional tsp format (check some examples in the /instances folder of this project)

# Using on Linux
Select the button "Load instance" and choose a .json file with the following format and properties:
```
{ "instance": "/home/my_user/Documents/instances/kroa100.tsp", 
 "outliers": [32,53,51,75,44], 
  "centers": [ { "center": 20, "nodes": [5,9,10,14,16,17,20,23,31,35,37,48,58,62,71,73,78,83,89,98]},
{ "center": 49, "nodes": [1,4,12,29,32,36,38,43,49,50,51,67,72,75,77,81,84,86,94,95]},
{ "center": 74, "nodes": [3,7,15,18,21,25,30,41,52,55,64,65,69,74,79,87,88,91,93,96]},
{ "center": 33, "nodes": [2,6,8,11,13,19,26,28,33,34,40,42,45,47,54,61,70,82,85,99]},
{ "center": 57, "nodes": [0,22,24,27,39,44,46,53,56,57,59,60,63,66,68,76,80,90,92,97]}
]}
```

# Example
![Alt text](./gui.png?raw=true "Title")