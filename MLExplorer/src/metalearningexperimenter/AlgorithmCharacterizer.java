/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.io.*;
import weka.core.Instance;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.FastVector;

/**
 *
 * @author danels
 */
public class AlgorithmCharacterizer {

public static final String TABLENAME = "C4.5 domain rules";

private Instances data;
final private String Directory = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/ANALYSIS";
//final private String fileName = "/Feb09Fixed/Feb09Fixed";
//final private String fileName = "/Feb09Kappa/Feb09Kappa";
//final private String fileName = "/Feb10Fixed/Feb10Fixed";
final private String fileName = "/mezcla1/mezcla1";
//final private String fileName = "/mejoresC45/mejoresC45";
//final private String fileName = "/AllCharFixed/AllCharFixed";
final private String ruleBaseFileName = "/Feb09Fixed/Feb09Fixed_IntervalsV4.2.arff";
//final private String ruleBaseFileName = "/Feb09Fixed/Selection2_Intervals.arff";
//final private String trainAttrName = "TrainKappa";
//final private String testAttrName = "TestKappa";
final private boolean toPercent = false; // llevar medidas a porciento: false para accuracy, true para kappa y AUC
final private String trainAttrName = "TrainAccuracy";
final private String testAttrName = "TestAccuracy";
private RuleSet rules;

public double trainingMean;
public double trainingStdDev;
public double testMean;
public double testStdDev;
private StatsDealer statsDealer;

public AlgorithmCharacterizer() {
  File dataFile = new File(Directory, fileName + ".arff");
  data = getInstances(dataFile);
  rules = new RuleSet();
  statsDealer = new StatsDealer(data, trainAttrName, testAttrName);
  loadRules(new File(Directory, ruleBaseFileName));
  RulesDefinition();
  basicStatistics();
  saveTable();
}

private void basicStatistics() {
  trainingMean = data.meanOrMode(data.attribute(trainAttrName));
  testMean = data.meanOrMode(data.attribute(testAttrName));
  trainingStdDev = Math.sqrt(data.variance(data.attribute(trainAttrName)));
  testStdDev = Math.sqrt(data.variance(data.attribute(testAttrName)));
  /**
  trainingMean = data.meanOrMode(data.attribute(Measures.maTrainAccuracy));
  testMean = data.meanOrMode(data.attribute(Measures.maTestAccuracy));
  trainingStdDev = Math.sqrt(data.variance(data.attribute(Measures.maTrainAccuracy)));
  testStdDev = Math.sqrt(data.variance(data.attribute(Measures.maTestAccuracy)));
  */
}

