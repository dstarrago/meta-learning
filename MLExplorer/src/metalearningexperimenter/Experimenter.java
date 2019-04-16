/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.io.*;
import java.util.Date;
import weka.classifiers.Evaluation;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.classifiers.AbstractClassifier;
import weka.core.converters.ArffLoader;
import weka.core.FastVector;
import weka.core.Attribute;
import weka.core.converters.ArffSaver;
import weka.core.Utils;
import RoughSet.RoughSet;
import weka.experiment.Stats;

/**
 *
 * @author Danel
 */
public class Experimenter {

  /**
   * Alternative saving modes
   */
  private final int smIncremental = 1;
  private final int smBatch = 2;

  /**
   *  *************** Experiment settings ********************
   */

  /**
   *  Experiment name
   */
  private String ExpName = "Exp001";

  /**
   *  Database path
   */
  private String DataPath = "D:/Users/Danel/ACADÉMICOS/INVESTIGACIÓN/SOFTCOMPUTING/DATOS/Prueba";

  /**
   *  Output directory
   */
  private String OutputDir = "D:/Users/Danel/ACADÉMICOS/INVESTIGACIÓN/SOFTCOMPUTING/DATOS/Metadata";

  /**
   *  Dataset characterization meta-attribute names
   */
  private final String maMeanAccuracy = "MeanAccuracy";
  private final String maClassificationQuality = "ClassificationQuality";
  private final String maWeightedClassifAccuracy = "WeightedClassifAccuracy";
  private final String maGenApproxRatio = "GenApproxRatio";
  private final String maGenClassifAccuracy1 = "GenClassifAccuracy1";
  private final String maGenClassifQuality1 = "GenClassifQuality1";
  private final String maGenClassifAccuracy2 = "GenClassifAccuracy2";
  private final String maGenClassifQuality2 = "GenClassifQuality2";
  private final String maGenClassifAccuracy3 = "GenClassifAccuracy3";
  private final String maGenClassifQuality3 = "GenClassifQuality3";
  /**
   * Normalization parameters meta-attribute names
   */
  private final String maMean = "Mean";
  private final String maStandDev = "StandDev";
  /**
   * Amount of parameters for storing algorithms
   */
  private final int algParamCount = 2;
  /**
   * Normalization parameters index
   */
  private final int piMean = 0;
  private final int piStdDev = 1;
  /**
   *  Performance measures names
   */
  private final String pmAccuracy = "Accuracy";
  private final String pmKappa = "Kappa";
  private final String pmFMeasure = "FMeasure";
  private final String pmRecall = "Recall";
  private final String pmPrecision = "Precision";
  private final String pmSpecificity = "Specificity";
  private final String pmAUC = "AUC";
  private final String pmTrainingTime = "TrainingTime";
  private final String pmTestingTime = "TestingTime";

  /**
   * Saving mode
   */
  private int SaveMode = smIncremental;

  /**
   *  *************** End of experiment settings ********************
   */
  
  /**
   * Learning algorithms
   */
  Classifier[] algorithm;

  /**
   *  List of learning algorithm names
   */
  private FastVector algorithmName;

  /**
   *  Meta-datasets
   */
  private Instances mdsCharacter;
  private Instances mdsNormCharacter;
  private Instances mdsNormMetricParams;
  private Instances [] mdsNormPerform;

  /**
   *  Meta-dataset names
   */
  private final String mdnCharacter = "character";
  private final String mdnNormCharacter = "normCharacter";
  private final String mdnNormMetricParams = "normMetricParams";
  /**
   * Vector for storing names of performances meta-datasets
   */
  private FastVector mdnPerformances = new FastVector();

  /**
   *  Objects to write meta-dataset files in Arff format
   */
  private ArffSaver characterSaver;
  private ArffSaver normCharacterSaver;
  private ArffSaver normMetricParamsSaver;
  private ArffSaver[] performSaver;

  /**
   *  Vectors holding meta-attributes structures
   */
  private FastVector atMeasures = new FastVector();
  private FastVector atNormalization = new FastVector();
  private FastVector atAlgorithms = new FastVector();

  /**
   * List of characteristics(measures) names
   */
  private FastVector anMeasures = new FastVector();
  
