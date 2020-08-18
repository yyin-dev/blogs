/**
 * This program is designed to test the Sym and SymTable classes.
 * The tests have been broken up into several private helper methods.  The 
 * documentation for each helper method describes in more detail what tests
 * are done in that method.
 * 
 * Problems with Sym and/or SymTable methods are reported to stderr.
 * 
 * @author Beck Hasti, copyright 2014
 */
public class P1 {
    public static void main(String[] args) {
        testSym();
        testExceptions();
        testAddDecl();
        testLookup();
        testPrint();
    }

    /**
     * Helper method for printing error messages. Prints to the standard 
     * error stream (stderr) in the following format:
     * 
     * ERROR[method]: msg
     * 
     * @param method Method in which error occurs
     * @param msg Associated message with further details about error
     */
    private static void error(String method, String msg) {
        System.err.println("ERROR[" + method + "]: " + msg);
    }

    /**
     * Tests the Sym class.
     */
    private static void testSym() {

        // test Sym methods using different types, including the empty 
        // string
        String[] typeList = {"bool", "int", ""};

        for (int i = 0; i < typeList.length; i++) {
            Sym sym = new Sym(new String(typeList[i]));

            String type = sym.getType();
            if (!type.equals(typeList[i])) {
                error("Sym.getType", 
                      "returns wrong value when type is " + typeList[i]);
            }

            type = sym.toString();
            if (!type.equals(typeList[i])) {
                error("Sym.toString", 
                      "returns wrong value when type is " + typeList[i]);
            }
        }

    }

