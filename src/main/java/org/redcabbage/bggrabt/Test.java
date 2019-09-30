package org.redcabbage.bggrabt;

import org.graalvm.polyglot.HostAccess;

public class Test {
  @HostAccess.Export
  public void doStuff(String str) {
    //((Consumer<String>)System.out::println).accept("bob");
    System.out.println(str);
  }

  @HostAccess.Export
  public String number = "121321";
}
