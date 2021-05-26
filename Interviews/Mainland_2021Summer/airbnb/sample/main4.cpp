#include <bits/stdc++.h>
using namespace std;

// Problem: https://www.geeksforgeeks.org/maximum-index-a-pointer-can-reach-in-n-steps-by-avoiding-a-given-index-b/

void print(vector<vector<int>> vec) {
    for (int i = 0; i < vec.size(); i++) {
        for (int j = 0; j < vec[i].size(); j++) {
            cout << vec[i][j] << ", ";
        }
        cout << endl;
    }
}

// Time: O(steps^3), space: P(steps^3)
int maxIndexRawDP(int steps, int badIndex) {
    // j = 1, ..., steps
    // 1 + 2 + ... + steps = (1 + steps) * (steps/2)

    int maxDistance = steps * (1 + steps) / 2;
    vector<vector<int>> dp(maxDistance + 1, vector<int>(steps + 2, 0));

    for (int i = 0; i < dp.size(); ++i) {
        dp[i][steps + 1] = i;
    }

    for (int j = steps; j >= 1; --j) {
        for (int i = 0; i < dp.size(); ++i) {
            if (i + j == badIndex) {
                dp[i][j] = dp[i][j + 1];
            } else {
                int res1 = j + 1 < steps + 2 ? dp[i][j + 1] : 0;
                int res2 = i + j < dp.size() && j + 1 < steps + 2 ? dp[i + j][j + 1] : 0;
                dp[i][j] = max(res1, res2);
            }
        }
    }
    return dp[0][1];
}

// Time: O(steps^3), space: P(steps^2)
// int maxIndex(int steps, int badIndex) {
//     int maxDistance = steps * (1 + steps) / 2;
//     vector<int> dp(maxDistance+1, 0);
//     vector<int> temp(maxDistance+1, 0);

//     for (int i = 0; i < dp.size(); ++i)
//         dp[i] = i;

//     for (int j = steps; j >= 1; --j) {
//         fill(temp.begin(), temp.end(), 0);

//         for (int i = j*(j+1)/2; i >= 0; --i) {
//             if (i + j == badIndex) {
//                 temp[i] = dp[i];
//             } else {
//                 int res1 = j + 1 < steps + 2 ? dp[i] : 0;
//                 int res2 = i + j < dp.size() && j + 1 < steps + 2 ? dp[i+j] : 0;
//                 temp[i] = max(res1, res2);
//             }
//         }

//         swap(dp, temp);
//     }
//     return dp[0];
// }

// Reference: https://www.geeksforgeeks.org/maximum-index-a-pointer-can-reach-in-n-steps-by-avoiding-a-given-index-b/
// Time: O(N^2).
// Note: max_index can only be in [(1+N) * N/2 - N, (1+N) * N / 2]. Reason: to
// reach max distance, you stay for at most one step: to avoid landing on B.
// The size of each step is at most N.
// So there're at most O(N) values for max_index. For each max_index, it takes
// O(n) time to traverse. Total complexity: O(n^2).
int maxIndex(int N, int B) {
    // Calculate maximum possible
    // index that can be reached
    int max_index = (1 + N) * N / 2;
    int current_index = max_index, step = N;

    while (1) {
        // Check if current index and step
        // both are greater than 0 or not
        while (current_index > 0 && N > 0) {
            // Decrement current_index by step
            current_index -= N;

            // Check if current index is
            // equal to B or not
            if (current_index == B) {
                // Restore to previous index
                current_index += N;
            }

            // Decrement step by one
            N--;
        }

        // If it reaches the 0th index
        if (current_index <= 0) {
            // Print result
            return max_index;
        } else {
            // If max index fails to
            // reach the 0th index

            N = step;
            current_index = --max_index;

            // If current index is equal to B
            if (current_index == B) {
                current_index = --max_index;
            }
        }
    }

    return 0;
}

// Actually, we don't have to treat the problem like a general game-state
// problem, like https://leetcode.com/problems/jump-game/ where the game state
// is complicated. 
// For this problem, the strategy is simple: To reach maximum
// distance, you should stop at most once, to avoid ladding on B.
//
// Case 1: never land on B. Thus, distance is N*(N+1)/2;
// Case 2: will land on B. Thus, stay for the 1st step. And jump for step 2...N.
// The distance will be N*(1+N)/2 - 1.
// 
// Complexity: O(N)

int main() {
    cout << maxIndex(2, 2) << endl;
    cout << maxIndex(4, 6) << endl;
}