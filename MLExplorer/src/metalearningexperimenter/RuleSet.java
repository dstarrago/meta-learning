/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import weka.core.FastVector;

/**
 *
 * @author danels
 */
public class RuleSet {

  FastVector rules = new FastVector();

  public void add(Rule rule) {
    rules.addElement(rule);
  }

  public void append(RuleSet ruleset) {
    rules.appendElements(ruleset.rules);
  }

  public Rule byIndex(int index) {
    return (Rule)rules.elementAt(index);
  }

  public Rule byID(String name) throws Exception {
    for (int i = 0; i < rules.size(); i++)
      if (byIndex(i).ID().equals(name)) return byIndex(i);
    throw new Exception("No rule with that name");
  }

  public int count() {
    return rules.size();
  }
}
