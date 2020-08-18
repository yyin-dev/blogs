import java.util.*;

/**
 * Abstract base class for all symbols.
 * 
 * Note that name of the symbol would not be stored. It's directly included
 * in SymTable as key.
 */
abstract public class Sym {
    abstract public String toString();
}

/**
 * VarSym is for non-struct definition.
 */
class VarSym extends Sym {
    private String type;

    public VarSym(String t) {
        this.type = t;
    }

    public String toString() {
        return this.type;
    }
}

/**
 * FnSym is for function declaration.
 */
class FnSym extends Sym {
    private String returnType;
    private List<VarSym> paramSyms;

    public FnSym(String rt, List<VarSym> params) {
        this.returnType = rt;
        this.paramSyms = params;
    }

    public String toString() {
        String str = new String("");
        for (VarSym varSym : this.paramSyms) {
            str += (varSym.toString() + ",");
        }
        if (this.paramSyms.size() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        str += "->";
        str += this.returnType;
        return str;
    }
}

class StructDefSym extends Sym {
    // Contains its own SymTable for field declaration
    private List<Sym> fieldSyms;
    public SymTable symTable; // TODO: fix public

    public StructDefSym(List<Sym> fields, SymTable symTable) {
        this.fieldSyms = fields;
        this.symTable = symTable;
    }

    public String toString() {
        String str = new String("{");
        for (Sym sym: this.fieldSyms) {
            str += (sym.toString() + ", ");
        }
        if (this.fieldSyms.size() > 0) {
            str = str.substring(0, str.length()-2);
        }
        str += "}";
        return str;
    }
}

class StructSym extends Sym {
    private String structType;

    public StructSym(String s) {
        this.structType = s;
    }

    public String getStructType() {
        return this.structType;
    }

    public String toString() {
        return "struct " + this.structType;
    }
}
