package compiler.sa;
import compiler.ts.*;

public class SaDecVar implements SaDec{
    private String nom;
    private SaType saType;
    public TsItemVar tsItem;

    public SaDecVar(String nom, SaType saType){
	this.nom = nom;
	this.saType = saType;
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

  public <T> T accept(SaVisitor <T> visitor) {
        return visitor.visit(this);
    }
}
