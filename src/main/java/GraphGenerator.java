package main.java;

import main.java.util.Defs;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.*;

/**
 * Generates a {@link DefaultUndirectedWeightedGraph} that is minimally connected
 * and weighted.Then writes it to a file. The graph size can be provided.
 *
 * @author Ryan Albertson
 */
public class GraphGenerator extends DefaultWeightedEdge {

    /**
     * @param graph {@link DefaultUndirectedWeightedGraph} to check for connectivity.
     * @return True if {@code graph} is connected, otherwise false.
     */
    private static boolean isConnected(DefaultUndirectedWeightedGraph<Integer,
            DefaultWeightedEdge> graph) {

        Set<Integer> vertices = graph.vertexSet();

        // Edges cases
        if (vertices.size() <= 1) return true;
        if (vertices.size() - 1 > graph.edgeSet().size()) return false;

        Set<Integer> visited = new HashSet<>();
        Deque<DefaultWeightedEdge> queue = new ArrayDeque<>();
        Integer startNode = vertices.iterator().next();

        // BFS to determine connectivity
        visited.add(startNode);
        graph.edgesOf(startNode).forEach(queue::addLast);
        while (visited.size() != vertices.size() && queue.size() != 0) {
            DefaultWeightedEdge edge = queue.pollFirst();
            Integer source = graph.getEdgeSource(edge);
            Integer target = graph.getEdgeTarget(edge);
            Integer adjNode = visited.contains(source) ? target : source;
            if (!visited.contains(adjNode)) {
                visited.add(adjNode);
                graph.edgesOf(adjNode).forEach(queue::addLast);
            }
        }
        return visited.size() == vertices.size();
    }


    /**
     * Generates a connected, undirected, and weighted
     * {@link DefaultUndirectedWeightedGraph}.
     *
     * @param gPanel A {@link GraphPanel} that has all graph metadata.
     * @throws IllegalArgumentException If {@code graphSize} is null.
     */
    public static void generateGraph(GraphPanel gPanel)
            throws IllegalArgumentException {

        if (gPanel.graphSize == null) {
            throw new IllegalArgumentException("ERROR: GraphPanel not initialized.");
        }

        // Define graph sizes
        gPanel.nodeCount = Defs.nodeCountST.get(gPanel.graphSize);

        gPanel.nodeCoords = new HashMap<>(gPanel.nodeCount);
        gPanel.nodeShapes = new HashMap<>(gPanel.nodeCount);
        gPanel.visitedEdges = new HashSet<>(gPanel.nodeCount);
        gPanel.path = new int[gPanel.nodeCount];
        Arrays.fill(gPanel.path, Integer.MAX_VALUE);

        DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge> graph =
                new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);
        for (int i = 0; i < gPanel.nodeCount; i++) graph.addVertex(i);

        // Generate either approx. min. connected graph or a more complete graph.
        Random rand = new Random();
        if (gPanel.isShortPathAlg) {
            do {
                int node = rand.nextInt(gPanel.nodeCount);
                int adjNode = rand.nextInt(gPanel.nodeCount);
                while (node == adjNode) adjNode = rand.nextInt(gPanel.nodeCount);
                graph.addEdge(node, adjNode);
            }
            while (!isConnected(graph) && graph.vertexSet().size() <= gPanel.nodeCount);
        } else {
            int maxEdges;
            if (gPanel.graphSize.equals("Small")) maxEdges = gPanel.nodeCount * 2;
            else maxEdges = gPanel.nodeCount * 5;

            do {
                int node = rand.nextInt(gPanel.nodeCount);
                int adjNode = rand.nextInt(gPanel.nodeCount);
                while (node == adjNode) adjNode = rand.nextInt(gPanel.nodeCount);
                graph.addEdge(node, adjNode);
            }
            while (!isConnected(graph) || graph.edgeSet().size() <= maxEdges);
        }

        // Calculate random coordinates for nodes.
        for (int node = 0; node < gPanel.nodeCount; node++) {
            // Get random node coordinates within GUI bounds
            int xCenter = GUI.WINDOW_WIDTH / 2;
            int yCenter = GUI.GRAPH_HEIGHT / 2;
            int xLowerBound = -GUI.WINDOW_WIDTH / 2 + Defs.NODE_RADIUS;
            int xUpperBound = GUI.WINDOW_WIDTH / 2 - Defs.NODE_RADIUS;
            int textPadding = 30;
            int yLowerBound = -GUI.GRAPH_HEIGHT / 2 + Defs.NODE_RADIUS +
                    textPadding;
            int yUpperBound = GUI.GRAPH_HEIGHT / 2 - Defs.NODE_RADIUS;
            int nodeX = rand.nextInt((xUpperBound - xLowerBound) + 1) +
                    xLowerBound + xCenter;
            int nodeY = rand.nextInt((yUpperBound - yLowerBound) + 1) +
                    yLowerBound + yCenter;
            gPanel.nodeCoords.putIfAbsent(node, new Integer[]{nodeX +
                    Defs.NODE_RADIUS / 2, nodeY + Defs.NODE_RADIUS / 2});
            Shape nodeShape = new Ellipse2D.Double(nodeX, nodeY,
                    Defs.NODE_RADIUS, Defs.NODE_RADIUS);
            gPanel.nodeShapes.putIfAbsent(node, nodeShape);
        }

        // Calculate edge weights using euclidean distance
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            Integer source = graph.getEdgeSource(edge);
            Integer target = graph.getEdgeTarget(edge);
            double nodeX = gPanel.nodeCoords.get(source)[0];
            double nodeY = gPanel.nodeCoords.get(source)[1];
            double adjNodeX = gPanel.nodeCoords.get(target)[0];
            double adjNodeY = gPanel.nodeCoords.get(target)[1];
            double weight = Math.sqrt(Math.pow((adjNodeX - nodeX), 2) +
                    Math.pow((adjNodeY - nodeY), 2));
            graph.setEdgeWeight(edge, weight);
        }
        gPanel.graph = graph;
    }
}
