import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Build {

  /**
   * Prints words that are reachable from the given vertex and are strictly shorter than k characters.
   * If the vertex is null or no reachable words meet the criteria, prints nothing.
   *
   * @param vertex the starting vertex
   * @param k the maximum word length (exclusive)
   */
  public static void printShortWords(Vertex<String> vertex, int k) {
    Set<Vertex<String>> visited = new HashSet<>();
    printShortWordsHelper(vertex, k, visited);
  }

  public static void printShortWordsHelper(Vertex<String> vertex, int k, Set<Vertex<String>> visited) {
    if (vertex == null || visited.contains(vertex)) return;

    visited.add(vertex);
    if (vertex.data.length() < k) System.out.println(vertex.data);
    for (Vertex<String> neighbor : vertex.neighbors) {
      printShortWordsHelper(neighbor, k, visited);
    }
  }

  /**
   * Returns the longest word reachable from the given vertex, including its own value.
   *
   * @param vertex the starting vertex
   * @return the longest reachable word, or an empty string if the vertex is null
   */
  public static String longestWord(Vertex<String> vertex) {
    Set<Vertex<String>> visited = new HashSet<>();

    return longestWordHelper(vertex, "", visited);
  }

  public static String longestWordHelper(Vertex<String> vertex, String longest, Set<Vertex<String>> visited) {
    if (vertex == null || visited.contains(vertex)) return "";

    visited.add(vertex);
    if (vertex.data.length() > longest.length()) longest = vertex.data;

    for (Vertex<String> neighbor : vertex.neighbors) {
      String compare = longestWordHelper(neighbor, longest, visited);
      if (compare.length() > longest.length()) longest = compare; 
    }

    return longest;
  }

  /**
   * Prints the values of all vertices that are reachable from the given vertex and 
   * have themself as a neighbor.
   *
   * @param vertex the starting vertex
   * @param <T> the type of values stored in the vertices
   */
  public static <T> void printSelfLoopers(Vertex<T> vertex) {
    Set<Vertex<T>> visited = new HashSet<>();
    printSelfLoopersHelper(vertex, visited);
  }

  public static <T> void printSelfLoopersHelper(Vertex<T> vertex, Set<Vertex<T>> visited) {
    if (vertex == null || visited.contains(vertex)) return;

    visited.add(vertex);
    for (Vertex<T> neighbor : vertex.neighbors) {
      if (neighbor == vertex) System.out.println(vertex.data);
      printSelfLoopersHelper(neighbor, visited);
    }
  }

  /**
   * Determines whether it is possible to reach the destination airport through a series of flights
   * starting from the given airport. If the start and destination airports are the same, returns true.
   *
   * @param start the starting airport
   * @param destination the destination airport
   * @return true if the destination is reachable from the start, false otherwise
   */
  public static boolean canReach(Airport start, Airport destination) {
    if (start == null || destination == null) return false;
    Set<Airport> visited = new HashSet<>();
    return canReachHelper(start, destination, visited);
  }

  public static boolean canReachHelper(Airport start, Airport destination, Set<Airport> visited) {
    if (start == destination) return true;
    if (visited.contains(start)) return false;

    visited.add(start);

    for (Airport flight : start.getOutboundFlights()) {
      if (canReachHelper(flight, destination, visited)) return true;
    }

    return false;
  }

  /**
   * Returns the set of all values in the graph that cannot be reached from the given starting value.
   * The graph is represented as a map where each vertex is associated with a list of its neighboring values.
   *
   * @param graph the graph represented as a map of vertices to neighbors
   * @param starting the starting value
   * @param <T> the type of values stored in the graph
   * @return a set of values that cannot be reached from the starting value
   */
  public static <T> Set<T> unreachable(Map<T, List<T>> graph, T starting) {
    Set<T> unreachable = new HashSet<>();
    Set<T> visited = new HashSet<>();

    unreachableHelper(graph, starting, visited);

    for (T key : graph.keySet()) {
      if (!visited.contains(key)) unreachable.add(key);
    }

    return unreachable;
  }

  public static <T> Set<T> unreachableHelper(Map<T, List<T>> graph, T starting, Set<T> visited) {
    if (graph == null || starting == null || !graph.containsKey(starting) || visited.contains(starting)) return null;

    visited.add(starting);
    List<T> neighbors = graph.get(starting);
    if (neighbors != null) {
      for (T neighbor : neighbors) {
        unreachableHelper(graph, neighbor, visited);
      }
    }
    return visited;
  }
}
