package org.redcabbage.bggrabt;

import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import javax.script.*;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class bggrabt {


  public static void main(String[] args) throws Exception {
    Context jsContext = Context.create("js");
  //  jsContext.eval("js", "console.log('Hello from the project')");


//    Value value = jsContext.eval("js", "o = new Object()");

    jsContext.getBindings("js").putMember("boodle", new Test());
    jsContext.eval("js", "boodle.doStuff();");
  }

  public static void oldmain(String[] args) throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("graal.js");

    if (engine == null) {
      log.error("graal.js isn't available.");
      System.exit(1);
    }

//    engine.getBindings(ScriptContext.ENGINE_SCOPE).put("one", map);new Object() {
//      public int a = 1;
//      public int b = 2;
//
//      public int getStuff() {
//        return 1;
//      }
//    });
//
//
//    engine.eval("print('type: ' + typeof one);");
//    engine.eval("for (var key in one) { print(key); }");


    Map map = (Map)engine.eval("o = new Object()");
    map.put("new", "bong");
    map.put("func", (Consumer<String>)System.out::println);
    engine.eval("o.func('whee');");
  }
}
