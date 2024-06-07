package rs.ac.bg.etf.pp1;

import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {
	
	boolean isFinal; // za FINAL modifikaciju
	
	private int mainPc;
	
	private Stack<Integer> addrToFix = new Stack<>();
	
	public int getMainPc() {
		return mainPc;
	}
	
	@Override
	public void visit(MethodTypeNameVoid methodTypeName) {
		methodTypeName.obj.setAdr(Code.pc);
		if ("main".equalsIgnoreCase(methodTypeName.getName())) {
			mainPc = Code.pc;
		}
		
		// Generate the entry.
		Code.put(Code.enter);
		Code.put(0);
		Code.put(methodTypeName.obj.getLocalSymbols().size());
	}
	
	@Override
	public void visit(MethodTypeNameNoVoid methodTypeName) {
		methodTypeName.obj.setAdr(Code.pc);
		
		// Generate the entry.
		Code.put(Code.enter);
		Code.put(0);
		Code.put(methodTypeName.obj.getLocalSymbols().size());
	}	
	
	@Override
	public void visit(MethodDecl MethodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(ReturnExpr ReturnExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(ReturnNoExpr ReturnNoExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(DesignatorStatementOptionsClassAssignExpression assignment) {
		Obj obj = assignment.getDesignatorTemp().getDesignator().obj;
		if(obj.getKind() == Obj.Elem && obj.getFpPos() == -1) {
			// ako je niz final, moramo da proverimo jel vec inicijalizovan
			// ako nije, smemo da smestimo vrednost
			// ako jeste, ne smemo, nema efekta
		}
		Code.store(obj);
	}
	
	@Override
	public void visit(DesignatorStatementPlusPlus incr) {
		Obj obj = incr.getDesignator().obj;
		if(obj.getKind() == Obj.Elem) {
			Code.put(Code.dup2);
		}
		Code.load(obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(obj);
	}
	
	@Override
	public void visit(DesignatorStatementMinusMinus decr) {
		Obj obj = decr.getDesignator().obj;
		if(obj.getKind() == Obj.Elem) {
			Code.put(Code.dup2);
		}
		Code.load(obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(obj);
	}
	
	@Override
	public void visit(FactorNumConstClass constFactor) {
		Code.loadConst(constFactor.getValue()); 
	}
	
	@Override
	public void visit(FactorCharConstClass constFactor) {
		Code.loadConst(constFactor.getValue()); 
	}
	
	@Override
	public void visit(FactorBoolConstClass constFactor) {
		Code.loadConst(constFactor.getValue() ? 1 : 0); 
	}
	
	@Override
	public void visit(FactorDsgntClass factorDsgnt) {
		Code.load(factorDsgnt.getDesignator().obj);
	}
	
	@Override
	public void visit(FactorTypeExprClass newArray) {
		if(isFinal) {
			// na ExprStack nam se nalazi velicina niza koji zelimo da napravimo
			// treba da je pomnozimo sa 2, pravimo duplo veci niz
			Code.loadConst(2);
			Code.put(Code.mul);
		}
		Code.put(Code.newarray);
		if(newArray.getType().struct == Tab.charType)
			Code.put(0);	// char
		else
			Code.put(1);	// int
		
		Code.put(Code.dup); // nakon ovoga imamo 2x adresu niza na ExprStack
		// prva adresa treba da ostane za rezultat
		// druga nam treba da racunamo broj elemenata niza (duzinu)
		
		if(isFinal) {
			Code.put(Code.enter);
			Code.put(1);
			Code.put(3);
			// dve lokalne promenljive, jedna da cuva adresu poslednjeg elementa u nizu
			// druga da cuva adresu trenutnog elementa
			
			Code.put(Code.load_n); // prvi arg metode
			Code.put(Code.arraylength);
			Code.put(Code.store_1); // duzinu niza pamtimo u prvu lokalnu promenljivu
			
			// trazimo inicijalnu vrednost za brojac i smestamo u drugu lokalnu promenljivu
			Code.put(Code.load_1);
			Code.loadConst(2);
			Code.put(Code.div);
			Code.put(Code.store_2);
			
			int addr = Code.pc;
			Code.put(Code.load_2);
			Code.put(Code.load_1);
			Code.putFalseJump(Code.lt, 0);
			int addr1 = Code.pc - 2;
			
			// inicijalizacija elementa niza nulom
			Code.put(Code.load_n);
			Code.put(Code.load_2);
			Code.loadConst(0);
			Code.put(Code.astore);
			
			// inkrementiranje brojaca
			Code.put(Code.load_2);
			Code.loadConst(1);
			Code.put(Code.add);
			Code.put(Code.store_2);
			
			Code.putJump(addr); // skacemo na proveru uslova petlje
			Code.fixup(addr1);
			
			Code.put(Code.load_n);
			
			Code.put(Code.exit);
		}
		
	}
	
	@Override
	public void visit(PrintNoNumConstStmt printStmt) {
		Code.loadConst(1);
		if(printStmt.getExpr().struct == Tab.charType)
			Code.put(Code.bprint);
		else
			Code.put(Code.print);
	}
	
	@Override
	public void visit(PrintNumConstStmt printStmt) {
		Code.loadConst(printStmt.getLength());
		if(printStmt.getExpr().struct == Tab.charType)
			Code.put(Code.bprint);
		else
			Code.put(Code.print);
	}
	
	@Override
	public void visit(ReadStmt readStmt) {
		Obj obj = readStmt.getDesignator().obj;
		if(obj.getType() == Tab.charType)
			Code.put(Code.bread);
		else
			Code.put(Code.read);
		Code.store(obj);
	}
	
	@Override
	public void visit(ExprListAddOp addExpr) {
		if(addExpr.getAddOp() instanceof OperatorPlus)
			Code.put(Code.add);
		else
			Code.put(Code.sub);
	}
	
	@Override
	public void visit(TermFactorMulOp mulExpr) {
		if(mulExpr.getMulOp() instanceof OperatorMul)
			Code.put(Code.mul);
		else if(mulExpr.getMulOp() instanceof OperatorDiv)
			Code.put(Code.div);
		else
			Code.put(Code.rem);	// mod
	}
	
	@Override
	public void visit(ExprListMinusTerm minusExpr) {
		Code.put(Code.neg);
	}
	
	@Override
	public void visit(DesignatorHelper dsgntHelper) {
		Code.load(dsgntHelper.getDesignator().obj);
	}
	
	@Override
	public void visit(BinaryHelper binaryHelper) {
		Code.put(Code.dup);
		Code.loadConst(0);
		Code.putFalseJump(Code.eq, 0);
		addrToFix.push(Code.pc - 2);
		Code.put(Code.pop);
	}
	
	@Override
	public void visit(ExprBinaryClass binaryExpr) {
		Code.fixup(addrToFix.pop());
	}
	
	/* ******* MODIFIKACIJE ******* */
	
	/* ******* FAKTORIJEL MODIFIKACIJA ******* */
	
//	@Override
//	public void visit(FactorielClass factoriel) {
//		Code.put(Code.enter);
//		Code.put(1);
//		Code.put(1);
//		
//		Code.loadConst(1);
//		
//		int addrUslova = Code.pc;
//		Code.put(Code.load_n);
//		Code.loadConst(1);
//		Code.putFalseJump(Code.gt, 0);
//		int addr = Code.pc - 2;
//		
//		Code.put(Code.load_n);
//		Code.put(Code.mul);
//		
//		Code.putJump(addrUslova);
//		Code.fixup(addr);
//		
//		Code.put(Code.exit);
//	}
	
	/*
	 * treba novi operator da se napravi u sledecem obliku
	 * designator @ expr, i primenjuje se samo na nizove
	 * recimo niz@2 treba da sabere dva simetricna elementa niza - drugi otpozadi i drugi spreda
	 */
	@Override
	public void visit(AtClass atOp) {
		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);
		
		Code.load(atOp.getDesignator().obj);	// simbol za niz, adresa
		Code.put(Code.load_n);
		Code.put(Code.aload);
		
		Code.load(atOp.getDesignator().obj);	// simbol za niz, adresa za aload
		
		Code.load(atOp.getDesignator().obj);	// simbol za niz, adresa za arraylenght
		Code.put(Code.arraylength);	// skida adresu niza sa steka i na stek vraca duzinu niza
		
		Code.loadConst(1);
		Code.put(Code.sub);	// oduzimam 1 da dobijem ineks poslednjeg elementa niza
		Code.put(Code.load_n);	// od poslednjeg treba da oduzmem parametar koji sam dobila
		Code.put(Code.sub);	// da bih dobila simetrican el niza
		// sad imam novi indeks niza koji mi treba
		
		Code.put(Code.aload);
		
		Code.put(Code.add);
		
		Code.put(Code.exit);
	}
	
	/*
	 * [ expr # expr ]
	 * prvi parametar je tipa char i mozemo predpostaviti da je sigurno veliko slovo
	 * drugi parametar je tipa int
	 * rezultat je tipa char
	 * prvi parametar treba da uvecas za onoliko koliki je drugi parametar, ali ako izadjes iz okvira abecede, nastavljas u krug da se vrtis
	 * primeri:
	 * [ 'A' # 2 ] = 'C'
	 * [[ 'A' # 2 ] # -3 ] = 'Z'
	 */
	
	@Override
	public void visit(TarabaClass tarabaOp) {
		Code.loadConst(26);
		Code.put(Code.rem);
		Code.put(Code.add);
		
		Code.put(Code.enter);
		Code.put(1);
		Code.put(2);
		
		Code.put(Code.load_n);
		Code.loadConst('A');
		Code.putFalseJump(Code.lt, 0);
		int addr = Code.pc - 2;
		
		Code.put(Code.load_n);
		Code.loadConst(26);
		Code.put(Code.add);
		Code.put(Code.store_1);
		
		Code.putJump(0);
		int elseAddr = Code.pc - 2;
		Code.fixup(addr);
		
		Code.put(Code.load_n);
		Code.loadConst('Z');
		Code.putFalseJump(Code.gt, 0);
		addr = Code.pc - 2;
		
		Code.put(Code.load_n);
		Code.loadConst(26);
		Code.put(Code.sub);
		Code.put(Code.store_1);
		
		Code.fixup(addr);
		Code.fixup(elseAddr);
		
		Code.put(Code.load_1);
		Code.put(Code.exit);
	}
	
	/*
	 * | a : b : c | = max
	 */
	
	@Override
	public void visit(MaximumClass maxOp) {
		Code.put(Code.enter);
		Code.put(3);
		Code.put(4);
		
		// load_n -> prvi arg funkcije - a
		// load_1 -> drugi arg funkcije - b
		// load_2 -> treci arg funkcije - c
		// load_3 -> lokalna promenljiva za rezultat, inicijalno 0
		
		Code.put(Code.load_n);
		Code.put(Code.store_3);	// u lokalnoj promenljivoj se nalazi a
		
		/*Code.put(Code.load_n);
		Code.put(Code.load_3);
		Code.putFalseJump(Code.gt, 0);
		int ifAddr = Code.pc - 2;
		
		// THEN a > max, u max smesti a
		Code.put(Code.load_n);
		Code.put(Code.store_3);*/
		
		// ELSE IF b > max
		//Code.fixup(ifAddr);
		Code.put(Code.load_1);
		Code.put(Code.load_3);
		Code.putFalseJump(Code.gt, 0);
		int ifAddr = Code.pc - 2;
		
		// THEN b > max, u max smesti b
		Code.put(Code.load_1);
		Code.put(Code.store_3);
		
		// ELSE IF c > max
		Code.fixup(ifAddr);
		Code.put(Code.load_2);
		Code.put(Code.load_3);
		Code.putFalseJump(Code.gt, 0);
		ifAddr = Code.pc - 2;
		
		// THEN c > max, u max smesti c
		Code.put(Code.load_2);
		Code.put(Code.store_3);
		
		Code.fixup(ifAddr);
		
		// Da ostavim max na ExprStack
		Code.put(Code.load_3);
		
		Code.put(Code.exit);
	}
	
	/* ******* MODIFIKACIJA STEPENOVANJE ******* */
	/*
	@Override
	public void visit(StepenTermClass stepenTerm) {
		Code.put(Code.enter);
		Code.put(2);
		Code.put(3);
		
		Code.loadConst(1);	// za rezultat
		Code.put(Code.store_2);
		
		int addrUslova = Code.pc;
		Code.put(Code.load_1);
		Code.loadConst(0);
		Code.putFalseJump(Code.gt, 0);	// dok je vece od 0 nema skoka, izvrsavamo telo while
		int addr = Code.pc - 2;
		
		// telo while
		Code.put(Code.load_2);
		Code.put(Code.load_n);
		Code.put(Code.mul);	// rezultat ostaje na steku
		Code.put(Code.store_2);	// rezultat smestamo u lokalnu promenljivu
		
		// dekrementiranje b
		Code.put(Code.load_1);
		Code.loadConst(1);
		Code.put(Code.sub);	
		Code.put(Code.store_1);
		
		Code.putJump(addrUslova); 	// treba skociti na proveru uslova while
		Code.fixup(addr); 	// iskacem iz while ako uslov nije ispunjen
		
		
		Code.put(Code.load_2); // rezultat ostavljamo na steku
		Code.put(Code.exit);
		
		/*
		 * int a = 2; int b = 5;
		 * int r = 0;
		 * while(b>0) {
		 * 		r*=a;
		 * 		b--;
		 * }
	}*/
	
	/* ******* MODIFIKACIJA FINAL ******* */
	// ZAVRSI
	@Override
	public void visit(DesignatorTemp dsgnTemp) {
		// ako je fpPos -1, niz JESTE final
		if(dsgnTemp.getDesignator().obj.getFpPos() == -1)
			isFinal = true;
		else
			isFinal = false;
	}
	
	/* ******* MODIFIKACIJA COMPARE ******* */
	
	@Override
	public void visit(CompareClass compareExpr) {
		Code.put(Code.enter);
		Code.put(2);
		Code.put(3);
		
		// ?? a = b
		Code.put(Code.load_n);
		Code.put(Code.load_1);
		Code.putFalseJump(Code.eq, 0);
		int addr1 = Code.pc - 2;
		
		// a = b
		Code.loadConst(0);
		Code.put(Code.store_2);
		
		Code.fixup(addr1);
		// ?? a > b
		Code.put(Code.load_n);
		Code.put(Code.load_1);
		Code.putFalseJump(Code.gt, 0);
		int addr2 = Code.pc - 2;
		
		// a > b
		Code.loadConst(1);
		Code.put(Code.store_2);
		
		Code.fixup(addr2);
		// ?? a < b
		Code.put(Code.load_n);
		Code.put(Code.load_1);
		Code.putFalseJump(Code.lt, 0);
		int addr3 = Code.pc - 2;
		
		// a < b
		Code.loadConst(-1);
		Code.put(Code.store_2);
		
		Code.fixup(addr3);
		Code.put(Code.load_2);
		Code.put(Code.exit);
		
		/*
		 * a <=> b
		 * a > b -> 1
		 * a < b -> -1
		 * a = b -> 0*/
	}
}
