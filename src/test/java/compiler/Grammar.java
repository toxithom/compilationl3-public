package compiler;

import compiler.sc.lexer.Lexer;
import compiler.sc.parser.Parser;

import helpers.FileLoader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.Arrays;
import java.util.stream.Stream;

public class Grammar {
  @ParameterizedTest
  @MethodSource
  void shouldNotThrowParserOrLexerException (File file) throws IOException {
    Lexer lexer = new Lexer(new PushbackReader(new FileReader(file)));
    Parser parser = new Parser(lexer);
    assertDoesNotThrow(parser::parse);
  }

  @SuppressWarnings("unused")
  private static Stream<File> shouldNotThrowParserOrLexerException () {
    return Arrays.stream(FileLoader.listFiles("input", FileLoader.onlySource));
  }
}
