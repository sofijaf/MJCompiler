// generated with ast extension for cup
// version 0.8
// 15/8/2022 19:19:23


package rs.ac.bg.etf.pp1.ast;

public class PrintNumConstStmt extends SingleStatement {

    private Expr Expr;
    private Integer length;

    public PrintNumConstStmt (Expr Expr, Integer length) {
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.length=length;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length=length;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Expr!=null) Expr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("PrintNumConstStmt(\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+length);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [PrintNumConstStmt]");
        return buffer.toString();
    }
}
