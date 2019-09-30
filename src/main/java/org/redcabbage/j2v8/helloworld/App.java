package org.redcabbage.j2v8.helloworld;

import com.eclipsesource.v8.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 */
@Slf4j
public class App {
  static ReferenceHandler refHandler = new ReferenceHandler() {
    @Override
    public void v8HandleCreated(V8Value object) {
      log.info("Handle created: {}", object.hashCode());
    }

    @Override
    public void v8HandleDisposed(V8Value object) {
      log.info("Handle released: {}", object.hashCode());
    }
  };

  public static void main(String[] args) {
    V8 runtime = V8.createV8Runtime();
    runtime.addReferenceHandler(refHandler);
    V8Object method = runtime.registerJavaMethod((receiver, params) -> {
      for (String s : receiver.getKeys()) {
        Object o  = receiver.get(s);
        log.info("{} {}: {}", o.getClass().getSimpleName(), s, o);
      }
      log.info("Params: " + params.toString());
      return null;
    }, "sendMeStuff");
    try {
      int result = runtime.executeIntegerScript(
              "var hello = 'hello, ';\n"
                      + "var world = 'world!';\n"
                      + "hello.concat(world).length;\n"
                      + "sendMeStuff('param', 'another one', 2);"
                      + "12;\n");

      System.out.println(result);
    } finally {
      method.release();
      runtime.release(true);
    }
  }
}