  /**
   *  List of dataset names with 5 folds cross validation schema
   */
  private FastVector DataSets5Folds;

  /**
   *  Index of Class of base dataset with less number of instances
   */ 
  private int MinorClass;

  /**
   *  Index of Class of base dataset with more number of instances
   */
  private int MayorClass;
  
  /**
   * A unique object to read files in ARFF format.
   */
  ArffLoader kl = new ArffLoader();

  /**
   * Constructor of the class
   */
  private Experimenter() {
    read5CVlist();
    setupAlgorithms();
    setupPerformanceMeasures();
    setupMetaAttributes();
    setupMetaDatasets();
    if (SaveMode == smIncremental) setupSavers(ArffSaver.INCREMENTAL);
    processBaseDS();
    normalizeDataCharacter();
    saveNormDataCharacter();
    if (SaveMode == smBatch) saveMetaData();
  }

  /**
   *  Read dataset list for 5 folds cross validation schema
   */
  private void read5CVlist() {
    File files5cv = new File(DataPath, "esquema5cv.txt");
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(files5cv));
      DataSets5Folds = new FastVector();
      String s = br.readLine();
      while (s != null) {
        DataSets5Folds.addElement(s);
        s = br.readLine();
      }
    } catch(IOException e) {               // For IOException or FileNotFoundException
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

  /**
   * Read list of learning algorithms
   */
  private void setupAlgorithms() {
    File filesAlg = new File(DataPath, "algorithms.txt");
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
    /**
     *  Now that we know the number and names of learning algorithms, 
     *  let's create it.
     */
    algorithm = new Classifier[algorithmName.size()];
    for (int i = 0; i < algorithmName.size(); i++) 
      try {
        algorithm[i] = AbstractClassifier.forName((String)algorithmName.elementAt(i), null);
        } catch (Exception e) {
             System.err.println(e.getMessage());
      }
  }

  /**
   *  Set the names of performance measures that we will work with
   */
  private void setupPerformanceMeasures() {
    mdnPerformances.addElement(pmAccuracy);
    mdnPerformances.addElement(pmKappa);
    mdnPerformances.addElement(pmFMeasure);
    mdnPerformances.addElement(pmRecall);
    mdnPerformances.addElement(pmPrecision);
    mdnPerformances.addElement(pmSpecificity);
    mdnPerformances.addElement(pmAUC);
    mdnPerformances.addElement(pmTrainingTime);
    mdnPerformances.addElement(pmTestingTime);
  }

  /**
   * Set meta-attribute information
   */
  private void setupMetaAttributes() {
    // anMeasures
    anMeasures.addElement(maMeanAccuracy);
    anMeasures.addElement(maClassificationQuality);
    anMeasures.addElement(maWeightedClassifAccuracy);
    anMeasures.addElement(maGenApproxRatio);
    anMeasures.addElement(maGenClassifAccuracy1);
    anMeasures.addElement(maGenClassifQuality1);
    anMeasures.addElement(maGenClassifAccuracy2);
    anMeasures.addElement(maGenClassifQuality2);
    anMeasures.addElement(maGenClassifAccuracy3);
    anMeasures.addElement(maGenClassifQuality3);
    for (int i = 0; i < anMeasures.size(); i++)
      atMeasures.addElement(new Attribute((String)anMeasures.elementAt(i)));
    atNormalization.addElement(new Attribute(maMean));
    atNormalization.addElement(new Attribute(maStandDev));
    atAlgorithms.addElement(new Attribute(maMean));
    atAlgorithms.addElement(new Attribute(maStandDev));
    for (int i = 0; i < algorithmName.size(); i++) {
      String atName = algName((String)algorithmName.elementAt(i));
      atAlgorithms.addElement(new Attribute(atName));
    }
  }

  /**
   * Create empty meta-datsets with attribute information
   */
  private void setupMetaDatasets() {
    mdsCharacter = new Instances(mdnCharacter, atMeasures, 1);
    mdsNormCharacter = new Instances(mdnNormCharacter, atMeasures, 1);
    mdsNormMetricParams = new Instances(mdnNormMetricParams, atNormalization, 1);
    mdsNormPerform = new Instances [mdnPerformances.size()];
    for (int i = 0; i < mdnPerformances.size(); i++)
      mdsNormPerform[i] = new Instances((String)mdnPerformances.elementAt(i), atAlgorithms, 1);
  }

/**
 * Create and configure the objects in charge of save datasets to files.
 * 
 * @param saveMode
 */
  private void setupSavers(int saveMode) {
    /**
     * Set the file address for meta-datasets
     */
    File characterFile = new File(OutputDir, mdnCharacter + ".arff");
    File[] performFile = new File[mdnPerformances.size()];
    for (int i = 0; i < mdnPerformances.size(); i++)
      performFile[i] = new File(OutputDir, (String)mdnPerformances.elementAt(i) + ".arff");
    /**
     * Try to create saver objects for every meta-dataset and configure it
     */
    try {
      characterSaver = new ArffSaver();
      characterSaver.setFile(characterFile);
      characterSaver.setDestination(characterFile);
      characterSaver.setInstances(mdsCharacter);
      characterSaver.setRetrieval(saveMode);
      performSaver = new ArffSaver[mdnPerformances.size()];
      for (int i = 0; i < mdnPerformances.size(); i++) {
        performSaver[i] = new ArffSaver();
        performSaver[i].setFile(performFile[i]);
        performSaver[i].setDestination(performFile[i]);
        performSaver[i].setInstances(mdsNormPerform[i]);
        performSaver[i].setRetrieval(saveMode);
      }
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
  }

  /**
   * Iterate through the directories containing databases processing each one
   */
  private void processBaseDS() {
    File data = new File(DataPath);
    File[] Directories = data.listFiles();
    for (int i = 0; i < Directories.length; i++)
      if (Directories[i].isDirectory())
        processDir(Directories[i]);
    //theSaver.resetOptions();
  }

  /**
   * Select the scheme of cross validation to apply.
   * 
   * @param Dir
   */
  private void processDir(File Dir) {
    if (DataSets5Folds.contains(Dir.getName()))
      applyScheme(Dir, 5);
    else
      applyScheme(Dir, 10);
  }

  /**
   * Apply an scheme of cross validation on a directory containing datasets.
   * 
   * @param Dir
   * @param folds
   */
  private void applyScheme(File Dir, int folds) {
    /**
     * Extract characteristics to non-partitioned dataset and add this 
     * to characteristics meta-dataset.
     */
    File baseDatasetFile = new File(Dir, Dir.getName() + ".dat");
    Instances baseDS = getInstances(baseDatasetFile);
    double[] predictiveMetaAttr = extractMetaAttributes(baseDS);
    mdsCharacter.add(new DenseInstance(1, predictiveMetaAttr));
    /**
     * We need to find the majority and minority classes
     * in order to compute performance measures like recall or precision,
     * then we look into dataset stats.
     */
    int[] nc = baseDS.attributeStats(baseDS.numAttributes()-1).nominalCounts;
    MayorClass = Utils.maxIndex(nc);
    MinorClass = Utils.minIndex(nc);
    /**
     * Data structure to store performances vectors of every algorithm.
     */
    double [][] performAlg = new double[mdnPerformances.size()][algorithmName.size()];
    /**
     * Iterate through the algorithms
     */
    for(int k = 0; k < algorithm.length; k++) {
      /**
       * Now we iterate through the folds running the experiments
       * and then averaging the results to obtain the array of performances.
       */
      File tr, ts;
      double [][] Results = new double[folds][];
      for (int i = 1; i <= folds; i++) {
        tr = new File(Dir, Dir.getName() + "-" + folds + "-" + i + "tra.dat");
        ts = new File(Dir, Dir.getName() + "-" + folds + "-" + i + "tst.dat");
        Results[i - 1] = runExperiment(algorithm[k], tr, ts);
      }
      double [] Performance = averageResults(Results);
      /**
       * In order to save every algorithm result into Performance databases
       * we compose vectors of performance into a matrix.
       */
      for (int i = 0; i < Performance.length; i++)
        performAlg[i][k] = Performance[i];
    }
    /**
     * For every performances measure normalize algorithms results and record 
     * mean and standard deviation at first positions of the array.
     */
    for (int i = 0; i < mdnPerformances.size(); i++) {
      double[] normPerformAlg;    // Array for storing normalized algorithms performance
      normPerformAlg = normalize(performAlg[i]);    // Do normalization
      mdsNormPerform[i].add(new DenseInstance(1, normPerformAlg));  // Add the normalized instance to the dataset
    }
    /**
     * If save mode is incremental then save the new meta instance.
     */
    if (SaveMode == smIncremental)
      try {
        characterSaver.writeIncremental(mdsCharacter.lastInstance());
        characterSaver.getWriter().flush();
        for (int i = 0; i < mdnPerformances.size(); i++) {
          performSaver[i].writeIncremental(mdsNormPerform[i].lastInstance());
          performSaver[i].getWriter().flush();
        }
        } catch (Exception e) {
             System.err.println(e.getMessage());
       }
  }

  /**
   * Normalize an array of doubles subtracting the mean and dividing the result 
   * by the standard deviation. Return the array normalized with the mean 
   * and the stardard deviation in the first and second positions respectively.
   */
  private double[] normalize(double[] data) {
    double[] normData = new double[algParamCount + data.length];
    Stats stats = new Stats();
    for(int i = 0; i < data.length; i++)
      stats.add(data[i]);
    stats.calculateDerived();
    normData[piMean] = stats.mean;
    normData[piStdDev] = stats.stdDev;
    for(int i = 0; i < data.length; i++)
      normData[algParamCount + i] = (data[i] - stats.mean) / stats.stdDev;
    return normData;
  }
  
  /**
   *  Extract the measures characterizing a dataset.
   * 
   * @param data
   * @return
   */
  private double[] extractMetaAttributes(Instances data) {
    double[] measures = null;
    try {
      RoughSet rst = new RoughSet(data);
      measures = new double[atMeasures.size()];
      //measures[anMeasures.indexOf(maMeanAccuracy)] = rst.MeanAccuracy();
      //measures[anMeasures.indexOf(maClassificationQuality)] = rst.ClassificationQuality();
      //measures[anMeasures.indexOf(maWeightedClassifAccuracy)] = rst.WeightedClassifAccuracy();
      //measures[anMeasures.indexOf(maGenApproxRatio)] = rst.GenApproxRatio();
      /*
      measures[anMeasures.indexOf(maGenClassifAccuracy1)] = rst.GenClassifAccuracy1();
      measures[anMeasures.indexOf(maGenClassifQuality1)] = rst.GenClassifQuality1();
      measures[anMeasures.indexOf(maGenClassifAccuracy2)] = rst.GenClassifAccuracy2();
      measures[anMeasures.indexOf(maGenClassifQuality2)] = rst.GenClassifQuality2();
      measures[anMeasures.indexOf(maGenClassifAccuracy3)] = rst.GenClassifAccuracy3();
      measures[anMeasures.indexOf(maGenClassifQuality3)] = rst.GenClassifQuality3();
       */
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
    return measures;
  }

  /**
   * Average the results of a number of experiments that have run in a 
   * cross validation scheme.
   * 
   * @param Results
   * @return
   */
  private double[] averageResults(double[][] Results) {
    double[] Average = new double[mdnPerformances.size()];
    for (int attrIndex = 0; attrIndex < mdnPerformances.size(); attrIndex++) {
      double S = 0;
      for (int ExperimentCount = 0; ExperimentCount < Results.length; ExperimentCount++)
        S += Results[ExperimentCount][attrIndex];
      Average[attrIndex] = (double) S / Results.length;
    }
    return Average;
  }

  /**
   * Open a dataset file an return the dataset.
   * 
   * @param f
   * @return
   */
  private Instances getInstances(File f) {
    Instances I = null;
    try {
      kl.setSource(f);
      I = kl.getDataSet();
      I.setClassIndex(I.numAttributes() - 1);
      kl.reset();
    } catch (Exception e) {
      // For IOException or FileNotFoundException
      e.printStackTrace(System.err);
      System.exit(1);
    }
    return I;
  }

  /**
   * Run a classifier on a train set to build a model and test it in a test set
   * for returning performance measures.
   * 
   * @param scheme
   * @param train
   * @param test
   * @return
   */
  private double[] runExperiment(Classifier scheme, File train, File test) {
    double[] Result = null;
    Instances trainInstances = getInstances(train);
    Instances testInstances = getInstances(test);
    try {
      double trtime, tstime;
      Date t0 = new Date();
      scheme.buildClassifier(trainInstances);
      Date t1 = new Date();
      trtime = t1.getTime() - t0.getTime();
      Evaluation evaluation = new Evaluation(trainInstances);
      t0 = new Date();
      evaluation.evaluateModel(scheme, testInstances);
      t1 = new Date();
      tstime = t1.getTime() - t0.getTime();
      Result = new double[mdnPerformances.size()];
      Result[mdnPerformances.indexOf(pmAccuracy)] = evaluation.pctCorrect();
      Result[mdnPerformances.indexOf(pmKappa)] = evaluation.kappa();
      Result[mdnPerformances.indexOf(pmFMeasure)] = evaluation.fMeasure(MinorClass);
      Result[mdnPerformances.indexOf(pmRecall)] = evaluation.recall(MinorClass);
      Result[mdnPerformances.indexOf(pmPrecision)] = evaluation.precision(MinorClass);
      Result[mdnPerformances.indexOf(pmSpecificity)] = evaluation.trueNegativeRate(MinorClass);
      Result[mdnPerformances.indexOf(pmAUC)] = evaluation.areaUnderROC(MayorClass);
      Result[mdnPerformances.indexOf(pmTrainingTime)] = trtime;
      Result[mdnPerformances.indexOf(pmTestingTime)] = tstime;
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
   return Result;
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
   *  Save meta-datasets in a batch.
   */
  private void saveMetaData() {
    setupSavers(ArffSaver.BATCH);
    try {
      characterSaver.writeBatch();
      //normCharacterSaver.writeBatch();
      for (int i = 0; i < mdnPerformances.size(); i++)
        performSaver[i].writeBatch();
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
  }

  /**
   * Populate de normalized characteristics dataset from applying normalization 
   * equation to characteristics dataset instances. 
   */
  private void normalizeDataCharacter() {
    /**
     * Array of doubles for storing a normalized instance.
     */
    double[] normChar = new double[atMeasures.size()];
    /**
     * Temporarily store mean and standard deviation values 
     * for avoiding recalculation. 
     */
    double[][] normParams = new double[atMeasures.size()][2];
    for(int m = 0; m < atMeasures.size(); m++) {
      normParams[m][piMean] = mdsCharacter.meanOrMode(m);
      normParams[m][piStdDev] = Math.sqrt(mdsCharacter. variance(m));
      mdsNormMetricParams.add(new DenseInstance(1, normParams[m]));
    }
    /**
     * For every instance in characterization dataset apply normalization 
     * and add normalized instance to normalized characterization dataset.
     */
    for(int i = 0; i < mdsCharacter.numInstances(); i++) {
      for(int m = 0; m < atMeasures.size(); m++)
        normChar[m] = (mdsCharacter.instance(i).value(m) - normParams[m][piMean]) / normParams[m][piStdDev];
      mdsNormCharacter.add(new DenseInstance(1, normChar));
    }
  }
  
  /**
   * Save normalized characterization dataset to file.
   */
  private void saveNormDataCharacter() {
    File normCharacterFile = new File(OutputDir, mdnNormCharacter + ".arff");
    File normMetricParamsFile = new File(OutputDir, mdnNormMetricParams + ".arff");
    try {
      normCharacterSaver = new ArffSaver();
      normCharacterSaver.setFile(normCharacterFile);
      normCharacterSaver.setDestination(normCharacterFile);
      normCharacterSaver.setInstances(mdsNormCharacter);
      normCharacterSaver.setRetrieval(ArffSaver.BATCH);
      normCharacterSaver.writeBatch();
      normMetricParamsSaver = new ArffSaver();
      normMetricParamsSaver.setFile(normMetricParamsFile);
      normMetricParamsSaver.setDestination(normMetricParamsFile);
      normMetricParamsSaver.setInstances(mdsNormMetricParams);
      normMetricParamsSaver.setRetrieval(ArffSaver.BATCH);
      normMetricParamsSaver.writeBatch();
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
  }
  
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
      Experimenter Exp1 = new Experimenter();
    }

}
