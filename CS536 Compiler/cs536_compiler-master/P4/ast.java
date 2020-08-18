import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a Wumbo program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Children
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int    <- concrete nameAnalysis()
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode         <- concrete nameAnalysis()
//       StructDeclNode    IdNode, DeclListNode     <- concrete nameAnalysis()

//     [Following 4 nodes only makes recursive calls to subnodes' nameAnalysis().]
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode

//     [Only StructNode requires nameAnalysis(). It needs to check that it's
//      of a struct declared. ]
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode           <- concrete nameAnalysis()

//     [NameAnalysis() of subclasses of StmtNode only does 2 possible things: 
//      1. Add/remove scope;
//      2. Make recursive call of nameAnalysis() on subnodes.
//      For StmtNode, the requirement is that used names are declared, which is
//      just what ExpNode's nameAnalysis() does. So the implementatation is right.]
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       RepeatStmtNode      ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode

//     ExpNode:
//       [NameAnalysis() of following nodes have concrete implementation. In fact,
//        only IdNode, DotAccessNode needs check, as AssignNode and CallExpNode
//        just make recursive calls. 
//        For IdNode and DotAccessNOde, nameAnalysis() should check they're
//        referring to declared names. ]
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --         <- concrete nameAnalysis()
//       DotAccessNode       ExpNode, IdNode    <- concrete nameAnalysis()
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode

//       [NameAnalysis() of classes below just make recursive calls on subnodes.]
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode

// [So given above, the following 6 classes require concrete nameAnalysis():  
//  VarDeclNode, FormalDeclNode, StructDeclNode, StructNode, IdNode, DotAccessExpNode.

//  VarDeclNode:
//  For non-struct variable declaration, check 1. dupliate name; 2. void type.
//  For struct variable declaration, check 1. struct type declared; 2. duplicate name.

//  FormalDeclNode:
//  By grammar, struct formal is not allowed. So just check for duplicate name
//  and void type.

//  StructDeclNode:
//  Check the struct name is no duplicate.
//  Construct a new private symTable for the struct. Check the field declarations.

//  StructNode
//  Check the type is valid.
 
//  IdNode:
//  When in DeclNode (name declared), check for duplicate.
//  When in ExpNode (name used), make links to Sym in symTable.

//  DotAccessNode:
//  Consider `lhs.rhs`.
//  By grammar, lhs can be IdNode or DotAccessNode. 
//  Only if name analysis on lhs succeeds, which means that it's some Sym of
//  struct type, then do name analysis on rhs.
// ]
// 
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of children, or
// internal nodes with a fixed number of children:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of children:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  RepeatStmtNode,
//        CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// ASTnode class (base class for all other kinds of nodes)
// **********************************************************************
abstract class ASTnode {
    /**
     * This method will not be called. It's needed for P4.java to compile.
     */
    public void nameAnalysis(SymTable symTable) {
        System.out.println("This should not be called");
    }

    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);

    // this method can be used by the unparse methods to do indenting
    protected void addIndentation(PrintWriter p, int indent) {
        for (int k = 0; k < indent; k++) {
            p.print(" ");
        }
    }

    // Helper function used for name analysis
    public final boolean addDeclWithoutException(SymTable symTable, String name, Sym sym, int lineNum, int charNum) {
        try {
            symTable.addDecl(name, sym);
            return true;
        } catch (DuplicateSymException e) {
            ErrMsg.fatal(lineNum, charNum, "Multiply declared identifier");
        } catch (Exception e) {
            System.out.println("This should never happen");
        }
        return false;
    }

    // Helper function used for name analysis
    public final void removeScopeWithoutException(SymTable symTable) {
        try {
            symTable.removeScope();
        } catch (Exception e) {
            System.out.println("This should never happen");
        }
    }

    // Helper function used for name analysis
    public final Sym lookupLocalWithoutException(SymTable symTable, String name) {
        try {
            return symTable.lookupLocal(name);
        } catch (Exception e) {
            System.out.println("This should never happen");
            return null;
        }
    }

    // Helper function used for name analysis
    public final Sym lookupGlobalWithoutException(SymTable symTable, String name) {
        try {
            return symTable.lookupGlobal(name);
        } catch (Exception e) {
            System.out.println("This should never happen");
            return null;
        }
    }
}

