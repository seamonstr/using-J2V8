package org.redcabbage;

import org.redcabbage.sandbox.*;

import java.util.HashMap;
import java.util.Map;

public class HandlerTest {
  public static class HelloWorlder {
    @SuppressWarnings("unused")
    public String helloWorld(int input) {
      return "Hello World single: " + input;
    }

    @SuppressWarnings("unused")
    public String helloWorld(int... input) {
      StringBuilder sb = new StringBuilder(input.length * 4);
      for (int i: input) {
        sb.append(i).append(" ");
      }

      return "Hello World array: " + sb;
    }
  }

  public static void main(String[] args) throws HandlerException {
    Map<String, Object> services = new HashMap<>();
    services.put("helloWorlder", new HelloWorlder());
    HandlerFactory handlerFactory = new JsHandlerFactory(services);

    Handler handler = handlerFactory.getHandler(HandlerTest.class.getResourceAsStream("test.js"));
    Map<String, Object> input = new HashMap<>();
    input.put("Key One", new int[]{10, 12, 13, 14});
    input.put("Key bob", 20);
//    input.put("Key 45", "Val 45");
//    input.put("Key doggie", "Val doggie");
//    input.put("Key bozo", "Val bozo");
    Map<String, Object> out = handler.handle(input);
    System.out.println(out.toString());
  }
}
