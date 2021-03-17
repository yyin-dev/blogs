#include <iostream>
#include <string>

using namespace std;

// 1.1 Reverse each space-separated word in string
// "Hello world" => "olleH dlrow"
void reverseByWord() {
}

// 1.2 Reverse the ordering of words, as well as space, in the string,
// "Hello world   a" => "a   world Hello"
void copyInto(const string& src, int srcStart, int len, string& dst, int dstStart) {
    if (dstStart + len - 1 >= dst.size()) { // -1 is necessary
        cout << "Exceed limit of dst!" << endl;
        return;
    }

    if (srcStart + len - 1 >= src.size()) { // -1 is necessary
        cout << "Exceed limit of src!" << endl;
        return;
    }

    for (int i = 0; i < len; ++i) {
        dst[dstStart + i] = src[srcStart + i];
    }
}

/**
 * Reflection:
 * 1. Several options. Assume construct a new string as result. 
 *    (1) Traverse input in normal order. Each time a word/space is found, insert
 *        to the start of the output. This can be inefficient.
 *    (2) Traverse input in normal order. Spceficy the size of result and write
 *        into it instead of inserting new chars.
 *    (3) Traverse input in reverse order and append to the result. However, 
 *        traversing backwards can be hard for implementation.
 *    (4) Reverse the entire string, then do a word-wise reverse. This saves
 *        copying space and also append to result (instead of insert). Example:
 *        https://www.educative.io/edpresso/how-to-reverse-the-order-of-words-in-a-sentence
 * 
 * 2. It's hard to get it right at the first time.
 * 3. Leetcode 151, 186, 577.
 */
string reverseWordOrdering1(const string& s) {
    string reversed(s.size(), ' ');

    int wordStart = 0, wordEnd, spaceStart = -1, spaceEnd;
    while (wordStart < s.size()) {
        // Determine space
        if (s[wordStart] == ' ') {
            if (spaceStart == -1) {
                spaceStart = wordStart;
            }
            ++wordStart;
            continue;
        }

        if (spaceStart != -1) { // This check is necessary.
            spaceEnd = wordStart;

            // Copy space
            // The space is [spaceStart, spaceEnd)
            copyInto(s, spaceStart, spaceEnd - spaceStart, reversed, s.size() - spaceEnd);
            spaceStart = -1;
        }

        // Determine word
        // s[wordStart] is at 1st char of the word
        wordEnd = wordStart;
        while (wordEnd < s.size() && s[wordEnd] != ' ') {
            ++wordEnd;
        }

        // Copy word
        // [wordStart, wordEnd) is the word
        copyInto(s, wordStart, wordEnd - wordStart, reversed, s.size() - wordEnd);

        // Advance wordStart
        wordStart = wordEnd + 1;
    }
    return reversed;
}

void testReverseWordOrdering() {
    string s1("Hello    world__ _");
    string s2(" with leading space   ...");
    cout << reverseWordOrdering1(s1) << "|" << endl;
    cout << reverseWordOrdering1(s2) << "|" << endl;
}

// 2. LCA of binary tree
// Leetcode 543
// https://leetcode.com/problems/diameter-of-binary-tree/discuss/548662/C++-8ms.-Short-and-clear.

int main() {
    testReverseWordOrdering();
}