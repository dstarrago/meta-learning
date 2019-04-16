/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.io.*;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Attribute;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.Filter;
/**
 * Regression Models
 */
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.lazy.KStar;
import weka.classifiers.functions.SMOreg;

/**
 *
 * @author Danel
 */
public class PredictionMaker {

  /**
   * Learner to be used in regression
   */
  private FastVector Predictors = new FastVector();
  private File[] predictFile;

  /**
   *  Meta-datasets
   */
  private Instances mdsCharact;
  private Instances[] mdsPrediction;

  /**
   *  List of learning algorithm names
   */
  private FastVector algorithmName;

  /**
   * Vector for storing names of performances meta-datasets
   */
  private FastVector mdnPerformances = new FastVector();

  /**
   *  Vectors holding meta-attributes structures
   */
  private FastVector atAlgorithms = new FastVector();

  /**
   * Number of folds in cross validation
   */
  private final int foldsNumber = 10;
  
  /**
   * Constructor of the class
   */
  private PredictionMaker() {
    setup();
    execute();
  }

  /**
   * Configure settings for the prediction process.
   */
  private void setup() {
    /**
     * Read algorithms involved
     */
    setupAlgorithms();
    /**
     * Set performance measure of interest
     */
    mdnPerformances.addElement(MetaDataCompiler.pmTestingAccuracy);
    //mdnPerformances.addElement(MetaDataCompiler.pmKappa);
    /**
     * Set attribute information for prediction dataset
     */
    for (int i = 0; i < algorithmName.size(); i++) {
      String atName = algName((String)algorithmName.elementAt(i));
      atAlgorithms.addElement(new Attribute(atName));
    }
    /**
     * Set predictors
     */
    Predictors.addElement(new LinearRegression());
    Predictors.addElement(new SimpleLogistic());
    Predictors.addElement(new MultilayerPerceptron());
    Predictors.addElement(new GaussianProcesses());
    Predictors.addElement(new KStar());
    Predictors.addElement(new SMOreg());
    /**
     * Create directory structure for store dataset files.
     */
    dirStructure();
  }

