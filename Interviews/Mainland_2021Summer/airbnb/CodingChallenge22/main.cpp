#include <bits/stdc++.h>
using namespace std;

// Reference: https://www.geeksforgeeks.org/find-recurring-sequence-fraction/
string divide(int dividend, int divisor) {
    if ((double)dividend / divisor < 0) {
        return "-" + divide(abs(dividend), abs(divisor));
    }

    int integer = dividend / divisor;
    int remainder = dividend % divisor;
    if (remainder == 0) {
        return to_string(integer);
    }
    
    string res = to_string(integer) + ".";
    unordered_map<int, int> hmap; // <digit, index>
    while (remainder != 0) {
        remainder *= 10;
        int digit = remainder / divisor;
        
        if (hmap.count(remainder) > 0) {
            res = res.substr(0, hmap[remainder]) + "(" + res.substr(hmap[remainder]) + ")";
            return res;
        }

        hmap[remainder] = res.size();
        res += to_string(digit);

        remainder = remainder % divisor;
    }

    return res;
}

int main() {
    cout << divide(2, 4) << endl;
    cout << divide(-2, -4) << endl;
    cout << divide(6, 3) << endl;
    cout << divide(-6, -3) << endl;
    cout << divide(-1, 3) << endl;
    cout << divide(229, 990) << endl;
    cout << divide(3456919, 9999000) << endl;
}