package org.redcabbage.sandbox;

import org.redcabbage.sandbox.Handler;
import org.redcabbage.sandbox.HandlerRepo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public class DirBasedHandlerRepo implements HandlerRepo {
  private File dir;
  private HandlerFactory handlerFactory;

  public DirBasedHandlerRepo(String fn) {
    dir = new File(fn);
    if (!(dir.exists() && dir.isDirectory())) {
      throw new IllegalArgumentException(String.format("%s either does not exist, or is not a directory.",
              fn));
    }
  }

  @Override
  public String[] ls() {
    File[] files = dir.listFiles();
    return Arrays.stream(files).map(file -> file.getName()).toArray(String[]::new);
  }

  @Override
  public Handler getHandler(String name) throws ScriptEvalException {
    Optional<File> scriptFile =
            Arrays.
                    stream(dir.listFiles()).
                    filter(file -> file.getName().equals(name)).
                    findFirst();

    if (!scriptFile.isPresent() || !scriptFile.get().isFile())
      return null;

    Handler ret = null;
    try {
      ret = handlerFactory.getHandler(new FileInputStream(scriptFile.get()));
    } catch (FileNotFoundException e) {
      // File has disappeared since we looked it up... weird, but possible
    }
    return ret;
  }
}
