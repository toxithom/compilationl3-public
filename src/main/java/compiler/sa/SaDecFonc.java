package compiler.sa;
import compiler.ts.*;

public class SaDecFonc implements SaDec{
    private String nom;
    private SaLDec parametres;
    private SaLDec variables;
    private SaInst corps;
    public TsItemFct tsItem;
    private final SaType saType;

    public SaDecFonc(SaType saType, String nom, SaLDec parametres, SaLDec variables, SaInst corps){
  this.saType = saType;
  this.nom = nom;
	this.parametres = parametres;
	this.variables = variables;
	this.corps = corps;
	this.tsItem = null;
    }

    public String getNom(){return this.nom;}

  @Override
  public SaType getSaType () {
    return saType;
  }

  @Override
  public Type getType () {
    return saType.getType();
  }

  public SaLDec getParametres(){return this.parametres;}
  public SaLDec getVariable(){return this.variables;}
  public SaInst getCorps(){return this.corps;}


  public <T> T accept(SaVisitor <T> visitor) {
        return visitor.visit(this);
    }
}
