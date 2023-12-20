import java.util.*;

public class UniversityGraph {

    private class Node implements Comparable<Node> {
        public final String name;
        public List<Edge> edges;
        public double distance;
        public Node previous;

        public Node(String name) {
            this.name = name;
            this.edges = new ArrayList<>();
            this.distance = Double.POSITIVE_INFINITY;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.distance, other.distance);
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private class Edge {
        public final Node from;
        public final Node to;
        public final double weight;

        public Edge(Node from, Node to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private final Map<String, Node> nodes;

    public UniversityGraph() {
        this.nodes = new HashMap<>();
    }

    public void addNode(String name) {
        nodes.putIfAbsent(name, new Node(name));
    }

    public void addEdge(String from, String to, double weight) {
        Node fromNode = nodes.get(from);
        Node toNode = nodes.get(to);

        if (fromNode == null || toNode == null) {
            throw new IllegalArgumentException("Node does not exist");
        }

        fromNode.edges.add(new Edge(fromNode, toNode, weight));
    }

    public List<Node> getShortestPath(String from, String to) {
        Node start = nodes.get(from);
        Node end = nodes.get(to);

        if (start == null || end == null) {
            throw new IllegalArgumentException("Node does not exist");
        }

        start.distance = 0.0;
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current == end) {
                List<Node> path = new ArrayList<>();
                while (current != null) {
                    path.add(0, current);
                    current = current.previous;
                }
                return path;
            }

            if (current.distance == Double.POSITIVE_INFINITY) {
                break;
            }

            for (Edge edge : current.edges) {
                Node neighbor = edge.to;
                double distance = current.distance + edge.weight;

                if (distance < neighbor.distance) {
                    neighbor.distance = distance;
                    neighbor.previous = current;
                    queue.add(neighbor);
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {
        UniversityGraph graph = new UniversityGraph();

        graph.addNode("Building A");
        graph.addNode("Building B");
        graph.addNode("Building C");
        graph.addNode("Building D");

        graph.addEdge("Building A", "Building B", 5.0);
        graph.addEdge("Building A", "Building C", 10.0);
        graph.addEdge("Building B", "Building C", 7.0);
        graph.addEdge("Building C", "Building D", 15.0);

        List<Node> path = graph.getShortestPath("Building A", "Building D");

        if (path != null) {
            System.out.println("Shortest path: " + path);
        } else {
            System.out.println("No path found");
        }
    }
}