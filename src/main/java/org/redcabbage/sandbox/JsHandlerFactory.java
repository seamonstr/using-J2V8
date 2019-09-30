package org.redcabbage.sandbox;

import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class JsHandlerFactory implements HandlerFactory {
  /*
    Maximum length of script that will be read
   */
  public static int MAX_SCRIPT_LEN = 32 * 1024;

  private final Map<String, Object> services;

  /**
   *
   * @param services Services that will be made available to scripts running in all handlers created by this factory.
   */
  public JsHandlerFactory(Map<String, Object> services) {
    this.services = services;
  }

  @Override
  public Handler getHandler(InputStream inputStream) throws ScriptEvalException {
    String script;
    try {
      script = new String(inputStream.readNBytes(MAX_SCRIPT_LEN));
    } catch (IOException e) {
      String msg = String.format("Error occurred reading a JS script: %s", e.getMessage());
      log.error(msg, e);
      throw new RuntimeException(msg, e);
    }
    return new JsHandler(script, services);
  }
}
