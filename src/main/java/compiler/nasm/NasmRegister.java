package compiler.nasm;

public class NasmRegister extends NasmOperand {
    public int val;
    public int color = Nasm.REG_UNK;

    public NasmRegister(int val){
	this.val = val;
    }

    public void colorRegister(int color){
	this.color = color;
    }

    public String toString(){
	if(this.color == Nasm.REG_ESP) return "esp";
	if(this.color == Nasm.REG_EBP) return "ebp";
	if(this.color == Nasm.REG_EAX) return "eax";
	if(this.color == Nasm.REG_EBX) return "ebx";
	if(this.color == Nasm.REG_ECX) return "ecx";
	if(this.color == Nasm.REG_EDX) return "edx";

	return "r" + this.val;
    }

    public boolean isGeneralRegister(){
	return this.val >=0;
    }

    public <T> T accept(NasmVisitor <T> visitor) {
        return visitor.visit(this);
    }
}
