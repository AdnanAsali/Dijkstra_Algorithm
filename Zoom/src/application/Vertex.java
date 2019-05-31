package application;

import application.MinHeap.Neighbour;
import application.MinHeap.State;

public class Vertex 
{
        int cost;
        int name;
        application.Main.Neighbour adj;
        int indegree;
        State state;
        int xCord;
        int yCord;

        public Vertex(int name, int xCord, int yCord) 
        {
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