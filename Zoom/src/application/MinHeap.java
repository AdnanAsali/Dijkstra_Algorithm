package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

public class MinHeap 
{
//	/home/adnan/eclipse-workspace-ubuntu/Dijkstra/Test.txt
//	/home/adnan/eclipse-workspace-ubuntu/Dijkstra/USA.txt

private int veNum;
private int x;
private int y;
public static Vertex[] verticesAfter;
private int size;
public static String finalCost;
@FXML
private Pane Pane;
ArrayList<Integer> pathCircles = new ArrayList<Integer>();

public static void main(String[] args) throws FileNotFoundException
{
    
}

    public MinHeap() 
    {
	    Scanner input = null;
		try 
		{
			input = new Scanner(new File("/home/adnan/USA.txt"));
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	    String sizeString = input.next();       //get the size as a string
	    size = Integer.parseInt(sizeString);
	
	    System.out.println("======The size is: " + size);
	    verticesAfter = new Vertex[size];
	   // addNodes();
	
	    input.next();

	    /*Now read the verticesAfter*/
	    for(int i = 0; i < verticesAfter.length; i++)
	    {
	        veNum = Integer.parseInt(input.next());
	        x = Integer.parseInt(input.next());
	        y = Integer.parseInt(input.next());
	
	        verticesAfter[i] = new Vertex(veNum, x, y);
	    }
	
	
	    /*Now read the edges */
	    while(input.hasNext())
	    {
	        int vertex1 = Integer.parseInt(input.next());
	        int vertex2 = Integer.parseInt(input.next());
	        int distance = (int) Math.sqrt(Math.pow(verticesAfter[vertex1].xCord - verticesAfter[vertex2].xCord, 2) + Math.pow(verticesAfter[vertex1].yCord - verticesAfter[vertex2].yCord, 2));
	        addEdge(vertex1, vertex2, distance);  
	    }
    }


    //======================================================================================================
// int indexForName(String name)
// {
//    for(int i = 0; i < verticesAfter.length; i++)
//    {
//    /*Look for the vertex name in the array to see if they match with the one the one from
//    the file */
//        if(verticesAfter[i].name == Integer.parseInt(name))
//        {
//            return i;
//        }
//    }
//    return -1;
//}

 //===============================================================================================================


    public void addEdge(int sourceName, int destinationName, int weight) 
    {
        int srcIndex = sourceName;
        int destiIndex = destinationName;
        verticesAfter[srcIndex].adj = new Neighbour(destiIndex, weight, verticesAfter[srcIndex].adj);
        verticesAfter[destiIndex].indegree++;
    }

    public void findShortestPaths(int sourceName, int end)
    {
        for (int i = 0; i < size; i++) 
        {
            if (verticesAfter[i].name == sourceName) 
            {
                applyDikjstraAlgorith(verticesAfter[i], verticesAfter[end]);
                break;// in this case we need not traverse the nodes which are
                // not reachable from the source Node
            }
        }
//        applyDikjstraAlgorith(verticesAfter[sourceName], verticesAfter[end]);
//         for(int i = 0; i < size; i++){
//           System.out.println("Distance of " + verticesAfter[i].name+" from Source: " + verticesAfter[i].cost);
//        }
    }

    public class Vertex 
    {
        int cost;
        int name;
        Neighbour adj;
        int indegree;
        State state;
        int xCord;
        int yCord;

        public Vertex(int name, int xCord, int yCord) {
            this.name = name;
            cost = Integer.MAX_VALUE;
            state = State.NEW;
            this.xCord = xCord;
            this.yCord = yCord;


        }

        public int compareTo(Vertex v) 
        {
            if (this.cost == v.cost) 
            {
                return 0;
            }
            if (this.cost < v.cost) 
            {
                return -1;
            }
            return 1;
        }
    }

    public enum State 
    {
        NEW, IN_Q, VISITED
    }

    public class Neighbour 
    {
        int index;
        Neighbour next;
        int weight;

        Neighbour(int index, int weight, Neighbour next) 
        {
            this.index = index;
            this.next = next;
            this.weight = weight;
        }
    }
    
    String ansString = "";

    public void applyDikjstraAlgorith(Vertex src, Vertex end) 
    {
    	
        Heap heap = new Heap(size);
        heap.add(src);
        src.state = State.IN_Q;
        src.cost = 0;
        while (!heap.isEmpty()) 
        {
            Vertex u = heap.remove();
            u.state = State.VISITED;
            Neighbour temp = u.adj;    //the neighbor of the vertex being removed. it accesses it adj neighbor list
            System.out.println("Edge weights :- ");
            while (temp != null) 
            {    //while it has a neighbor
                if (verticesAfter[temp.index].state == State.NEW) 
                {  //if that neighbor is unvisited
                    heap.add(verticesAfter[temp.index]);             //add the unvisited verticesAfter to the heap
                    verticesAfter[temp.index].state = State.IN_Q;    //make the state indicating its in the heap
//		            pathCircles.add(verticesAfter[temp.index].name);
                }
                
                System.out.println("Weight from "+ verticesAfter[u.name].name + " to " + verticesAfter[temp.index].name +" is "+ temp.weight);
                ansString += verticesAfter[u.name].name + " " + verticesAfter[temp.index].name + " " + temp.weight + "\n";
                if (verticesAfter[temp.index].cost > u.cost + temp.weight) //if the neighbors weight is less than 
                {  
                    verticesAfter[temp.index].cost = u.cost + temp.weight;
                    heap.heapifyUP(verticesAfter[temp.index]);
                    System.out.println("The cost until this point is : " + verticesAfter[temp.index].cost);
                    finalCost = verticesAfter[temp.index].cost + "";
                }
                pathCircles.add(temp.index);
                temp = temp.next;
            }
        }
    }

    public static class Heap 
    {
        private Vertex[] heap;
        private int maxSize;
        private int size; //starts off as 0


        public Heap(int maxSize) 
        {
            this.maxSize = maxSize;
            heap = new Vertex[maxSize];  //make the max size for the heap array made of verticesAfter 
        }

        public void add(Vertex u) 
        {
            heap[size++] = u;       //fill the heap array with the verticesAfter, starting at position 0
            heapifyUP(size - 1);      //pass each vertext ino heapifyUP (vertex type)
        }

        public void heapifyUP(Vertex u) 
        {
            for (int i = 0; i < maxSize; i++) 
            {  //look for vertex in the heap array 
                if (u == heap[i]) 
                { 
                    heapifyUP(i);  //if its found, go to heapifyUp method (int type) and pass in the vertex num
                    break;
                }
            }
        }

        public void heapifyUP(int position) 
        {
            int currentIndex = position;
            Vertex currentItem = heap[currentIndex];

            int parentIndex = (currentIndex - 1) / 2;
            Vertex parentItem = heap[parentIndex];
            
            while (currentItem.compareTo(parentItem) == -1) 
            {
                swap(currentIndex, parentIndex);
                currentIndex = parentIndex;

                if (currentIndex == 0) 
                {
                    break;
                }
                currentItem = heap[currentIndex];
                parentIndex = (currentIndex - 1) / 2;
                parentItem = heap[parentIndex];
            }
        }

        public Vertex remove() 
        {
            Vertex v = heap[0];
            swap(0, size - 1);
            heap[size - 1] = null;
            size--;
            heapifyDown(0);
            return v;
        }

        public void heapifyDown(int postion) 
        {
            if (size == 1) 
            {
                return;
            }

            int currentIndex = postion;
            Vertex currentItem = heap[currentIndex];
            int leftChildIndex = 2 * currentIndex + 1;
            int rightChildIndex = 2 * currentIndex + 2;
            int childIndex;
            if (heap[leftChildIndex] == null) 
            {
                return;
            }
            if (heap[rightChildIndex] == null) 
            {
                childIndex = leftChildIndex;
            } else if (heap[rightChildIndex].compareTo(heap[leftChildIndex]) == -1) 
            {
                childIndex = rightChildIndex;
            } else {
                childIndex = leftChildIndex;
            }
            Vertex childItem = heap[childIndex];
            while (currentItem.compareTo(childItem) == 1) 
            {
                swap(currentIndex, childIndex);
                currentIndex = childIndex;
                currentItem = heap[currentIndex];
                leftChildIndex = 2 * currentIndex + 1;
                rightChildIndex = 2 * currentIndex + 2;
                if (heap[leftChildIndex] == null) 
                {
                    return;
                }
                if (heap[rightChildIndex] == null) 
                {
                    childIndex = leftChildIndex;
                } 
                else if (heap[rightChildIndex].compareTo(heap[leftChildIndex]) == -1) 
                {
                    childIndex = rightChildIndex;

                } 
                else 
                {
                    childIndex = leftChildIndex;
                }
            }
        }

        public void swap(int index1, int index2) 
        {
            Vertex temp = heap[index1];
            heap[index1] = heap[index2];
            heap[index2] = temp;
        }

        public boolean isEmpty() 
        {
            return size == 0;
        }
    }
}