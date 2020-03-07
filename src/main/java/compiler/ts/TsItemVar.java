package compiler.ts;

import compiler.sa.SaType;
import compiler.sa.Type;

public class TsItemVar implements TsItem {
    public Ts portee;
    public int adresse;
    public String identif;
    public int taille;
    public boolean isParam;
    public final SaType saType;
    //public int adresse;

    public TsItemVar(String identif, SaType saType){
	this.identif = identif;
	this.taille = saType.getSize();
	this.adresse = 0;
	this.saType = saType;
	this.portee = null;
	this.isParam = false;
    }

    public int getTaille(){return this.taille;}
    public int getAdresse(){return this.adresse;}
    public Ts getTable(){return null;}
    public String getIdentif(){return this.identif;}
    public Type getType() {return saType.getType();}

    public String toString(){
	if(this.isParam)
	    return this.identif + "\tPARAM\t" + this.taille + "\t" + this.adresse;
	else
	    return this.identif + "\tVAR  \t" + this.taille + "\t" + this.adresse;
    }

}

