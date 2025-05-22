import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

public class BuildTest {

  /**
   * Simple container for vertices of the complex graph.
   */
  static class GraphData {
    Vertex<Integer> v3;
    Vertex<Integer> v7;
    Vertex<Integer> v12;
    Vertex<Integer> v34;
    Vertex<Integer> v56;
    Vertex<Integer> v78;
    Vertex<Integer> v91;
    Vertex<Integer> v45;
    Vertex<Integer> v23;
    Vertex<Integer> v67;

    GraphData(Vertex<Integer> v3, Vertex<Integer> v7, Vertex<Integer> v12, Vertex<Integer> v34,
              Vertex<Integer> v56, Vertex<Integer> v78, Vertex<Integer> v91,
              Vertex<Integer> v45, Vertex<Integer> v23, Vertex<Integer> v67) {
      this.v3 = v3;
      this.v7 = v7;
      this.v12 = v12;
      this.v34 = v34;
      this.v56 = v56;
      this.v78 = v78;
      this.v91 = v91;
      this.v45 = v45;
      this.v23 = v23;
      this.v67 = v67;
    }
  }

  private static class AirportData {
    public final Airport atl;
    public final Airport jfk;
    public final Airport ord;
    public final Airport sfo;
    public final Airport den;
    public final Airport mia;
    public final Airport sea;
    public final Airport dfw;
    public final Airport las;
    public final Airport phx;
  
    public AirportData(Airport atl, Airport jfk, Airport ord, Airport sfo, Airport den,
                       Airport mia, Airport sea, Airport dfw, Airport las, Airport phx) {
      this.atl = atl;
      this.jfk = jfk;
      this.ord = ord;
      this.sfo = sfo;
      this.den = den;
      this.mia = mia;
      this.sea = sea;
      this.dfw = dfw;
      this.las = las;
      this.phx = phx;
    }
  }

  private static AirportData buildAirportData() {
    // Create airports with an empty list for outbound flights
    Airport atl = new Airport("Hartsfield–Jackson Atlanta International Airport", "ATL", new ArrayList<>());
    Airport jfk = new Airport("John F. Kennedy International Airport", "JFK", new ArrayList<>());
    Airport ord = new Airport("O'Hare International Airport", "ORD", new ArrayList<>());
    Airport sfo = new Airport("San Francisco International Airport", "SFO", new ArrayList<>());
    Airport den = new Airport("Denver International Airport", "DEN", new ArrayList<>());
    Airport mia = new Airport("Miami International Airport", "MIA", new ArrayList<>());
    Airport sea = new Airport("Seattle-Tacoma International Airport", "SEA", new ArrayList<>());
    Airport dfw = new Airport("Dallas/Fort Worth International Airport", "DFW", new ArrayList<>());
    Airport las = new Airport("McCarran International Airport", "LAS", new ArrayList<>());
    Airport phx = new Airport("Phoenix Sky Harbor International Airport", "PHX", new ArrayList<>());

    // Build the graph following the same structure as GraphData (self-loop on SFO removed)
    // ATL -> JFK, SFO
    atl.getOutboundFlights().addAll(Arrays.asList(jfk, sfo));

    // JFK -> ORD, DFW, SFO, DEN
    jfk.getOutboundFlights().addAll(Arrays.asList(ord, dfw, sfo, den));

    // ORD -> JFK, DEN, MIA
    ord.getOutboundFlights().addAll(Arrays.asList(jfk, den, mia));

    // SFO -> SEA (self-loop removed)
    sfo.getOutboundFlights().add(sea);

    // DEN -> MIA
    den.getOutboundFlights().add(mia);

    // MIA -> SEA
    mia.getOutboundFlights().add(sea);

    // SEA -> DEN
    sea.getOutboundFlights().add(den);

    // DFW -> LAS
    dfw.getOutboundFlights().add(las);

    // LAS -> (no outbound flights)

    // PHX -> SEA
    phx.getOutboundFlights().add(sea);

    return new AirportData(atl, jfk, ord, sfo, den, mia, sea, dfw, las, phx);
  }

