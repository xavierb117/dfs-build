import java.util.List;

public class Airport {
  private final String name;
  private final String airportCode;
  private final List<Airport> outboundFlights;
  
  public String getName() {
    return name;
  }

  public String getAirportCode() {
    return airportCode;
  }

  public List<Airport> getOutboundFlights() {
    return outboundFlights;
  }

  public Airport(String name, String airportCode, List<Airport> outboundFlights) {
    this.name = name;
    this.airportCode = airportCode;
    this.outboundFlights = outboundFlights;
  }

  
}
