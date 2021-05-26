#include <bits/stdc++.h>
using namespace std;

// Problem: https://leetcode.com/problems/consecutive-numbers-sum/

int consecutive(long n) {
    int maxK = sqrt(2 * n);
    int res = 0;

    for (int k = 2; k <= maxK; ++k) {
        if (k % 2 == 1) {
            if (n % k == 0 && n / k - k / 2 > 0) {
                cout << k << endl;
                ++res;
            }
        } else {
            if (n % k == k / 2) {
                // must use long
                long x = (2 * n + k) / (2 * k);
                long first = x - k / 2;

                cout << fixed << "n: " << n << ", k: " << k << ", x: " << float(2 * n + k) / (2 * k) << ", first: " << first << endl;
                if (first > 0) {
                    cout << k << endl;
                    ++res;
                }
            }
        }
    }

    return res;
}

int main() {
    cout << consecutive(99999999999) << endl;
}