/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.io.*;
import java.util.Observable;
import weka.core.Instances;
import weka.core.DenseInstance;
import weka.core.Attribute;
import weka.core.FastVector;

/**
 *
 * @author Danel
 */
public class StudioModel extends Observable implements Serializable {

  public Instances data;
  private Measures measures = new Measures();

  //private final String Source = "C:/Documents and Settings/Danel/My Documents/Tesis/Data/Metadata1/Assambles/";
  private MeasureCharacter[] mc;
  public transient MeasureCharacter selectedMeasure = null;
  public int PerformanceMeasureIndex;
  public int TestAttr;
  public int TrainAttr;

  transient FastVector struct = null;
  transient FastVector behav;
  transient private final String beBAD = "Bad";
  transient private final String beGOOD = "Good";

  public Instances getIntervals(String relationName) {
    int posCount = 0;
    int negCount = 0;
    String ruleName;
    if (struct == null) buildStruct();
    Instances rules = new Instances(relationName, struct, 1);
    for (int i = 0; i < mc.length; i++) {
      data.sort(i);
      for (int j = 0; j < mc[i].getIntervalsCount(); j++) {
        int left = mc[i].getInterval(j).getLeft();
        int right = mc[i].getInterval(j).getRight();
        double[] r = new double[5];
        r[2] = (left == 0)? 0 : data.instance(left).value(i);
        r[3] = (right == data.numInstances())? 1 : data.instance(right).value(i);
        if (getTestAccuracyMean() - getTestAccuracyMeanAtInterval(left, right) < 0) {
          r[4] = behav.indexOf(beGOOD);
          ruleName = "P" + ++posCount;
        } else {
          r[4] = behav.indexOf(beBAD);
          ruleName = "N" + ++negCount;
        }
        rules.add(new DenseInstance(1, r));
        rules.lastInstance().setValue(0, ruleName);
        rules.lastInstance().setValue(1, mc[i].name);
      }
    }
    data.sort(selectedMeasure.getAttrIndex());
    return rules;
  }

  private void buildStruct() {
    struct = new FastVector();
    struct.addElement(new Attribute("ID", (FastVector)null));
    struct.addElement(new Attribute("measure", (FastVector)null));
    struct.addElement(new Attribute("begin"));
    struct.addElement(new Attribute("end"));
    behav = new FastVector();
    behav.addElement(beBAD);
    behav.addElement(beGOOD);
    struct.addElement(new Attribute("behavior", behav));
  }

  public void loadFromFile(File dataFile) {
    //File dataFile = new File(Source, fileName);
    //File dataFile = new File(fileName);     // Archivo que debe encontrarse en la carpeta de la aplicación
    data = AlgorithmCharacterizer.getInstances(dataFile);
    //MessageStr msg = new SpanishStr();
    mc = new MeasureCharacter[data.numAttributes()];
    for (int i = 0; i < data.numAttributes(); i++)
      mc[i] = new MeasureCharacter(data.attribute(i).name(),data, i);
    TestAttr = PerformanceMeasureIndex = measures.indexOf(Measures.maTestAccuracy);
    TrainAttr = measures.indexOf(Measures.maTrainAccuracy);
    /*
    mc[0] = new MeasureCharacter(msg.sTrainingAccuracy(), data, TrainAttr = measures.indexOf(Measures.maTrainAccuracy));
    mc[TestAccuracyIndex = 1] = new MeasureCharacter(msg.sTestAccuracy(), data, TestAttr = measures.indexOf(Measures.maTestAccuracy));
    mc[2] = new MeasureCharacter(msg.sQualClassif(), data, measures.indexOf(Measures.maClassifQuality));   // usada x Yailé
    mc[3] = new MeasureCharacter(msg.sAccClassif(), data, measures.indexOf(Measures.maClassifAccuracy));  // usada x Yailé
    mc[4] = new MeasureCharacter(msg.sGenAccMemb(), data, measures.indexOf(Measures.maAccuracyXmembership));
    mc[5] = new MeasureCharacter(msg.sGenPrecMemb(), data, measures.indexOf(Measures.maPrecisionXmembership));  // usada x Yailé
    mc[6] = new MeasureCharacter(msg.sGenAccInv(), data, measures.indexOf(Measures.maAccuracyXinvolvement));  // usada x Yailé
    mc[7] = new MeasureCharacter(msg.sGenPrecInv(), data, measures.indexOf(Measures.maPrecisionXinvolvement));
    mc[8] = new MeasureCharacter(msg.sGenAccAgr(), data, measures.indexOf(Measures.maAccuracyXagreement));
    mc[9] = new MeasureCharacter(msg.sGenPrecAgr(), data, measures.indexOf(Measures.maPrecisionXagreement));
     */
    setChanged();
    notifyObservers();
  }

  public void addInterval(int left, int right) {
    if (selectedMeasure != null)
      selectedMeasure.addInterval(left, right);
    setChanged();
    notifyObservers();
  }

  public int getIntervalsCount() {
    return (selectedMeasure == null)? 0: selectedMeasure.getIntervalsCount();
  }

  public Interval getInterval(int Index) {
    return selectedMeasure.getInterval(Index);
  }

  public int intervalLeft(int intervalIndex) {
    return selectedMeasure.getInterval(intervalIndex).getLeft();
  }

  public int intervalRight(int intervalIndex) {
    return selectedMeasure.getInterval(intervalIndex).getRight();
  }

  public void removeInterval(Interval interval) {
    selectedMeasure.removeInterval(interval);
  }

  public double measureVal(int index) {
    return data.instance(index).value(selectedMeasure.getAttrIndex());
  }

  public boolean measureSelected() {
    return selectedMeasure != null;
  }

  public void selectMeasure(int Index) {
    selectedMeasure = mc[Index];
    data.sort(selectedMeasure.getAttrIndex());
    setChanged();
    notifyObservers();
  }

  public double getTestAccuracyMean() {
    return mc[PerformanceMeasureIndex].getMean();
  }

  public String[] listAttrNames() {
    String[] attrNames = new String[data.numAttributes()];
    for (int i = 0; i < data.numAttributes(); i++)
      attrNames[i] = data.attribute(i).name();
    return attrNames;
  }

  public double getTestAccuracyMeanAtInterval(int left, int right) {
    Interval in = new Interval(mc[PerformanceMeasureIndex], left, right);
    return in.getMean();
  }

}
