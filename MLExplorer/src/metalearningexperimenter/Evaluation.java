/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.io.*;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.converters.ArffLoader;
import weka.core.FastVector;
import weka.core.converters.ArffSaver;
import weka.core.Utils;

/**
 *
 * @author Danel
 */
public class Evaluation {

  /**
   *  List of regression models names
   */
  private FastVector modelName;

  /**
   * Vector for storing names of performances meta-datasets
   */
  private FastVector mdnPerformances = new FastVector();

  /**
   * Directory where evaluations will be saved
   */
  private File evalDir;

  /**
   * Constructor of the class
   */
  private Evaluation() {
    setup();
    /**
     * For every performance measure load performace and prediction datasets
     * and evaluate NMSE.
     */
    for (int q = 0; q < mdnPerformances.size(); q++) {
      evaluateNMSE(q);
      evaluateRanking(q);
    }
  }

  private void evaluateNMSE(int perf) {
    /**
     * Load actual performance dataset
     */
    String pName = (String)mdnPerformances.elementAt(perf);
    File actFile = new File(MetaDataCompiler.MetaDataPath, pName + ".arff");
    Instances actual = getInstances(actFile);
    /**
     * Set evaluation dataset attribute structure
     */
    Instances atAlgorithms = new Instances(actual, 0);
    atAlgorithms.insertAttributeAt(new Attribute("Model", (FastVector)null), 0);
    /**
     * Create the evaluation dataset where attributes are the classification
     * algorithms and instances are the regression models used to obtain
     * predictions.
     */
    Instances evalNMSE = new Instances(atAlgorithms, modelName.size());
    evalNMSE.setRelationName("NMSE-" + pName);
    /**
     * For every regression model (i.e. instance in evaluation dataset)
     * load the corresponding prediction dataset, compute NMSE evaluation
     * measure and add it to evaluation dataset.
     */
    for (int i = 0; i < modelName.size(); i++) {
      String mName = (String)modelName.elementAt(i);
      File predDir = new File(MetaDataCompiler.MetaDataPath, mName);
      File predFile = new File(predDir, pName + ".arff");
      Instances prediction = getInstances(predFile);
      /**
       * Computation of NMSE evaluation
       */
      double[] evaluation = computeNMSE(actual, prediction);
      evalNMSE.add(new DenseInstance(1, evaluation));
      evalNMSE.lastInstance().setValue(0, mName);
    }
    saveEvaluation(evalNMSE);
  }

  private double[] computeNMSE(Instances actDS, Instances predDS) {
    double[] eval = new double[1 + predDS.numAttributes()];
    for (int i = 0; i < predDS.numAttributes(); i++){
      double num = 0, den = 0;
      double mean = actDS.meanOrMode(i);
      for (int j = 0; j < predDS.numInstances(); j++) {
        double pred = predDS.instance(j).value(i);
        double act = actDS.instance(j).value(i);
        num += Math.pow(pred - act, 2);
        den += Math.pow(act - mean, 2);
      }
      eval[1 + i] = num / den;
    }
    return eval;
  }

