import java.util.*;

/**
 * SymTable
 * 
 * The class representing the symbol table storing identifiers declared in the
 * program (e.g., function and variable names) and info about each identifier
 * (e.g., its type, where it will be stored at runtime). For now, the only
 * information in a Sym will be the type of the identifier, (e.g., "int",
 * "double", etc.).
 */
public class SymTable {
    /** Symbol table's list for different scopes of the program. */
    private List<HashMap<String, Sym>> tables;

    /**
     * Constructor: constructs a new SymTable.
     */
    public SymTable() {
        this.tables = new LinkedList<HashMap<String, Sym>>();
        this.tables.add(new HashMap<String, Sym>());
    }

    /**
     * Add a new declaration to the current SymTable.
     * 
     * @param name The name of the declaration.
     * @param sym  The Sym object of the declaration.
     * @throws EmptySymTableException   if the symbol table does not contain any
     *                                  scope.
     * @throws IllegalArgumentException if at least one of name or sym is null.
     * @throws DuplicateSymException    if the first HashMap in the list already
     *                                  contains the given name as a key.
     */
    public void addDecl(String name, Sym sym) 
        throws DuplicateSymException, EmptySymTableException {
        if (this.tables.isEmpty()) {
            throw new EmptySymTableException();
        }

        if (name == null || sym == null) {
            throw new IllegalArgumentException();
        }

        if (this.tables.get(0).containsKey(name)) {
            throw new DuplicateSymException();
        } else {
            this.tables.get(0).put(name, sym);
        }
    }

    /**
     * Add a new scope to the symbol table.
     */
    public void addScope() {
        this.tables.add(0, new HashMap<String, Sym>());
    }

    /**
     * Check if a name exists in the local scope.
     * 
     * @param name the name of the symbol.
     * @return true if the local scope contains the name, false otherwise.
     * @throws EmptySymTableException if the symbol table does not contain any
     *                                scope.
     */
    public Sym lookupLocal(String name) throws EmptySymTableException {
        if (this.tables.isEmpty()) {
            throw new EmptySymTableException();
        }

        if (this.tables.get(0).containsKey(name)) {
            return this.tables.get(0).get(name);
        } else {
            return null;
        }
    }

    /**
     * Check if a name exists globally.
     * 
     * @param name the name of the symbol.
     * @return true if any scope contains the name, false otherwise.
     * @throws EmptySymTableException if the symbol table does not contain any
     *                                scope.
     */
    public Sym lookupGlobal(String name) throws EmptySymTableException {
        if (this.tables.isEmpty()) {
            throw new EmptySymTableException();
        }

        for (HashMap<String, Sym> currentTable : this.tables) {
            if (currentTable.containsKey(name)) {
                return currentTable.get(name);
            }
        }

        return null;
    }

    /**
     * Remove the current local scope from the symbol table.
     * 
     * @throws EmptySymTableException if the symbol table does not contain any
     *                                scope.
     */
    public void removeScope() throws EmptySymTableException {
        if (this.tables.isEmpty()) {
            throw new EmptySymTableException();
        }

        this.tables.remove(0);
    }

    /**
     * Method for debugging purpose. First, print "\nSym Table\n". Then, print
     * each scope in the symbol table. All output to System.out.
     */
    public void print() {
        System.out.print("\nSym Table\n");
        for (HashMap<String, Sym> currentTable : this.tables) {
            System.out.println(currentTable.toString());
        }
        System.out.println();
    }
}