// **********************************************************************
// ProgramNode, DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

class ProgramNode extends ASTnode {
    public ProgramNode(DeclListNode L) {
        myDeclList = L;
    }

    public void nameAnalysis(SymTable symTable) {
        // symTable is initialized with one Table for the global scope.
        myDeclList.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    private DeclListNode myDeclList;
}

/**
 * DeclListNode and FormalsListNode have similar implementation.
 */
class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;

        // construct this.validSyms and this.validDecls
        validSyms = new LinkedList<Sym>();
        validDecls = new LinkedList<DeclNode>();
    }

    public void nameAnalysis(SymTable symTable) {
        for (DeclNode declNode : myDecls) {
            declNode.nameAnalysis(symTable);
        }

        // Building validSyms and validDecls should be after nameAnalysis has
        // been called on all DeclNode in myDecls. Otherwise, the order of error
        // reporting would be wrong.
        HashSet<String> seenDeclNames = new HashSet<String>();
        for (DeclNode declNode : myDecls) {
            if (seenDeclNames.contains(declNode.myId.toString())) {
                continue;
            }
            seenDeclNames.add(declNode.myId.toString());
            validSyms.add(declNode.getSym());
            validDecls.add(declNode);
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode) it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    public List<Sym> getSyms() {
        return this.validSyms;
    }

    private List<DeclNode> myDecls;
    private List<DeclNode> validDecls;
    private List<Sym> validSyms;
}

/**
 * DeclListNode and FormalsListNode have similar implementation.
 */
