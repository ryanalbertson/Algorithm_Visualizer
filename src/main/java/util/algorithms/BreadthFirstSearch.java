package main.java.util.algorithms;

import main.java.GraphPanel;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public class BreadthFirstSearch implements Runnable {


    private final GraphPanel graphPanel;
    private final Object pauseLock = new Object();
    private boolean targetFound = false;


    /**
     * If user has stopped the animation, clears the animation .
     *
     * @return True if user has stopped the animation, false otherwise.
     */
    private boolean isStopped() {

        if (graphPanel.stop) {
            // Clear animation
            Arrays.fill(graphPanel.path, Integer.MAX_VALUE);
            Arrays.fill(graphPanel.visited, false);
            graphPanel.repaint();
            return true;
        }
        return false;
    }


    /**
     * Checks if user has paused the animation. If so, the animation process is
     * held until the user has unpaused it.
     */
    private void checkForPause() {

        synchronized (pauseLock) {
            boolean paused;
            while (paused = graphPanel.pause) {
                try {
                    pauseLock.wait(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Repaints the animation within the {@link GraphPanel}. Does it slowly such
     * that the user can visualize the algorithm stepping through.
     */
    private void animate() {

        try {
            // Update animation, slowly
            graphPanel.repaint();
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * ADD DESCRIPTION
     *
     * @param node Source node of the search.
     */
    private void bfs(Integer node) {

        Deque<Integer> queue = new ArrayDeque<>();
        queue.addLast(node);

        // DOESNT STOP WHEN TARGET IS FOUND
        while (!queue.isEmpty()) {
            // Check if user has stopped or paused algorithm
            if (isStopped()) return;
            checkForPause();

            int currentNode = queue.removeFirst();
            graphPanel.visited[currentNode] = true;


            // Stop BFS when target is found
            if (currentNode == graphPanel.targetNode) {
                animate();
                break;
            }
            for (Integer adj : graphPanel.adjNodes.get(currentNode)) {
                if (!graphPanel.visited[adj]) {
                    graphPanel.path[adj] = currentNode;
                    queue.addLast(adj);
                }
            }
            animate();
        }
    }


    /**
     * Starts the algorithm process.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        // Don't start algorithm if user hasn't selected source & target nodes
        if (graphPanel.sourceNode != null || graphPanel.targetNode != null) {
            bfs(graphPanel.sourceNode);
        }
    }


    /**
     * Constructs a {@link BreadthFirstSearch}.
     *
     * @param graphPanel The {@link javax.swing.JPanel} containing a graph.
     * @throws IllegalArgumentException If {@code graphPanel} is null.
     */
    public BreadthFirstSearch(GraphPanel graphPanel) {

        if (graphPanel == null) {
            throw new IllegalArgumentException("GraphPanel is null");
        } else this.graphPanel = graphPanel;
    }
}
