package Terrain;

import java.util.Random;

public class SmoothNoise {

      public static double[][] getMap(int width, int height, double scale, double granularity) {
            double[][] noiseVals = new double[height][width];
            long seed = new Random().nextLong();
            for (int row = 0; row < height; row++) {
                  for (int col = 0; col < width; col++) {
                        double nx = col * scale;
                        double ny = row * scale;
                        noiseVals[row][col] = OpenSimplex2S.noise2(seed, nx, ny);
                  }
            }

            noiseVals = granulate(noiseVals, seed, scale, width, height, granularity);

            return noiseVals;

            // double steps = 5;
            // double threshold = 0.1;
            // int translationMax = 9;
            // String[][] map = new String[height][width];
            // for (int row = 0; row < height; row++) {
            // for (int col = 0; col < width; col++) {
            // if (noiseVals[row][col] > threshold) {
            // double val = noiseVals[row][col];
            // int translatedValue = translationMax;
            // for (double i = steps; i > 0; i--) {
            // if (val > threshold + (((1 - threshold) / steps) * (i - 1))) {
            // map[row][col] = "" + translatedValue;
            // break;
            // }
            // translatedValue--;
            // }
            // } else {
            // map[row][col] = "0";
            // }
            // }
            // }

            // return map;
      }

      private static double[][] granulate(double[][] noise, long seed, double scale, int width, int height,
                  double granularity) {
            int octaves = 2;
            double persistence = granularity;
            for (int row = 0; row < height; row++) {
                  for (int col = 0; col < width; col++) {
                        double nx = col * scale;
                        double ny = row * scale;
                        double value = 0;
                        double amplitude = 1;
                        double frequency = 1;
                        for (int i = 0; i < octaves; i++) {
                              value += amplitude * OpenSimplex2S.noise2(seed, nx * frequency, ny *
                                          frequency);
                              amplitude *= persistence;
                              frequency *= 2;
                        }
                        noise[row][col] = value;
                  }
            }
            return noise;
      }
}
