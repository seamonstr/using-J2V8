package org.redcabbage.sandbox;

import java.io.InputStream;

public interface HandlerFactory {
  Handler getHandler(InputStream fileInputStream) throws ScriptEvalException;
}
