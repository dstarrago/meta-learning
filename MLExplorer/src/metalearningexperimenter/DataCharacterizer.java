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
import weka.core.converters.ArffSaver;

/**
 *
 * @author Danel
 */
public class DataCharacterizer {

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
  public static String DataPath = "C:/Users/Danel/Documents/Investigación/SOFTCOMPUTING/Tesis/LAB/DATA/Feb09";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/Feb09";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/Feb10";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/All";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/mejoresC45";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/YinYan";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/RosaBúlgara";     //concept = new MosaicConcept(10, 500, 500, 4, 4);
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/Chess";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/Spots";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/Elipse";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/Rombo";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/Sinuous";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/Lineal";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/Mosaics 99";
  //public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/mezcla1";

  /**
   *  Output directory
   */
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/Feb09Fixed";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/Feb10Fixed";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/AllCharFixed";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/mejoresC45";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/YinYan";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/RosaBúlgara";  //concept = new MosaicConcept(10, 500, 500, 4, 4);
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/Chess";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/Spots";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/Elipse";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/Rombo";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/Sinuous";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/Lineal";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/Feb09Kappa";
  //public static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/Mosaics 99";
  public static String MetaDataPath = "C:/Users/Danel/Documents/Investigación/SOFTCOMPUTING/Tesis/LAB/ANALYSIS/Jun2012";

  /**
   *  Meta-dataset names
   */
  //static final String fileName = "Feb09Fixed";
  //static final String fileName = "Feb10Fixed";
  //static final String fileName = "AllCharFixed";
  //static final String fileName = "mejoresC45";
  //static final String fileName = "YinYan";
  //static final String fileName = "RosaBúlgara";   //concept = new MosaicConcept(10, 500, 500, 4, 4);
  //static final String fileName = "Chess";
  //static final String fileName = "Spots";         //concept = new MosaicConcept(3, 500, 500, 4, 4);
  //static final String fileName = "Elipse";
  //static final String fileName = "Rombo";
  //static final String fileName = "Sinuous";
  //static final String fileName = "Lineal";
  //static final String fileName = "Feb09Kappa";
  //static final String fileName = "Mosaics 99";
  static final String fileName = "Jun2012";

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
  private Classifier algorithm;
  String algorithmName = "weka.classifiers.trees.J48";

  /**
   *  Meta-datasets
   */
  private Instances mdsCharacter;

  /**
   *  Objects to write meta-dataset files in Arff format
   */
  private ArffSaver characterSaver;

  /**
   * Object to write log file.
   */
  private PrintWriter compLogWriter;

  /**
   * A counter for total compilation time
   */
  long compTime;

  /**
   *
   */
  public final int patronRuleSupport = 5;

  private Measures measures = new Measures();

  /**
   * Constructor of the class
   */
  private DataCharacterizer() {
    beginCompilationLog();
    logMeasures();
    setupAlgorithm();
    setupMetaDatasets();
    if (SaveMode == smIncremental) setupSavers(ArffSaver.INCREMENTAL);
    processBaseDS();
    if (SaveMode == smBatch) saveMetaData();
    endCompilationLog();
  }

  private void setupAlgorithm() {
    try {
      algorithm = AbstractClassifier.forName(algorithmName, null);
      } catch (Exception e) {
           System.err.println(e.getMessage());
    }
  }

  /**
   * Set meta-attribute information
   */
  private void logMeasures() {
    compLogWriter.print("\n");
    compLogWriter.print("Data characterization measures: \n");
    for (int i = 1; i < measures.count(); i++) {
      compLogWriter.print(measures.name(i) + "\n");
    }
  }