  private void evaluateRanking(int perf) {
    /**
     * Load actual performance dataset
     */
    String pName = (String)mdnPerformances.elementAt(perf);
    File actFile = new File(MetaDataCompiler.MetaDataPath, pName + ".arff");
    Instances actual = getInstances(actFile);
    /**
     * Create evaluation dataset structure
     */
    FastVector atRanking = new FastVector();
    atRanking.addElement(new Attribute("Model", (FastVector)null));
    atRanking.addElement(new Attribute("defSpearman"));
    atRanking.addElement(new Attribute("Spearman"));
    atRanking.addElement(new Attribute("Spearman-def"));
    atRanking.addElement(new Attribute("defWeightedSpearman"));
    atRanking.addElement(new Attribute("WeightedSpearman"));
    atRanking.addElement(new Attribute("WeightedSpearman-def"));
    atRanking.addElement(new Attribute("defPearson"));
    atRanking.addElement(new Attribute("Pearson"));
    atRanking.addElement(new Attribute("Pearson-def"));
    atRanking.addElement(new Attribute("defKendall"));
    atRanking.addElement(new Attribute("Kendall"));
    atRanking.addElement(new Attribute("Kendall-def"));
    /**
     * Create evaluation dataset
     */
    Instances eval = new Instances("RankingTests-" + pName, atRanking, modelName.size());
    /**
     * Compute Spearman, Weighted Spearman and Pearson correlations for default (mean) accuracy dataset
     */
    double dS = defaultSpearman(actual);
    double dWS = defaultWeightedSpearman(actual);
    double dP = defaultPearson(actual);
    double dK = defaultKendall(actual);
    /**
     * For every regression model
     */
    for (int i = 0; i < modelName.size(); i++) {
      /**
       * Load prediction dataset
       */
      String mName = (String)modelName.elementAt(i);
      File predDir = new File(MetaDataCompiler.MetaDataPath, mName);
      File predFile = new File(predDir, pName + ".arff");
      Instances prediction = getInstances(predFile);
      /**
       * Computation of Spearman Rank evaluation
       */
      double[] evaluation = new double[13];
      evaluation[1] = dS;
      evaluation[2] = computeSpearman(actual, prediction);
      evaluation[3] = evaluation[2] - dS;
      evaluation[4] = dWS;
      evaluation[5] = computeWeightedSpearman(actual, prediction);
      evaluation[6] = evaluation[5] - dWS;
      evaluation[7] = dP;
      evaluation[8] = computePearson(actual, prediction);
      evaluation[9] = evaluation[8] - dP;
      evaluation[10] = dK;
      evaluation[11] = computeKendall(actual, prediction);
      evaluation[12] = evaluation[11] - dK;
      eval.add(new DenseInstance(1, evaluation));
      eval.lastInstance().setValue(0, mName);
    }
    saveEvaluation(eval);
  }
  
  private double defaultSpearman(Instances actDS) {
    double cumSr = 0;
    double[] predRank = Rank.descending(datasetMean(actDS));
    int numAttrs = actDS.numAttributes();
    for (int i = 0; i < actDS.numInstances(); i++) {
      double[] actRank  = Rank.descending( actDS.instance(i).toDoubleArray());
      double sqDif = 0;
      for (int j = 0; j < numAttrs; j++) 
        sqDif += Math.pow(predRank[j] - actRank[j], 2);
      double Sr = 1 - 6 * sqDif / (Math.pow(numAttrs, 3) - numAttrs);
      cumSr += Sr;
    }
    return cumSr / actDS.numInstances();
  }
  
  private double[] datasetMean(Instances dataset) {
    double[] dsMean = new double[dataset.numAttributes()];
    for (int i = 0; i < dataset.numAttributes(); i++) 
      dsMean[i] = dataset.meanOrMode(i);
    return dsMean;
  }
  
  private double defaultWeightedSpearman(Instances actDS) {
    int N = actDS.numAttributes();
    double denom = Math.pow(N, 4) + Math.pow(N, 3) - Math.pow(N, 2) - N;
    double[] predRank = Rank.descending(datasetMean(actDS));
    double cumSr = 0;
    for (int i = 0; i < actDS.numInstances(); i++) {
      double[] actRank  = Rank.descending( actDS.instance(i).toDoubleArray());
      double sqDif = 0;
      for (int j = 0; j < N; j++) {
        double W = 2 * N + 2 - predRank[j] - actRank[j];
        sqDif += Math.pow(predRank[j] - actRank[j], 2) * W;
      }
      double Sr = 1 - 6 * sqDif / denom;
      cumSr += Sr;
    }
    return cumSr / actDS.numInstances();
  }
  
  private double computeWeightedSpearman(Instances actDS, Instances predDS) {
    int N = actDS.numAttributes();
    double denom = Math.pow(N, 4) + Math.pow(N, 3) - Math.pow(N, 2) - N;
    double cumSr = 0;
    for (int i = 0; i < actDS.numInstances(); i++) {
      double[] actRank  = Rank.descending( actDS.instance(i).toDoubleArray());
      double[] predRank = Rank.descending(predDS.instance(i).toDoubleArray());
      double sqDif = 0;
      for (int j = 0; j < N; j++) {
        double W = 2 * N + 2 - predRank[j] - actRank[j];
        sqDif += Math.pow(predRank[j] - actRank[j], 2) * W;
      }
      double Sr = 1 - 6 * sqDif / denom;
      cumSr += Sr;
    }
    return cumSr / actDS.numInstances();
  }

