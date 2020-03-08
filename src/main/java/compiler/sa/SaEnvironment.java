package compiler.sa;
import java.util.*;
import compiler.ts.*;

public class SaEnvironment {
  private int[] vars;
  private int[] args;
  private int returnValue;

  public SaEnvironment (TsItemFct fct) {
	  Ts localTable = fct.getTable();

//		System.out.println("allocation d'un nouvel environnement");
//		System.out.println("nb var = " + localTable.nbVar());
//		System.out.println("nb arg = " + localTable.nbArg());

    // @FIXME :: such a waste
		args = new int[localTable.getAdrArgCourant()];
	  vars = new int[localTable.getAdrVarCourante()];
	  returnValue = 0;
  }

  public int getVar(int i){return vars[i];}
  public void setVar(int i, int val){vars[i] = val;}

  public int getArg(int i){return args[i];}
  public void setArg(int i, int val){args[i] = val;}

  public int getReturnValue(){return returnValue;}
  public void setReturnValue(int val){returnValue = val;}
}