  /**
   * Create empty meta-datasets with attribute information
   */
  private void setupMetaDatasets() {
    mdsCharacter = new Instances(fileName, measures.struct(), 1);
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
    File characterFile = new File(MetaDataPath, fileName + ".arff");
    /**
     * Try to create saver objects for every meta-dataset and configure it
     */
    try {
      characterSaver = new ArffSaver();
      characterSaver.setFile(characterFile);
      characterSaver.setDestination(characterFile);
      characterSaver.setInstances(mdsCharacter);
      characterSaver.setRetrieval(saveMode);
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
    definePatronRuleParams();
    //theSaver.resetOptions();
  }

  private void definePatronRuleParams() {
    mdsCharacter.sort(measures.indexOf(Measures.maTestAccuracy));
    meanPatronRule();
    percentilPatronRule(5);
    percentilPatronRule(10);
    percentilPatronRule(25);
    percentilPatronRule(50);
}

  private void percentilPatronRule(int support) {
    double acc = mdsCharacter.attributeStats(measures.indexOf(Measures.maTestAccuracy)).numericStats.mean;
    int C = mdsCharacter.numInstances() * support /100;
    double Sn = 0, Sp = 0;
    for (int i = 0; i < C; i++) {
      Sn += mdsCharacter.instance(i).value(measures.indexOf(Measures.maTestAccuracy));
      Sp += mdsCharacter.instance(mdsCharacter.numInstances() - i - 1).value(measures.indexOf(Measures.maTestAccuracy));
    }
    Sn /= C;
    Sp /= C;
    compLogWriter.print("\n");
    compLogWriter.print("*** For Patron Rule Support ");
    compLogWriter.print(support + "% ***");
    compLogWriter.print("\n");
    compLogWriter.print("Positive Rule Test accuracy: ");
    compLogWriter.print(Sp + "\n");
    compLogWriter.print("Negative Rule Test accuracy: ");
    compLogWriter.print(Sn + "\n");
    compLogWriter.print("Separation: ");
    compLogWriter.print(Sp - Sn);
    compLogWriter.print("\n");
    compLogWriter.print("Positive Difference: ");
    compLogWriter.print(Sp - acc);
    compLogWriter.print("\n");
    compLogWriter.print("Negative Difference: ");
    compLogWriter.print(Sn - acc);
    compLogWriter.print("\n");
    compLogWriter.flush();
  }

  private void meanPatronRule() {
    double acc = mdsCharacter.attributeStats(measures.indexOf(Measures.maTestAccuracy)).numericStats.mean;
    double stddev = mdsCharacter.attributeStats(measures.indexOf(Measures.maTestAccuracy)).numericStats.stdDev;
    double Sn = 0, Sp = 0;
    int Cn = 0, Cp = 0;
    for (int i = 0; i < mdsCharacter.numInstances(); i++) {
      double v = mdsCharacter.instance(i).value(measures.indexOf(Measures.maTestAccuracy));
      if (v > acc) {
        Cp++;
        Sp += v;
      }
      if (v < acc) {
        Cn++;
        Sn += v;
      }
    }
    if (Cp == 0)
      Sp = Sn = acc;
    else {
      Sn /= Cn;
      Sp /= Cp;
    }
    compLogWriter.print("\n");
    compLogWriter.print("Mean and StdDev : ");
    compLogWriter.print(acc + ", " + stddev + "\n");
    compLogWriter.print("\n");
    compLogWriter.print("*** For Mean Patron Rule ***");
    compLogWriter.print("\n");
    compLogWriter.print("Positive Rule Test accuracy: ");
    compLogWriter.print(Sp + "\n");
    compLogWriter.print("Negative Rule Test accuracy: ");
    compLogWriter.print(Sn + "\n");
    compLogWriter.print("Separation: ");
    compLogWriter.print(Sp - Sn);
    compLogWriter.print("\n");
    compLogWriter.print("Positive Difference: ");
    compLogWriter.print(Sp - acc);
    compLogWriter.print("\n");
    compLogWriter.print("Negative Difference: ");
    compLogWriter.print(Sn - acc);
    compLogWriter.print("\n");
    compLogWriter.flush();
  }

  /**
   * Select the scheme of cross validation to apply.
   * 
   * @param Dir
   */
  private void processDir(File Dir) {
    /**
     * Extract characteristics to non-partitioned dataset and add this
     * to characteristics meta-dataset.
     */
    File baseDatasetFile = new File(Dir, Dir.getName() + ".dat");
    Instances baseDS = getInstances(baseDatasetFile);
    double[] characterPerformancesVector = measures.extractMetaAttributes(baseDS);

    /**
     * 5  fold cross validation for datasets with 100 or less instances
     * 10 fold cross validation for datasets with more than 100 instances
     */
    int folds = (baseDS.numInstances() <= 100)? 5 : 10;
    
    /**
     * Now we iterate through the folds running the experiments
     * and then averaging the results to obtain the array of performances.
     */
    File tr, ts;
    double [][] Results = new double[folds][];
    for (int i = 1; i <= folds; i++) {
      tr = new File(Dir, Dir.getName() + "-" + folds + "-" + i + "tra.dat");
      ts = new File(Dir, Dir.getName() + "-" + folds + "-" + i + "tst.dat");
      Results[i - 1] = runExperiment(algorithm, tr, ts);
    }
    double [] Performance = averageResults(Results);
    /**
     * In order to save every algorithm result into Performance databases
     * we compouse vectors of performance into a matrix.
     */
    for (int i = 0; i < measures.numPerformance(); i++)
      characterPerformancesVector[measures.numCharacter() + i] = Performance[i];

    mdsCharacter.add(new DenseInstance(1, characterPerformancesVector));
    mdsCharacter.lastInstance().setValue(measures.indexOf(Measures.maProblemID), Dir.getName());
    //mdsCharacter.lastInstance().setValue(measures.indexOf(Measures.maDataDescription), baseDS.toSummaryString());
    /**
     * If save mode is incremental then save the new meta instance.
     */
    if (SaveMode == smIncremental)
      try {
        characterSaver.writeIncremental(mdsCharacter.lastInstance());
        characterSaver.getWriter().flush();
        } catch (Exception e) {
             System.err.println(e.getMessage());
       }
    /**
     * Logging
     */
    compLogWriter.print(mdsCharacter.numInstances() + ": " + folds +
            "CV to " + baseDatasetFile.getName() + "\n");
    compLogWriter.flush();
}

  /**
   * Average the results of a number of experiments that have run in a
   * cross validation scheme.
   *
   * @param Results
   * @return
   */
  private double[] averageResults(double[][] Results) {
    double[] Average = new double[measures.numPerformance()];
    for (int attrIndex = 0; attrIndex < measures.numPerformance(); attrIndex++) {
      double S = 0;
      for (int ExperimentCount = 0; ExperimentCount < Results.length; ExperimentCount++)
        S += Results[ExperimentCount][attrIndex];
      Average[attrIndex] = (double) S / Results.length;
    }
    return Average;
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
      scheme.buildClassifier(trainInstances);
      Result = new double[measures.numPerformance()];
      Evaluation trainEvaluation = new Evaluation(trainInstances);
      Evaluation testEvaluation = new Evaluation(testInstances);
      trainEvaluation.evaluateModel(scheme, trainInstances);
      testEvaluation.evaluateModel(scheme, testInstances);
      Result[measures.indexOf(Measures.maTrainAccuracy) - measures.numCharacter()] = trainEvaluation.pctCorrect();
      Result[measures.indexOf(Measures.maTestAccuracy) - measures.numCharacter()] = testEvaluation.pctCorrect();
      Result[measures.indexOf(Measures.maTrainKappa) - measures.numCharacter()] = trainEvaluation.kappa();
      Result[measures.indexOf(Measures.maTestKappa) - measures.numCharacter()] = testEvaluation.kappa();
      Result[measures.indexOf(Measures.maTrainAUC) - measures.numCharacter()] = trainEvaluation.areaUnderROC(0);
      Result[measures.indexOf(Measures.maTestAUC) - measures.numCharacter()] = testEvaluation.areaUnderROC(0);
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
   return Result;
  }


  /**
   * Open a dataset file in ARFF format an return the dataset.
   * 
   * @param f
   * @return
   */
  public static Instances getInstances(File f) {
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
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
  }

  private void beginCompilationLog() {
    Date currentTime = new Date();
    compTime = currentTime.getTime();
    File metaDataFile = new File(MetaDataPath);
    if (!metaDataFile.exists()) metaDataFile.mkdir();
    File compLogFile = new File(metaDataFile, fileName + "_log.rtf");
    BufferedWriter writer;
    try {
      OutputStream output = new FileOutputStream(compLogFile);
      writer = new BufferedWriter(new OutputStreamWriter(output));
      compLogWriter = new PrintWriter(writer);
      compLogWriter.print("Dataset Characterization System for Data Complexity" + "\n");
      compLogWriter.print("\n");
      compLogWriter.print("Starting time: " + currentTime + "\n");
      compLogWriter.print("\n");
      compLogWriter.print("Base-datasets directory:\n");
      compLogWriter.print(DataPath + "\n");
      compLogWriter.print("\n");
      compLogWriter.print("Meta-datasets directory\n");
      compLogWriter.print(MetaDataPath + "\n");
      compLogWriter.print("\n");
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
      DataCharacterizer Exp1 = new DataCharacterizer();
    }

}
