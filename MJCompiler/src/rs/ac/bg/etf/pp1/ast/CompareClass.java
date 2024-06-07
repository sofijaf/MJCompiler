// generated with ast extension for cup
// version 0.8
// 15/8/2022 19:19:23


package rs.ac.bg.etf.pp1.ast;

public class CompareClass extends CompareExpr {

    private CompareExpr CompareExpr;
    private Factor Factor;

    public CompareClass (CompareExpr CompareExpr, Factor Factor) {
        this.CompareExpr=CompareExpr;
        if(CompareExpr!=null) CompareExpr.setParent(this);
        this.Factor=Factor;
        if(Factor!=null) Factor.setParent(this);
    }

    public CompareExpr getCompareExpr() {
        return CompareExpr;
    }

    public void setCompareExpr(CompareExpr CompareExpr) {
        this.CompareExpr=CompareExpr;
    }

    public Factor getFactor() {
        return Factor;
    }

    public void setFactor(Factor Factor) {
        this.Factor=Factor;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(CompareExpr!=null) CompareExpr.accept(visitor);
        if(Factor!=null) Factor.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(CompareExpr!=null) CompareExpr.traverseTopDown(visitor);
        if(Factor!=null) Factor.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(CompareExpr!=null) CompareExpr.traverseBottomUp(visitor);
        if(Factor!=null) Factor.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CompareClass(\n");

        if(CompareExpr!=null)
            buffer.append(CompareExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Factor!=null)
            buffer.append(Factor.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CompareClass]");
        return buffer.toString();
    }
}
