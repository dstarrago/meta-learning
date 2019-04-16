/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute;
import java.util.Iterator;

/**
 *
 * @author Danel
 */
public class StatsDealer {

  private Instances data;
  private Attribute testAttr;
  private Attribute trainAttr;
  private double meanTrainAcc;
  private double meanTestAcc;
  private int mode;

  public StatsDealer(Instances data, String trainAttrName) {
    this.data = data;
    mode = 1;
    trainAttr = data.attribute(trainAttrName);
    meanTrainAcc = data.meanOrMode(trainAttr);
  }

  public StatsDealer(Instances data, String trainAttrName, String testAttrName) {
    this.data = data;
    mode = 2;
    testAttr = data.attribute(testAttrName);
    trainAttr = data.attribute(trainAttrName);
    meanTestAcc = data.meanOrMode(testAttr);
    meanTrainAcc = data.meanOrMode(trainAttr);
  }

  public  RuleStats getStats(Rule r) {
    if (r.elements == null) return null;
    return (mode == 1)? getStats1(r) : getStats2(r);
  }
  
  private RuleStats getStats2(Rule r) {
    RuleStats stats = new RuleStats();
    double trainSum = 0;
    double testSum = 0;
    double trainSumSq = 0;
    double testSumSq = 0;
    stats.support = 100.0 * r.elements.cardinal() / data.numInstances();
    double trainMean = 0;
    double testMean = 0;
    double trainStdDev = Double.POSITIVE_INFINITY;
    double testStdDev = Double.POSITIVE_INFINITY;
    Iterator iter = r.elements.iterator();
    while (iter.hasNext()) {
      Instance inst = (Instance)iter.next();
      double aTest = inst.value(testAttr);
      double aTrain = inst.value(trainAttr);
      testSum += aTest;
      trainSum += aTrain;
      testSumSq += aTest * aTest;
      trainSumSq += aTrain * aTrain;
    }
    testMean = testSum / r.size();
    trainMean = trainSum / r.size();
    if (r.size() > 1) {
      testStdDev = testSumSq - (testSum * testSum) / r.size();
      trainStdDev = trainSumSq - (trainSum * trainSum) / r.size();
      testStdDev /= (r.size() - 1);
      trainStdDev /= (r.size() - 1);
      if (trainStdDev < 0) trainStdDev = 0; // StdDev rounded to zero
      if (testStdDev < 0) testStdDev = 0; // StdDev rounded to zero
      testStdDev = Math.sqrt(testStdDev);
      trainStdDev = Math.sqrt(trainStdDev);
      }
    stats.trainMean = trainMean;
    stats.trainStdDev = trainStdDev;
    stats.trainDiff = trainMean - meanTrainAcc;
    stats.testMean = testMean;
    stats.testStdDev = testStdDev;
    stats.testDiff = testMean - meanTestAcc;
    return stats;
  }

  private RuleStats getStats1(Rule r) {
    RuleStats stats = new RuleStats();
    double accuracySum = 0;
    double accuracySumSq = 0;
    stats.support = 100.0 * r.size() / data.numInstances();
    double accuracyMean = 0;
    double accuracyStdDev = Double.POSITIVE_INFINITY;
    Iterator iter = r.elements.iterator();
    while (iter.hasNext()) {
      Instance inst = (Instance)iter.next();
      double aaccuracy = inst.value(trainAttr);
      accuracySum += aaccuracy;
      accuracySumSq += aaccuracy * aaccuracy;
    }
    accuracyMean = accuracySum / r.size();
    if (r.size() > 1) {
      accuracyStdDev = accuracySumSq - (accuracySum * accuracySum) / r.size();
      accuracyStdDev /= (r.size() - 1);
      if (accuracyStdDev < 0) accuracyStdDev = 0; // StdDev rounded to zero
      accuracyStdDev = Math.sqrt(accuracyStdDev);
      }
    stats.trainMean = accuracyMean;
    stats.trainStdDev = accuracyStdDev;
    stats.trainDiff = accuracyMean - meanTrainAcc;
    return stats;
  }

}
