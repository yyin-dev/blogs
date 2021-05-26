#include <bits/stdc++.h>
using namespace std;

// Problem: 
// https://leetcode-cn.com/problems/minimize-rounding-error-to-meet-target/

float roundUpDiff(float f) {
    int i = f + 1;
    return f - i;
}

vector<int> roundPricesToMatchTarget(vector<float> prices, int target) {
    int n = prices.size();
    vector<int> floors(prices.begin(), prices.end());
    int floorSum = accumulate(floors.begin(), floors.end(), 0);

    int roundUps = target - floorSum;
    auto comp = [](pair<int, float>& p1, pair<int, float>& p2) {
        return roundUpDiff(p1.second) > roundUpDiff(p2.second);
    };
    priority_queue<pair<int, float>, vector<pair<int, float>>, decltype(comp)> pq(comp);
    for (int i = 0; i < n; ++i) {
        pq.push({i, prices[i]});
        if (pq.size() > roundUps) {
            pq.pop();
        }
    }

    while (!pq.empty()) {
        auto p = pq.top(); pq.pop();
        floors[p.first] += 1;
    }

    return floors;
}

int main() {
    vector<float> input({0.70, 2.80, 4.90});
    vector<int> res = roundPricesToMatchTarget(input, 8);
    for (auto i = res.begin(); i != res.end(); ++i)
        std::cout << *i << ' ';
}