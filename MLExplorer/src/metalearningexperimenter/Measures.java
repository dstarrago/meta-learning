/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import weka.core.FastVector;
import weka.core.Attribute;
import RoughSet.RoughSet;
import RoughSet.RSTmeasures;
import weka.core.Instances;

/**
 *
 * @author Danel
 */
public class Measures {
  /**
   *  Dataset characterization attributes names
   */
  public static final String maProblemID = "ProblemID";
  //public static final String maDataDescription = "Description";

  public static final String maNumInstances = "NumInstances";
  public static final String maNumAttrs = "NumAttrs";
  public static final String maMajorClassSize = "MajSize";
  public static final String maMinorClassSize = "MinSize";
  public static final String maBalance = "Balance";
  public static final String maMajorClassProportion = "MajProp";
  public static final String maMinorClassProportion = "MinProp";

  public static final String maMajorClassUASize = "MajUASize";
  public static final String maMajorClassLASize = "MajLASize";
  public static final String maMinorClassUASize = "MinUASize";
  public static final String maMinorClassLASize = "MinLASize";
  public static final String maMajorClassUAProportion = "MajUAProp";
  public static final String maMajorClassLAProportion = "MajLAProp";
  public static final String maMinorClassUAProportion = "MinUAProp";
  public static final String maMinorClassLAProportion = "MinLAProp";
  public static final String maMajorClassUARatio = "MajUARatio";
  public static final String maMajorClassLARatio = "MajLARatio";
  public static final String maMinorClassUARatio = "MinUARatio";
  public static final String maMinorClassLARatio = "MinLARatio";
  public static final String maMajorClassApproxRatio = "MajAppRatio";
  public static final String maMinorClassApproxRatio = "MinAppRatio";
  //public static final String maFirstClassPosSize = "FirstClassPosSize";
  //public static final String maFirstClassNegSize = "FirstClassNegSize";
  public static final String maFirstClassBonSize = "BonSize";
  public static final String maFirstClassBonRatio = "BonRatio";
  //public static final String maSecondClassPosSize = "SecondClassPosSize";
  //public static final String maSecondClassNegSize = "SecondClassNegSize";
  //public static final String maSecondClassBonSize = "SecondClassBonSize";

  public static final String maM7 = "M7";
  public static final String maM8 = "M8";
  public static final String maM9 = "M9";
  public static final String maM10 = "M10";
  public static final String maM11 = "M11";
  public static final String maM12 = "M12";
  public static final String maM13 = "M13";
  public static final String maM14 = "M14";
  public static final String maM16 = "M16";
  public static final String maM17 = "M17";
  public static final String maM19 = "M19";
  public static final String maM20 = "M20";
  public static final String maM21 = "M21";
  public static final String maM22 = "M22";
  public static final String maM23 = "M23";
  public static final String maM24 = "M24";
  public static final String maM25 = "M25";
  public static final String maM26 = "M26";
  public static final String maM27 = "M27";
  public static final String maM29 = "M29";
  public static final String maM30 = "M30";
  public static final String maM31 = "M31";
  public static final String maM32 = "M32";
  public static final String maM33 = "M33";
  public static final String maM34 = "M34";
  public static final String maM35 = "M35";

  public static final String maQ4 = "Q4";
  public static final String maQ41 = "Q41";
  public static final String maQ42 = "Q42";
  public static final String maQ43 = "Q43";
  public static final String maQ51 = "Q51";
  public static final String maQ52 = "Q52";
  public static final String maQ53 = "Q53";
  public static final String maQ6 = "Q6";
  public static final String maQ61 = "Q61";
  public static final String maQ62 = "Q62";
  public static final String maQ63 = "Q63";
  public static final String maQ7 = "Q7";
  public static final String maQ71 = "Q71";
  public static final String maQ72 = "Q72";
  public static final String maQ73 = "Q73";
  public static final String maQ8 = "Q8";
  public static final String maQ81 = "Q81";
  public static final String maQ82 = "Q82";
  public static final String maQ83 = "Q83";

  public static final String maS1 = "S1";
  public static final String maS2 = "S2";

