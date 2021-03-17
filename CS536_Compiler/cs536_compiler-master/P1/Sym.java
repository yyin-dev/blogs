public class Sym {
    /** Type of the symbol. */
    private String type;

    /**
     * Construct a symbol with type.
     * 
     * @param type type of the symbol.
     */
    public Sym(String type) {
        this.type = type;
    }

    /**
     * Return the type of the symbol.
     * 
     * @return the type of the symbol
     */
    public String getType() {
        return this.type;
    }

    /**
     * Return the string representation of the symbol.
     * 
     * @return the string representation of the symbol.
     */
    public String toString() {
        return this.type;
    }
}