  /**
   * Open a dataset file in arff format an return the dataset.
   *
   * @param f
   * @return
   */
  public static Instances getInstances(File f) {
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

  private void loadRules(File f) {
    Instances ruleBase = getInstances(f);
    for (int i = 0; i < ruleBase.numInstances(); i++) {
      Instance r = ruleBase.instance(i);
      SimpleRule s = new SimpleRule(r.stringValue(0), r.stringValue(1), r.value(2), r.value(3), (int)r.value(4));
      rules.add(s);
    }
  }

  private void RulesDefinition() {
    try {
      Rule PRD = getPRD(rules);
      Rule NRD = getNRD(rules);
      //RuleSet disj1 = pairwiseDisjunctRules(rules);
      //RuleSet conj = pairwiseConjunctRules(rules);
      //RuleSet conj = pairwiseConjunctRulesAndNegative(disj);
      rules.add(PRD);
      rules.add(NRD);
      //rules.append(conj);
      //Rule PR = rules.byID("PC18");
      //Rule PR = rules.byID("P19");
      //Rule NR = rules.byID("NC13322");  //NC13185
      //Rule NR = rules.byID("N13");
      IntersectRules ir = addIntersections(PRD, NRD);
      executeRules();
      //extractRegions(PRD, ir.nPRaNR);
      extractRegions(rules.byID("P4"), rules.byID("N4"));   // regions corresponding to simple rules R5+ y R5- (M5)
    } catch (Exception e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

  public Rule getPRD(RuleSet rules) {
    Rule disj = null;
    for (int i = 0; i < rules.count(); i++) {
      Rule r1 = rules.byIndex(i);
      if (r1.behavior == Rule.bhGood) {
        if (disj == null)
          disj = r1;
        else {
          disj = new Or(disj, r1);
          disj.setID(disj.toString());
        }
      }
    }
    disj.setID("PRD");
    disj.setBehavior(Rule.bhGood);
    return disj;
  }
  
  public Rule getNRD(RuleSet rules) {
    Rule disj = null;
    for (int i = 0; i < rules.count(); i++) {
      Rule r1 = rules.byIndex(i);
      if (r1.behavior == Rule.bhBad) {
        if (disj == null)
          disj = r1;
        else {
          disj = new Or(disj, r1);
          disj.setID(disj.toString());
        }
      }
    }
    disj.setID("NRD");
    disj.setBehavior(Rule.bhBad);
    return disj;
  }

  public RuleSet pairwiseDisjunctRules(RuleSet rules) {
    RuleSet disjRules = new RuleSet();
    int posCount = 0;
    String posPrefix = "PO";
    int negCount = 0;
    String negPrefix = "NO";
    for (int i = 0; i < rules.count(); i++) {
      Rule r1 = rules.byIndex(i);
      for (int j = i + 1; j < rules.count(); j++) {
        Rule r2 = rules.byIndex(j);
        if (r1.behavior == r2.behavior) {
          if (r1.behavior == Rule.bhGood)
            disjRules.add(new Or(posPrefix + String.valueOf(++posCount), Rule.bhGood, r1, r2));
          else
            disjRules.add(new Or(negPrefix + String.valueOf(++negCount), Rule.bhBad, r1, r2));
        }
      }
    }
    return disjRules;
  }

  public RuleSet pairwiseConjunctRules(RuleSet rules) {
    RuleSet conjRules = new RuleSet();
    int posCount = 0;
    String posPrefix = "PC";
    int negCount = 0;
    String negPrefix = "NC";
    for (int i = 0; i < rules.count(); i++) {
      Rule r1 = rules.byIndex(i);
      for (int j = i + 1; j < rules.count(); j++) {
        Rule r2 = rules.byIndex(j);
        if (r1.behavior == r2.behavior) {
          if (r1.behavior == Rule.bhGood)
            conjRules.add(new And(posPrefix + String.valueOf(++posCount), Rule.bhGood, r1, r2));
          else
            conjRules.add(new And(negPrefix + String.valueOf(++negCount), Rule.bhBad, r1, r2));
        }
      }
    }
    return conjRules;
  }


  public RuleSet pairwiseConjunctRulesAndNegative(RuleSet rules) {
    RuleSet conjRules = new RuleSet();
    int posCount = 0;
    String posPrefix = "PCN";
    int negCount = 0;
    String negPrefix = "NCN";
    for (int i = 0; i < rules.count(); i++) {
      Rule r1 = rules.byIndex(i);
      for (int j = i + 1; j < rules.count(); j++) {
        Rule r2 = rules.byIndex(j);
        if (r1.behavior == r2.behavior) {
          if (r1.behavior == Rule.bhGood)
            conjRules.add(new And(posPrefix + String.valueOf(++posCount), Rule.bhGood, r1, r2));
          else
            conjRules.add(new And(negPrefix + String.valueOf(++negCount), Rule.bhBad, r1, r2));
        } else {
          if (r1.behavior == Rule.bhGood) {
            conjRules.add(new And(posPrefix + String.valueOf(++posCount), Rule.bhGood, r1, new Not("~" + r2.ID(), r2)));
            conjRules.add(new And(negPrefix + String.valueOf(++negCount), Rule.bhBad, new Not("~" + r1.ID(), r1), r2));
          }
          else {
            conjRules.add(new And(negPrefix + String.valueOf(++negCount), Rule.bhBad, r1, new Not("~" + r2.ID(), r2)));
            conjRules.add(new And(posPrefix + String.valueOf(++posCount), Rule.bhGood, new Not("~" + r1.ID(), r1), r2));
          }
        }
      }
    }
    return conjRules;
  }

  public class IntersectRules {
    public Rule PRaNR;    // PRD and NRD
    public Rule nPRaNR;   // not PRD and NRD
    public Rule PRanNR;   // PRD and not NRD
  }

  public IntersectRules addIntersections(Rule PR, Rule NR) {
    IntersectRules ir = new IntersectRules();
    ir.PRanNR = new And("PR&~NR", Rule.bhGood, PR, new Not("~NR", NR));
    ir.nPRaNR = new And("~PR&NR", Rule.bhBad, new Not("~PR", PR), NR);
    ir.PRaNR = new And("PR&NR", Rule.bhGood, PR, NR);
    Rule unChar = new And("Uncharacterized", new Not(PR), new Not(ir.nPRaNR));
    rules.add(ir.PRanNR);
    rules.add(ir.nPRaNR);
    rules.add(ir.PRaNR);
    rules.add(unChar);
    return ir;
  }

  public void saveDataset(Instances data, String filename) {
    File file = new File(Directory, filename + ".arff");
    //File file = new File(filename + ".arff");
    ArffSaver Saver = new ArffSaver();
    try {
      Saver.setFile(file);
      Saver.setDestination(file);
      Saver.setInstances(data);
      Saver.setRetrieval(ArffSaver.BATCH);
      Saver.writeBatch();
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
  }

  private void executeRules() {
    for (int i = 0; i < rules.count(); i++) 
      rules.byIndex(i).execute(data);
  }

  public void extractRegions(Rule PR, Rule NR) {
    Set positives = new Set(PR.elements);
    Set negatives = new Set(NR.elements);
    Set universe = new Set(data);
    Set characterized = new Union(positives, negatives).operate();
    Set uncharacterized = new Complement(characterized, universe).operate();
    saveDataset(positives.toDataset(), fileName + "_pos");
    saveDataset(negatives.toDataset(), fileName + "_neg");
    saveDataset(uncharacterized.toDataset(), fileName + "_unchar");
  }

  private void saveTable() {
    FastVector attrs = new FastVector();
    attrs.addElement(new Attribute("ID", (FastVector)null));
    attrs.addElement(new Attribute("Rule", (FastVector)null));
    attrs.addElement(new Attribute("Support"));
    attrs.addElement(new Attribute("MeanTraining"));
    attrs.addElement(new Attribute("StddevTraining"));
    attrs.addElement(new Attribute("TrainingDiff"));
    attrs.addElement(new Attribute("MeanTesting"));
    attrs.addElement(new Attribute("StddevTesting"));
    attrs.addElement(new Attribute("TestingDiff"));
    Instances table = new Instances(TABLENAME, attrs, 0);
    double sfactor = (toPercent)? 100:1;
    for (int i = 0; i < rules.count(); i++) {
      double[] vals = new double[9];
      Rule r = rules.byIndex(i);
      RuleStats stats = statsDealer.getStats(r);
      vals[2] = stats.support;
      vals[3] = stats.trainMean * sfactor;
      vals[4] = stats.trainStdDev * sfactor;
      vals[5] = stats.trainDiff * sfactor;
      vals[6] = stats.testMean * sfactor;
      vals[7] = stats.testStdDev * sfactor;
      vals[8] = stats.testDiff * sfactor;
      Instance ins = new DenseInstance(1, vals);
      table.add(ins);
      table.lastInstance().setValue(0, r.ID());
      table.lastInstance().setValue(1, r.toString());
    }
    saveTable(table);
  }

  /**
   * Save current prediction dataset to an arff file.
   */
  private void saveTable(Instances ds) {
    saveDataset(ds, fileName + "_Rules");
  }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      AlgorithmCharacterizer ac = new AlgorithmCharacterizer();
    }

}
