/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.io.*;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.experiment.Stats;

/**
 *
 * @author danels
 */
public class Summarizer {
  Stats numAttr = new Stats();
  Stats numInst = new Stats();
  Stats numClasses = new Stats();

  public Summarizer() {
    processBaseDS();
    writeStats();
  }
  
  /**
   * Iterate through the directories containing databases processing each one
   */
  private void processBaseDS() {
    File data = new File(MetaDataCompiler.DataPath);
    File[] Directories = data.listFiles();
    for (int i = 0; i < Directories.length; i++)
      if (Directories[i].isDirectory())
        processDir(Directories[i]);
  }

  private void processDir(File Dir) {
    /**
     * Extract characteristics to non-partitioned dataset and add this 
     * to characteristics meta-dataset.
     */
    File baseDatasetFile = new File(Dir, Dir.getName() + ".dat");
    Instances baseDS = getInstances(baseDatasetFile);
    numAttr.add(baseDS.numAttributes());
    numInst.add(baseDS.numInstances());
    numClasses.add(baseDS.numClasses());
  }

  private void writeStats() {
    numAttr.calculateDerived();
    numInst.calculateDerived();
    numClasses.calculateDerived();
    File summaryFile = new File(MetaDataCompiler.MetaDataPath, "Summary.txt");
    BufferedWriter writer;
    PrintWriter summaryWriter;
    try {
      OutputStream output = new FileOutputStream(summaryFile);
      writer = new BufferedWriter(new OutputStreamWriter(output));
      summaryWriter = new PrintWriter(writer);
      summaryWriter.print("Meta-dataset Summary" + "\n\n");
      summaryWriter.print("Attributes range from " + numAttr.min + " to " + numAttr.max + 
              " ( mean: " + numAttr.mean + " )\n\n");
      summaryWriter.print("Instances range from " + numInst.min + " to " + numInst.max + 
              " ( mean: " + numInst.mean + " )\n\n");
      summaryWriter.print("Classes range from " + numClasses.min + " to " + numClasses.max + 
              " ( mean: " + numClasses.mean + " )\n\n");
      summaryWriter.flush();
    } catch (Exception e) {
         System.err.println(e.getMessage());
   }
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
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      Summarizer s = new Summarizer();
    }

}
