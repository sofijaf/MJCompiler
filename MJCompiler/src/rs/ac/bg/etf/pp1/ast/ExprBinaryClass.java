// generated with ast extension for cup
// version 0.8
// 15/8/2022 19:19:23


package rs.ac.bg.etf.pp1.ast;

public class ExprBinaryClass extends Expr {

    private ExprList ExprList;
    private BinaryHelper BinaryHelper;
    private ExprList ExprList1;

    public ExprBinaryClass (ExprList ExprList, BinaryHelper BinaryHelper, ExprList ExprList1) {
        this.ExprList=ExprList;
        if(ExprList!=null) ExprList.setParent(this);
        this.BinaryHelper=BinaryHelper;
        if(BinaryHelper!=null) BinaryHelper.setParent(this);
        this.ExprList1=ExprList1;
        if(ExprList1!=null) ExprList1.setParent(this);
    }

    public ExprList getExprList() {
        return ExprList;
    }

    public void setExprList(ExprList ExprList) {
        this.ExprList=ExprList;
    }

    public BinaryHelper getBinaryHelper() {
        return BinaryHelper;
    }

    public void setBinaryHelper(BinaryHelper BinaryHelper) {
        this.BinaryHelper=BinaryHelper;
    }

    public ExprList getExprList1() {
        return ExprList1;
    }

    public void setExprList1(ExprList ExprList1) {
        this.ExprList1=ExprList1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprList!=null) ExprList.accept(visitor);
        if(BinaryHelper!=null) BinaryHelper.accept(visitor);
        if(ExprList1!=null) ExprList1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprList!=null) ExprList.traverseTopDown(visitor);
        if(BinaryHelper!=null) BinaryHelper.traverseTopDown(visitor);
        if(ExprList1!=null) ExprList1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprList!=null) ExprList.traverseBottomUp(visitor);
        if(BinaryHelper!=null) BinaryHelper.traverseBottomUp(visitor);
        if(ExprList1!=null) ExprList1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ExprBinaryClass(\n");

        if(ExprList!=null)
            buffer.append(ExprList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(BinaryHelper!=null)
            buffer.append(BinaryHelper.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExprList1!=null)
            buffer.append(ExprList1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ExprBinaryClass]");
        return buffer.toString();
    }
}
