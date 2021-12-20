package com.southernsoft.tcgtournament.service;

import org.jgrapht.Graph;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedPerfectMatching;
import org.jgrapht.alg.matching.blossom.v5.ObjectiveSense;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MatchingService {

    private Graph<Integer, DefaultWeightedEdge> graph;

    @Inject
    public MatchingService() {}

    public void createGraph() {
        graph = new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);
    }

    public void addGraphVertices(List<Integer> vertices) {
        for (Integer vertex : vertices)
            graph.addVertex(vertex);
    }

    public DefaultWeightedEdge addEdge(Integer sourceVertex, Integer targetVertex) {
        return graph.addEdge(sourceVertex, targetVertex);
    }

    public void setEdgeWeight(DefaultWeightedEdge edge, double weight) {
        graph.setEdgeWeight(edge, weight);
    }

    public List<PlayerPair> getPlayerPairs() throws IllegalArgumentException{
        KolmogorovWeightedPerfectMatching<Integer, DefaultWeightedEdge> matching = new KolmogorovWeightedPerfectMatching<>(graph, ObjectiveSense.MAXIMIZE);

        List<PlayerPair> pairings = new ArrayList<>();
        for (DefaultWeightedEdge edge : matching.getMatching().getEdges()) {
            PlayerPair pair = new PlayerPair();
            pair.playerId = graph.getEdgeSource(edge);
            pair.opponentId = graph.getEdgeTarget(edge);
            pair.totalMatchPoints = graph.getEdgeWeight(edge);

            pairings.add(pair);
        }
        return pairings;
    }

    public static class PlayerPair implements Comparable<PlayerPair> {
        int playerId;
        int opponentId;
        double totalMatchPoints;

        public PlayerPair() {}

        public PlayerPair(int playerId, int opponentId, double totalMatchPoints) {
            this.playerId = playerId;
            this.opponentId = opponentId;
            this.totalMatchPoints = totalMatchPoints;
        }

        @Override
        public int compareTo(PlayerPair anotherPair) {
            return Double.compare(this.totalMatchPoints, anotherPair.totalMatchPoints);
        }
    }
}