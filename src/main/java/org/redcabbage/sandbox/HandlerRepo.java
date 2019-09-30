package org.redcabbage.sandbox;

public interface HandlerRepo {
  String[] ls();
  Handler getHandler(String name) throws ScriptEvalException;
}