  public static final String maClassifQuality = "ClassifQuality";
  public static final String maClassifAccuracy = "ClassifAccuracy";
  public static final String maAccuracyXmembership = "Acc-Membership";
  public static final String maPrecisionXmembership = "Prec-Membership";
  public static final String maAccuracyXinvolvement = "Acc-Involvement";
  public static final String maPrecisionXinvolvement = "Prec-Involvement";
  public static final String maAccuracyXagreement = "Acc-Agreement";
  public static final String maPrecisionXagreement = "Prec-Agreement";

  // Performances attributes
  public static final String maTrainAccuracy = "TrainAccuracy";
  public static final String maTestAccuracy = "TestAccuracy";
  public static final String maTrainKappa = "TrainKappa";
  public static final String maTestKappa = "TestKappa";
  public static final String maTrainAUC = "TrainAUC";
  public static final String maTestAUC = "TestAUC";

  private int NUM_CHARACTER_ATTRS;
  private int NUM_PERFORMANCE_ATTRS;

  /**
   * List of characteristics(measures) names
   */
  public FastVector anMeasures = new FastVector();

  /**
   *  Vectors holding meta-attributes structures
   */
  public FastVector atMeasures = new FastVector();

  public Measures() {
    setupMeasures();
  }

  public int count() {
    return atMeasures.size();
  }

  public String name(int i) {
    return (String)anMeasures.elementAt(i);
  }

  public FastVector struct() {
    return atMeasures;
  }

  public int indexOf(String name) {
    return anMeasures.indexOf(name);
  }

  public int numCharacter() {
    return NUM_CHARACTER_ATTRS;
  }

  public int numPerformance() {
    return NUM_PERFORMANCE_ATTRS;
  }
  
  /**
   * Set meta-attribute information
   */
  private void setupMeasures() {
    /**
     * Add characterization attributes
     */
     // String attributes
    anMeasures.addElement(maProblemID);
    // Numeric attributes
    anMeasures.addElement(maNumInstances);
    anMeasures.addElement(maNumAttrs);
    anMeasures.addElement(maMajorClassSize);
    anMeasures.addElement(maMinorClassSize);
    anMeasures.addElement(maBalance);
    anMeasures.addElement(maMajorClassProportion);
    anMeasures.addElement(maMinorClassProportion);

    anMeasures.addElement(maMajorClassUASize);
    anMeasures.addElement(maMajorClassLASize);
    anMeasures.addElement(maMinorClassUASize);
    anMeasures.addElement(maMinorClassLASize);
    anMeasures.addElement(maMajorClassUAProportion);
    anMeasures.addElement(maMajorClassLAProportion);
    anMeasures.addElement(maMinorClassUAProportion);
    anMeasures.addElement(maMinorClassLAProportion);
    anMeasures.addElement(maMajorClassUARatio);
    anMeasures.addElement(maMajorClassLARatio);
    anMeasures.addElement(maMinorClassUARatio);
    anMeasures.addElement(maMinorClassLARatio);
    anMeasures.addElement(maMajorClassApproxRatio);
    anMeasures.addElement(maMinorClassApproxRatio);
    //anMeasures.addElement(maFirstClassPosSize);
    //anMeasures.addElement(maFirstClassNegSize);
    anMeasures.addElement(maFirstClassBonSize);
    anMeasures.addElement(maFirstClassBonRatio);
    //anMeasures.addElement(maSecondClassPosSize);
    //anMeasures.addElement(maSecondClassNegSize);
    //anMeasures.addElement(maSecondClassBonSize);

    anMeasures.addElement(maM7);
    anMeasures.addElement(maM8);
    anMeasures.addElement(maM9);
    anMeasures.addElement(maM10);
    anMeasures.addElement(maM11);
    anMeasures.addElement(maM12);
    anMeasures.addElement(maM13);
    anMeasures.addElement(maM14);
    anMeasures.addElement(maM16);
    anMeasures.addElement(maM17);
    anMeasures.addElement(maM19);
    anMeasures.addElement(maM20);
    anMeasures.addElement(maM21);
    anMeasures.addElement(maM22);
    anMeasures.addElement(maM23);
    anMeasures.addElement(maM24);
    anMeasures.addElement(maM25);
    anMeasures.addElement(maM26);
    anMeasures.addElement(maM27);
    anMeasures.addElement(maM29);
    anMeasures.addElement(maM30);
    anMeasures.addElement(maM31);
    anMeasures.addElement(maM32);
    anMeasures.addElement(maM33);
    anMeasures.addElement(maM34);
    anMeasures.addElement(maM35);

    anMeasures.addElement(maQ4);
    anMeasures.addElement(maQ41);
    anMeasures.addElement(maQ42);
    anMeasures.addElement(maQ43);
    anMeasures.addElement(maClassifQuality);
    anMeasures.addElement(maQ51);
    anMeasures.addElement(maQ52);
    anMeasures.addElement(maQ53);
    anMeasures.addElement(maQ6);
    anMeasures.addElement(maQ61);
    anMeasures.addElement(maQ62);
    anMeasures.addElement(maQ63);
    anMeasures.addElement(maQ7);
    anMeasures.addElement(maQ71);
    anMeasures.addElement(maQ72);
    anMeasures.addElement(maQ73);
    anMeasures.addElement(maQ8);
    anMeasures.addElement(maQ81);
    anMeasures.addElement(maQ82);
    anMeasures.addElement(maQ83);

    anMeasures.addElement(maS1);
    anMeasures.addElement(maS2);

    anMeasures.addElement(maClassifAccuracy);
    anMeasures.addElement(maAccuracyXmembership);
    anMeasures.addElement(maPrecisionXmembership);
    anMeasures.addElement(maAccuracyXinvolvement);
    anMeasures.addElement(maPrecisionXinvolvement);
    anMeasures.addElement(maAccuracyXagreement);
    anMeasures.addElement(maPrecisionXagreement);

    NUM_CHARACTER_ATTRS = anMeasures.size();
    /**
     * Add performances attributes
     */
    anMeasures.addElement(maTestAccuracy);
    anMeasures.addElement(maTrainAccuracy);
    anMeasures.addElement(maTestKappa);
    anMeasures.addElement(maTrainKappa);
    anMeasures.addElement(maTestAUC);
    anMeasures.addElement(maTrainAUC);
    NUM_PERFORMANCE_ATTRS = anMeasures.size() - NUM_CHARACTER_ATTRS;
    //anMeasures.addElement(maDataDescription);
    /**
     * Create and add ID attribute (string)
     */
    atMeasures.addElement(new Attribute(maProblemID, (FastVector)null));
    /**
     * Create and add remainder attributes (numeric)
     */
    for (int i = 1; i < anMeasures.size(); i++) {
      String s = (String)anMeasures.elementAt(i);
      atMeasures.addElement(new Attribute(s));
    }
    /**
     * Create and add DataDescription attribute (string)
     */
    //atMeasures.addElement(new Attribute(maDataDescription, (FastVector)null));
  }