  /**
   * Read list of learning algorithms
   */
  private void setupAlgorithms() {
    File filesAlg = new File(MetaDataCompiler.DataPath, "algorithms.txt");
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(filesAlg));
      algorithmName = new FastVector();
      String s = br.readLine();
      while (s != null) {
        algorithmName.addElement(s);
        s = br.readLine();
      }
    } catch(IOException e) {               // For IOException or FileNotFoundException
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

  /**
   * Create the directory structure for store file predictions.
   */
  private void dirStructure() {
    predictFile = new File[Predictors.size()];
    for (int i = 0; i < Predictors.size(); i++) {
      String predName = algName(Predictor(i).getClass().getName());
      predictFile[i] = new File(MetaDataCompiler.MetaDataPath, predName);
      if (!predictFile[i].exists())
        predictFile[i].mkdir();
    }
  }

  private void execute() {
    /**
     * Load dataset characterization meta-dataset
     */
    String fName = MetaDataCompiler.mdnNormCharacter + ".arff";
    File charName = new File(MetaDataCompiler.MetaDataPath, fName);
    mdsCharact = getInstances(charName);
    /**
     * Do prediction for every performance measure
     */
    for (int i = 0; i < mdnPerformances.size(); i++) {
      predictPerformance(i);
    }
  }

  private void predictPerformance(int perfIndex) {
    Instances mdsPerform, mdsData = null;
    /**
     * Load a classifier performance meta-dataset specify by index perfIndex
     */
    String performanceName = (String)mdnPerformances.elementAt(perfIndex)  + ".arff";
    File performanceFile = new File(MetaDataCompiler.MetaDataPath, "norm" + performanceName);
    mdsPerform = getInstances(performanceFile);
    /**
     * Create datasets that will store predictions corresponding to current
     * performance for every regression model.
     */
    mdsPrediction = new Instances[Predictors.size()];
    for (int i = 0; i < Predictors.size(); i++)
      mdsPrediction[i] = new Instances(performanceName, atAlgorithms,
                      mdsPerform.numInstances());
    /**
     * Data structure to store temporarily the values of performance.
     */
    int dim1 = Predictors.size();
    int dim2 = mdsPerform.numInstances();
    int dim3 = atAlgorithms.size();
    double[][][] normPrediction = new double[dim1][dim2][dim3];
    /**
     * Do prediction for every classifier in performance dataset
     */
    for (int k = 0; k < atAlgorithms.size(); k++) {
      /**
       * Filter the performance of current classifier from performances dataset.
       * Create Remove filter for deleting all attributes except for the
       * current classifier.
       */
      Remove removeFilter = new Remove();
      removeFilter.setAttributeIndices(String.valueOf(k + 1));
      removeFilter.setInvertSelection(true);
      try {
        removeFilter.setInputFormat(mdsPerform);
        /**
         * Merge characterization dataset with filtered dataset containing the
         * current classifier performance. And set the class index to the last
         * attribute.
         */
        Instances atFiltered = Filter.useFilter(mdsPerform, removeFilter);
        mdsData = Instances.mergeInstances(mdsCharact, atFiltered);
        mdsData.setClassIndex(mdsData.numAttributes() - 1);
      } catch (Exception e) {
        e.printStackTrace(System.err);
        System.exit(1);
      }
      /**
       * Apply cross validation in order to predict instances in every testing 
       * fold with the model trained with the remaining folds.
       */
      Instances trn, tst;
      int offset = 0;
      for (int i = 0; i < foldsNumber; i++) {     // For every fold do:
        trn = mdsData.trainCV(foldsNumber, i);
        tst = mdsData.testCV(foldsNumber, i);
        /**
         * For every predictor build a regression model and do predictions
         */
        for (int q = 0; q < Predictors.size(); q++) {
          try {
            /**
             * Build the regression model with the training set for this fold.
             */
            Predictor(q).buildClassifier(trn);
            /**
             * For every instance in test set predict performance for
             * current classifier with the regression model.
             */
            for (int j = 0; j < tst.numInstances(); j++)
              normPrediction[q][offset + j][k] = Predictor(q).classifyInstance(tst.instance(j));
          } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
          }
        }
        offset+= tst.numInstances();
      }
    }
    File normParamsFile = new File(MetaDataCompiler.MetaDataPath,
            MetaDataCompiler.mdnNormParamsPrefix + performanceName);
    Instances mdsNormParamsPerform = getInstances(normParamsFile);
    /**
     * Now that prediction table is complete we proceed to add rows
     * (i.e instances) to prediction dataset for every regression model.
     */
    for (int q = 0; q < Predictors.size(); q++) {
      for (int i = 0; i < mdsPerform.numInstances(); i++) {
        double mean = mdsNormParamsPerform.instance(i).value(Normalization.piMean);
        double stdDev = mdsNormParamsPerform.instance(i).value(Normalization.piStdDev);
        mdsPrediction[q].add(new DenseInstance(1, denormalize(normPrediction[q][i], mean, stdDev)));
      }
      savePrediction(performanceName, q);
    }
  }

  private double[] denormalize(double[] normData, double mean, double stdDev) {
    double[] data = new double[normData.length];
    for (int i = 0; i < data.length; i++)
      data[i] = mean + normData[i] * stdDev;
    return data;
  }

  /**
   * Save current prediction dataset to an arff file.
   */
  private void savePrediction(String fileName, int model) {
    File predictionFile = new File(predictFile[model], fileName);
    ArffSaver predictionSaver = new ArffSaver();
    try {
      predictionSaver.setFile(predictionFile);
      predictionSaver.setDestination(predictionFile);
      predictionSaver.setInstances(mdsPrediction[model]);
      predictionSaver.setRetrieval(ArffSaver.BATCH);
      predictionSaver.writeBatch();
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
  }

  /**
   * Open a dataset file in arff format an return the dataset.
   *
   * @param f
   * @return
   */
  private Instances getInstances(File f) {
    Instances I = null;
    ArffLoader loader = new ArffLoader();
    try {
      loader.setSource(f);
      I = loader.getDataSet();
    } catch (Exception e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
    return I;
  }

  /**
   * Return a predictor in the predictor list
   */
  private Classifier Predictor(int index) {
    return (Classifier)Predictors.elementAt(index);
  }

  /**
   * From the full name of an algorithm, return a short name that is the
   * last part of the full name.
   *
   * @param fullName
   * @return
   */
  private String algName(String fullName) {
	int index = fullName.lastIndexOf('.');
	return fullName.substring(index + 1);
  }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      PredictionMaker evaluator = new PredictionMaker();
    }

}