  /**
   * Builds the complex graph as follows:
   *
   *   v3.neighbors  = { v7, v34 }
   *   v7.neighbors  = { v12, v45, v34, v56 }
   *   v12.neighbors = { v7, v56, v78 }
   *   v34.neighbors = { v34, v91 }    (self-loop on v34)
   *   v56.neighbors = { v78 }
   *   v78.neighbors = { v91 }
   *   v91.neighbors = { v56 }
   *   v45.neighbors = { v23 }
   *   v23.neighbors = { }           (leaf)
   *   v67.neighbors = { v91 }
   *
   * We use v3 as the starting vertex.
   */
  private GraphData buildComplexGraph() {
    Vertex<Integer> v3  = new Vertex<>(3);
    Vertex<Integer> v7  = new Vertex<>(7);
    Vertex<Integer> v12 = new Vertex<>(12);
    Vertex<Integer> v34 = new Vertex<>(34);
    Vertex<Integer> v56 = new Vertex<>(56);
    Vertex<Integer> v78 = new Vertex<>(78);
    Vertex<Integer> v91 = new Vertex<>(91);
    Vertex<Integer> v45 = new Vertex<>(45);
    Vertex<Integer> v23 = new Vertex<>(23);
    Vertex<Integer> v67 = new Vertex<>(67);

    v3.neighbors  = new ArrayList<>(Arrays.asList(v7, v34));
    v7.neighbors  = new ArrayList<>(Arrays.asList(v12, v45, v34, v56));
    v12.neighbors = new ArrayList<>(Arrays.asList(v7, v56, v78));
    v34.neighbors = new ArrayList<>(Arrays.asList(v34, v91)); // self-loop on v34
    v56.neighbors = new ArrayList<>(Arrays.asList(v78));
    v78.neighbors = new ArrayList<>(Arrays.asList(v91));
    v91.neighbors = new ArrayList<>(Arrays.asList(v56));
    v45.neighbors = new ArrayList<>(Arrays.asList(v23));
    v23.neighbors = new ArrayList<>();
    v67.neighbors = new ArrayList<>(Arrays.asList(v91));

    return new GraphData(v3, v7, v12, v34, v56, v78, v91, v45, v23, v67);
  }

  private Map<Integer, Set<Integer>> buildComplexMap() {
    Map<Integer, Set<Integer>> graph = new HashMap<>();
    graph.put(3, new HashSet<>(Arrays.asList(7, 34)));
    graph.put(7, new HashSet<>(Arrays.asList(12, 45, 34, 56)));
    graph.put(12, new HashSet<>(Arrays.asList(7, 56, 78)));
    graph.put(34, new HashSet<>(Arrays.asList(34, 91)));
    graph.put(56, new HashSet<>(Arrays.asList(78)));
    graph.put(78, new HashSet<>(Arrays.asList(91)));
    graph.put(91, new HashSet<>(Arrays.asList(56)));
    graph.put(45, new HashSet<>(Arrays.asList(23)));
    graph.put(23, new HashSet<>());
    graph.put(67, new HashSet<>(Arrays.asList(91)));

    return graph;
  }

  // ====================================================
  // TeeOutputStream inner class for capturing output
  // ====================================================
  // Used for testing purposes so you can still see your print statements when debugging.
  // (Not modified)
  static class TeeOutputStream extends OutputStream {
    private final OutputStream first;
    private final OutputStream second;

    public TeeOutputStream(OutputStream first, OutputStream second) {
      this.first = first;
      this.second = second;
    }

    @Override
    public void write(int b) {
      try {
        first.write(b);
        second.write(b);
      } catch (Exception e) {
        throw new RuntimeException("Error writing to TeeOutputStream", e);
      }
    }

    @Override
    public void flush() {
      try {
        first.flush();
        second.flush();
      } catch (Exception e) {
        throw new RuntimeException("Error flushing TeeOutputStream", e);
      }
    }

    @Override
    public void close() {
      try {
        first.close();
        second.close();
      } catch (Exception e) {
        throw new RuntimeException("Error closing TeeOutputStream", e);
      }
    }
  }

