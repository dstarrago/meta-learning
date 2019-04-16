/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

/**
 *
 * @author danels
 */

import weka.experiment.Stats;

class Normalization {

  /**
   * Normalized data
   */
  public double[] normData;

  /**
   * Parameters of the normalization
   */
  public double[] params;

  /**
   * Normalization parameters index
   */
  public static final int piMean = 0;
  public static final int piStdDev = 1;

  /**
   * Normalize an array of doubles by subtracting the mean and dividing the
   * result by the standard deviation.
   */
  Normalization (double[] data) {
    params = new double[2];
    normData = new double[data.length];
    Stats stats = new Stats();
    for(int i = 0; i < data.length; i++)
      stats.add(data[i]);
    stats.calculateDerived();
    params[piMean] = stats.mean;
    params[piStdDev] = stats.stdDev;
    for(int i = 0; i < data.length; i++)
      normData[i] = (stats.stdDev == 0)? 0: (data[i] - stats.mean) / stats.stdDev;
  }
}
