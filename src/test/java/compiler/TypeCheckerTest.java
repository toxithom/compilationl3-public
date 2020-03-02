package compiler;

import compiler.sa.Sc2sa;
import compiler.sc.lexer.Lexer;
import compiler.sc.lexer.LexerException;
import compiler.sc.parser.Parser;
import compiler.sc.parser.ParserException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TypeCheckerTest {
  private void check (String code) throws ParserException, IOException, LexerException {
    var tree = new Parser(new Lexer(new PushbackReader(new StringReader(code)))).parse();
    var sc2sa = new Sc2sa();
    tree.apply(sc2sa);
    new TypeChecker(sc2sa.getRoot(), new Sa2ts(sc2sa.getRoot()).getTableGlobale());
  }

  @ParameterizedTest
  @MethodSource
  void shouldRaiseTypeError (String code) {
    assertThrows(TypeChecker.TypeException.class, () -> check(code));
  }

  @SuppressWarnings("unused")
  private static Stream<String> shouldRaiseTypeError () {
    // @TODO :: populate!
    return Stream.of(
      "void main () entier a; { a = true; }",
      "entier main () { retour false; }",
      "void main () { retour 1; }",
      "entier a[2]; entier main () { retour a; }",
      "void main () bool a, entier b; { a = true; b = 1; si a < b alors { ecrire(a); }}",
      "void main () bool a, entier b; { a = true; b = 1; si b < a alors { ecrire(a); }}",
      "void main () bool a, entier b; { a = true; b = 1;  b = b + a; }",
      "void main () bool a, entier b; { a = true; b = 1;  b = b - a; }",
      "void main () bool a, entier b; { a = true; b = 1;  b = b * a; }",
      "void main () bool a, entier b; { a = true; b = 1;  b = b / a; }",
      "void a; void main () { retour a; }",
      "void a[10]; void main () { ecrire(a[0]); }"
    );
  }
}
