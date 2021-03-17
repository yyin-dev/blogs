// https://blog.csdn.net/sinat_27990891/article/details/105056793
// 阿里：3.23
// 第一题2601提到过，不难
// 第二题可能要用3d DP
#include <iostream>
#include <string>
#include <vector>
using namespace std;

int q1(int n) {
    if (n <= 0) {
        return 0;
    }
    return n * (1 << (n - 1));
}

typedef vector<vector<vector<int>>> dpTable;

int solveMaze(int row, int col, int symMove, vector<vector<char>>& maze, dpTable& dp) {
    // no symMove available, or out of bound
    if (symMove < 0 || row < 0 || row >= maze.size() || col < 0 || col >= maze[0].size()) return -1;

    // trivial cases
    if (dp[row][col][symMove] >= -1) return dp[row][col][symMove];
    if (dp[row][col][symMove] == -2) return -1;

    // dp[row][col][symMove] == -3
    if (maze[row][col] == '#') return -1;
    if (maze[row][col] == 'E') return 0;

    // recursive calls
    dp[row][col][symMove] = -2;
    int fromCurrent = -1;

    vector<int> rowChange({-1, 1, 0, 0});
    vector<int> colChange({0, 0, -1, 1});

    for (int i = 0; i < 4; ++i) {
        int fromThere = solveMaze(row + rowChange[i], col + colChange[i], symMove, maze, dp);
        if (fromThere == -1) continue;

        // fromThere >= 0
        if (fromCurrent == -1) {
            fromCurrent = fromThere + 1;
        } else { // fromCurrent >= 0
            fromCurrent = min(fromCurrent, fromThere + 1);
        }
    }

    int fromSym = solveMaze(maze.size() - 1 - row, maze[0].size() - 1 - col, symMove - 1, maze, dp);
    if (fromCurrent == -1) {
        fromCurrent = fromSym + 1;
    } else { // fromCurrent >= 0
        fromCurrent = min(fromCurrent, fromSym + 1);
    }

    dp[row][col][symMove] = fromCurrent;
    printf("[%d, %d, %d] = %d\n", row, col, symMove, fromCurrent);
    return dp[row][col][symMove];
}

/**
4 4
#S..
E#..
#...
....
 */
int q2() {
    int n, m;  // n: rows, m: cols
    cin >> n >> m;

    int startRow, startCol;
    vector<vector<char>> maze(n, vector<char>(m));
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < m; ++j) {
            cin >> maze[i][j];
            if (maze[i][j] == 'S') {
                startRow = i;
                startCol = j;
            }
        }
    }
    int symMove = 4;
    dpTable dp(n, vector<vector<int>>(m, vector<int>(6, -3)));  // -3: not explored yet
                                                                // -2: being explored now
    int res = solveMaze(startRow, startCol, symMove, maze, dp);
    return res;
}

int main() {
    cout << q2();
}