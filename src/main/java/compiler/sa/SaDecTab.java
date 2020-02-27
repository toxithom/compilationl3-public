package compiler.sa;
import compiler.ts.*;

public class SaDecTab implements SaDec{
    private String nom;
    private int taille;
    public TsItemVar tsItem;
    private SaType saType;

    public SaDecTab(String nom, int taille, SaType saType){
	this.nom = nom;
	this.taille = taille;
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

  public int getTaille(){return this.taille;}

  public <T> T accept(SaVisitor <T> visitor) {
        return visitor.visit(this);
    }

}
