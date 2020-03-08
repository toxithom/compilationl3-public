package compiler.ts;

import compiler.sa.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class TsItemFct implements TsItem {
    public String identif;
    public int nbArgs;
    private Ts table;
    public SaDecFonc saDecFonc;

    public TsItemFct(String identif, int nbArgs, Ts table, SaDecFonc saDecFonc){
	this.identif = identif;
	this.nbArgs = nbArgs;
	this.table = table;
	this.saDecFonc = saDecFonc;
    }

    public int getNbArgs(){return this.nbArgs;}
    public int getTaille(){return 0;}
    public Ts getTable(){return this.table;}
    public String getIdentif(){return this.identif;}
    public Type getType () {return saDecFonc.getType();}
    public List<TsItemVar> getParams () {
      return table.variables.values().stream()
        .filter(v -> v.isParam)
        .sorted(Comparator.comparingInt(TsItemVar::getAdresse))
        .collect(Collectors.toList());
    }
    public String toString(){
    	return this.identif +  "\tFCT\t" + getType() + "\t" + this.nbArgs;
    }

}

