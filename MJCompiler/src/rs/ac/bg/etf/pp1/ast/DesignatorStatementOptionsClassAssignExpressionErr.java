// generated with ast extension for cup
// version 0.8
// 15/8/2022 19:19:23


package rs.ac.bg.etf.pp1.ast;

public class DesignatorStatementOptionsClassAssignExpressionErr extends DesignatorStatement {

    private DesignatorTemp DesignatorTemp;
    private AssignOp AssignOp;

    public DesignatorStatementOptionsClassAssignExpressionErr (DesignatorTemp DesignatorTemp, AssignOp AssignOp) {
        this.DesignatorTemp=DesignatorTemp;
        if(DesignatorTemp!=null) DesignatorTemp.setParent(this);
        this.AssignOp=AssignOp;
        if(AssignOp!=null) AssignOp.setParent(this);
    }

    public DesignatorTemp getDesignatorTemp() {
        return DesignatorTemp;
    }

    public void setDesignatorTemp(DesignatorTemp DesignatorTemp) {
        this.DesignatorTemp=DesignatorTemp;
    }

    public AssignOp getAssignOp() {
        return AssignOp;
    }

    public void setAssignOp(AssignOp AssignOp) {
        this.AssignOp=AssignOp;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DesignatorTemp!=null) DesignatorTemp.accept(visitor);
        if(AssignOp!=null) AssignOp.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesignatorTemp!=null) DesignatorTemp.traverseTopDown(visitor);
        if(AssignOp!=null) AssignOp.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesignatorTemp!=null) DesignatorTemp.traverseBottomUp(visitor);
        if(AssignOp!=null) AssignOp.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorStatementOptionsClassAssignExpressionErr(\n");

        if(DesignatorTemp!=null)
            buffer.append(DesignatorTemp.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AssignOp!=null)
            buffer.append(AssignOp.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorStatementOptionsClassAssignExpressionErr]");
        return buffer.toString();
    }
}
