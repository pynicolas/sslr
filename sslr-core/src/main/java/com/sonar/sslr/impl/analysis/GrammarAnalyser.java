/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.impl.analysis;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.matcher.RuleDefinition;
import com.sonar.sslr.impl.matcher.RuleMatcher;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.*;
import static com.sonar.sslr.impl.analysis.FirstVisitor.*;

public class GrammarAnalyser {

  private final Set<RuleMatcher> rules;
  private final Map<RuleMatcher, LeftRecursionException> dependOnLeftRecursiveRules = Maps.newHashMap();
  private final Map<RuleMatcher, LeftRecursionException> leftRecursiveRules = Maps.newHashMap();

  public GrammarAnalyser(Grammar grammar) {
    rules = getRuleMatchers(grammar);

    for (RuleMatcher rule : rules) {
      detectIssues(rule);
    }
  }

  public Set<RuleMatcher> getRules() {
    return Collections.unmodifiableSet(rules);
  }

  public boolean isLeftRecursive(RuleMatcher rule) {
    return leftRecursiveRules.containsKey(rule);
  }

  public boolean isDependingOnLeftRecursiveRule(RuleMatcher rule) {
    return dependOnLeftRecursiveRules.containsKey(rule);
  }

  public LeftRecursionException getLeftRecursionxception(RuleMatcher rule) {
    LeftRecursionException e = dependOnLeftRecursiveRules.get(rule);
    if (e == null) {
      e = leftRecursiveRules.get(rule);
    }
    checkArgument(e != null, "The given rule \"" + rule.getName() + "\" has no associated left recursion exception");

    return e;
  }

  public boolean hasIssues() {
    return !dependOnLeftRecursiveRules.isEmpty() || !leftRecursiveRules.isEmpty();
  }

  private Set<RuleMatcher> getRuleMatchers(Grammar grammar) {
    try {
      Set<RuleMatcher> ruleMatchers = Sets.newHashSet();

      for (Field ruleField : grammar.getAllRuleFields(grammar.getClass())) {
        RuleDefinition rule = (RuleDefinition) ruleField.get(grammar);
        ruleMatchers.add(rule.getRule());
      }

      return ruleMatchers;
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private void detectIssues(RuleMatcher rule) {
    try {
      first(rule);
    } catch (LeftRecursionException e) {
      if (rule.equals(e.getLeftRecursiveRule())) {
        leftRecursiveRules.put(rule, e);
      } else {
        dependOnLeftRecursiveRules.put(rule, e);
      }
    }
  }

}