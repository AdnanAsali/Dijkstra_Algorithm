package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import application.MinHeap.Neighbour;
//import application.MinHeap.Vertex;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

class PannableCanvas extends Pane 
{

    DoubleProperty myScale = new SimpleDoubleProperty(1.0);

    public PannableCanvas() 
    {
        setPrefSize(10000, 2000);
//        setStyle("-fx-background-color: lightgrey; -fx-border-color: blue;");

        // add scale transform
        scaleXProperty().bind(myScale);
        scaleYProperty().bind(myScale);
    }

    /**
     * Add a grid to the canvas, send it to back
     */
    public void addGrid() 
    {

        double w = getBoundsInLocal().getWidth();
        double h = getBoundsInLocal().getHeight();

        // add grid
        Canvas grid = new Canvas(w, h);

        // don't catch mouse events
        grid.setMouseTransparent(true);

//        GraphicsContext gc = grid.getGraphicsContext2D();

//        gc.setStroke(Color.GRAY);
//        gc.setLineWidth(1);

        // draw grid lines
        double offset = 50;
        for( double i=offset; i < w; i+=offset) 
        {
//            gc.strokeLine( i, 0, i, h);
//            gc.strokeLine( 0, i, w, i);
        }

        getChildren().add( grid);

        grid.toBack();
    }

    public double getScale() 
    {
        return myScale.get();
    }

    public void setScale( double scale) 
    {
        myScale.set(scale);
    }

    public void setPivot( double x, double y) 
    {
        setTranslateX(getTranslateX()-x);
        setTranslateY(getTranslateY()-y);
    }
}


/**
 * Mouse drag context used for scene and nodes.
 */
class DragContext {

    double mouseAnchorX;
    double mouseAnchorY;

    double translateAnchorX;
    double translateAnchorY;

}

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if parent is zoomed.
 */
class NodeGestures {

    private DragContext nodeDragContext = new DragContext();

    PannableCanvas canvas;