  /**
   *  Extract the measures characterizing a dataset.
   *
   * @param data
   * @return
   */
  public double[] extractMetaAttributes(Instances data) {
    double[] measures = null;
    try {

      RoughSet rst = new RoughSet(data);
      RSTmeasures m = new RSTmeasures(rst);
      measures = new double[atMeasures.size()];
      measures[anMeasures.indexOf(maNumInstances)] = data.numInstances();
      measures[anMeasures.indexOf(maNumAttrs)] = data.numAttributes();
      int maxClassIndex, minClassIndex;
      if (rst.classPartition.at(0).cardinality() > rst.classPartition.at(1).cardinality()) {
        maxClassIndex = 0;
        minClassIndex = 1;
      } else {
        maxClassIndex = 1;
        minClassIndex = 0;
      }
      double maxClassSize = rst.classPartition.at(maxClassIndex).cardinality();
      double minClassSize = rst.classPartition.at(minClassIndex).cardinality();
      measures[anMeasures.indexOf(maMajorClassSize)] = maxClassSize;
      measures[anMeasures.indexOf(maMinorClassSize)] = minClassSize;
      double Balance = minClassSize / maxClassSize;
      measures[anMeasures.indexOf(maBalance)] = Balance;
      double majProp = maxClassSize / (double)data.numInstances();
      double minProp = minClassSize / (double)data.numInstances();
      measures[anMeasures.indexOf(maMajorClassProportion)] = majProp;
      measures[anMeasures.indexOf(maMinorClassProportion)] = minProp;

      double majorClassUASize, majorClassLASize, minorClassUASize, minorClassLASize;
      majorClassUASize = rst.UpperApprox.at(maxClassIndex).cardinality();
      majorClassLASize = rst.LowerApprox.at(maxClassIndex).cardinality();
      minorClassUASize = rst.UpperApprox.at(minClassIndex).cardinality();
      minorClassLASize = rst.LowerApprox.at(minClassIndex).cardinality();
      measures[anMeasures.indexOf(maMajorClassUASize)] = majorClassUASize;
      measures[anMeasures.indexOf(maMajorClassLASize)] = majorClassLASize;
      measures[anMeasures.indexOf(maMinorClassUASize)] = minorClassUASize;
      measures[anMeasures.indexOf(maMinorClassLASize)] = minorClassLASize;
      double majUAProp = majorClassUASize / (double)data.numInstances();
      double majLAProp = majorClassLASize / (double)data.numInstances();
      double minUAProp = minorClassUASize / (double)data.numInstances();
      double minLAProp = minorClassLASize / (double)data.numInstances();
      measures[anMeasures.indexOf(maMajorClassUAProportion)] = majUAProp;
      measures[anMeasures.indexOf(maMajorClassLAProportion)] = majLAProp;
      measures[anMeasures.indexOf(maMinorClassUAProportion)] = minUAProp;
      measures[anMeasures.indexOf(maMinorClassLAProportion)] = minLAProp;
      double majUARatio = maxClassSize / majorClassUASize;
      double majLARatio = majorClassLASize / maxClassSize;
      double minUARatio = minClassSize / minorClassUASize;
      double minLARatio = minorClassLASize / minClassSize;
      measures[anMeasures.indexOf(maMajorClassUARatio)] = majUARatio;
      measures[anMeasures.indexOf(maMajorClassLARatio)] = majLARatio;
      measures[anMeasures.indexOf(maMinorClassUARatio)] = minUARatio;
      measures[anMeasures.indexOf(maMinorClassLARatio)] = minLARatio;
      double majAppRatio = majorClassLASize / majorClassUASize;
      double minAppRatio = minorClassLASize / minorClassUASize;
      measures[anMeasures.indexOf(maMajorClassApproxRatio)] = majAppRatio;
      measures[anMeasures.indexOf(maMinorClassApproxRatio)] = minAppRatio;
      //measures[anMeasures.indexOf(maFirstClassPosSize)] = m.roughset.Positive.cardinality();
      //measures[anMeasures.indexOf(maFirstClassNegSize)] = m.roughset.Negative.cardinality();
      measures[anMeasures.indexOf(maFirstClassBonSize)] = rst.Boundary.cardinality();
      measures[anMeasures.indexOf(maFirstClassBonRatio)] = (double)rst.Boundary.cardinality()/(double)data.numInstances();

      //measures[anMeasures.indexOf(maSecondClassPosSize)] = m.roughset.LowerApprox.getSetAt(1).cardinality();
      //measures[anMeasures.indexOf(maSecondClassNegSize)] = m.roughset.LowerApprox.getSetAt(1).cardinality();
      //measures[anMeasures.indexOf(maSecondClassBonSize)] = m.roughset.LowerApprox.getSetAt(1).cardinality();

      measures[anMeasures.indexOf(maM7)] = majProp * majAppRatio;
      measures[anMeasures.indexOf(maM8)] = minProp * minAppRatio;
      measures[anMeasures.indexOf(maM9)] = Balance * majLARatio;
      measures[anMeasures.indexOf(maM10)] = (1 - Balance) * minLARatio;
      measures[anMeasures.indexOf(maM11)] = majProp * majUARatio;
      measures[anMeasures.indexOf(maM12)] = minProp * minUARatio;
      measures[anMeasures.indexOf(maM13)] = (1 - Balance) * majUARatio;
      measures[anMeasures.indexOf(maM14)] = Balance * minUARatio;
      measures[anMeasures.indexOf(maM16)] = (1 - Balance) * minUARatio;
      measures[anMeasures.indexOf(maM17)] = (1 - Balance) * majLARatio;
      measures[anMeasures.indexOf(maM19)] = Balance * minLARatio;
      measures[anMeasures.indexOf(maM20)] = majProp * minUARatio;
      measures[anMeasures.indexOf(maM21)] = Balance * majLAProp;
      measures[anMeasures.indexOf(maM22)] = Balance * minLAProp;
      measures[anMeasures.indexOf(maM23)] = Balance * majAppRatio;
      measures[anMeasures.indexOf(maM24)] = (1 - Balance) * minAppRatio;
      measures[anMeasures.indexOf(maM25)] = (1 - Balance) * majAppRatio;
      measures[anMeasures.indexOf(maM26)] = Balance * minAppRatio;
      measures[anMeasures.indexOf(maM27)] = Balance * majUARatio;
      measures[anMeasures.indexOf(maM29)] = minProp * minLARatio;
      measures[anMeasures.indexOf(maM30)] = majProp * majLARatio;
      measures[anMeasures.indexOf(maM31)] = majProp * minAppRatio;
      measures[anMeasures.indexOf(maM32)] = minProp * majAppRatio;
      measures[anMeasures.indexOf(maM33)] = majProp * minLARatio;
      measures[anMeasures.indexOf(maM34)] = minProp * majLARatio;
      measures[anMeasures.indexOf(maM35)] = minProp * majUARatio;

      measures[anMeasures.indexOf(maQ4)] = majProp * majAppRatio + minProp * minAppRatio;
      measures[anMeasures.indexOf(maQ41)] = Balance * majAppRatio + (1 - Balance) * minAppRatio;
      measures[anMeasures.indexOf(maQ42)] = (1 - Balance) * majAppRatio + Balance * minAppRatio;
      measures[anMeasures.indexOf(maQ43)] = minProp * majAppRatio + majProp * minAppRatio;

      measures[anMeasures.indexOf(maQ51)] = Balance * majLARatio + (1 - Balance) * minLARatio;
      measures[anMeasures.indexOf(maQ52)] = (1 - Balance) * majLARatio + Balance * minLARatio;
      measures[anMeasures.indexOf(maQ53)] = minProp * majLARatio + majProp * minLARatio;

      measures[anMeasures.indexOf(maQ6)] = majProp * majUARatio + minProp * minUARatio;
      measures[anMeasures.indexOf(maQ61)] = Balance * majUARatio + (1 - Balance) * minUARatio;
      measures[anMeasures.indexOf(maQ62)] = (1 - Balance) * majUARatio + Balance * minUARatio;
      measures[anMeasures.indexOf(maQ63)] = minProp * majUARatio + majProp * minUARatio;

      measures[anMeasures.indexOf(maQ7)] = majProp * majUARatio + minProp * minLARatio;
      measures[anMeasures.indexOf(maQ71)] = Balance * majUARatio + (1 - Balance) * minLARatio;
      measures[anMeasures.indexOf(maQ72)] = (1 - Balance) * majUARatio + Balance * minLARatio;
      measures[anMeasures.indexOf(maQ73)] = minProp * majUARatio + majProp * minLARatio;

      measures[anMeasures.indexOf(maQ8)] = majProp * majLARatio + minProp * minUARatio;
      measures[anMeasures.indexOf(maQ81)] = Balance * majLARatio + (1 - Balance) * minUARatio;
      measures[anMeasures.indexOf(maQ82)] = (1 - Balance) * majLARatio + Balance * minUARatio;
      measures[anMeasures.indexOf(maQ83)] = minProp * majLARatio + majProp * minUARatio;

      measures[anMeasures.indexOf(maS1)] = majProp * majAppRatio + minProp * minUARatio;
      measures[anMeasures.indexOf(maS2)] = majProp * majLARatio + minProp * minUARatio;

      measures[anMeasures.indexOf(maClassifQuality)] = m.ClassificationQuality();
      measures[anMeasures.indexOf(maClassifAccuracy)] = m.GenApproxRatio();
      measures[anMeasures.indexOf(maAccuracyXmembership)] = m.GenClassifAccuracy1();
      measures[anMeasures.indexOf(maPrecisionXmembership)] = m.GenClassifQuality1();
      measures[anMeasures.indexOf(maAccuracyXinvolvement)] = m.GenClassifAccuracy2();
      measures[anMeasures.indexOf(maPrecisionXinvolvement)] = m.GenClassifQuality2();
      measures[anMeasures.indexOf(maAccuracyXagreement)] = m.GenClassifAccuracy3();
      measures[anMeasures.indexOf(maPrecisionXagreement)] = m.GenClassifQuality3();

    } catch (Exception e) {
           System.err.println(e.getMessage());
     }
    return measures;
  }

}
