
package com.jaca.kcenterplotter.util;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    public static double[][] getAdjacencyMatrix(List<double[]> nodes) {

        double[][] adjacencyMatrix = new double[nodes.size()][nodes.size()];
        var euc = new EuclideanDistance();
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {

                double[] v1 = nodes.get(i);
                double[] v2 = nodes.get(j);

                double d = euc.compute(v1, v2);
                adjacencyMatrix[i][j] = d;
                adjacencyMatrix[j][i] = d;
            }
        }
        return adjacencyMatrix;
    }

    public static List<double[]> readNodes(String fileName) throws IOException {

        List<double[]> nodes = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        String[] lineArr = line.trim().split("\\s+");
        if (lineArr.length == 1) {
            nodes = new ArrayList<>(Integer.parseInt(line));
        } else {
            double[] coord = Arrays.stream(Arrays.copyOfRange(lineArr, 1, lineArr.length)).mapToDouble(Double::valueOf).toArray();
            nodes.add(coord);
        }

        for (; (line = reader.readLine()) != null; ) {
            lineArr = line.trim().split("\\s+");
            double[] coord = Arrays.stream(Arrays.copyOfRange(lineArr, 1, lineArr.length)).mapToDouble(Double::valueOf).toArray();
            nodes.add(coord);
        }
        return nodes;
    }

    public static List<int[]> normNodes(List<double[]> nodes, double xMin, double xMax, double yMin, double yMax,
                                        int xMaxPref, int yMaxPref) {

        List<int[]> dataNormalized = new ArrayList<>();
        for (double[] coordinate : nodes) {
            int[] coordinateNorm = new int[2];
            coordinateNorm[0] = (int) (((coordinate[0] - xMin) / (xMax - xMin)) * xMaxPref);
            coordinateNorm[1] = (int) (((coordinate[1] - yMin) / (yMax - yMin)) * yMaxPref);
            dataNormalized.add(coordinateNorm);
        }
        return dataNormalized;
    }
}
