import java.util.List;
import java.util.Map;
import java.util.Set;

public class Build {
  // Print words reachable in the graph that are strictly shorter than k characters
  // If vertex is null or no reachable values meet the criteria, print nothing
  public static void printShortWords(Vertex<String> vertex, int k) {
  }

  // Return the longest word reachable from the given vertex (including its own value)
  // If vertex is null, return an empty string
  public static String longestWord(Vertex<String> vertex) {
    return "";
  }


  // Print the values of all vertices that have themself as a neighbor and are
  // reachable from the given starting vertex
  public static <T> void printSelfLoopers(Vertex<T> vertex) {
  }



  // Return the set of all values in the graph that cannot be reached from the
  // given starting value.
  // The graph is represented as a map of vertices to neighbors
  public static <T> Set<T> unreachable(Map<T, List<T>> graph, T starting) {
    return null;
  }
}