  // ------------------------------------------------
  // Utility to capture System.out output for testing.
  // ------------------------------------------------
  private String captureOutput(Runnable runnable) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    PrintStream ps = new PrintStream(baos);
    System.setOut(ps);
    try {
      runnable.run();
    } finally {
      System.out.flush();
      System.setOut(originalOut);
    }
    return baos.toString().trim();
  }

  // ====================================================
  // Tests for printShortWords(Vertex<String>, int)
  // ====================================================

  @Test
  public void testPrintShortWords_NullVertex() {
    // If vertex is null, nothing should be printed.
    String output = captureOutput(() -> Build.printShortWords(null, 5));
    assertEquals("", output);
  }

  @Test
  public void testPrintShortWords_SingleVertexMatch() {
    // A single vertex "cat" (3 letters) with k=4 should print "cat".
    Vertex<String> vertex = new Vertex<>("cat");
    String output = captureOutput(() -> Build.printShortWords(vertex, 4));
    assertEquals("cat", output);
  }

  @Test
  public void testPrintShortWords_MultipleMatches() {
    // Graph:
    //    "hello" (5 letters) with neighbors: "dog" (3), "ant" (3), "elephant" (8), "bee" (3)
    // k = 5 means words strictly less than 5 letters are printed.
    Vertex<String> hello = new Vertex<>("hello");
    Vertex<String> dog = new Vertex<>("dog");
    Vertex<String> ant = new Vertex<>("ant");
    Vertex<String> elephant = new Vertex<>("elephant");
    Vertex<String> bee = new Vertex<>("bee");
    hello.neighbors = new ArrayList<>(Arrays.asList(dog, ant, elephant, bee));

    String output = captureOutput(() -> Build.printShortWords(hello, 5));
    // Expected: "dog", "ant", "bee" (order is not guaranteed).
    Set<String> expected = new HashSet<>(Arrays.asList("dog", "ant", "bee"));
    Set<String> actual = new HashSet<>(Arrays.asList(output.split("\\s+")));
    assertEquals(expected, actual);
  }

  @Test
  public void testPrintShortWords_CyclicGraph() {
    // Build a cyclic graph with a self-loop:
    //    "a" -> "bb" -> "ccc" -> "a"
    //    and "bb" also has a self-loop.
    // k = 3 should print vertices with words shorter than 3: "a" (1) and "bb" (2)
    Vertex<String> a = new Vertex<>("a");
    Vertex<String> bb = new Vertex<>("bb");
    Vertex<String> ccc = new Vertex<>("ccc");
    a.neighbors = new ArrayList<>(Arrays.asList(bb));
    bb.neighbors = new ArrayList<>(Arrays.asList(ccc, bb)); // self-loop on bb
    ccc.neighbors = new ArrayList<>(Arrays.asList(a));

    String output = captureOutput(() -> Build.printShortWords(a, 3));
    Set<String> expected = new HashSet<>(Arrays.asList("a", "bb"));
    Set<String> actual = new HashSet<>(Arrays.asList(output.split("\\s+")));
    assertEquals(expected, actual);
  }

   @Test
  public void testPrintShortWords_ComplexGraph() {
    // Build a complex string graph with forks and a cycle:
    //   "ab" -> "cde", "f", "ghij"
    //   "cde" -> "klmno", "f"
    //   "f"   -> "pqr"
    //   "ghij"-> "cde", "st"
    //   "st"  -> "ab" (cycle back to root)
    Vertex<String> ab     = new Vertex<>("ab");
    Vertex<String> cde    = new Vertex<>("cde");
    Vertex<String> f      = new Vertex<>("f");
    Vertex<String> ghij   = new Vertex<>("ghij");
    Vertex<String> klmno  = new Vertex<>("klmno");
    Vertex<String> pqr    = new Vertex<>("pqr");
    Vertex<String> st     = new Vertex<>("st");

    ab.neighbors   = new ArrayList<>(Arrays.asList(cde, f, ghij));
    cde.neighbors  = new ArrayList<>(Arrays.asList(klmno, f));
    f.neighbors    = new ArrayList<>(Arrays.asList(pqr));
    ghij.neighbors = new ArrayList<>(Arrays.asList(cde, st));
    st.neighbors   = new ArrayList<>(Arrays.asList(ab)); // cycle
    // klmno and pqr are leaves

    String output = captureOutput(() -> Build.printShortWords(ab, 4));
    Set<String> actual = new HashSet<>(Arrays.asList(output.split("\\s+")));
    // Words with length < 4 reachable: "ab"(2), "cde"(3), "f"(1), "pqr"(3), "st"(2)
    Set<String> expected = new HashSet<>(Arrays.asList("ab", "cde", "f", "pqr", "st"));
    assertEquals(expected, actual);
  }

  // ====================================================
  // Tests for longestWord(Vertex<String>)
  // ====================================================

  @Test
  public void testLongestWord_Null() {
    // A null vertex should yield an empty string.
    assertEquals("", Build.longestWord(null));
  }

  @Test
  public void testLongestWord_SingleVertex() {
    Vertex<String> vertex = new Vertex<>("simple");
    assertEquals("simple", Build.longestWord(vertex));
  }

  @Test
  public void testLongestWord_MultipleVertices() {
    // Graph:
    //   "cat" -> "elephant", "dog"
    // Expect longest word "elephant"
    Vertex<String> cat = new Vertex<>("cat");
    Vertex<String> elephant = new Vertex<>("elephant");
    Vertex<String> dog = new Vertex<>("dog");
    cat.neighbors = new ArrayList<>(Arrays.asList(elephant, dog));
    assertEquals("elephant", Build.longestWord(cat));
  }

  @Test
  public void testLongestWord_CyclicGraph() {
    // Graph with cycle:
    //    "one" -> "two" -> "three" -> "one"
    // Expect longest word "three"
    Vertex<String> one = new Vertex<>("one");
    Vertex<String> two = new Vertex<>("two");
    Vertex<String> three = new Vertex<>("three");
    one.neighbors = new ArrayList<>(Arrays.asList(two));
    two.neighbors = new ArrayList<>(Arrays.asList(three));
    three.neighbors = new ArrayList<>(Arrays.asList(one));
    assertEquals("three", Build.longestWord(one));
  }

  @Test
  public void testLongestWord_ComplexGraph() {
    Vertex<String> ab     = new Vertex<>("ab");
    Vertex<String> cde    = new Vertex<>("cde");
    Vertex<String> f      = new Vertex<>("f");
    Vertex<String> ghij   = new Vertex<>("ghij");
    Vertex<String> klmno  = new Vertex<>("klmno");
    Vertex<String> pqr    = new Vertex<>("pqr");
    Vertex<String> st     = new Vertex<>("st");

    ab.neighbors   = new ArrayList<>(Arrays.asList(cde, f, ghij));
    cde.neighbors  = new ArrayList<>(Arrays.asList(klmno, f));
    f.neighbors    = new ArrayList<>(Arrays.asList(pqr));
    ghij.neighbors = new ArrayList<>(Arrays.asList(cde, st));
    st.neighbors   = new ArrayList<>(Arrays.asList(ab)); // cycle

    // The longest reachable word is "klmno" (5 chars)
    assertEquals("klmno", Build.longestWord(ab));
  }

  // ====================================================
  // Tests for printSelfLoopers(Vertex<T>)
  // ====================================================

  @Test
  public void testPrintSelfLoopers_Null() {
    String output = captureOutput(() -> Build.printSelfLoopers(null));
    assertEquals("", output);
  }

  @Test
  public void testPrintSelfLoopers_NoLoopers() {
    // Graph with no vertex that has itself as a neighbor.
    Vertex<String> a = new Vertex<>("a");
    Vertex<String> b = new Vertex<>("b");
    a.neighbors = new ArrayList<>(Arrays.asList(b));
    b.neighbors = new ArrayList<>();
    String output = captureOutput(() -> Build.printSelfLoopers(a));
    assertEquals("", output);
  }

  @Test
  public void testPrintSelfLoopers_OneLooper() {
    // Single vertex with a self-loop.
    Vertex<String> self = new Vertex<>("self");
    self.neighbors = new ArrayList<>(Arrays.asList(self));
    String output = captureOutput(() -> Build.printSelfLoopers(self));
    assertEquals("self", output.trim());
  }

  @Test
  public void testPrintSelfLoopers_MultipleLoopers() {
    // Graph: A -> B, A -> C; B -> B (self-loop), C -> C (self-loop)
    Vertex<String> A = new Vertex<>("A");
    Vertex<String> B = new Vertex<>("B");
    Vertex<String> C = new Vertex<>("C");
    A.neighbors = new ArrayList<>(Arrays.asList(B, C));
    B.neighbors = new ArrayList<>(Arrays.asList(B));
    C.neighbors = new ArrayList<>(Arrays.asList(C));
    String output = captureOutput(() -> Build.printSelfLoopers(A));
    Set<String> expected = new HashSet<>(Arrays.asList("B", "C"));
    Set<String> actual = new HashSet<>(Arrays.asList(output.split("\\s+")));
    assertEquals(expected, actual);
  }

   @Test
  public void testPrintSelfLoopers_DeepAndForked() {
    // A -> B, C
    // B -> D
    // C -> E
    // D -> D (self-loop)
    // E -> E (self-loop)
    Vertex<String> A = new Vertex<>("A");
    Vertex<String> B = new Vertex<>("B");
    Vertex<String> C = new Vertex<>("C");
    Vertex<String> D = new Vertex<>("D");
    Vertex<String> E = new Vertex<>("E");

    A.neighbors = new ArrayList<>(Arrays.asList(B, C));
    B.neighbors = new ArrayList<>(Arrays.asList(D));
    C.neighbors = new ArrayList<>(Arrays.asList(E));
    D.neighbors = new ArrayList<>(Arrays.asList(D)); // self-loop deep in branch
    E.neighbors = new ArrayList<>(Arrays.asList(E)); // another self-loop

    String output = captureOutput(() -> Build.printSelfLoopers(A));
    Set<String> actual = new HashSet<>(Arrays.asList(output.split("\\s+")));
    Set<String> expected = new HashSet<>(Arrays.asList("D", "E"));
    assertEquals(expected, actual);
  }

  // ====================================================
  // Tests for canReach(Airport, Airport)
  // ====================================================

  @Test
  public void testCanReach_SameAirport() {
    AirportData data = buildAirportData();
    // Starting and destination are the same.
    assertTrue(Build.canReach(data.atl, data.atl));
  }

  @Test
  public void testCanReach_DirectFlight() {
    AirportData data = buildAirportData();
    // ATL -> JFK is a direct flight.
    assertTrue(Build.canReach(data.atl, data.jfk));
  }

  @Test
  public void testCanReach_MultipleHops() {
    AirportData data = buildAirportData();
    // ATL -> JFK -> DEN should be reachable.
    assertTrue(Build.canReach(data.atl, data.den));
  }

  @Test
  public void testCanReach_Unreachable() {
    AirportData data = buildAirportData();
    // From DFW, only LAS is reachable; ATL should be unreachable.
    assertFalse(Build.canReach(data.dfw, data.atl));
  }

  @Test
  public void testCanReach_Cyclic() {
    AirportData data = buildAirportData();
    // PHX -> SEA -> DEN -> MIA -> SEA forms a cycle.
    // PHX should be able to reach DEN.
    assertTrue(Build.canReach(data.phx, data.den));
  }

  @Test
  public void testCanReach_ReachViaDifferentPaths() {
    AirportData data = buildAirportData();
    // PHX -> SEA -> DEN -> MIA
    assertTrue(Build.canReach(data.phx, data.mia));
  }

  // ====================================================
  // Tests for unreachable(Map<T, List<T>>, T)
  // ====================================================

  @Test
  public void testUnreachable_StartingFrom3() {
    // Using the complex map.
    Map<Integer, Set<Integer>> graph = buildComplexMap();
    // Convert to Map<Integer, List<Integer>>
    Map<Integer, List<Integer>> graphList = new HashMap<>();
    for (Map.Entry<Integer, Set<Integer>> entry : graph.entrySet()) {
      graphList.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
    // From 3, reachable should be: {3, 7, 34, 12, 45, 56, 78, 91, 23}
    // Unreachable: {67}
    Set<Integer> unreachable = Build.unreachable(graphList, 3);
    Set<Integer> expected = new HashSet<>(Arrays.asList(67));
    assertEquals(expected, unreachable);
  }

  @Test
  public void testUnreachable_StartingFrom67() {
    Map<Integer, Set<Integer>> graph = buildComplexMap();
    Map<Integer, List<Integer>> graphList = new HashMap<>();
    for (Map.Entry<Integer, Set<Integer>> entry : graph.entrySet()) {
      graphList.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
    // From 67, reachable: {67, 91, 56, 78}; unreachable: {3, 7, 12, 34, 45, 23}
    Set<Integer> unreachable = Build.unreachable(graphList, 67);
    Set<Integer> expected = new HashSet<>(Arrays.asList(3, 7, 12, 34, 45, 23));
    assertEquals(expected, unreachable);
  }

  @Test
  public void testUnreachable_SingleNodeGraph() {
    // Graph with one node mapping to an empty list.
    Map<Integer, List<Integer>> graph = new HashMap<>();
    graph.put(1, new ArrayList<>());
    // From 1, only 1 is reachable.
    Set<Integer> unreachable = Build.unreachable(graph, 1);
    assertTrue(unreachable.isEmpty());
  }

  @Test
  public void testUnreachable_CycleGraph() {
    // Graph with a cycle: A -> B, B -> C, C -> A.
    Map<String, List<String>> graph = new HashMap<>();
    graph.put("A", new ArrayList<>(Arrays.asList("B")));
    graph.put("B", new ArrayList<>(Arrays.asList("C")));
    graph.put("C", new ArrayList<>(Arrays.asList("A")));
    // From "A", every node is reachable.
    Set<String> unreachable = Build.unreachable(graph, "A");
    assertTrue(unreachable.isEmpty());
  }

  @Test
  public void testUnreachable_StartingNotInGraph() {
    // Graph with nodes but the starting node is not a key.
    Map<String, List<String>> graph = new HashMap<>();
    graph.put("A", new ArrayList<>(Arrays.asList("B")));
    graph.put("B", new ArrayList<>());
    graph.put("C", new ArrayList<>());
    // Starting from "D" (not in graph), expect all nodes to be unreachable.
    Set<String> unreachable = Build.unreachable(graph, "D");
    Set<String> expected = new HashSet<>(Arrays.asList("A", "B", "C"));
    assertEquals(expected, unreachable);
  }

   @Test
  public void testUnreachable_ForkedStringGraph_AllReachable() {
    // A -> B, C
    // B -> D
    // C -> D, E
    // D -> (leaf)
    // E -> F
    // F -> (leaf)
    Map<String, List<String>> graph = new HashMap<>();
    graph.put("A", Arrays.asList("B", "C"));
    graph.put("B", Arrays.asList("D"));
    graph.put("C", Arrays.asList("D", "E"));
    graph.put("D", new ArrayList<>());
    graph.put("E", Arrays.asList("F"));
    graph.put("F", new ArrayList<>());

    // From A, all nodes (A,B,C,D,E,F) are reachable
    Set<String> unreachable = Build.unreachable(graph, "A");
    assertTrue(unreachable.isEmpty());
  }

  @Test
  public void testUnreachable_ForkedStringGraph_PartialUnreachable() {
    // Same graph as above
    Map<String, List<String>> graph = new HashMap<>();
    graph.put("A", Arrays.asList("B", "C"));
    graph.put("B", Arrays.asList("D"));
    graph.put("C", Arrays.asList("D", "E"));
    graph.put("D", new ArrayList<>());
    graph.put("E", Arrays.asList("F"));
    graph.put("F", new ArrayList<>());

    // From B, reachable are {B, D}; unreachable {A, C, E, F}
    Set<String> unreachable = Build.unreachable(graph, "B");
    Set<String> expected = new HashSet<>(Arrays.asList("A", "C", "E", "F"));
    assertEquals(expected, unreachable);
  }
}
