// generated with ast extension for cup
// version 0.8
// 15/8/2022 19:19:23


package rs.ac.bg.etf.pp1.ast;

public class NoMethodsOption extends MethodsOption {

    public NoMethodsOption () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NoMethodsOption(\n");

        buffer.append(tab);
        buffer.append(") [NoMethodsOption]");
        return buffer.toString();
    }
}
