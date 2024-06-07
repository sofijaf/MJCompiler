// generated with ast extension for cup
// version 0.8
// 15/8/2022 19:19:23


package rs.ac.bg.etf.pp1.ast;

public class VarDeclNoErr extends VarDecl {

    private OptFinal OptFinal;
    private Type Type;
    private VariableList VariableList;

    public VarDeclNoErr (OptFinal OptFinal, Type Type, VariableList VariableList) {
        this.OptFinal=OptFinal;
        if(OptFinal!=null) OptFinal.setParent(this);
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.VariableList=VariableList;
        if(VariableList!=null) VariableList.setParent(this);
    }

    public OptFinal getOptFinal() {
        return OptFinal;
    }

    public void setOptFinal(OptFinal OptFinal) {
        this.OptFinal=OptFinal;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public VariableList getVariableList() {
        return VariableList;
    }

    public void setVariableList(VariableList VariableList) {
        this.VariableList=VariableList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptFinal!=null) OptFinal.accept(visitor);
        if(Type!=null) Type.accept(visitor);
        if(VariableList!=null) VariableList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptFinal!=null) OptFinal.traverseTopDown(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(VariableList!=null) VariableList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptFinal!=null) OptFinal.traverseBottomUp(visitor);
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(VariableList!=null) VariableList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclNoErr(\n");

        if(OptFinal!=null)
            buffer.append(OptFinal.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VariableList!=null)
            buffer.append(VariableList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclNoErr]");
        return buffer.toString();
    }
}
