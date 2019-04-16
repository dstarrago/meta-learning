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
import RoughSet.RSTmeasures;

/**
 *
 * @author Danel
 */
public class MetaDataCompiler {

  /**
   * Alternative saving modes
   */
  private final int smIncremental = 1;
  private final int smBatch = 2;

  /**
   *  *************** Experiment settings ********************
   */

  /**
   *  Database path
   */
  static String DataPath = "D:/Users/Danel/ACADÉMICOS/INVESTIGACIÓN/SOFTCOMPUTING/DATOS/feb09";

  /**
   *  Output directory
   */
  static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Tesis/Data/Metadata1";

  /**
   *  Dataset characterization meta-attribute names
   */
  private final String maMeanAccuracy = "MeanAccuracy";
  private final String maClassificationQuality = "ClassificationQuality";
  //private final String maWeightedClassifAccuracy = "WeightedClassifAccuracy";
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
  static final String pmTrainingAccuracy = "TrainingAccuracy";
  static final String pmTestingAccuracy = "TestingAccuracy";
  static final String pmKappa = "Kappa";
  static final String pmFMeasure = "FMeasure";
  static final String pmRecall = "Recall";
  static final String pmPrecision = "Precision";
  static final String pmSpecificity = "Specificity";
  static final String pmAUC = "AUC";
  //static final String pmTrainingTime = "TrainingTime";
  //static final String pmTestingTime = "TestingTime";

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
  private Classifier[] algorithm;

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
  private Instances [] mdsPerform;
  private Instances [] mdsNormPerform;
  private Instances [] mdsNormParamsPerform;

  /**
   *  Meta-dataset names
   */
  static final String mdnCharacter = "character";
  static final String mdnNormCharacter = "normCharacter";
  static final String mdnNormMetricParams = "normMetricParams";
  static final String mdnNormParamsPrefix = "normParams";   // dataset name prefix
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
  private ArffSaver[] normPerformSaver;
  private ArffSaver[] algParamsSaver;

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
   * Object to write log file.
   */
  private PrintWriter compLogWriter;

  /**
   * A counter for total compilation time
   */
  long compTime;

