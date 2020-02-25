package compiler;

import compiler.sa.Sc2sa;
import compiler.sc.lexer.Lexer;
import compiler.sc.lexer.LexerException;
import compiler.sc.parser.Parser;
import compiler.sc.parser.ParserException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

// @TODO :: already declared items
class Sa2tsTest {
  private void buildTs(String code) throws ParserException, IOException, LexerException {
    var tree = new Parser(new Lexer(new PushbackReader(new StringReader(code)))).parse();
    var sc2sa = new Sc2sa();
    tree.apply(sc2sa);
    new Sa2ts(sc2sa.getRoot());
  }

  @Test
  void shouldRaiseNWhenMainIsAbsent () {
    assertThrows(NullPointerException.class, () -> buildTs("test () { retour 1; }"));
  }

  @Test
  void shouldRaiseWhenMainHasParameters () {
    assertThrows(RuntimeException.class, () -> buildTs("main (entier a) { retour a; }"));
  }

  @Test
  void shouldNotRaiseWhenShadowingGlobalVariable () {
    assertDoesNotThrow(() -> buildTs("entier a, entier a; main () { main(); }"));
  }

  @Test
  void shouldRaiseWhenShadowingParameter () {
    assertThrows(RuntimeException.class, () -> buildTs("test (entier a) entier a; { test(a); }"));
  }

  @Test
  void shouldRaiseWhenShadowingLocalVariable () {
    assertThrows(RuntimeException.class, () -> buildTs("test () entier a, entier a; { retour a; }"));
  }

  @Test
  void shouldRaiseWhenVariableIdentifierIsUndefined () {
    assertThrows(NullPointerException.class, () -> buildTs("main () { retour a; }"));
  }

  @Test
  void shouldNotRaiseWhenUsingGlobalVariableInsideLocalScope () {
    assertDoesNotThrow(() -> buildTs("entier a; main () { retour a; }"));
  }

  @Test
  void shouldRaiseWhenFunctionIdentifierIsUndefined () {
    assertThrows(NullPointerException.class, () -> buildTs("main () { test(); }"));
  }

  @Test
  void shouldNotRaiseWhenCallingFunctionBeforeDeclaration () {
    assertDoesNotThrow(() -> buildTs("main () { test (); } test () { retour 1; }"));
  }

  @Test
  void shouldRaiseWhenTooManyArguments () {
    assertThrows(IllegalArgumentException.class, () -> buildTs("main () { main(1); }"));
  }

  @Test
  void shouldRaiseWhenTooFewArguments () {
    assertThrows(IllegalArgumentException.class, () -> buildTs(
      "test (entier a) { retour a;} main () { test(); }"));
  }

  @Test
  void shouldRaiseWhenArrayDeclarationInsideLocalScope () {
    assertThrows(RuntimeException.class, () -> buildTs("main () entier a[1]; { main ();}"));
  }

  @Test
  void shouldRaiseWhenArrayDeclarationAsParameter () {
    assertThrows(RuntimeException.class, () -> buildTs("main (entier a[1]) { main ();}"));
  }
}