  /**
   * Compute Spearman's rank-order correlation coefficient
   *
   * @param actDS
   * @param predDS
   * @return
   */
  private double computeSpearman(Instances actDS, Instances predDS) {
    double cumSr = 0;
    for (int i = 0; i < actDS.numInstances(); i++) {
      double[] actRank  = Rank.descending( actDS.instance(i).toDoubleArray());
      double[] predRank = Rank.descending(predDS.instance(i).toDoubleArray());
      int numAttrs = actDS.numAttributes();
      double sqDif = 0;
      for (int j = 0; j < numAttrs; j++) 
        sqDif += Math.pow(predRank[j] - actRank[j], 2);
      double Sr = 1 - 6 * sqDif / (Math.pow(numAttrs, 3) - numAttrs);
      cumSr += Sr;
    }
    return cumSr / actDS.numInstances();
  }
  
  /**
   * Compute Kendall's rank-order correlation coefficient
   *
   * @param actDS
   * @param predDS
   * @return
   */
  private double computeKendall(Instances actDS, Instances predDS) {
    double cumTau = 0;
    for (int i = 0; i < actDS.numInstances(); i++) {
      double[] act = actDS.instance(i).toDoubleArray();
      int ind[] = Utils.sort(act);
      double[] actRank  = Rank.ascending(act);
      double[] predRank = Rank.ascending(predDS.instance(i).toDoubleArray());
      double[] rx = new double[actDS.numAttributes()];
      double[] ry = new double[actDS.numAttributes()];
      for (int j = 0; j < actDS.numAttributes(); j++) {
        rx[j] = actRank[ind[j]];
        ry[j] = predRank[ind[j]];
      }
      cumTau += kendall(rx, ry);
    }
    return cumTau / actDS.numInstances();
  }
  
  private double defaultKendall(Instances actDS) {
    double cumTau = 0;
    double[] predRank = Rank.ascending(datasetMean(actDS));
    for (int i = 0; i < actDS.numInstances(); i++) {
      double[] act = actDS.instance(i).toDoubleArray();
      int ind[] = Utils.sort(act);
      double[] actRank  = Rank.ascending(act);
      double[] rx = new double[actDS.numAttributes()];
      double[] ry = new double[actDS.numAttributes()];
      for (int j = 0; j < actDS.numAttributes(); j++) {
        rx[j] = actRank[ind[j]];
        ry[j] = predRank[ind[j]];
      }
      cumTau += kendall(rx, ry);
    }
    return cumTau / actDS.numInstances();
  }
  
  private double kendall(double[] rx, double[] ry) {
    int nc = 0;
    int nd = 0;
    int TX = 0;
    int TY = 0;
    int n = rx.length;
    for (int l = 0; l < n - 1; l++) {
      int tx = 1;
      int ty = 1;
      for (int j = l + 1; j < n; j++) {
        if (l == 0 && rx[l] == rx[j] || 
           (l > 0  && rx[l - 1] != rx[l]) && rx[l] == rx[j]) 
          tx++;
        else
          if (tx > 1) {
            TX += tx * tx - tx;
            tx = 1;
          }
        if (ry[l] == ry[j]) ty++;
        else
          if (ty > 1) {
            TY += ty * ty - ty;
            ty = 1;
          }
        if (rx[l] != rx[j] && ry[l] != ry[j]) {
          if (ry[l] < ry[j])
            nc++;
          else 
            nd++;
        }
      }
      if (tx > 1) TX += tx * tx - tx;
      if (ty > 1) TY += ty * ty - ty;
    }
    int fn = n * (n - 1);
    double tau = (fn == TX || fn == TY)? 0:
                  2 * (nc - nd) / (Math.sqrt(fn - TX) * Math.sqrt(fn - TY));      
    return tau;
  }
  
