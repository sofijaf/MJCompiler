package rs.ac.bg.etf.pp1;
import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;

public class SemanticAnalyzer extends VisitorAdaptor {
	
	boolean isFinal; // za FINAL modifikaciju - setujem ga u visit metodama za FINAL cvor
	boolean errorDetected = false;
	Obj currentMethod = null;
	boolean returnFound = false;
	int nVars;
	
	Struct boolType = new Struct(Struct.Bool);
	Struct lastType;

	Logger log = Logger.getLogger(getClass());
	
	public SemanticAnalyzer() {
		Tab.currentScope.addToLocals(new Obj(Obj.Type, "bool", boolType));
	}

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}
	
	@Override
	public void visit(Program program) {
		Obj mainObj = Tab.currentScope.findSymbol("main");
		if(mainObj != null) {
			if(mainObj.getKind() != Obj.Meth) {
				report_error("Kind simbola main mora biti Meth", program);
			}
			if(mainObj.getType() != Tab.noType) {
				report_error("Tip main metode mora biti void", program);
			}
		} else {
			report_error("Ne postoji simbol sa imenom main", program);
		}
		nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();
	}

	@Override
	public void visit(ProgName progName) {
		if(Tab.currentScope.findSymbol(progName.getName()) == null) {
			progName.obj = Tab.insert(Obj.Prog, progName.getName(), Tab.noType);
		} else {
			progName.obj = Tab.noObj;
			report_error("Simbol sa imenom " + progName.getName() + " vec postoji u trenutnom scope-u", progName);
		}
		Tab.openScope();     	
	}

	@Override
	public void visit(Type type) {
		Obj typeObj = Tab.find(type.getName());
		if (typeObj == Tab.noObj) {
			report_error("Nije pronadjen simbol sa imenom " + type.getName() + " u tabeli simbola", type);
			type.struct = Tab.noType;
		} 
		else {
			if (Obj.Type == typeObj.getKind()) {
				type.struct = typeObj.getType();
			} 
			else {
				report_error("Simbol sa imenom " + type.getName() + " ne predstavlja tip ", type);
				type.struct = Tab.noType;
			}
		}
		lastType = type.struct;
	}
	
	/* ******* METHODS ******* */

	@Override
	public void visit(MethodDecl methodDecl) {
		if (!returnFound && currentMethod.getType() != Tab.noType) {
			report_error("Funkcija " + currentMethod.getName() + " nema return iskaz, a nije void", methodDecl);
		}
		
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		
		returnFound = false;
		currentMethod = null;
	}

	@Override
	public void visit(MethodTypeNameNoVoid methodTypeName) {
		if(Tab.currentScope.findSymbol(methodTypeName.getName()) == null) {
			currentMethod = Tab.insert(Obj.Meth, methodTypeName.getName(), methodTypeName.getType().struct);
			methodTypeName.obj = currentMethod;
		} else {
			currentMethod = Tab.noObj; 
			report_error("Simbol sa imenom " + methodTypeName.getName() + " vec postoji u trenutnom scope-u", methodTypeName);
		}
		Tab.openScope();
	}
	
	@Override
	public void visit(MethodTypeNameVoid methodTypeName) {
		if(Tab.currentScope.findSymbol(methodTypeName.getName()) == null) {
			currentMethod = Tab.insert(Obj.Meth, methodTypeName.getName(), Tab.noType);
			methodTypeName.obj = currentMethod;
		} else {
			currentMethod = Tab.noObj;
			report_error("Simbol sa imenom " + methodTypeName.getName() + " vec postoji u trenutnom scope-u", methodTypeName);
		}
		Tab.openScope();
	}

	@Override
	public void visit(FormParams formPars) {
		if(currentMethod.getName() == "main") {
			report_error("Main metoda ne moze imati parametre", formPars);
		}
	}
	
	/* ******* CONSTANTS ******* */

	@Override
	public void visit(ConstInitNum numConst) {
		if(lastType != Tab.intType)
			report_error("Tip konstante " + numConst.getName() + " nije int.", numConst);
		if(Tab.currentScope.findSymbol(numConst.getName()) == null) {
			Tab.insert(Obj.Con, numConst.getName(), lastType).setAdr(numConst.getValue());
		} else {
			report_error("Simbol sa imenom " + numConst.getName() + " vec postoji u trenutnom scope-u", numConst);
		}
	}

	@Override
	public void visit(ConstInitChar charConst) {
		if(lastType != Tab.charType)
			report_error("Tip konstante " + charConst.getName() + " nije char.", charConst);
		if(Tab.currentScope.findSymbol(charConst.getName()) == null) {
			Tab.insert(Obj.Con, charConst.getName(), lastType).setAdr(charConst.getValue());
		} else {
			report_error("Simbol sa imenom " + charConst.getName() + " vec postoji u trenutnom scope-u", charConst);
		}
	}

	@Override
	public void visit(ConstInitBool boolConst) {
		if(lastType != boolType)
			report_error("Tip konstante " + boolConst.getName() + " nije bool.", boolConst);
		if(Tab.currentScope.findSymbol(boolConst.getName()) == null) {
			Tab.insert(Obj.Con, boolConst.getName(), lastType).setAdr(boolConst.getValue() ? 1 : 0);
		} else {
			report_error("Simbol sa imenom " + boolConst.getName() + " vec postoji u trenutnom scope-u", boolConst);
		}
	}
	
	/* ******* VARIABLES ******* */
	
	@Override
	public void visit(VariableNoArray variableNoArray) {
		if(isFinal)
			report_error("Promenljiva " + variableNoArray.getName() + " nije niz int-ova, pa ne moze biti final", variableNoArray);
		if(Tab.currentScope.findSymbol(variableNoArray.getName()) == null) {
			Tab.insert(Obj.Var, variableNoArray.getName(), lastType);
		} else {
			report_error("Simbol sa imenom " + variableNoArray.getName() + " vec postoji u trenutnom scope-u", variableNoArray);
		}
	}
	
	@Override
	public void visit(VariableArray variableArray) {
		if(Tab.currentScope.findSymbol(variableArray.getName()) == null) {
			Obj insertObj = Tab.insert(Obj.Var, variableArray.getName(), new Struct(Struct.Array, lastType));
			if(isFinal) {
				if(lastType == Tab.intType)
					insertObj.setFpPos(-1); // nizovi koji imaju fpPos -1 su FINAL, tako smo odabrali, indikator
				else
					report_error("Promenljiva " + variableArray.getName() + " nije niz int-ova, pa ne moze biti final", variableArray);
			}
				
		} else {
			report_error("Simbol sa imenom " + variableArray.getName() + " vec postoji u trenutnom scope-u", variableArray);
		}
	}
	
	/* ******* DESIGNATOR ******* */
	
	@Override
	public void visit(DesignatorSingle designatorSingle){
		Obj obj = Tab.find(designatorSingle.getName());
		if (obj == Tab.noObj) { 
			report_error("Simbol sa imenom " + designatorSingle.getName() + " nije deklarisan ", designatorSingle);
		} else {
			if(obj.getKind() == Obj.Var) {
				DumpSymbolTableVisitor dumpVar = new DumpSymbolTableVisitor();
				dumpVar.visitObjNode(obj);
				if(obj.getLevel() == 0) { // globalna
					report_info("Detektovano koriscenje globalne promenljive " + dumpVar.getOutput(), designatorSingle);
				} else { // lokalna
					report_info("Detektovano koriscenje lokalne promenljive " + dumpVar.getOutput(), designatorSingle);
				}
			}
		}
		designatorSingle.obj = obj;
	}

	@Override
	public void visit(DesignatorOptionArray designatorArray) {
		Obj designatorArrayObj = designatorArray.getDesignatorHelper().getDesignator().obj;	// simbol za niz
		boolean errorFound = false;
		if(designatorArrayObj.getType().getKind() != Struct.Array ) {
			errorFound = true;
			designatorArray.obj = Tab.noObj;
			report_error("Designator mora biti tipa Array", designatorArray);
		}
		if(designatorArray.getExpr().struct != Tab.intType) {
			errorFound = true;
			designatorArray.obj = Tab.noObj;
			report_error("Expr mora biti tipa int", designatorArray);
		}
		if(!errorFound) {
			designatorArray.obj = new Obj(Obj.Elem, "", designatorArrayObj.getType().getElemType());
			designatorArray.obj.setFpPos(designatorArrayObj.getFpPos()); // da znamo jel elem niza final
			DumpSymbolTableVisitor dumpVar = new DumpSymbolTableVisitor();
			dumpVar.visitObjNode(designatorArrayObj);
			report_info("Detektovan pristup elementu niza " + dumpVar.getOutput(), designatorArray);
		}
	}

	/* ******* DESIGNATOR STATEMENTS ******* */

	@Override
	public void visit(DesignatorStatementOptionsClassAssignExpression dsgntAssignOp) {
		Obj obj = dsgntAssignOp.getDesignatorTemp().getDesignator().obj;
		if(obj.getKind() != Obj.Var && obj.getKind() != Obj.Elem && obj.getKind() != Obj.Fld)
			report_error("Designator mora biti promenljiva, element niza ili polje klase uz AssignOp", dsgntAssignOp);
		if(!dsgntAssignOp.getExpr().struct.assignableTo(dsgntAssignOp.getDesignatorTemp().getDesignator().obj.getType()))
			report_error("Nekompatibilni tipovi pri dodeli uz AssignOp", dsgntAssignOp);
			
	}

	@Override
	public void visit(DesignatorStatementPlusPlus dsgntPlusPlusOp) {
		Obj obj = dsgntPlusPlusOp.getDesignator().obj;
		if(obj.getKind() != Obj.Var && obj.getKind() != Obj.Elem && obj.getKind() != Obj.Fld)
			report_error("Designator mora biti promenljiva, element niza ili polje klase uz PlusPlusOp", dsgntPlusPlusOp);
		else if(obj.getKind() == Obj.Elem && obj.getFpPos() == -1)
			report_error("Moze da se inkrementira samo element niza koji nije final", dsgntPlusPlusOp);
		if(obj.getType() != Tab.intType)
			report_error("Designator mora biti tipa int uz PlusPlusOp", dsgntPlusPlusOp);
	}

	@Override
	public void visit(DesignatorStatementMinusMinus dsgntMinusMinusOp) {
		Obj obj = dsgntMinusMinusOp.getDesignator().obj;
		if(obj.getKind() != Obj.Var && obj.getKind() != Obj.Elem &&	obj.getKind() != Obj.Fld)
			report_error("Designator mora biti promenljiva, element niza ili polje klase uz MinusMinusOp", dsgntMinusMinusOp);
		else if(obj.getKind() == Obj.Elem && obj.getFpPos() == -1)
			report_error("Moze da se dekrementira samo element niza koji nije final", dsgntMinusMinusOp);
		if(obj.getType() != Tab.intType)
			report_error("Designator mora biti tipa int uz MinusMinusOp", dsgntMinusMinusOp);
	}
	
	/* ******* (SINGLE) STATEMENTS ******* */

	@Override
	public void visit(ReadStmt readStmt) {
		Obj obj = readStmt.getDesignator().obj;
		if(obj.getKind() != Obj.Var && obj.getKind() != Obj.Elem && obj.getKind() != Obj.Fld) {
			report_error("Designator mora biti promenljiva, element niza ili polje klase uz readStatement", readStmt);
		}
		if(obj.getType() != Tab.intType && obj.getType() != Tab.charType && obj.getType() != boolType)
			report_error("Designator mora biti tipa int, char ili bool uz readStatement", readStmt);
	}

	@Override
	public void visit(PrintNoNumConstStmt printStmt) {
		if(printStmt.getExpr().struct != Tab.intType &&
				printStmt.getExpr().struct != Tab.charType &&
				printStmt.getExpr().struct != boolType) {
			report_error("Expr mora biti tipa int, char ili bool uz printStatement", printStmt);
		}
	}

	@Override
	public void visit(PrintNumConstStmt printStmt) {
		if(printStmt.getExpr().struct != Tab.intType &&
				printStmt.getExpr().struct != Tab.charType &&
				printStmt.getExpr().struct != boolType) {
			report_error("Expr mora biti tipa int, char ili bool uz printStatement", printStmt);
		}
	}

	@Override
	public void visit(ReturnExpr returnExpr){
		returnFound = true;
		Struct currMethType = currentMethod.getType();
		if (!currMethType.compatibleWith(returnExpr.getExpr().struct)) {
			report_error("Tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije " + currentMethod.getName(), returnExpr);
		}  	     	
	}

	@Override
	public void visit(ReturnNoExpr returnNoExpr) {
		if(currentMethod.getType() != Tab.noType) {
			report_error("Povratna vrednost funkcije " + currentMethod.getName() + " mora biti VOID", returnNoExpr);
		}
	}
	
	/* ******* EXPR ******* */

	@Override
	public void visit(ExprNoBinaryClass exprNoBinary) {
		exprNoBinary.struct = exprNoBinary.getExprList().struct;
	}

	@Override
	public void visit(ExprBinaryClass exprBinary) {
		Struct t1 = exprBinary.getExprList().struct;
		Struct t2 = exprBinary.getExprList().struct;
		if (t1 == Tab.intType && t2 == Tab.intType)
			exprBinary.struct = Tab.intType;
		else {
			report_error("Nekompatibilni tipovi u izrazu uz BinaryOp", exprBinary);
			exprBinary.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(ExprListAddOp addExpr) {
		Struct t1 = addExpr.getExprList().struct;
		Struct t2 = addExpr.getTerm().struct;
		if (t1 == Tab.intType && t2 == Tab.intType)
			addExpr.struct = Tab.intType;
		else {
			report_error("Nekompatibilni tipovi u izrazu uz AddOp", addExpr);
			addExpr.struct = Tab.noType;
		} 
	}
	
	@Override
	public void visit(ExprListTerm termExpr) {
		termExpr.struct = termExpr.getTerm().struct;
	}
	
	@Override
	public void visit(ExprListMinusTerm minusTermExpr) {
		Struct t1 = minusTermExpr.getTerm().struct;
		if(t1 == Tab.intType)
			minusTermExpr.struct = Tab.intType;
		else {
			report_error("Nekompatibilan tip u izrazu uz MinusTermExpr", minusTermExpr);
			minusTermExpr.struct = Tab.noType;
		}
	}
	
	/* ******* TERM ******* */
	
//	@Override
//	public void visit(TermFactorMulOp mulExpr) {
//		Struct t1 = mulExpr.getTerm().struct;
//		Struct t2 = mulExpr.getFactor().struct;
//		if (t1 == Tab.intType && t2 == Tab.intType)
//			mulExpr.struct = Tab.intType;
//		else {
//			report_error("Nekompatibilni tipovi u izrazu uz MulOp", mulExpr);
//			mulExpr.struct = Tab.noType;
//		} 
//	}
//	
//	@Override
//	public void visit(TermFactor termFactor) {
//		termFactor.struct = termFactor.getFactor().struct;
//	}
	
	// izmenjeno zbog compare modifikacije
	@Override
	public void visit(TermFactorMulOp mulExpr) {
		Struct t1 = mulExpr.getTerm().struct;
		Struct t2 = mulExpr.getCompareExpr().struct;
		if (t1 == Tab.intType && t2 == Tab.intType)
			mulExpr.struct = Tab.intType;
		else {
			report_error("Nekompatibilni tipovi u izrazu uz MulOp", mulExpr);
			mulExpr.struct = Tab.noType;
		} 
	}
	
	// izmenjeno zbog compare modifikacije
	@Override
	public void visit(TermFactor termFactor) {
		termFactor.struct = termFactor.getCompareExpr().struct;
	}
	
	/* ******* FACTOR ******* */
	
	@Override
	public void visit(FactorDsgntClass factorDsgnt) {
		factorDsgnt.struct = factorDsgnt.getDesignator().obj.getType();
	}
	
	@Override
	public void visit(FactorNumConstClass factorNumCnst){
		factorNumCnst.struct = Tab.intType;    	
	}
	
	@Override
	public void visit(FactorCharConstClass factorCharCnst) {
		factorCharCnst.struct = Tab.charType;
	}
	
	@Override
	public void visit(FactorBoolConstClass factorBoolCnst) {
		factorBoolCnst.struct = boolType;
	}
	
	@Override
	public void visit(FactorTypeExprClass factorTypeExpr) {
		if(factorTypeExpr.getExpr().struct != Tab.intType) {
			factorTypeExpr.struct = Tab.noType;
			report_error("Expr mora biti tipa int pri indeksiranju niza", factorTypeExpr);
		} else 
			factorTypeExpr.struct = new Struct(Struct.Array, lastType);
	}
	
	@Override
	public void visit(FactorExprClass factorExpr) {
		factorExpr.struct = factorExpr.getExpr().struct;
	}
	
//	@Override
//	public void visit(FactorielClass factoriel) {
//		if(factoriel.getFactor().struct != Tab.intType) {
//			factoriel.struct = Tab.noType;
//			report_error("Parametar za faktorijel mora biti int", factoriel);
//		} else 
//			factoriel.struct = Tab.intType;
//	}
	
	@Override
	public void visit(AtClass atOp) {
		atOp.struct = Tab.intType;
		if(!atOp.getDesignator().obj.getType().equals(new Struct(Struct.Array, Tab.intType))) {
			report_error("Prvi operand mora biti niz intova", atOp);
			atOp.struct = Tab.noType;
		}
		if(atOp.getFactor().struct != Tab.intType) {
			report_error("Drugi operand mora biti int", atOp);
			atOp.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(TarabaClass tarabaOp) {
		tarabaOp.struct = Tab.charType;
		if(tarabaOp.getExpr().struct != Tab.charType) {
			report_error("Prvi operand mora biti char", tarabaOp);
			tarabaOp.struct = Tab.noType;
		}
		if(tarabaOp.getExpr1().struct != Tab.intType) {
			report_error("Drugi operand mora biti int", tarabaOp);
			tarabaOp.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(MaximumClass maxOp) {
		maxOp.struct = Tab.intType;
		if(maxOp.getExpr().struct != Tab.intType) {
			report_error("Prvi operand mora biti int", maxOp);
			maxOp.struct = Tab.noType;
		}
		if(maxOp.getExpr1().struct != Tab.intType) {
			report_error("Drugi operand mora biti int", maxOp);
			maxOp.struct = Tab.noType;
		}
		if(maxOp.getExpr2().struct != Tab.intType) {
			report_error("Treci operand mora biti int", maxOp);
			maxOp.struct = Tab.noType;
		}
	}
	
	/* ******* MODIFIKACIJA STEPENOVANJE ******* */
	/*
	@Override
	public void visit(StepenTermClass1 term1) {
		term1.struct = term1.getTerm().struct;
	}
	
	@Override
	public void visit(StepenTermClass stepenTerm) {
		Struct t1 = stepenTerm.getStepenTerm().struct;
		Struct t2 = stepenTerm.getTerm().struct;
		if (t1 == Tab.intType && t2 == Tab.intType)
			stepenTerm.struct = Tab.intType;
		else {
			report_error("Nekompatibilni tipovi u izrazu uz stepenovanje", stepenTerm);
			stepenTerm.struct = Tab.noType;
		} 
	}*/
	
	/* ******* MODIFIKACIJA FINAL ******* */
	
	@Override
	public void visit(OptFinalIma optFinal) {
		isFinal = true;
	}
	
	@Override
	public void visit(OptFinalNema optFinal) {
		isFinal = false;
	}
	
	/* ******* OPORAVAK OD GRESKE ******* */
	
	@Override
	public void visit(VarDeclErr varDeclErr) {
		report_info("Uspesan oporavak od sintaksne greske prilikom deklaracije promenljive do ';'", varDeclErr);
	}
	
	@Override
	public void visit(VarDeclarationsErr varDeclarationsErr) {
		report_info("Uspesan oporavak od sintaksne greske prilikom deklaracije promenljive do ','", varDeclarationsErr);
	}
	
	@Override
	public void visit(DesignatorStatementOptionsClassAssignExpressionErr assignErr) {
		report_info("Uspesan oporavak od sintaksne greske pri dodeli vrednosti do ';'", assignErr);
	}
	
	/* ******* COMPARE MODIFIKACIJA ******* */
	
	@Override
	public void visit(CompareClass compareExpr) {
		Struct t1 = compareExpr.getCompareExpr().struct;
		Struct t2 = compareExpr.getFactor().struct;
		if (t1 == Tab.intType && t2 == Tab.intType)
			compareExpr.struct = Tab.intType;
		else {
			report_error("Nekompatibilni tipovi u izrazu uz Compare operator", compareExpr);
			compareExpr.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(NoCompareClass compareExpr) {
		compareExpr.struct = compareExpr.getFactor().struct;
	}
	
	/* ******* ERROR DETECTED ******* */

	public boolean passed() {
		return !errorDetected;
	}
	
}

