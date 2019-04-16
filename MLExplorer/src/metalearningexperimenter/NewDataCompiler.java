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
import RoughSet.RoughSet;
import RoughSet.RSTmeasures;

/**
 *
 * @author Danel
 */
public class NewDataCompiler {

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
  static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/Data/feb09";

  /**
   *  Output directory
   */
  static String MetaDataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/Data/validation";

  /**
   *  Dataset characterization meta-attribute names
   */
  private final String maClassificationQuality = "ClassificationQuality";
  private final String maGenApproxRatio = "GenApproxRatio";
  private final String maGenClassifAccuracy1 = "GenClassifAccuracy1";
  private final String maGenClassifQuality1 = "GenClassifQuality1";
  private final String maGenClassifAccuracy2 = "GenClassifAccuracy2";
  private final String maGenClassifQuality2 = "GenClassifQuality2";
  private final String maGenClassifAccuracy3 = "GenClassifAccuracy3";
  private final String maGenClassifQuality3 = "GenClassifQuality3";
  private final String PerformanceMeasure = "Accuracy";

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
   *  Meta-dataset names
   */
  static final String mdnCharacter = "comprobando";

  /**
   *  Objects to write meta-dataset files in Arff format
   */
  private ArffSaver characterSaver;

  /**
   *  Vectors holding meta-attributes structures
   */
  private FastVector atMeasures = new FastVector();

  /**
   * List of characteristics(measures) names
   */
  private FastVector anMeasures = new FastVector();
  
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
  private NewDataCompiler() {
    beginCompilationLog();
    setupMetaAttributes();
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
  private void setupMetaAttributes() {
    compLogWriter.print("/n");
    compLogWriter.print("Data characterization metrics: /n");
    anMeasures.addElement(maClassificationQuality);  
    anMeasures.addElement(maGenApproxRatio);      
    anMeasures.addElement(maGenClassifAccuracy1); 
    anMeasures.addElement(maGenClassifQuality1);   
    anMeasures.addElement(maGenClassifAccuracy2);  
    anMeasures.addElement(maGenClassifQuality2);   
    anMeasures.addElement(maGenClassifAccuracy3);  
    anMeasures.addElement(maGenClassifQuality3);   
    anMeasures.addElement(PerformanceMeasure);
    for (int i = 0; i < anMeasures.size(); i++) {
      String s = (String)anMeasures.elementAt(i);
      atMeasures.addElement(new Attribute(s));
      compLogWriter.print(s + "/n");
    }
  }

  /**
   * Create empty meta-datsets with attribute information
   */
  private void setupMetaDatasets() {
    mdsCharacter = new Instances(mdnCharacter, atMeasures, 1);
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
    compLogWriter.print("/n");
    compLogWriter.print("Processed datasets:/n");
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
    /**
     * Extract characteristics to non-partitioned dataset and add this
     * to characteristics meta-dataset.
     */
    File baseDatasetFile = new File(Dir, Dir.getName() + ".dat");
    Instances baseDS = getInstances(baseDatasetFile);
    double[] predictiveMetaAttr = extractMetaAttributes(baseDS);
      try {
        algorithm.buildClassifier(baseDS);
        Evaluation evaluation = new Evaluation(baseDS);
        evaluation.evaluateModel(algorithm, baseDS);
        predictiveMetaAttr[anMeasures.indexOf(PerformanceMeasure)] = evaluation.pctCorrect();
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
    mdsCharacter.add(new DenseInstance(1, predictiveMetaAttr));
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
      measures[anMeasures.indexOf(maClassificationQuality)] = m.ClassificationQuality();
      measures[anMeasures.indexOf(maGenApproxRatio)] = m.GenApproxRatio();
      measures[anMeasures.indexOf(maGenClassifAccuracy1)] = m.GenClassifAccuracy1();
      measures[anMeasures.indexOf(maGenClassifQuality1)] = m.GenClassifQuality1();
      measures[anMeasures.indexOf(maGenClassifAccuracy2)] = m.GenClassifAccuracy2();
      measures[anMeasures.indexOf(maGenClassifQuality2)] = m.GenClassifQuality2();
      measures[anMeasures.indexOf(maGenClassifAccuracy3)] = m.GenClassifAccuracy3();
      measures[anMeasures.indexOf(maGenClassifQuality3)] = m.GenClassifQuality3();
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
    return measures;
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
    File compLogFile = new File(metaDataFile, "compilation.txt");
    BufferedWriter writer;
    try {
      OutputStream output = new FileOutputStream(compLogFile);
      writer = new BufferedWriter(new OutputStreamWriter(output));
      compLogWriter = new PrintWriter(writer);
      compLogWriter.print("Dataset Characterization System for Data Complexity" + "/n");
      compLogWriter.print("/n");
      compLogWriter.print("Starting time: " + currentTime + "/n");
      compLogWriter.print("/n");
      compLogWriter.print("Base-datasets directory:/n");
      compLogWriter.print(DataPath + "/n");
      compLogWriter.print("/n");
      compLogWriter.print("Meta-datasets directory/n");
      compLogWriter.print(MetaDataPath + "/n");
    } catch (Exception e) {
         System.err.println(e.getMessage());
   }
  }

  private void endCompilationLog() {
    Date currentTime = new Date();
    compTime = (currentTime.getTime() - compTime) / 60000;
    compLogWriter.print("/n");
    compLogWriter.print("Ending time: " + currentTime + "/n");
    compLogWriter.print("/n");
    compLogWriter.print("Compilation time: " + compTime + " minutes /n");
    compLogWriter.flush();
  }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      NewDataCompiler Exp1 = new NewDataCompiler();
    }

}
