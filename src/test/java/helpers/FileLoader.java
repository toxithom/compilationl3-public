package helpers;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

public class FileLoader {
  public static FilenameFilter onlySource = (file, s) -> s.toLowerCase().endsWith(".l");
  public static FilenameFilter onlySa = (file, s) -> s.toLowerCase().endsWith(".sa");

  private FileLoader () {}

  public static File[] listFiles (String directory, FilenameFilter filter) {
    return getFile(directory).listFiles(filter);
   }

  public static File getFile (String filename) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url = loader.getResource(filename);
    assert url != null;
    return new File(url.getPath());
  }
}