class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;

        // construct this.validSyms and this.validFormals
        validFormals = new LinkedList<FormalDeclNode>();
        validSyms = new LinkedList<VarSym>();
    }

    // Building validFormals and validSyms should be after nameAnalysis has been
    // called on all FormalDeclNode in myFormals. Otherwise, the order of error
    // reporting would be wrong.
    public void nameAnalysis(SymTable symTable) {
        for (FormalDeclNode formalDeclNode : myFormals) {
            formalDeclNode.nameAnalysis(symTable);
        }

        HashSet<String> seenFormalNames = new HashSet<String>();
        for (FormalDeclNode formalDeclNode : this.myFormals) {
            if (seenFormalNames.contains(formalDeclNode.getId())) {
                continue;
            }
            seenFormalNames.add(formalDeclNode.getId());
            validSyms.add(formalDeclNode.getSym());
            validFormals.add(formalDeclNode);
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) { // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        }
    }

    // Called by FnDeclNode to construct the FnSym
    public List<VarSym> getSymList() {
        return this.validSyms;
    }

    private List<FormalDeclNode> myFormals;
    private List<FormalDeclNode> validFormals;
    private List<VarSym> validSyms;
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    public void nameAnalysis(SymTable symTable) {
        myDeclList.nameAnalysis(symTable);
        myStmtList.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void nameAnalysis(SymTable symTable) {
        for (StmtNode stmtNode : myStmts) {
            stmtNode.nameAnalysis(symTable);
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }

    private List<StmtNode> myStmts;
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

    public void nameAnalysis(SymTable symTable) {
        for (ExpNode expNode : myExps) {
            expNode.nameAnalysis(symTable);
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) { // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        }
    }

    private List<ExpNode> myExps;
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
    abstract public void nameAnalysis(SymTable symTable);

    abstract public Sym getSym();

    // Used by makeSymTable of DeclListNode
    public void addSymToSymTable(SymTable symTable) {
        addDeclWithoutException(symTable, myId.toString(), this.getSym(), myId.getLineNum(), myId.getCharNum());
    }

    protected IdNode myId;
}

class VarDeclNode extends DeclNode {
    // VarDeclNode could be the declaration of a primitive, or an instance
    // of a struct.
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

    public void nameAnalysis(SymTable symTable) {
        // Bad declaration: "void" for non-function; or bad struct type.
        // For bad declaration, check for duplicate name:
        // If multiply declared name: report error
        // If not multiply declared: do nothing
        if (myType.isStructType()) {
            StructNode castedType = (StructNode) myType;
            if (castedType.isDeclared(symTable) == false) {
                myId.errOnDuplicate(symTable);
            } else {
                if (myId.errOnDuplicate(symTable) == false) { // no duplicate
                    addDeclWithoutException(symTable, myId.toString(), this.getSym(), myId.getLineNum(), myId.getCharNum());
                }
            }
        } else {
            if (myType.toString() == "void") {
                ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declared void");
                myId.errOnDuplicate(symTable);
            } else {
                if (myId.errOnDuplicate(symTable) == false) { // no duplicate
                    addDeclWithoutException(symTable, myId.toString(), this.getSym(), myId.getLineNum(), myId.getCharNum());
                }
            }
        }
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(";");
    }

    public Sym getSym() {
        if (myType instanceof StructNode) {
            return new StructSym(((StructNode) myType).getTypeName());
        }
        return new VarSym(myType.toString());
    }

    private TypeNode myType;
    private int mySize; // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;
}

class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type, IdNode id, FormalsListNode formalList, FnBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
    }

    public void nameAnalysis(SymTable symTable) {
        // Multiply declared function names are allowed.
        // You should still process the formals and the body of the function;
        // don't add a new entry to the current symbol table for the function,
        // but do add a new hashtable to the front of the SymTable's list for
        // the names declared in the formal list and the body.
        addDeclWithoutException(symTable, myId.toString(), this.getSym(), myId.getLineNum(), myId.getCharNum());

        // MyFormalsList and myBody will be in the same scope during name analysis.
        // The scope would be removed after being analyzed.
        symTable.addScope();
        myFormalsList.nameAnalysis(symTable);
        myBody.nameAnalysis(symTable);

        // For testing
        // System.out.print("Function scope symTable for " + myId.toString());
        // symTable.print();

        removeScopeWithoutException(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent + 4);
        p.println("}\n");
    }

    public Sym getSym() {
        return new FnSym(myType.toString(), myFormalsList.getSymList());
    }

    private TypeNode myType;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

    public void nameAnalysis(SymTable symTable) {
        // similar to VarDeclNode
        if (myType.toString() == "void") {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declared void");
            myId.errOnDuplicate(symTable);
        } else {
            if (myId.errOnDuplicate(symTable) == false) { // no duplicate
                addDeclWithoutException(symTable, myId.toString(), this.getSym(), myId.getLineNum(), myId.getCharNum());
            }
        }
    }

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
    }

    public String getId() {
        return this.myId.toString();
    }

    public VarSym getSym() {
        return new VarSym(this.myType.toString());
    }

    private TypeNode myType;
}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
        this.structInternalSymTable = null;
    }

    public void nameAnalysis(SymTable symTable) {
        // According to the grammar, StructDeclNode could only appear at global
        // scope. So when nameAnalysis is called, symTable only contains the
        // table for the global scope.
        // According to the writeup, the name of the struct cannot be the same
        // as a variable or a function declared in the same scope. However, the
        // name of a struct can be the same as a field in another struct declared
        // previously.

        if (myId.errOnDuplicate(symTable) == false) { // no duplicate
            symTable.addScope();
            myDeclList.nameAnalysis(symTable);
            this.structInternalSymTable = symTable.currentScopeAsSymTable();
            removeScopeWithoutException(symTable);
            addDeclWithoutException(symTable, myId.toString(), this.getSym(), myId.getLineNum(), myId.getCharNum());
        }

        // For testing
        // System.out.println("Internal SymTable for struct " + myId.toString());
        // ((StructDefSym) this.getSym()).symTable.print();
        // System.out.println("Global SymTable at this time: ");
        // symTable.print();
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("struct ");
        myId.unparse(p, 0);
        p.println("{");
        myDeclList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("};\n");
    }

    public Sym getSym() {
        return new StructDefSym(myDeclList.getSyms(), this.structInternalSymTable);
    }

    private DeclListNode myDeclList;
    private SymTable structInternalSymTable;
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
    public boolean isStructType() {
        return false;
    }
}

class IntNode extends TypeNode {
    public IntNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }

    public String toString() {
        return "int";
    }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }

    public String toString() {
        return "bool";
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }

    public String toString() {
        return "void";
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
        myId = id;
    }

    public boolean isStructType() {
        return true;
    }

    public boolean isDeclared(SymTable symTable) {
        Sym found = lookupGlobalWithoutException(symTable, myId.toString());
        if (found instanceof StructDefSym == false) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Invalid name of struct type");
            return false;
        }
        return true;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        myId.unparse(p, 0);
    }

    public String getTypeName() {
        return myId.toString();
    }

    public String toString() {
        return "struct " + myId.toString();
    }

    private IdNode myId;
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************
//////////////////////////////////////////////////////////////////////////
// The implementation of nameAnalysis() of all subclasses of StmtNode just
// recursively call nameAnalysis on subnodes. 
//////////////////////////////////////////////////////////////////////////

