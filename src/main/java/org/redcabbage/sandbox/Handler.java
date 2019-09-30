package org.redcabbage.sandbox;

import java.util.Map;

public interface Handler {
  /**
   * Invokes the handler to process the given input, returning any output via the return value.
   *
   * @param input
   * @return output of whatever processing is applied
   * @throws HandlerException
   */
  Map<String, Object> handle(Map<String, Object> input) throws HandlerException;
}
