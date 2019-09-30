package org.redcabbage.sandbox;

public class HandlerException extends Exception {
  public HandlerException(String err, Exception e) {
    super(err, e);
  }
}
