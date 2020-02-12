package compiler.sa;

import compiler.sc.lexer.Lexer;
import compiler.sc.lexer.LexerException;
import compiler.sc.parser.Parser;
import compiler.sc.parser.ParserException;
import helpers.FileLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.Arrays;
import java.util.stream.Stream;

class Sc2saTest {
  private final DocumentBuilder db;

  Sc2saTest () throws ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(false);
    dbf.setNamespaceAware(false);
    dbf.setCoalescing(true);
    dbf.setIgnoringElementContentWhitespace(true);
    dbf.setIgnoringComments(true);
    db = dbf.newDocumentBuilder();
  }

  @BeforeAll
  static void buildSaFiles () throws IOException, LexerException, ParserException {
    for (File file : FileLoader.listFiles("input", FileLoader.onlySource)) {
      var tree = new Parser(new Lexer(new PushbackReader(new FileReader(file.getAbsolutePath())))).parse();
      var sc2sa = new Sc2sa();
      tree.apply(sc2sa);
      new Sa2Xml(sc2sa.getRoot(), file.getAbsolutePath().split("\\.")[0]);
    }
  }

  @ParameterizedTest
  @MethodSource
  void compareTrees (File file) throws IOException, SAXException {
    var generated = db.parse(file);
    var ref = db.parse(FileLoader.getFile("sa-ref/" + file.getName()));
    generated.normalizeDocument();
    ref.normalizeDocument();

    assertTrue(generated.isEqualNode(ref));
  }

  @SuppressWarnings("unused")
  private static Stream<File> compareTrees () {
    return Arrays.stream(FileLoader.listFiles("input",  FileLoader.onlySa));
  }
}
