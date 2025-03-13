import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
   * https://auberonedu.github.io/graph-explore/graph_site/viz.html
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
  // You do not need to modify this.
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
}