abstract class StmtNode extends ASTnode {
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

    public void nameAnalysis(SymTable symTable) {
        myAssign.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }

    private AssignNode myAssign;
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void nameAnalysis(SymTable symTable) {
        myExp.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }

    private ExpNode myExp;
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void nameAnalysis(SymTable symTable) {
        myExp.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }

    private ExpNode myExp;
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

    public void nameAnalysis(SymTable symTable) {
        myExp.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 child (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void nameAnalysis(SymTable symTable) {
        myExp.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    private ExpNode myExp;
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

    public void nameAnalysis(SymTable symTable) {
        // If has its own scope
        myExp.nameAnalysis(symTable);

        symTable.addScope();
        myDeclList.nameAnalysis(symTable);
        myStmtList.nameAnalysis(symTable);
        removeScopeWithoutException(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent + 4);
        myStmtList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("}");
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1, StmtListNode slist1, DeclListNode dlist2,
            StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

    public void nameAnalysis(SymTable symTable) {
        // If has its own scope
        myExp.nameAnalysis(symTable);

        symTable.addScope();
        myThenDeclList.nameAnalysis(symTable);
        myThenStmtList.nameAnalysis(symTable);
        removeScopeWithoutException(symTable);

        symTable.addScope();
        myElseDeclList.nameAnalysis(symTable);
        myElseStmtList.nameAnalysis(symTable);
        removeScopeWithoutException(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent + 4);
        myThenStmtList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("}");
        addIndentation(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent + 4);
        myElseStmtList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("}");
    }

    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }

    public void nameAnalysis(SymTable symTable) {
        myExp.nameAnalysis(symTable);

        symTable.addScope();
        myDeclList.nameAnalysis(symTable);
        myStmtList.nameAnalysis(symTable);
        removeScopeWithoutException(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent + 4);
        myStmtList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("}");
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class RepeatStmtNode extends StmtNode {
    public RepeatStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }

    public void nameAnalysis(SymTable symTable) {
        myExp.nameAnalysis(symTable);

        symTable.addScope();
        myDeclList.nameAnalysis(symTable);
        myStmtList.nameAnalysis(symTable);
        removeScopeWithoutException(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("repeat (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent + 4);
        myStmtList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("}");
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

    public void nameAnalysis(SymTable symTable) {
        myCall.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    private CallExpNode myCall;
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void nameAnalysis(SymTable symTable) {
        myExp.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }

    private ExpNode myExp; // possibly null
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
    public Sym symInSymTable(SymTable symTable) {
        return null;
    }
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }

    public void nameAnalysis(SymTable symTable) {
        // Do nothing
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void nameAnalysis(SymTable symTable) {
        // Do nothing
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void nameAnalysis(SymTable symTable) {
        // Do nothing
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }

    private int myLineNum;
    private int myCharNum;
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void nameAnalysis(SymTable symTable) {
        // Do nothing
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }

    private int myLineNum;
    private int myCharNum;
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
        sym = null;
    }

    // Used by ExpNode to add link to symbol
    public void nameAnalysis(SymTable symTable) {
        Sym globalFound = lookupGlobalWithoutException(symTable, myStrVal);
        if (globalFound != null) {
            sym = globalFound;
            return;
        } else {
            ErrMsg.fatal(myLineNum, myCharNum, "Undeclared identifier");
        }
    }

    /**
     * Used by DeclNode to check for duplicated names.
     * Checks whether there's duplicate name in the local scope. If yes, report
     * the error.
     * 
     * @param symTable
     * @return true if duplicate found. Otherwise false.
     */
    public boolean errOnDuplicate(SymTable symTable) {
        Sym localFound = lookupLocalWithoutException(symTable, myStrVal);
        if (localFound != null) {
            ErrMsg.fatal(myLineNum, myCharNum, "Multiply declared identifier");
            return true;
        }
        return false;
    }

    // symInTable should be called only after nameAnalysis()
    public Sym symInSymTable(SymTable symTable) {
        return this.sym;
    }

    public void unparse(PrintWriter p, int indent) {
        String s = new String(myStrVal);
        if (sym instanceof FnSym) {
            s += "(" + sym.toString() + ")";
        } else if (sym instanceof StructSym) {
            s += "(" + ((StructSym) sym).getStructType() + ")";
        } else if (sym instanceof VarSym) {
            s += "(" + sym.toString() + ")";
        }
        p.print(s);
    }

    public void setSym(Sym s) {
        this.sym = s;
    }

    public Sym getSym() {
        return this.sym;
    }

    public int getLineNum() {
        return this.myLineNum;
    }

    public int getCharNum() {
        return this.myCharNum;
    }

    public String toString() {
        return this.myStrVal;
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    private Sym sym;
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        lhs = loc;
        rhs = id;
    }

    public void nameAnalysis(SymTable symTable) {
        // lhs.rhs
        // 1. lhs is a variable declared to be of struct type
        // 2. rhs is the name of the field type associated with lhs
        // According to the grammar, lhs can only be IdNode or DotAccessNode

        lhs.nameAnalysis(symTable);
        // If lhs is not declared, return directly
        if (lhs.symInSymTable(symTable) == null) {
            return;
        }

        // If lhs is declared but not of struct type
        if (lhs.symInSymTable(symTable) instanceof StructSym == false) {
            if (lhs instanceof IdNode) {
                IdNode casted = (IdNode) lhs;
                ErrMsg.fatal(casted.getLineNum(), casted.getCharNum(), "Dot-access of non-struct type");
            } else if (lhs instanceof DotAccessExpNode) {
                DotAccessExpNode casted = (DotAccessExpNode) lhs;
                ErrMsg.fatal(casted.getLineNum(), casted.getCharNum(), "Dot-access of non-struct type");
            }
            return;
        }

        // lhs is declared of struct type, check if rhs is a field of it
        StructSym lhsStructSym = (StructSym) lhs.symInSymTable(symTable);
        String lhsStructType = lhsStructSym.getStructType();
        StructDefSym lhsStructDefSym = (StructDefSym) lookupGlobalWithoutException(symTable, lhsStructType);
        Sym structFieldSym = lookupGlobalWithoutException(lhsStructDefSym.symTable, rhs.toString());
        if (structFieldSym == null) {
            ErrMsg.fatal(rhs.getLineNum(), rhs.getCharNum(), "Invalid struct field name");
            return;
        } else {
            rhs.setSym(structFieldSym);
        }
    }

    public Sym symInSymTable(SymTable symTable) {
        return rhs.getSym();
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        lhs.unparse(p, 0);
        p.print(").");
        rhs.unparse(p, 0);
    }

    public int getLineNum() {
        return rhs.getLineNum();
    }

    public int getCharNum() {
        return rhs.getCharNum();
    }

    private ExpNode lhs;
    private IdNode rhs;
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

    public void nameAnalysis(SymTable symTable) {
        myLhs.nameAnalysis(symTable);
        myExp.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)
            p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myExp.unparse(p, 0);
        if (indent != -1)
            p.print(")");
    }

    private ExpNode myLhs;
    private ExpNode myExp;
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    public void nameAnalysis(SymTable symTable) {
        myId.nameAnalysis(symTable);
        myExpList.nameAnalysis(symTable);
    }

    public void unparse(PrintWriter p, int indent) {
        myId.unparse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");
    }

    private IdNode myId;
    private ExpListNode myExpList; // possibly null
}

/////////////////////////////////////////////////////////////////////////////////
///////// Classes below have trivial implementation for nameAnalysis()
/////////////////////////////////////////////////////////////////////////////////
abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }

    // The same for all subclasses
    public void nameAnalysis(SymTable symTable) {
        myExp.nameAnalysis(symTable);
    }

    protected ExpNode myExp;
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }

    // The same for all subclasses
    public void nameAnalysis(SymTable symTable) {
        myExp1.nameAnalysis(symTable);
        myExp2.nameAnalysis(symTable);
    }

    protected ExpNode myExp1;
    protected ExpNode myExp2;
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" + ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" - ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" * ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" / ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" && ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" || ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" == ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class NotEqualsNode extends BinaryExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" != ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" < ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" > ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" <= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" >= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}