  private double defaultPearson(Instances actDS) {
    double[] predDS = datasetMean(actDS);
    double cumPr = 0;
    int totalInst = actDS.numInstances();
    for (int l = 0; l < actDS.numInstances(); l++) {
      int numAttrs = actDS.numAttributes();
      double Sx = 0, Sy = 0, SsqX = 0, SsqY = 0, Sxy = 0;
      for (int j = 0; j < numAttrs; j++) {
        double x = actDS.instance(l).value(j);
        double y = predDS[j];
        Sx += x;
        Sy += y;
        SsqX += x * x;
        SsqY += y * y;
        Sxy += x * y;
      }
      double SPxy = Sxy - Sx * Sy / numAttrs;
      double SSx = SsqX - Sx * Sx / numAttrs;
      double SSy = SsqY - Sy * Sy / numAttrs;
      if (SSx == 0 || SSy == 0)
        totalInst--;
      else 
        cumPr += SPxy / Math.sqrt(SSx * SSy);
    }
    return cumPr / totalInst;
  }

  /**
   * Compute Pearson's product-moment correlation coefficient
   *
   * @param actDS
   * @param predDS
   * @return
   */
  private double computePearson(Instances actDS, Instances predDS) {
    double cumPr = 0;
    int totalInst = actDS.numInstances();
    for (int l = 0; l < actDS.numInstances(); l++) {
      int numAttrs = actDS.numAttributes();
      double Sx = 0, Sy = 0, SsqX = 0, SsqY = 0, Sxy = 0;
      for (int j = 0; j < numAttrs; j++) {
        double x = actDS.instance(l).value(j);
        double y = predDS.instance(l).value(j);
        Sx += x;
        Sy += y;
        SsqX += x * x;
        SsqY += y * y;
        Sxy += x * y;
      }
      double SPxy = Sxy - Sx * Sy / numAttrs;
      double SSx = SsqX - Sx * Sx / numAttrs;
      double SSy = SsqY - Sy * Sy / numAttrs;
      if (SSx == 0 || SSy == 0)
        totalInst--;
      else 
        cumPr += SPxy / Math.sqrt(SSx * SSy);
    }
    return cumPr / totalInst;
  }

  /**
   * Save current prediction dataset to an arff file.
   */
  private void saveEvaluation(Instances ds) {
    File evalFile = new File(evalDir, ds.relationName() + ".arff");
    ArffSaver Saver = new ArffSaver();
    try {
      Saver.setFile(evalFile);
      Saver.setDestination(evalFile);
      Saver.setInstances(ds);
      Saver.setRetrieval(ArffSaver.BATCH);
      Saver.writeBatch();
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
  }

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
  //@ requires (\forall int l; 0 <= l && l < index.length; 0 <= index[l] && index[l] < array.length);
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

  private void setup() {
    readModelList();
    /**
     * Prepare output directory
     */
    evalDir = new File(MetaDataCompiler.MetaDataPath, "Evaluation");
    if (!evalDir.exists())
      evalDir.mkdir();
    /**
     * Set performance measure of interest
     */
    mdnPerformances.addElement(MetaDataCompiler.pmTestingAccuracy);
    //mdnPerformances.addElement(MetaDataCompiler.pmKappa);
}

  /**
   * Read list of learning algorithms
   */
  private void readModelList() {
    File fileModel = new File(MetaDataCompiler.MetaDataPath, "models.txt");
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(fileModel));
      modelName = new FastVector();
      String s = br.readLine();
      while (s != null) {
        modelName.addElement(s);
        s = br.readLine();
      }
    } catch(IOException e) {               // For IOException or FileNotFoundException
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

  /**
   * Open a dataset file in arff format an return the dataset.
   *
   * @param f
   * @return
   */
  private Instances getInstances(File f) {
    Instances l = null;
    ArffLoader loader = new ArffLoader();
    try {
      loader.setSource(f);
      l = loader.getDataSet();
    } catch (Exception e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
    return l;
  }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      Evaluation eval = new Evaluation();
    }

}