    /**
     * Tests that the SymTable class throws EmptySymTableExceptions
     * and IllegalArgumentExceptions under the correct conditions. 
     *  
     * Also tests:
     * addScope : add HashMap to an empty and non-empty SymTable
     * addDecl : add Sym into empty SymTable (i.e., has no HashMaps)
     * removeScope : remove HashMap from empty and non-empty SymTable
     * 
     * Note: testing DuplicateSymException happens in testInsert
     */
    private static void testExceptions() {
        SymTable symTab;

        // test remove after initially calling constructor
        try {
            symTab = new SymTable();
            symTab.removeScope();  // this should NOT cause an exception
            try {
                symTab.removeScope();  // this SHOULD cause an exception
                error("SymTable.removeScope", 
                      "exception NOT thrown on empty SymTable");
            } catch (EmptySymTableException e) {
                // expected
            } catch (Exception e) {
                error("SymTable.removeScope",
                      "wrong exception thrown on empty SymTable");
            }
        } catch (EmptySymTableException e) {
            error("SymTable.removeScope", "EmptySymTableException thrown " +
                  "after calling SymTable constructor");
        } catch (Exception e) {
            error("SymTable.removeScope", e.toString() +
                  "thrown after calling SymTable constructor");
        }

        // test remove after adding and removing some maps
        try {
            symTab = new SymTable();
            symTab.addScope();
            symTab.addScope(); // should have 3 maps now
            try {
                symTab.removeScope();
                symTab.removeScope();
                symTab.removeScope(); // now should have no maps
                try {
                    symTab.removeScope();  // should cause error
                    error("SymTable.removeScope", 
                          "exception NOT thrown on empty SymTable");
                } catch (EmptySymTableException e) {
                    // expected
                } catch (Exception e) {
                    error("SymTable.removeScope",
                          "wrong exception thrown on empty SymTable");
                }
            } catch (Exception e) {
                error("SymTable.removeScope", "unexpected exception " + 
                      e.toString() + "thrown on NON-empty SymTable");
            }
        } catch (Exception e) {
            error("SymTable.addScope", "unexpected exception " + e.toString());
        }

        // test addDecl throws EmptySymTableException
        try {
            symTab = new SymTable();
            symTab.removeScope();
            try {
                symTab.addDecl("name", new Sym("type"));
                error("SymTable.addDecl", 
                      "EmptySymTableException NOT thrown on empty SymTable");
            } catch (EmptySymTableException e) {
                // expected
            } catch (Exception e) {
                error("SymTable.addDecl", 
                      "wrong exception thrown on empty SymTable");
            }
        } catch (Exception e) {
            error("SymTable.removeScope", "unexpected exception " + 
                  e.toString() + "thrown on NON-empty SymTable");
        }

        // test addDecl throws IllegalArgumentException
        symTab = new SymTable();
        try {
            symTab.addDecl(null, new Sym("type"));
            error("SymTable.addDecl", 
                  "IllegalArgumentException NOT thrown on addDecl(null, sym)");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (Exception e) {
            error("SymTable.addDecl", 
                  "wrong exception thrown on addDecl(null, sym)");
        }

        try {
            symTab.addDecl("name", null);
            error("SymTable.addDecl", 
                  "IllegalArgumentException NOT thrown on addDecl(name, null)");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (Exception e) {
            error("SymTable.addDecl", 
                  "wrong exception thrown on addDecl(name, null)");
        }

        try {
            symTab.addDecl(null, null);
            error("SymTable.addDecl", 
                  "IllegalArgumentException NOT thrown on addDecl(null, null)");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (Exception e) {
            error("SymTable.addDecl", 
                  "wrong exception thrown on addDecl(null, null)");
        }
    }

    /**
     * Tests addDecl method of SymTable:
     * - add into SymTable with 1 HashMap
     * - add into SymTable with >1 HashMap
     * (note: add into SymTable with 0 HashMaps done in testExceptions)
     * - add >1 Sym with unique names
     * - add Sym with duplicate name in SymTable with one HashMap and with >1
     *   HashMap (where name is in 1st HashMap) (should cause exception in 
     *   both cases)
     * - add Sym with name already in a HashMap but not the 1st one (should NOT
     *   cause an exception)
     */
    private static void testAddDecl() {
        SymTable symTab;
        String name1 = "aaa", name2 = "bbb", name3 = "ccc";
        Sym sym1 = new Sym("bool"), sym2 = new Sym("int");

        // add into SymTable with just one HashMap
        try {
            symTab = new SymTable();
            symTab.addDecl(name1, sym1);
            symTab.addDecl(name2, sym2);
            symTab.addDecl(name3, sym1);

            try {
                symTab.addDecl(name2, sym1);
                error("SymTable.addDecl", "exception NOT thrown when " +
                      "duplicate name added with 1 HashMap");
            } catch (DuplicateSymException e) {
                // expected
            } catch (Exception e) {
                error("SymTable.addDecl", "wrong exception thrown when " +
                      "duplicate name added with 1 HashMap");
            }
        } catch (Exception e) {
            error("SymTable.addDecl", "unexpected exception " + 
                  e.toString() + " with 1 HashMap");
        }

        // add into SymTable with >1 HashMap
        try {
            symTab = new SymTable();
            symTab.addDecl(name1, sym1);

            symTab.addScope();
            symTab.addDecl(name2, sym2);

            symTab.addScope();
            symTab.addDecl(name3, sym1);

            try {
                symTab.addDecl(name1, sym2); // should NOT throw exception
            } catch (DuplicateSymException e) {
                error("SymTable.addDecl", 
                      "exception thrown when name in another HashMap added");
            }

            try {
                symTab.addDecl(name3, sym2); // should throw exception
                error("SymTable.addDecl", "exception NOT thrown when " +
                      "duplicate name added with >1 HashMap");
            } catch (DuplicateSymException e) {
                // expected
            } catch (Exception e) {
                error("SymTable.addDecl", "wrong exception thrown when " +
                      "duplicate name added with >1 HashMap");
            }
        } catch (Exception e) {
            error("SymTable.addDecl", "unexpected exception " + 
                  e.toString() + " with >1 HashMap");
        }
    }

    /**
     * Tests lookupLocal and lookupGlobal methods of SymTable class.
     * 
     * lookupLocal: 
     * - both successful and failing lookups in a SymTable with just one 
     *   HashMap, and in a SymTable with multiple HashMaps (including a 
     *   case where lookupLocal should fail, but lookupGlobal should succeed)
     * - lookup after adding a name then calling removeScope
     * - also test lookupLocal in a SymTab with NO HashMap (should just return 
     *   null, no exception)
     *   
     * lookupGlobal: 
     * - both successful and failing lookups in a SymTable with just one 
     *   HashMap, and in a SymTable with multiple HashMaps, including cases 
     *   where the looked-up name is in the first HashMap, the last HashMap, 
     *   and some intermediate HashMap
     * - also test lookupGlobal in a SymTable with NO HashMap (should just 
     *   return null, no exception)
     */
    private static void testLookup() {
        Sym sym, oneSym = new Sym("int");
        SymTable symTab = new SymTable();
        String name;  

        // put one big try-catch around entire method to catch unexpected 
        // exceptions that happen with what should be normal removeScope and
        // addDecl operations
        try { 

            // test lookupLocal and lookupGlobal in a SymTable with no map
            symTab = new SymTable();
            symTab.removeScope();

            try {
                if (symTab.lookupLocal("aaa") != null) {
                    error("SymTable.lookupLocal",
                          "did not throw Exception for SymTable with no maps");
                } 
            } catch (Exception e) {
                //Expected
            }

            try {
                if (symTab.lookupGlobal("aaa") != null) {
                    error("SymTable.lookupGlobal", 
                          "did not throw Exception for SymTable with no maps");
                } 
            } catch (Exception e) {
                //Expected
            }

            // test lookupLocal and lookupGlobal in a SymTable with one map
            symTab = new SymTable();
            if (symTab.lookupLocal("aaa") != null) {
                error("SymTable.lookupLocal", 
                      "null not returned for lookup of aaa in new SymTable");
            }

            if (symTab.lookupGlobal("aaa") != null) {
                error("SymTable.lookupGlobal",
                      "null not returned for lookup of aaa in new SymTable");
            }

            symTab.addDecl("aaa", oneSym);
            if (symTab.lookupLocal("aaa") == null) {
                error("SymTable.lookupLocal", 
                      "unexpected failure for table with 1 item ");
            }
            if (symTab.lookupLocal("a") != null) {
                error("SymTable.lookupLocal", 
                      "unexpected success for table with 1 item ");
            }

            if (symTab.lookupGlobal("aaa") == null) {
                error("SymTable.lookupGlobal", 
                      "unexpected failure for table with 1 item ");
            }
            if (symTab.lookupGlobal("a") != null) {
                error("SymTable.lookupGlobal", 
                      "unexpected success for table with 1 item ");
            }

            // test lookupLocal and lookupGlobal in a SymTable with four maps
            symTab.addScope();
            symTab.addDecl("bbb", oneSym);
            symTab.addScope();
            symTab.addDecl("ccc", oneSym);
            symTab.addScope();
            Sym localSym = new Sym("double");
            symTab.addDecl("ddd", localSym);
            if (symTab.lookupLocal("aaa") != null) {
                error("SymTable.lookupLocal", 
                      "null not returned for lookup of value aaa in 4th map");
            }
            if (symTab.lookupGlobal("aaa") != oneSym) {
                error("SymTable.lookupGlobal", 
                      "bad value returned for lookup of value aaa in 4th map");
            }

            if (symTab.lookupLocal("bbb") != null) {
                error("SymTable.lookupLocal", 
                      "null not returned for lookup of value bbb in 3rd map");
            }
            if (symTab.lookupGlobal("bbb") != oneSym) {
                error("SymTable.lookupGlobal", 
                      "bad value returned for lookup of value bbb in 3rd map");
            }

            if (symTab.lookupLocal("ddd") != localSym) {
                error("SymTable.lookupLocal", 
                      "bad value returned for lookup of value ddd in local map");
            }
            if (symTab.lookupGlobal("ddd") != localSym) {
                error("SymTable.lookupGlobal", 
                      "bad value returned for lookup of value ddd in local map");
            }

            // test local and global lookups after removing a map
            symTab.removeScope();
            if (symTab.lookupLocal("ddd") != null) {
                error("SymTable.lookupLocal", "null not returned for " +
                      "lookup of ddd after removing its table");
            }
            if (symTab.lookupGlobal("ddd") != null) {
                error("SymTable.lookupGlobal", "null not returned for " +
                      "lookup of ddd after removing its table");
            }

            // add 10 items, look them all up both locally and globally,
            // both just after one addition, and after all additions
            symTab = new SymTable();
            name = "b";
            sym = new Sym("float");
            for (int j=0; j<10; j++) {
                try {
                    symTab.addDecl(name, sym);
                } catch (DuplicateSymException e) {
                    error("SymTable.addDecl", "DuplicateSymException for " +
                          "table with 1 HashMap, multiple entries");
                } catch (EmptySymTableException e) {
                    error("SymTable.addDecl", "EmptySymTableException " +
                          "for table with 1 HashMap, multiple entries");
                }
                if (symTab.lookupLocal(name) == null) {
                    error("SymTable.lookupLocal", "unexpected failure " +
                          "for table with 1 HashMap, multiple entries");
                }
                else if (symTab.lookupLocal(name) != sym) {
                    error("SymTable.lookupLocal", "wrong value returned " +
                          "for table with 1 HashMap, multiple entries");
                }
                if (symTab.lookupGlobal(name) == null) {
                    error("SymTable.lookupGlobal", "unexpected failure " +
                          "for table with 1 HashMap, multiple entries");
                }
                else if (symTab.lookupGlobal(name) != sym) {
                    error("SymTable.lookupGlobal", "wrong value returned " +
                          "for table with 1 HashMap, multiple entries");
                }
                name += "b";
            }

            name = "b";
            for (int j=0; j<10; j++) {
                if (symTab.lookupLocal(name) == null) {
                    error("SymTable.lookupLocal", 
                          "unexpected failure for table with 1 HashMap, " +
                          "multiple entries (lookup after adding all)");
                }
                if (symTab.lookupGlobal(name) == null) {
                    error("SymTable.lookupGlobal", 
                          "unexpected failure for table with 1 HashMap, " +
                          "multiple entries (lookup after adding all)");
                }
                name += "b";
            }

            // SymTable with two HashMaps
            // add a new HashMap and try both local and global lookup
            // of entries in the old HashMap
            symTab.addScope();
            name = "b";
            if (symTab.lookupGlobal(name) != sym) {
                error("SymTable.lookupGlobal", "bad value returned for " +
                      "name in non-local HashMap of table with 2 HashMaps");
            }
            for (int j=0; j<10; j++) {
                if (symTab.lookupLocal(name) != null) {
                    error("SymTable.lookupLocal", 
                          "unexpected success for table with 2 HashMaps");
                }
                if (symTab.lookupGlobal(name) != sym) {
                    error("SymTable.lookupGlobal", 
                          "unexpected failure for table with 2 HashMaps ");
                }
                name += "b";
            }

            // add names that are already in the first HashMap to the new
            // HashMap; make sure that they can be added and that they're
            // found by both local and global lookup
            name = "b";
            for (int j=0; j<10; j++) {
                sym = new Sym("float");
                try {
                    symTab.addDecl(name, sym);
                    if (symTab.lookupLocal(name) == null) {
                        error("SymTable.lookupLocal", 
                              "unexpected null for table with 2 HashMaps");
                    }
                    else if (symTab.lookupLocal(name) != sym) {
                        error("SymTable.lookupLocal", 
                              "wrong value returned for table with 2 HashMaps");
                    }
                    if (symTab.lookupGlobal(name) == null) {
                        error("SymTable.lookupGlobal", 
                              "unexpected failure for table with 2 HashMaps ");
                    }
                    else if (symTab.lookupGlobal(name) != sym) {
                        error("SymTable.lookupGlobal", 
                              "unexpected failure for table with 2 HashMaps");
                    }
                } catch (DuplicateSymException e) {
                    error("SymTable.addDecl", 
                          "unexpected DuplicateSymException for add into " +
                          "table with 2 HashMaps");
                } catch (EmptySymTableException e) {
                    error("SymTable.addDecl", 
                          "unexpected EmptySymTableException for add " +
                          "into table with 2 HashMaps");
                }
                name += "b";
            }

            // add names (to the 2nd HashMap) that are NOT in the 1st one
            // make sure both local and global lookup find them
            name = "c";
            for (int j=0; j<10; j++) {
                sym = new Sym("int");
                try {
                    symTab.addDecl(name, sym);
                    if (symTab.lookupLocal(name) == null) {
                        error("SymTable.lookupLocal", 
                              "unexpected failure for table with 2 HashMaps, " +
                              "new name");
                    }
                    else if (symTab.lookupLocal(name) != sym) {
                        error("SymTable.lookupLocal", 
                              "bad value returned for table with 2 HashMaps, " +
                              "new name");
                    }
                    if (symTab.lookupGlobal(name) == null) {
                        error("SymTable.lookupGlobal", 
                              "unexpected failure for table with 2 HashMaps, " +
                              "new name");
                    }
                    else if (symTab.lookupGlobal(name) != sym) {
                        error("SymTable.lookupGlobal",
                              "bad value returned for table with 2 HashMaps, " +
                              "new name");
                    }
                } catch (DuplicateSymException e) {
                    error("SymTable.addDecl", 
                          "unexpected DuplicateSymException for table with 2 " +
                          "HashMaps, new name");
                } catch (EmptySymTableException e) {
                    error("SymTable.addDecl", 
                          "unexpected EmptySymTableException for table " +
                          "with 2 HashMaps, new name");
                }
                name += "b";
            }

            // SymTable with many HashMaps (20 names in each)
            for (int j=0; j<100; j++) {
                Integer tableint = Integer.valueOf(j);
                symTab.addScope();
                for (int k=0; k<20; k++) {
                    Integer symint = Integer.valueOf(k);
                    name = tableint.toString() + symint.toString();
                    sym = new Sym("int");
                    try {
                        symTab.addDecl(name, sym);
                        if (symTab.lookupLocal(name) != sym) {
                            error("SymTable.lookupLocal", 
                                  "unexpected failure for table with many HashMaps");
                        }
                        if (symTab.lookupGlobal(name) != sym) {
                            error("SymTable.lookupGlobal", 
                                  "unexpected failure for table with many HashMaps ");
                        }
                    } catch (DuplicateSymException e) {
                        error("SymTable.addDecl", 
                              "DuplicateSymException for table with many HashMaps");
                    } catch (EmptySymTableException e) {
                        error("SymTable.addDecl", 
                              "EmptySymTableException for table with many HashMaps");
                    }
                }
            }

            // test lookupGlobal on one name from each HashMap
            for (int j=0; j<100; j++) {
                Integer tableint = Integer.valueOf(j);
                name = tableint.toString() + "6";
                if (symTab.lookupGlobal(name) == null) {
                    error("SymTable.lookupGlobal", 
                          "unexpected failure for table with many HashMaps " +
                          "(after all added)");
                }
            }

        } catch (Exception e) {
            error("SymTable", "unexpected exception " + e.toString() + 
                  " using addDecl/removeScope when testing lookup");
        }
    }

    /**
     * Tests the SymTable.print method by calling it with one empty HashMap,
     * with several non-empty HashMaps, and with no HashMaps.
     */
    private static void testPrint() {
        SymTable symTab = new SymTable();
        try {
            symTab.print();
            try {
                symTab.addDecl("aaa", new Sym("bool"));
                symTab.addDecl("bbb", new Sym("int"));
                symTab.addScope();
                symTab.addDecl("ccc", new Sym("void"));
                symTab.addScope();
                symTab.addDecl("ddd", new Sym("double"));
            } catch (Exception e) {
                error("SymTable.addDecl", 
                      "unexpected exception " + e.toString());
            }
            symTab.print();
            
            for (int i = 0; i < 3; i++) {
                try {
                    symTab.removeScope();
                } catch (Exception e) {
                    error("SymTable.removeScope", 
                          "unexpected exception " + e.toString());
                }
                symTab.print();
            } 
            
        } catch (Exception e) {
            error("SymTable.print", 
                  "unexpected exception " + e.toString());
        }
    }
}