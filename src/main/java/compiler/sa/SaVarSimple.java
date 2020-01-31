package compiler.sa;
import ts.*;

public class SaVarSimple implements SaVar{
    public String nom;
    public TsItem tsItem;

    public SaVarSimple(String nom){
	this.nom = nom;
	this.tsItem = null;
    }

    public String getNom(){return this.nom;}

    public <T> T accept(SaVisitor <T> visitor) {
        return visitor.visit(this);
    }

}
