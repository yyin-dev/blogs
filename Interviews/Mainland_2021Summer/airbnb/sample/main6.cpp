#include <bits/stdc++.h>
using namespace std;

// Very easy problem.

int helper(string s) {
    int res = 0;
    for (int i = 0; i < s.size(); ++i) {
        // suffix: s[i:]
        string suffix = s.substr(i);

        int commonPrefixIdx = 0;
        while (commonPrefixIdx < suffix.size() && s[commonPrefixIdx] == suffix[commonPrefixIdx])
            ++commonPrefixIdx;

        res += commonPrefixIdx;
    }

    return res;
}

vector<int> commonPrefix(vector<string> inputs) {
    // common prefix between 1. original string, and 2. suffixes of string
    int n = inputs.size();
    vector<int> res(n);
    for (int i = 0; i < n; ++i) {
        res[i] = helper(inputs[i]);
    }
    return res;
}

int main() {
    vector<string> strings({"abcabcd", "ababaa"});
    cout << commonPrefix(strings)[0] << endl;
    cout << commonPrefix(strings)[1] << endl;
}