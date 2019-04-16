/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.io.*;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.Filter;

/**
 *
 * @author danels
 */
public class Assembler {

  public static String Destination = MetaDataCompiler.MetaDataPath + "/Assambles";

  public Assembler() {
    File charFile = new File(MetaDataCompiler.MetaDataPath, "character.arff");
    Instances dsChar = getInstances(charFile);
    File testAccFile = new File(MetaDataCompiler.MetaDataPath, "TestingAccuracy.arff");
    Instances dsTestAcc = getInstances(testAccFile);
    File trainAccFile = new File(MetaDataCompiler.MetaDataPath, "TrainingAccuracy.arff");
    Instances dsTrainAcc = getInstances(trainAccFile);
    Instances target = new Instances(dsChar);
    Remove removeFilter = new Remove();
    removeFilter.setAttributeIndices("8");
    removeFilter.setInvertSelection(true);
    try {
      removeFilter.setInputFormat(dsTestAcc);
      Instances atFiltered = Filter.useFilter(dsTestAcc, removeFilter);
      atFiltered.renameAttribute(0, "accuracyTesting");
      target = Instances.mergeInstances(dsChar, atFiltered);
      removeFilter.setInputFormat(dsTrainAcc);
      atFiltered = Filter.useFilter(dsTrainAcc, removeFilter);
      atFiltered.renameAttribute(0, "accuracyTraining");
      target = Instances.mergeInstances(target, atFiltered);
    } catch (Exception e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
    target.setRelationName("C4.5-accuracy");
    saveDataset(target);
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
   * Save current prediction dataset to an arff file.
   */
  private void saveDataset(Instances data) {
    saveDataset(data, data.relationName());
  }

  private void saveDataset(Instances data, String fileName) {
    File file = new File(Destination, fileName + ".arff");
    ArffSaver saver = new ArffSaver();
    try {
      saver.setFile(file);
      saver.setDestination(file);
      saver.setInstances(data);
      saver.setRetrieval(ArffSaver.BATCH);
      saver.writeBatch();
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
  }

  /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      Assembler A = new Assembler();
    }

}