  /**
   * Constructor of the class
   */
  private MetaDataCompiler() {
    beginCompilationLog();
    read5CVlist();
    setupAlgorithms();
    setupMetaAttributes();
    setupPerformanceMeasures();
    setupMetaDatasets();
    if (SaveMode == smIncremental) setupSavers(ArffSaver.INCREMENTAL);
    processBaseDS();
    normalizeDataCharacter();
    saveNormDataCharacter();
    if (SaveMode == smBatch) saveMetaData();
    endCompilationLog();
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
    compLogWriter.print("\n");
    compLogWriter.print("Learning Algorithms:" + "\n");
    try {
      br = new BufferedReader(new FileReader(filesAlg));
      algorithmName = new FastVector();
      String s = br.readLine();
      while (s != null) {
        algorithmName.addElement(s);
        compLogWriter.print(s + "\n");
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
    mdnPerformances.addElement(pmTrainingAccuracy);
    mdnPerformances.addElement(pmTestingAccuracy);
    //mdnPerformances.addElement(pmKappa);
    //mdnPerformances.addElement(pmFMeasure);
    //mdnPerformances.addElement(pmRecall);
    //mdnPerformances.addElement(pmPrecision);
    //mdnPerformances.addElement(pmSpecificity);
    //mdnPerformances.addElement(pmAUC);

    //mdnPerformances.addElement(pmTrainingTime);
    //mdnPerformances.addElement(pmTestingTime);
    compLogWriter.print("\n");
    compLogWriter.print("Classification performance measures:\n");
    for (int i = 0; i < mdnPerformances.size(); i++)
      compLogWriter.print(mdnPerformances.elementAt(i) + "\n");
  }

  /**
   * Set meta-attribute information
   */
  private void setupMetaAttributes() {
    compLogWriter.print("\n");
    compLogWriter.print("Data characterization metrics: \n");
    //anMeasures.addElement(maMeanAccuracy);
    anMeasures.addElement(maClassificationQuality);  // used by Yaile 
    //anMeasures.addElement(maWeightedClassifAccuracy);
    anMeasures.addElement(maGenApproxRatio);      // used and introduced by Yaile
    anMeasures.addElement(maGenClassifAccuracy1);  // used and introduced by Yaile
    anMeasures.addElement(maGenClassifQuality1);   // used and introduced by Yaile
    anMeasures.addElement(maGenClassifAccuracy2);  // used and introduced by Yaile
    anMeasures.addElement(maGenClassifQuality2);   // used and introduced by Yaile
    anMeasures.addElement(maGenClassifAccuracy3);  // used and introduced by Yaile
    anMeasures.addElement(maGenClassifQuality3);   // used and introduced by Yaile
    for (int i = 0; i < anMeasures.size(); i++) {
      String s = (String)anMeasures.elementAt(i);
      atMeasures.addElement(new Attribute(s));
      compLogWriter.print(s + "\n");
    }
    atNormalization.addElement(new Attribute(maMean));
    atNormalization.addElement(new Attribute(maStandDev));
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
    mdsPerform = new Instances [mdnPerformances.size()];
    mdsNormPerform = new Instances [mdnPerformances.size()];
    mdsNormParamsPerform = new Instances [mdnPerformances.size()];
    for (int i = 0; i < mdnPerformances.size(); i++) {
      String pn = (String)mdnPerformances.elementAt(i);
      mdsPerform[i] = new Instances(pn, atAlgorithms, 1);
      mdsNormPerform[i] = new Instances("norm " + pn, atAlgorithms, 1);
      mdsNormParamsPerform[i] = new Instances(mdnNormParamsPrefix + pn, atNormalization, 1);
    }
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
    File characterFile = new File(MetaDataPath, mdnCharacter + ".arff");
    File[] performFile = new File[mdnPerformances.size()];
    File[] normPerformFile = new File[mdnPerformances.size()];
    File[] algParamFile = new File[mdnPerformances.size()];
    for (int i = 0; i < mdnPerformances.size(); i++) {
      performFile[i] = new File(MetaDataPath, (String)mdnPerformances.elementAt(i) + ".arff");
      normPerformFile[i] = new File(MetaDataPath, "norm" + (String)mdnPerformances.elementAt(i) + ".arff");
      algParamFile[i] = new File(MetaDataPath, mdnNormParamsPrefix + (String)mdnPerformances.elementAt(i) + ".arff");
    }
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
      normPerformSaver = new ArffSaver[mdnPerformances.size()];
      algParamsSaver = new ArffSaver[mdnPerformances.size()];
      for (int i = 0; i < mdnPerformances.size(); i++) {
        performSaver[i] = new ArffSaver();
        performSaver[i].setFile(performFile[i]);
        performSaver[i].setDestination(performFile[i]);
        performSaver[i].setInstances(mdsPerform[i]);
        performSaver[i].setRetrieval(saveMode);
        normPerformSaver[i] = new ArffSaver();
        normPerformSaver[i].setFile(normPerformFile[i]);
        normPerformSaver[i].setDestination(normPerformFile[i]);
        normPerformSaver[i].setInstances(mdsNormPerform[i]);
        normPerformSaver[i].setRetrieval(saveMode);
        algParamsSaver[i] = new ArffSaver();
        algParamsSaver[i].setFile(algParamFile[i]);
        algParamsSaver[i].setDestination(algParamFile[i]);
        algParamsSaver[i].setInstances(mdsNormParamsPerform[i]);
        algParamsSaver[i].setRetrieval(saveMode);
      }
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
  }

  /**
   * Iterate through the directories containing databases processing each one
   */
  private void processBaseDS() {
    /**
     * Logging
     */
    compLogWriter.print("\n");
    compLogWriter.print("Processed datasets:\n");
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
       * we compouse vectors of performance into a matrix.
       */
      for (int i = 0; i < Performance.length; i++)
        performAlg[i][k] = Performance[i];
    }
    /**
     * For every performances measure normalize algorithms results. Parameters
     * of normalization (mean and standard deviation) are collected. 
     */
    for (int i = 0; i < mdnPerformances.size(); i++) {
      Normalization normPerform = new Normalization(performAlg[i]);
      //normParams = normalizePerformance(performAlg[i]);    // Do normalization
      /**
       * Add normalized instance and normalization parameters to their 
       * respectives datasets.
       */
      mdsPerform[i].add(new DenseInstance(1, performAlg[i])); 
      mdsNormPerform[i].add(new DenseInstance(1, normPerform.normData));
      mdsNormParamsPerform[i].add(new DenseInstance(1, normPerform.params));
    }
    /**
     * If save mode is incremental then save the new meta instance.
     */
    if (SaveMode == smIncremental)
      try {
        characterSaver.writeIncremental(mdsCharacter.lastInstance());
        characterSaver.getWriter().flush();
        for (int i = 0; i < mdnPerformances.size(); i++) {
          performSaver[i].writeIncremental(mdsPerform[i].lastInstance());
          performSaver[i].getWriter().flush();
          normPerformSaver[i].writeIncremental(mdsNormPerform[i].lastInstance());
          normPerformSaver[i].getWriter().flush();
          algParamsSaver[i].writeIncremental(mdsNormParamsPerform[i].lastInstance());
          algParamsSaver[i].getWriter().flush();
        }
        } catch (Exception e) {
             System.err.println(e.getMessage());
       }
    /**
     * Logging
     */
    compLogWriter.print(mdsNormPerform[0].numInstances() + ": " + folds +
            "CV to " + baseDatasetFile.getName() + "\n");
    compLogWriter.flush();
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
      RSTmeasures m = new RSTmeasures(rst);
      measures = new double[atMeasures.size()];
      //measures[anMeasures.indexOf(maMeanAccuracy)] = rst.MeanAccuracy();
      measures[anMeasures.indexOf(maClassificationQuality)] = m.ClassificationQuality();
      //measures[anMeasures.indexOf(maWeightedClassifAccuracy)] = rst.WeightedClassifAccuracy();
      measures[anMeasures.indexOf(maGenApproxRatio)] = m.GenApproxRatio();
      /*
      measures[anMeasures.indexOf(maGenClassifAccuracy1)] = m.GenClassifAccuracy1();
      measures[anMeasures.indexOf(maGenClassifQuality1)] = m.GenClassifQuality1();
      measures[anMeasures.indexOf(maGenClassifAccuracy2)] = m.GenClassifAccuracy2();
      measures[anMeasures.indexOf(maGenClassifQuality2)] = m.GenClassifQuality2();
      measures[anMeasures.indexOf(maGenClassifAccuracy3)] = m.GenClassifAccuracy3();
      measures[anMeasures.indexOf(maGenClassifQuality3)] = m.GenClassifQuality3();
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
   * Open a dataset file in Keel format an return the dataset.
   * 
   * @param f
   * @return
   */
  private Instances getInstances(File f) {
    Instances I = null;
    ArffLoader kl = new ArffLoader();
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
      //double trtime, tstime;
      //Date t0 = new Date();
      scheme.buildClassifier(trainInstances);
      //Date t1 = new Date();
      //trtime = t1.getTime() - t0.getTime();
      Result = new double[mdnPerformances.size()];
      Evaluation trainEvaluation = new Evaluation(trainInstances);
      Evaluation testEvaluation = new Evaluation(testInstances);
      //t0 = new Date();
      trainEvaluation.evaluateModel(scheme, trainInstances);
      testEvaluation.evaluateModel(scheme, testInstances);
      //t1 = new Date();
      //tstime = t1.getTime() - t0.getTime();
      Result[mdnPerformances.indexOf(pmTrainingAccuracy)] = trainEvaluation.pctCorrect();
      Result[mdnPerformances.indexOf(pmTestingAccuracy)] = testEvaluation.pctCorrect();
      //Result[mdnPerformances.indexOf(pmKappa)] = testEvaluation.kappa();
      //Result[mdnPerformances.indexOf(pmFMeasure)] = testEvaluation.fMeasure(MinorClass);
      //Result[mdnPerformances.indexOf(pmRecall)] = testEvaluation.recall(MinorClass);
      //Result[mdnPerformances.indexOf(pmPrecision)] = testEvaluation.precision(MinorClass);
      //Result[mdnPerformances.indexOf(pmSpecificity)] = testEvaluation.trueNegativeRate(MinorClass);
      //Result[mdnPerformances.indexOf(pmAUC)] = testEvaluation.areaUnderROC(MayorClass);

      //Result[mdnPerformances.indexOf(pmTrainingTime)] = trtime;
      //Result[mdnPerformances.indexOf(pmTestingTime)] = tstime;
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
   *  Save meta-datsets in a batch.
   */
  private void saveMetaData() {
    setupSavers(ArffSaver.BATCH);
    try {
      characterSaver.writeBatch();
      for (int i = 0; i < mdnPerformances.size(); i++) {
        normPerformSaver[i].writeBatch();
        algParamsSaver[i].writeBatch();
      }
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
     * and add normalized instance to normalized characterization datset.
     */
    for(int i = 0; i < mdsCharacter.numInstances(); i++) {
      double[] normChar = new double[atMeasures.size()];
      for(int m = 0; m < atMeasures.size(); m++)
        normChar[m] = (mdsCharacter.instance(i).value(m) - normParams[m][piMean]) / normParams[m][piStdDev];
      mdsNormCharacter.add(new DenseInstance(1, normChar));
    }
  }
  
  /**
   * Save normalized characterization dataset to file.
   */
  private void saveNormDataCharacter() {
    File normCharacterFile = new File(MetaDataPath, mdnNormCharacter + ".arff");
    File normMetricParamsFile = new File(MetaDataPath, mdnNormMetricParams + ".arff");
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

  private void beginCompilationLog() {
    Date currentTime = new Date();
    compTime = currentTime.getTime();
    File metaDataFile = new File(MetaDataPath);
    if (!metaDataFile.exists()) metaDataFile.mkdir();
    File compLogFile = new File(metaDataFile, "compilation.txt");
    BufferedWriter writer;
    try {
      OutputStream output = new FileOutputStream(compLogFile);
      writer = new BufferedWriter(new OutputStreamWriter(output));
      compLogWriter = new PrintWriter(writer);
      compLogWriter.print("Dataset Characterization System for Meta-Learning" + "\n");
      compLogWriter.print("\n");
      compLogWriter.print("Starting time: " + currentTime + "\n");
      compLogWriter.print("\n");
      compLogWriter.print("Base-datasets directory:\n");
      compLogWriter.print(DataPath + "\n");
      compLogWriter.print("\n");
      compLogWriter.print("Meta-datasets directory\n");
      compLogWriter.print(MetaDataPath + "\n");
    } catch (Exception e) {
         System.err.println(e.getMessage());
   }
  }

  private void endCompilationLog() {
    Date currentTime = new Date();
    compTime = (currentTime.getTime() - compTime) / 60000;
    compLogWriter.print("\n");
    compLogWriter.print("Ending time: " + currentTime + "\n");
    compLogWriter.print("\n");
    compLogWriter.print("Compilation time: " + compTime + " minutes \n");
    compLogWriter.flush();
  }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      MetaDataCompiler Exp1 = new MetaDataCompiler();
    }

}
