// generated with ast extension for cup
// version 0.8
// 15/8/2022 19:19:23


package rs.ac.bg.etf.pp1.ast;

public class DesignatorOptionArray extends Designator {

    private DesignatorHelper DesignatorHelper;
    private Expr Expr;

    public DesignatorOptionArray (DesignatorHelper DesignatorHelper, Expr Expr) {
        this.DesignatorHelper=DesignatorHelper;
        if(DesignatorHelper!=null) DesignatorHelper.setParent(this);
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
    }

    public DesignatorHelper getDesignatorHelper() {
        return DesignatorHelper;
    }

    public void setDesignatorHelper(DesignatorHelper DesignatorHelper) {
        this.DesignatorHelper=DesignatorHelper;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DesignatorHelper!=null) DesignatorHelper.accept(visitor);
        if(Expr!=null) Expr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesignatorHelper!=null) DesignatorHelper.traverseTopDown(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesignatorHelper!=null) DesignatorHelper.traverseBottomUp(visitor);
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorOptionArray(\n");

        if(DesignatorHelper!=null)
            buffer.append(DesignatorHelper.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorOptionArray]");
        return buffer.toString();
    }
}
