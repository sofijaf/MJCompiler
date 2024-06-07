// generated with ast extension for cup
// version 0.8
// 15/8/2022 19:19:23


package rs.ac.bg.etf.pp1.ast;

public class TermFactor extends Term {

    private CompareExpr CompareExpr;

    public TermFactor (CompareExpr CompareExpr) {
        this.CompareExpr=CompareExpr;
        if(CompareExpr!=null) CompareExpr.setParent(this);
    }

    public CompareExpr getCompareExpr() {
        return CompareExpr;
    }

    public void setCompareExpr(CompareExpr CompareExpr) {
        this.CompareExpr=CompareExpr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(CompareExpr!=null) CompareExpr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(CompareExpr!=null) CompareExpr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(CompareExpr!=null) CompareExpr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("TermFactor(\n");

        if(CompareExpr!=null)
            buffer.append(CompareExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [TermFactor]");
        return buffer.toString();
    }
}
