import java.sql.*;
import java.util.*;

public class DijkstraShortestPath {
    static final int INF = 99999999;

    public static void main(String[] args) {
        // establish the connection
        String url = "jdbc:mysql://localhost:3306/college";
        String user = "root";
        String password = "1234";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // read building names and map ids to names
            Map<Integer, String> buildingsMap = new HashMap<>();
            String query = "SELECT * FROM Buildings";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                buildingsMap.put(resultSet.getInt("id"), resultSet.getString("name"));
            }

            // read distances between buildings
            List<List<Integer>> graph = new ArrayList<>();
            query = "SELECT * FROM Distances";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int fromBuilding = resultSet.getInt("from_building");
                int toBuilding = resultSet.getInt("to_building");
                int distance = (int) resultSet.getDouble("distance");
                if (graph.size() <= fromBuilding) {
                    graph.add(new ArrayList<>());
                }
                graph.get(fromBuilding).add(toBuilding);
                graph.get(fromBuilding).add(distance);
            }
            // prompt the user for input
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the current location: ");
            String fromLocation = scanner.nextLine();
            int fromId = getBuildingId(fromLocation, buildingsMap);

            System.out.println("Enter the destination: ");
            String toLocation = scanner.nextLine();
            int toId = getBuildingId(toLocation, buildingsMap);

            // calculate the shortest path
            List<Integer> path = dijkstra(graph, fromId, toId);
            System.out.println("Shortest path: " + pathToString(path, buildingsMap));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getBuildingId(String buildingName, Map<Integer, String> buildingsMap) {
        for (Map.Entry<Integer, String> entry : buildingsMap.entrySet()) {
            if (entry.getValue().equals(buildingName)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private static List<Integer> dijkstra(List<List<Integer>> graph, int start, int end) {
        int len = graph.size();
        int[] d = new int[len];
        int[] pre = new int[len];
        boolean[] s = new boolean[len];
        for (int i = 0; i < len; i++) {
            d[i] = INF;
            pre[i] = -1;
        }
        d[start] = 0;
        for (int i = 0; i < len - 1; i++) {
            int min = INF;
            int index = -1;
            for (int j = 0; j < len; j++) {
                if (!s[j] && d[j] < min) {
                    min = d[j];
                    index = j;
                }
            }
            if (index == -1) {
                return null;
            }
            s[index] = true;
            for (int i1 = 0; i1 < graph.get(index).size(); i1 += 2) {
                int j = graph.get(index).get(i1);
                int k = graph.get(index).get(i1 + 1);
                if (!s[j] && d[index] + k < d[j]) {
                    d[j] = d[index] + k;
                    pre[j] = index;
                }
            }
        }
        List<Integer> path = new ArrayList<>();
        int cur = end;
        while (cur != -1) {
            path.add(0, cur);
            cur = pre[cur];
        }
        return path;
    }

    private static String pathToString(List<Integer> path, Map<Integer, String> buildingsMap) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) {
                sb.append(" -> ");
            }
            sb.append(buildingsMap.get(path.get(i)));
        }
        return sb.toString();
    }
}