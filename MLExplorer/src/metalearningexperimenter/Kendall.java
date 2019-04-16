/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import weka.core.Utils;
import java.util.Arrays;

/**
 *
 * @author danels
 */
public class Kendall {

  public double getTau(double[] x, double[] y) {
    double tau = 0;
    int n = x.length;
    int ind[] = Utils.sort(x);
    double[] ra = Rank.rank(x, Rank.rdAscending);
    double[] rb = Rank.rank(y, Rank.rdAscending);
    double[] rx = new double[n];
    double[] ry = new double[n];
    for (int i = 0; i < n; i++) {
      rx[i] = ra[ind[i]];
      ry[i] = rb[ind[i]];
    }
    //for (int i = 0; i < ind.length; i++) ind[i]++;
    System.out.println();
    System.out.println("Subject: " + Arrays.toString(ind));
    System.out.println("Rx: " + Arrays.toString(rx));
    System.out.println("Ry: " + Arrays.toString(ry));
    int nc = 0;
    int nd = 0;
    int TX = 0;
    int TY = 0;
    for (int i = 0; i < n - 1; i++) {
      int tx = 1;
      int ty = 1;
      for (int j = i + 1; j < n; j++) {
        if (i == 0 && rx[i] == rx[j] || 
           (i > 0  && rx[i - 1] != rx[i]) && rx[i] == rx[j]) 
          tx++;
        else
          if (tx > 1) {
            TX += tx * tx - tx;
            tx = 1;
          }
        if (ry[i] == ry[j]) ty++;
        else
          if (ty > 1) {
            TY += ty * ty - ty;
            ty = 1;
          }
        if (rx[i] != rx[j] && ry[i] != ry[j]) {
          if (ry[i] < ry[j])
            nc++;
          else 
            nd++;
        }
      }
      if (tx > 1) TX += tx * tx - tx;
      if (ty > 1) TY += ty * ty - ty;
    }
    int fn = n * (n - 1);
    tau = 2 * (nc - nd) / (Math.sqrt(fn - TX) * Math.sqrt(fn - TY));
    return tau;
  }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      //Ranking rt = new Ranking();
      //double[] a = {80, 15, 80, 160, 160, 160, 160, 160, 160, 160};
      double[] a = {70, 15, 80, 100, 90, 60, 50, 30, 15, 40};
      //double[] b = {3, 8.5, 2, 0.5, 1, 4, 5, 7, 8.5, 6};
      double[] b = {45, 20, 60, 80, 70, 194, 194, 35, 10, 35};
      /**
      int ind[] = Utils.sort(a);
      double[] ra = rt.rank(a, Ranking.rdAscending);
      double[] rb = rt.rank(b, Ranking.rdAscending);
      double[] rx = new double[ra.length];
      double[] ry = new double[rb.length];
      for (int i = 0; i < a.length; i++) {
        rx[i] = ra[ind[i]]+1;
        ry[i] = rb[ind[i]]+1;
      }
      for (int i = 0; i < ind.length; i++) ind[i]++;
      System.out.println();
      System.out.println("Subject: " + Arrays.toString(ind));
      //System.out.println("Ra: " + Arrays.toString(ra));
      //System.out.println("Rb: " + Arrays.toString(rb));
      System.out.println("Rx: " + Arrays.toString(rx));
      System.out.println("Ry: " + Arrays.toString(ry));
       */
      Kendall kendall = new Kendall();
      System.out.println("Tau: " + kendall.getTau(a, b));
    }

}