    public NodeGestures( PannableCanvas canvas) 
    {
        this.canvas = canvas;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() 
    {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() 
    {
        return onMouseDraggedEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() 
    {
        public void handle(MouseEvent event) 
        {
            // left mouse button => dragging
            if( !event.isPrimaryButtonDown())
                return;
            nodeDragContext.mouseAnchorX = event.getSceneX();
            nodeDragContext.mouseAnchorY = event.getSceneY();

            Node node = (Node) event.getSource();

            nodeDragContext.translateAnchorX = node.getTranslateX();
            nodeDragContext.translateAnchorY = node.getTranslateY();
        }
    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() 
    {
        public void handle(MouseEvent event) 
        {
            // left mouse button => dragging
            if( !event.isPrimaryButtonDown())
                return;

            double scale = canvas.getScale();

            Node node = (Node) event.getSource();

            node.setTranslateX(nodeDragContext.translateAnchorX + (( event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
            node.setTranslateY(nodeDragContext.translateAnchorY + (( event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));

            event.consume();

        }
    };
}

/**
 * Listeners for making the scene's canvas draggable and zoomable
 */
class SceneGestures {

    private static final double MAX_SCALE = 10.0d;
    private static final double MIN_SCALE = .1d;

    private DragContext sceneDragContext = new DragContext();

    PannableCanvas canvas;

    public SceneGestures( PannableCanvas canvas) {
        this.canvas = canvas;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {

            // right mouse button => panning
            if( !event.isSecondaryButtonDown())
                return;

            sceneDragContext.mouseAnchorX = event.getSceneX();
            sceneDragContext.mouseAnchorY = event.getSceneY();

            sceneDragContext.translateAnchorX = canvas.getTranslateX();
            sceneDragContext.translateAnchorY = canvas.getTranslateY();

        }

    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {

            // right mouse button => panning
            if( !event.isSecondaryButtonDown())
                return;

            canvas.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
            canvas.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);

            event.consume();
        }
    };

    /**
     * Mouse wheel handler: zoom to pivot point
     */
    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

        @Override
        public void handle(ScrollEvent event) {

            double delta = 1.2;

            double scale = canvas.getScale(); // currently we only use Y, same value is used for X
            double oldScale = scale;

            if (event.getDeltaY() < 0)
                scale /= delta;
            else
                scale *= delta;

            scale = clamp( scale, MIN_SCALE, MAX_SCALE);

            double f = (scale / oldScale)-1;

            double dx = (event.getSceneX() - (canvas.getBoundsInParent().getWidth()/2 + canvas.getBoundsInParent().getMinX()));
            double dy = (event.getSceneY() - (canvas.getBoundsInParent().getHeight()/2 + canvas.getBoundsInParent().getMinY()));

            canvas.setScale(scale);

            // note: pivot value must be untransformed, i. e. without scaling
            canvas.setPivot(f*dx, f*dy);

            event.consume();

        }

    };


    public static double clamp( double value, double min, double max) 
    {
        if( Double.compare(value, min) < 0)
            return min;

        if( Double.compare(value, max) > 0)
            return max;

        return value;
    }
}



/**
 * An application with a zoomable and pannable canvas.
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    private int veNum;
	private int x;
	private int y;
	private int size;
    @FXML
    private Pane Pane = new Pane();

    Vertex[] vertices = new Vertex[87575];

    @Override
    public void start(Stage stage) throws FileNotFoundException {

        Group group = new Group();
        TextField from = new TextField("From");
        TextField to = new TextField("To");
        TextField ansTextField = new TextField("Cost");
        Button shortestPath = new Button("Find Shortest Path");
        VBox vb = new VBox();
        TextArea path = new TextArea("Path");
        vb.setSpacing(10);
        vb.setMargin(from, new Insets(20, 20, 20, 20));  
        vb.setMargin(to, new Insets(20, 20, 20, 20));
        vb.setMargin(ansTextField, new Insets(20, 20, 20, 20));
        vb.setMargin(shortestPath, new Insets(20, 20, 20, 20));  
        vb.setMargin(path, new Insets(20, 20, 20, 20));  
        vb.setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, null, null)));
        vb.setPrefWidth(300);
        vb.setPrefHeight(1124);
        vb.getChildren().addAll(from, to, shortestPath, ansTextField, path);
        
        // create canvas
        PannableCanvas canvas = new PannableCanvas();

        // we don't want the canvas on the top/left in this example => just
        // translate it a bit
        canvas.setTranslateX(0);
        canvas.setTranslateY(0);

        // create sample nodes which can be dragged
        NodeGestures nodeGestures = new NodeGestures( canvas);
        
        Scanner input = null;
//        String file = "/home/adnan/USA.txt";
//        	String file = "/home/adnan/Desktop/grid10x10.txt";
//        	Scanner wantedFile = new Scanner(System.in);
//        	String file = wantedFile.next();
        try {
			input = new Scanner(new File("/home/adnan/USA.txt"));
//			input = new Scanner(new File("/home/adnan/Desktop/cs210-summer2014-master/project2/grid10x10.txt"));
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
          String sizeString = input.next();       //get the size as a string
          size = Integer.parseInt(sizeString);

          System.out.println("======The size is: " + size);
//          vertices = new Vertex[size];
         // addNodes();

          input.next();
          Circle circle;

          /*Now read the vertices*/
          for(int i = 0; i < vertices.length; i++)
          {
        	  veNum = Integer.parseInt(input.next());
  	        	x = input.nextInt();
  	        	y = input.nextInt();
              circle = new Circle();
              circle.setCenterX(x);
              circle.setCenterY(y);
              circle.setRadius(7);
              circle.setFill(Color.DARKRED);
              Pane.getChildren().add(circle);
//              System.out.println(circle.toString());
              vertices[i] = new Vertex(veNum, x, y);
          }
          
          
          
          
          /*Now read the edges */
          while(input.hasNext())
          {
              int vertex1 = Integer.parseInt(input.next());
              int vertex2 = Integer.parseInt(input.next());
              
              System.out.println(vertex1 + " adj to --> " + vertex2);
              //System.out.println("====" + vertex1 + "====" + vertex2);

              /*Formula to calculate the distance(weight)*/
              int distance = (int) Math.sqrt(Math.pow(vertices[vertex1].xCord - vertices[vertex2].xCord, 2) + Math.pow(vertices[vertex1].yCord - vertices[vertex2].yCord, 2));

            	  Line line = new Line(vertices[vertex1].xCord,vertices[vertex1].yCord,vertices[vertex2].xCord,vertices[vertex2].yCord);
            	  Pane.getChildren().add(line);
            	  line.setStroke(Color.BLUEVIOLET);
            	  System.out.println(line.toString());
              
              /*Pass the 2 vertexes that make an edge, and their distance to addEdge method*/
              addEdge(vertex1, vertex2, distance);  


          }

        canvas.getChildren().addAll(Pane);

        group.getChildren().addAll(canvas,vb);

        // create scene which can be dragged and zoomed
        Scene scene = new Scene(group, 1024, 768);

        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        stage.setScene(scene);
		stage.setFullScreen(true);
        stage.show();

        canvas.addGrid();
      
        for (int j = 0; j < vertices.length; j++) 
        {
        	int x = j;
        	Pane.getChildren().get(j).setOnMouseClicked(e -> 
        	{
        		((Shape) Pane.getChildren().get(x)).setFill(Color.BLACK);
        		Alert alert = new Alert(AlertType.INFORMATION);
        		alert.setTitle("Information Dialog");
        		alert.setHeaderText("Name of Vertex is : " + vertices[x].name);
        		alert.setContentText("X-Coordinate : " + vertices[x].xCord + "\n" 
        							+ "Y-Coordinate : " + vertices[x].yCord + "\n" );
        		int selectedVertex = vertices[x].adj.index;
        		int selectedVertex2 = vertices[x].adj.weight;
//        		((Shape) Pane.getChildren().get(selectedVertex)).setFill(Color.BLUE);        		
//        		((Shape) Pane.getChildren().get(selectedVertex2)).setFill(Color.BLUE);        		
        		alert.showAndWait();
        	});
		}
        MinHeap sol = new MinHeap();
        
        shortestPath.setOnAction(e -> 
        {
        	resetUI();
        	sol.findShortestPaths(Integer.parseInt(from.getText()), Integer.parseInt(to.getText()));
        	ansTextField.setText(sol.finalCost);
        	System.out.println("This is the final cost : " + sol.finalCost);
        	for (int i = 0; i < sol.pathCircles.size(); i++) 
        	{
        		((Shape) Pane.getChildren().get(sol.pathCircles.get(i))).setFill(Color.YELLOW);  
//        		System.out.println(sol.pathCircles.get(i));
//        		path.setText(sol.ansString);
        	}
        	
        });
        
        Button search = new Button("Search");
        TextField wantedNode = new TextField("Enter node name");
        
        search.setOnAction(e -> 
        {
        	((Shape) Pane.getChildren().get( Integer.parseInt(wantedNode.getText()))).setFill(Color.GREEN);        		
        	((Circle) Pane.getChildren().get( Integer.parseInt(wantedNode.getText()))).setRadius(50);        		
        });
        
        vb.setMargin(search, new Insets(20, 20, 20, 20));
        vb.setMargin(wantedNode, new Insets(20, 20, 20, 20));  
        vb.getChildren().addAll(wantedNode, search);
    }
    
    
    
    public void addEdge(int sourceName, int destinationName, int weight) 
    {
        int srcIndex = sourceName;
        int destiIndex = destinationName;
        vertices[srcIndex].adj = new Neighbour(destiIndex, weight, vertices[srcIndex].adj);
        vertices[destiIndex].indegree++;
    }
    
    public static class Neighbour 
    {
        int index;
        Neighbour next;
        int weight;

        public Neighbour(int index, int weight, Neighbour next) 
        {
            this.index = index;
            this.next = next;
            this.weight = weight;
        }
    }
    
    public void resetUI()
    {
    	for (int i = 0; i < vertices.length; i++) 
    	{
    		((Shape) Pane.getChildren().get(i)).setFill(Color.BLACK);  
        	((Circle) Pane.getChildren().get(i)).setRadius(7);        		

		}
    }
    
    

}