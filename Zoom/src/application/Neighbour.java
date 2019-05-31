package application;

//import application.MinHeap.Neighbour;

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