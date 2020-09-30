package main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.Scanner;


/**
 *
 */
public class GraphPanel extends JPanel {


    static final int NODE_DIMENSION = 10;
    static final Color VISITED_COLOR = Color.RED;
    static final Color UNVISITED_COLOR = Color.BLACK;
    static final Color PATH_COLOR = Color.GREEN;
    static final String[] MOUSE_STATES = {"startNode", "endNode", "reset"};
    static final double NODE_RADIUS = 0.5;

    private boolean isFirstPaint = true;
    private String mouseState = MOUSE_STATES[0];
    private String algName;
    private String graphSize;
    protected Integer startNode;
    protected Integer endNode;
    private boolean[] visited;
    private HashMap<Shape, Integer> allNodes;
    private HashMap<Integer, Integer[]> adjNodes;
    private Deque<Integer> path;


    /**
     *
     */
    public GraphPanel() {

        // Defaults
        this.setAlgName("Breadth-First Search");
        this.setGraphSize("Small");

        // Detect user-selected start and end nodes
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {

                super.mouseClicked(me);

                if (mouseState.equals("reset")) {
                    startNode = endNode = null;
                    repaint();
                    mouseState = "startNode";
                } else {
                    for (Shape shape : allNodes.keySet()) {
                        if (shape.contains(me.getPoint())) {
                            if (mouseState.equals("startNode")) {
                                startNode = allNodes.get(shape);
                                repaint();
                                mouseState = "endNode";
                            } else {
                                endNode = allNodes.get(shape);
                                repaint();
                                mouseState = "reset";
                            }
                        }
                    }
                }
            }
        });
    }


    /**
     * @param g
     */
    public void paint(Graphics g) {

        super.paint(g);

        // Paint start and end nodes or unpaint if either is null

        // Loop through all nodes and paint them as unvisited or visited
//        g.setColor(UNVISITED_COLOR);
//        g.setColor(VISITED_COLOR);
//        g.drawOval(0, 0, NODE_DIMENSION, NODE_DIMENSION);
//        g.fillOval(0, 0, NODE_DIMENSION, NODE_DIMENSION);

        // Loop through all edges and paint them as unvisited
//        g.setColor(UNVISITED_COLOR);
//        g.drawLine(0, 0, 0, 0);

        // Loop through nodes in path and paint them, along with their edges
//        g.setColor(PATH_COLOR);
    }


    /**
     *
     */
    private void initGraph() {

        // READ NODES AND ADJACENCY'S FROM FILE. UPDATE adj AS YOU GO.

        String graphFileLocation = System.getProperty("user.dir" +
                "\\src\\main\\java\\" + "resources" + graphSize + ".txt");
        try {
            Scanner scanner = new Scanner(new File(graphFileLocation));
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");
                Integer currentNode = Integer.parseInt(line[0]);
                double currentNodeX = Double.parseDouble(line[1]);
                double currentNodeY = Double.parseDouble(line[2]);
                Shape nodeShape = new Ellipse2D.Double(currentNodeX,
                        currentNodeY, NODE_RADIUS, NODE_RADIUS);
                allNodes.put(nodeShape, currentNode);
                //ADD EDGES TO ADJ LIST ///////////

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        int numNodes = 0;


        visited = new boolean[numNodes];
    }


    /**
     *
     */
    protected void startAlgorithm() {
    }


    /**
     *
     */
    protected void stopAlgorithm() {
    }


    /**
     *
     */
    protected void pauseAlgorithm() {
    }


    /**
     * Sets which algorithm will be animated.
     *
     * @param algName Name of an algorithm to be animated.
     */
    protected void setAlgName(String algName) {

        this.algName = algName;
    }


    /**
     * Sets the size of the graph input for the algorithm.
     *
     * @param graphSize Size of the graph.
     */
    protected void setGraphSize(String graphSize) {

        this.graphSize = graphSize;
    }
}
