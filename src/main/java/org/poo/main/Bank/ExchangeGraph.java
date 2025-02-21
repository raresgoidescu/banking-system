package org.poo.main.Bank;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ExchangeGraph {
  private static volatile ExchangeGraph instance;

  private final Map<String, Map<String, Double>> graph;

  private ExchangeGraph() {
    graph = new HashMap<>();
  }

  /**
   * @return The singleton instance of the ExchangeGraph class.
   */
  public static ExchangeGraph getInstance() {
    if (instance == null) {
      synchronized (ExchangeGraph.class) {
        if (instance == null) {
          instance = new ExchangeGraph();
        }
      }
    }

    return instance;
  }

  /**
   * Resets the singleton instance of the ExchangeGraph class.
   */
  public static void resetInstance() {
    instance = null;
  }

  /**
   * Adds a currency exchange rate to the graph. If a direct connection
   * between the currencies does not exist, it will be created. Additionally,
   * the reverse rate between the two currencies is also added.
   *
   * @param from The source currency in the exchange rate (e.g., "USD").
   * @param to   The target currency in the exchange rate (e.g., "EUR").
   * @param rate The exchange rate from the source to the target currency.
   */
  public void addExchange(final String from, final String to, final double rate) {
    graph.putIfAbsent(from, new HashMap<>());
    graph.get(from).put(to, rate);
    graph.putIfAbsent(to, new HashMap<>());
    graph.get(to).put(from, 1.0 / rate);
  }

  /**
   * Computes the exchange rate between two currencies using the graph structure.
   * If a direct or indirect path exists between the currencies, their rate is
   * calculated. If no path exists, the method returns null.
   */
  public Double getRate(final String from, final String to) {
    if (!graph.containsKey(from) || !graph.containsKey(to)) {
      return null;
    }

    if (from.equals(to)) {
      return 1.0;
    }

    Set<String> visited = new HashSet<>();

    Double rate = findRateDFS(from, to, 1.0, visited);

    assert rate != null;

    return rate;
  }

  private Double findRateDFS(final String from, final String to,
                             final double rate, final Set<String> visited) {
    if (visited.contains(from) || !graph.containsKey(from) || !graph.containsKey(to)) {
      return null;
    }

    if (from.equals(to)) {
      return rate;
    }

    visited.add(from);

    /* Go as deep as possible while visiting the neighbors */
    for (Map.Entry<String, Double> neigh : graph.get(from).entrySet()) {
      Double res = findRateDFS(neigh.getKey(), to, rate * neigh.getValue(), visited);

      if (res != null) {
        return res;
      }
    }

    /* No valid path found */
    return null;
  }
}
