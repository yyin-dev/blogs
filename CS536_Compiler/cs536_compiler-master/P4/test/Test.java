import java.util.*;

public class Test {
    /**
     * One problem about name analysis implementation is that: the Sym is stored
     * in SymTable, a list of HashMaps, while IdNode also holds reference to
     * its corresponding Sym. What happens if the scope is removed from the
     * SymTable? Can IdNode access its Sym after the removal?
     */
    public static void main(String[] args) throws DuplicateSymException, EmptySymTableException {
        SymTable symTab = new SymTable();

        symTab.addScope();
        symTab.addDecl("x", new VarSym("int"));

        symTab.addScope();
        symTab.addDecl("y", new VarSym("bool"));

        symTab.print();
        
        System.out.println(symTab.lookupGlobal("x"));
        System.out.println(symTab.lookupGlobal("y"));
        Sym ySym = symTab.lookupGlobal("y");

        // Remove outer scope
        System.out.println("Remove y's scope");
        symTab.removeScope();
        symTab.print();

        System.out.println(ySym);

        // So even if the HashMap where ySym is stored is deleted
        // the object is not deleted by Java! GC knows you still hold the 
        // reference. (Recall that every non-primitive object in Java is 
        // a reference).

        // The same idea apply to the name analysis process. After IdNode's link
        // to Sym in SymTable is set up, the HashMap is removed when current 
        // scope is finished. However, the Sym can still be accessed through
        // IdNode's link! Life is easy in Java!
    }
}