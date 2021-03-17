#include <iostream>
#include <vector>
using namespace std;

/* Source: https://www.nowcoder.com/discuss/388330?type=2&order=0&pos=1&page=1 */

void print(vector<int> const& input) {
    for (int i = 0; i < input.size(); i++) {
        cout << input.at(i) << ' ';
    }
    cout << endl;
}

// quick sort
void qSort(vector<int>& v, int l, int r) {
    if (l >= r) {
        return;
    }

    // take 1st element as pivot
    // [3, 5, 1, 2] => [1, 2, 3, 5]
    int pivotIndex = l, pivot = v[pivotIndex];
    int j = r;
    for (int i = r; i >= l + 1; --i) {
        if (v[i] >= pivot) {
            swap(v[i], v[j]);
            --j;
        }
    }
    swap(v[j], v[pivotIndex]);
    pivotIndex = j;

    // Recursive call
    qSort(v, l, pivotIndex - 1);
    qSort(v, pivotIndex + 1, r);
}

void quickSort(vector<int>& v) {
    qSort(v, 0, v.size() - 1);
}

void testQuickSort() {
    vector<int> v1({5, 4, 3, 2, 1});
    quickSort(v1);
    print(v1);

    vector<int> v2({10, 1, 1, 1, 2, 2, 0});
    quickSort(v2);
    print(v2);
}

// bubble sort
void bubbleSort(vector<int>& v) {
    if (v.size() <= 1) return;

    bool moved = true;

    while (moved) {
        moved = false;
        for (int i = 1; i < v.size(); ++i) {
            if (v[i - 1] <= v[i]) continue;

            // v[i-1] > v[i]
            while (i < v.size() && v[i] < v[i - 1]) {
                swap(v[i], v[i - 1]);
                moved = true;
                ++i;
            }
            break;
        }
    }
}

void testBubbleSort() {
    vector<int> v1({5, 4, 3, 2, 1});
    bubbleSort(v1);
    print(v1);

    vector<int> v2({10, 2, 2, 1, 1, 1, 2, 2, 0});
    bubbleSort(v2);
    print(v2);
}

// implement std::strcpy
// char* strcpy (char *dst, const char *src)
char* _strcpy(char* dst, char* src) {
    char* dstOrig = dst;

    while (*src != '\0') {
        *(dst++) = *(src++);
    }
    *dst = '\0';
    return dstOrig;
}

void testStrCpy() {
    char str1[] = "Hello Geeks!";
    char str2[] = "GeeksforGeeks";
    char str3[40];
    char str4[40];
    char str5[] = "GfG";

    _strcpy(str2, str1);
    _strcpy(str3, "Copy successful");
    _strcpy(str4, str5);
    printf("str1: %s\nstr2: %s\nstr3: %s\nstr4: %s\n", str1, str2, str3, str4);
}

// Recursive definition of catalan number
// C(0) = 1
// C(n+1) = C(0) * C(n) + C(1) * C(n-1) + ... + C(n) * C(0)
int catalanNum(int n) {
    if (n == 0) return 1;

    // Use pen to write down examples!
    vector<int> dp(n + 1, 0);
    dp[0] = 1;
    for (int i = 1; i <= n; ++i) {
        for (int j = 0; j <= (i / 2) - 1; ++j) {
            dp[i] += 2 * dp[j] * dp[i - 1 - j];
        }
        if (i % 2 == 1) {
            dp[i] += dp[i / 2] * dp[i / 2];
        }
    }

    return dp[n];
}

vector<int> catalanNumInRange(int range) {
    if (range <= 0) return {};

    vector<int> dp(1, 1);
    for (int i = 1;; ++i) {
        int i_th = 0;
        for (int j = 0; j <= (i / 2) - 1; ++j) {
            i_th += 2 * dp[j] * dp[i - 1 - j];
        }
        if (i % 2 == 1) {
            i_th += dp[i / 2] * dp[i / 2];
        }

        if (i_th > range) {
            return dp;
        } else {
            dp.push_back(i_th);
            if (i_th == range) {
                return dp;
            }
        }
    }

    return {-1};
}

// List cycle detection
// https://leetcode.com/problems/linked-list-cycle/solution/
// 1. The fast pointer starts at head.next, the slow at head
// 2. In each round, both pointers move. Checking is done at the end of each round.
// 3. Remeber to check for null.

void testCatalanNum() {
    if (catalanNum(0) != 1) cout << "err on 0" << endl;
    if (catalanNum(3) != 5) cout << "err on 3" << endl;
    if (catalanNum(8) != 1430) cout << "err on 8" << endl;
    print(catalanNumInRange(9999999999));
    print(catalanNumInRange(742900));
}

int main() {
    testQuickSort();
    testBubbleSort();
    testStrCpy();
    testCatalanNum();
    return 0;
}