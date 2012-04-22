/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.sslr.api.symboltable;

import com.google.common.base.Predicate;
import com.sonar.sslr.api.AstNode;

import java.util.Collection;

/**
 * Region of code with a well-defined boundaries that groups symbol definitions.
 */
public interface Scope extends SymbolTableElement {

  /**
   * Returns associated AST node.
   */
  AstNode getAstNode();

  /**
   * Returns enclosing scope.
   */
  Scope getEnclosingScope();

  /**
   * Returns all nested scopes.
   */
  Collection<Scope> getNestedScopes();

  /**
   * TODO Godin: in fact tree of scopes should be immutable after construction.
   * However question: how we can achieve this, if this interface might be implemented by clients?
   */
  void addNestedScope(Scope nestedScope);

  /**
   * Returns all symbols defined in this scope.
   */
  Collection<Symbol> getMembers();

  /**
   * TODO Godin: in fact tree of scopes should be immutable after construction.
   * However question: how we can achieve this, if this interface might be implemented by clients?
   */
  void define(Symbol symbol);

  Symbol lookup(String name, Predicate predicate);

}
