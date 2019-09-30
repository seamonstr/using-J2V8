package org.redcabbage.sandbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class JsHandler implements Handler {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final String PREAMBLE_FILENAME = "preamble.js";
  private static final String MAIN_FUNCTION_NAME = "_main";
  private static String JS_PREAMBLE;

  static {
    try {
      JS_PREAMBLE = new String(JsHandler.class.getResourceAsStream(PREAMBLE_FILENAME).readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException("Unable to load JSHandler preamble resource");
    }
  }

  private final Context jsContext;
  private final Value main;

  /**
   * @param script   Script for this handler.  This script will be evaluated once on construction; it needs to declare
   *                 the main() method that is then invoked by subsequent calls to handle().
   * @param services Set the services that the script is able to call.  The keys are used as the top-level scope name to
   *                 access the service; all public methods of the object are made available to the script.
   */
  public JsHandler(String script, Map<String, Object> services) throws ScriptEvalException {
    jsContext = buildGraalContext(services);

    try {
      jsContext.eval("js", JS_PREAMBLE + "\n" + script);
    } catch (PolyglotException e) {
      throw new ScriptEvalException("An error occurred on first evaluation of the script", e);
    }

    main = jsContext.getBindings("js").getMember(MAIN_FUNCTION_NAME);
    if (main == null || !main.canExecute())
      throw new RuntimeException(MAIN_FUNCTION_NAME + " is either not available or not executable.");
  }

  private Context buildGraalContext(Map<String, Object> services) {
    if (services == null) {
      return Context.newBuilder("js").allowHostAccess(HostAccess.NONE).build();
    }

    HostAccess hostAccess = buildHostAccessFor(services);
    Context context = Context.newBuilder("js").allowHostAccess(hostAccess).build();
    services.forEach((k, o) -> context.getBindings("js").putMember(k, o));
    return context;
  }

  private HostAccess buildHostAccessFor(Map<String, Object> services) {
    assert services != null;

    HostAccess.Builder builder = HostAccess.newBuilder();
    services.forEach((k, o) -> {
              for (Method m : o.getClass().getMethods()) {
                if (Modifier.isPublic(m.getModifiers())) {
                  builder.allowAccess(m);
                }
              }
            }
    );
    return builder.build();
  }

  @Override
  public Map<String, Object> handle(Map<String, Object> input) throws HandlerException {
    Map<String, Object> ret;
    try {
      ret = main.execute(ProxyObject.fromMap(input)).as(Map.class);
    } catch (PolyglotException e) {
      String msg = String.format("Error occurred handling %s", input);
      log.error(msg);
      throw new HandlerException(msg, e);
    }

    StringBuilder str = new StringBuilder(ret.size() * 10);
    str.append("{");
    ret.forEach((k, o) -> str.append(k).append(": ").append("\"").append(o).append("\" "));
    str.append("}");
    log.info(String.format("Returned from %s: %s, %s", MAIN_FUNCTION_NAME, ret.getClass().getSimpleName(), str));

    return ret;
  }

  private Map<String, String> returnedValToMap(String returned) throws HandlerException {
    try {
      return objectMapper.readValue(returned, Map.class);
    } catch (IOException e) {
      String err = "Unable to convert returned string to a map";
      log.error(err, e);
      throw new HandlerException(err, e);
    }
  }

  private String inputMapToJson(Map<String, String> input) throws HandlerException {
    try {
      return objectMapper.writeValueAsString(input);
    } catch (JsonProcessingException e) {
      String err = "Unable to convert input map to JSON";
      log.error(err, e);
      throw new HandlerException(err, e);
    }
  }
}
