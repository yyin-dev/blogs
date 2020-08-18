/**
 * TestList
 *
 * This is a class whose sole purpose is to test the List<E> class, which
 * provides the following operations:
 *       no-arg constructor   -- create an empty list
 *       void add(E item)     -- add item to the end of the list
 *       int indexOf(E item)  -- return the index of the first occurrance of
 *                               item in the list, or -1 if there is no such
 *                               occurrance
 *       void remove(int index) -- remove the item at the given position;
 *                                throw an IndexOutOfBoundsException if
 *                                index < 0 or >= size of list
 *
 * This code tests every List operation, including both correct and
 * bad calls to the operation (remove) that can throw an exception.
 * It produces output ONLY if a test fails.
 */

public class TestList {
    public static void main(String[] args) {
        List<Integer> L = new List<Integer>();

        // test indexOf for empty list, item not in list, item only at
        // start of list, only in middle of list, only at end of list,
        // and in more than one place in the list

        // test indexOf for empty list
        if (L.indexOf(1) != -1) {
            System.out.println("indexOf for empty list != -1");
        }

        // add values 0 to 9 to list then test indexOf for all values (at front,
        // middle, end)

        for (int j = 0; j < 10; j++) {
            L.add(j);
        }

        for (int j = 0; j < 10; j++) {
            if (L.indexOf(j) != j) {
                System.out.println(
                        "Wrong result (" + L.indexOf(j) + ") for indexOf(" + j + ") when only 1 instance in list");
            }
        }

        // add values 0 to 9 again and test that indexOf returns FIRST index

        for (int j = 0; j < 10; j++) {
            L.add(j);
        }

        for (int j = 0; j < 10; j++) {
            if (L.indexOf(j) != j) {
                System.out.println(
                        "Wrong result (" + L.indexOf(j) + ") for indexOf(" + j + ") when two instances are in list");
            }
        }

        // remove the first 5 items (values 0-4), then test indexOf again
        // on values 0-4;
        // this tests whether remove really does remove the items

        try {
            for (int j = 0; j < 5; j++) {
                L.remove(0);
            }
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Exception thrown on attempt to remove " + "first item");
        }

        for (int j = 0; j < 5; j++) {
            if (L.indexOf(j) != 5 + j) {
                System.out.println("Wrong result (" + L.indexOf(j) + ") for indexOf(" + j
                        + ") when there are 15 items in the list");
            }
        }

        // there should be 15 items in the list now
        // test remove of first item, a middle item, last item

        try {
            L.remove(0);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Exception thrown on attempt to remove " + "first item");
        }

        try {
            L.remove(7);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Exception thrown on attempt to remove " + "a middle item");
        }

        // now there should be 13 items

        try {
            L.remove(12);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Exception thrown on attempt to remove " + "last item");
        }

        // test attempt to remove with bad index: too low, just 1 too high,
        // and way too high

        List<String> L2 = new List<String>();
        L2.add("a");

        try {
            L2.remove(-1);
            System.out.println("NO exception thrown on attempt to remove " + "item -1");
        } catch (IndexOutOfBoundsException ex) {
        }

        try {
            L2.remove(1);
            System.out.println("NO exception thrown on attempt to remove " + "item 1");
        } catch (IndexOutOfBoundsException ex) {
        }

        try {
            L2.remove(10);
            System.out.println("NO exception thrown on attempt to remove " + "item 10");
        } catch (IndexOutOfBoundsException ex) {
        }
    }
}