# Dijkstra_Algorithm

## Official Project Description
Implement the classic Dijkstra’s shortest path algorithm and optimize it for maps. Such algorithms are widely used in geographic information systems (GIS) including MapQuest and GPS-based car navigation systems.

For this project we will be working with maps, or graphs whose vertices are points in the plane and are connected by edges whose weights are Euclidean distances. Think of the vertices as cities and the edges as roads connected to them. To represent a map in a file, we list the number of vertices and edges, then list the vertices (index followed by its x and y coordinates), then list the edges (pairs of vertices), and finally the source and sink vertices. For example, represents the map below:

![image](https://user-images.githubusercontent.com/36306586/58710979-335ce500-83c6-11e9-93e5-8222527ceb7e.png)


Dijkstra’s algorithm is a classic solution to the shortest path problem on a weighted graph. The basic idea is not difficult to understand. We maintain, for every vertex in the graph, the length of the shortest known path from the source to that vertex, and we maintain these lengths in a priority queue. Initially, we put all the vertices on the queue with an artificially high priority and then assign priority 0.0 to the source. The algorithm proceeds by taking the lowest-priority vertex off the PQ, then checking all the vertices that can be reached from that vertex by one edge to see whether that edge gives a shorter path to the vertex from the source than the shortest previously-known path. If so, it lowers the priority to reflect this new information.
Here is a step-by-step description that shows how Dijkstra’s algorithm finds the shortest path 0-1-2-5 from 0 to 5 in the example above:

process 0 (0.0)
    lower 3 to 3841.9
    lower 1 to 1897.4
process 1 (1897.4)
    lower 4 to 3776.2
    lower 2 to 2537.7
process 2 (2537.7)
    lower 5 to 6274.0
process 4 (3776.2)
process 3 (3841.9)
process 5 (6274.0)

This method computes the length of the shortest path. To keep track of the path, we also maintain for each vertex, its predecessor on the shortest path from the source to that vertex.

Your goal. Optimize Dijkstra’s algorithm so that it can process thousands of shortest path queries for a given map. Once you read in (and optionally preprocess) the map, your program should solve shortest path problems in sublinear time. One method would be to precompute the shortest path for all pairs of vertices; however you cannot afford the quadratic space required to store all of this information. Your goal is to reduce the amount of work involved per shortest path computation, without using excessive space.

Idea. The naive implementation of Dijkstra’s algorithm examines all V vertices in the graph. An obvious strategy to reduce the number of vertices examined is to stop the search as soon as you discover the shortest path to the destination. With this approach, you can make the running time per shortest path query proportional to E’ log V’ where E’ and V’ are the number of edges and vertices examined by Dijkstra’s algorithm. However, this requires some care because just re-initializing all of the distances to ∞ would take time proportional to V. Since you are doing repeated queries, you can speed things up dramatically by only re-initializing those values that changed in the previous query.

Testing. The file usa.txt contains 87,575 intersections and 121,961 roads in the continental United States. The graph is very sparse. Your main goal should be to answer shortest path queries quickly for pairs of vertices on this network. Your algorithm will likely perform differently depending on whether the two vertices are nearby or far apart. We provide input files that test both cases. You may assume that all of the x and y coordinates are integers between 0 and 10,000.
You can test your code using the files test.txt on the map usa.txt



-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------

# ScreenShots of the actual project up and running on my machine

### This image is a sample run where it shows the -From- and the -To- text fields and the distance of the shortest path.

![dij](https://user-images.githubusercontent.com/36306586/58710034-19ba9e00-83c4-11e9-8553-06e83a2ff4c8.png)

### This image shows the "Zooming" effect done by JavaFX library and also the searching function among all the nodes on the map.

![zoomDij](https://user-images.githubusercontent.com/36306586/58710048-217a4280-83c4-11e9-8fe6-7158a9735a3f.png)


