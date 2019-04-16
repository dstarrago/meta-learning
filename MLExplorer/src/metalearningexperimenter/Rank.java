/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

/**
 *
 * @author danels
 */
public class Rank {
  
  public static final int rdAscending = 0;
  public static final int rdDescending = 1;

  /**
   * Implements quicksort according to Manber's "Introduction to
   * Algorithms".
   *
   * @param array the array of doubles to be sorted
   * @param index the index into the array of doubles
   * @param left the first index of the subset to be sorted
   * @param right the last index of the subset to be sorted
   */
  //@ requires 0 <= first && first <= right && right < array.length;
  //@ requires (\forall int i; 0 <= i && i < index.length; 0 <= index[i] && index[i] < array.length);
  //@ requires array != index;
  //  assignable index;
  private static void quickSort(/*@non_null@*/ double[] array, /*@non_null@*/ int[] index, 
                                int left, int right) {

    if (left < right) {
      int middle = partition(array, index, left, right);
      quickSort(array, index, left, middle);
      quickSort(array, index, middle + 1, right);
    }
  }
  
  /**
   * Partitions the instances around a pivot. Used by quicksort and
   * kthSmallestValue.
   *
   * @param array the array of doubles to be sorted
   * @param index the index into the array of doubles
   * @param left the first index of the subset 
   * @param right the last index of the subset 
   *
   * @return the index of the middle element
   */
  private static int partition(double[] array, int[] index, int l, int r) {
    
    double pivot = array[index[(l + r) / 2]];
    int help;

    while (l < r) {
      while ((array[index[l]] < pivot) && (l < r)) {
        l++;
      }
      while ((array[index[r]] > pivot) && (l < r)) {
        r--;
      }
      if (l < r) {
        help = index[l];
        index[l] = index[r];
        index[r] = help;
        l++;
        r--;
      }
    }
    if ((l == r) && (array[index[r]] > pivot)) {
      r--;
    } 

    return r;
  }

  public static double[] ascending(double[] array) {
    return rank(array, rdAscending);  
  }
  
  public static double[] descending(double[] array) {
    return rank(array, rdDescending);
  }
  
  public static double[] rank(double[] array, int dir) {
    int [] index = new int[array.length];
    for (int i = 0; i < index.length; i++) {
      index[i] = i;
      if (Double.isNaN(array[i])) {
        array[i] = Double.MAX_VALUE;
      }
    }
    quickSort(array, index, 0, array.length - 1);
    double[] ranking = new double[array.length];
    double lastScore = Double.NaN;
    int sameScore = 1;
    for (int i = 0; i < index.length; i++) {
      if (array[index[i]] == lastScore) {
        sameScore++;
      } else {
        ranking[index[i]] = (dir == rdAscending)? i : index.length - 1 - i;
        lastScore = array[index[i]];
        if (sameScore > 1) {
          double av = (double)(2 * i - sameScore - 1) / 2;
          for (int j = 1; j <= sameScore; j++) 
            ranking[index[i - j]] =  (dir == rdAscending)? av : index.length - 1 - av;
          }
        sameScore = 1;
      }
    }
    if (sameScore > 1) {
      double av = (double)(2 * index.length - sameScore - 1) / 2;
      for (int j = 1; j <= sameScore; j++) 
        ranking[index[index.length - j]] =  (dir == rdAscending)? av : index.length - 1 - av;
    }
    return ranking;
  }
 
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      /**
      Ranking rt = new Ranking();
      double[] a = {21, 34, 56, 16, 21, 42};
      double[] r = rt.rank(a);
       */
    }

}
