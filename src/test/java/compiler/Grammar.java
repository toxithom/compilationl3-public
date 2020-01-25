package compiler;

import compiler.sc.lexer.Lexer;
import compiler.sc.parser.Parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class Grammar {

  @ParameterizedTest
  @MethodSource
  void shouldNotThrowParserException (File file) throws IOException {
    Lexer lexer = new Lexer(new PushbackReader(new FileReader(file.getAbsolutePath())));
    Parser parser = new Parser(lexer);
    assertDoesNotThrow(parser::parse);
  }

  @SuppressWarnings("unused")
  private static Stream<File> shouldNotThrowParserException () {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url = loader.getResource("input");
    assert url != null;
    return Arrays.stream(Objects.requireNonNull(new File(url.getPath()).listFiles()));
  }
}
