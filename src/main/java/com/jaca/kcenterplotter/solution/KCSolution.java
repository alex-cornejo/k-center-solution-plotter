package com.jaca.kcenterplotter.solution;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KCSolution {
    private String instance;
    private int[] outliers;
    private Center[] centers;

    public double computeFitness(double[][] G) {
        double radius = 0;
        for (var center : centers) {
            for (var v : center.getNodes()) {
                if (G[v][center.getCenter()] > radius) {
                    radius = G[v][center.getCenter()];
                }
            }
        }
        return radius;
    }
}
