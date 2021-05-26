#include <algorithm>
#include <bitset>
#include <climits>
#include <cmath>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <ctime>
#include <deque>
#include <fstream>
#include <iostream>
#include <limits>
#include <list>
#include <map>
#include <numeric>
#include <queue>
#include <set>
#include <sstream>
#include <stack>
#include <string>
#include <unordered_map>
#include <vector>

using namespace std;

// Problem:
// CSV Parsing: https://1o24bbs.com/t/topic/12548

vector<string> split(string s, string delim) {
    vector<string> res;

    size_t last = 0;
    size_t next = 0;
    while ((next = s.find(delim, last)) != string::npos) {
        res.push_back(s.substr(last, next - last));
        last = next + 1;
    }
    res.push_back(s.substr(last));
    return res;
}

pair<string, int> getOne(string s, int start) {
    // invariant: start == 0 || s[start-1] = ','
    // a,b,c

    string res;
    bool quoted = false;
    int curr = start;
    while (curr < s.size()) {
        if (s[curr] == '"') {
            if (!quoted) {
                // first quote, must be the starting quote of the field
                quoted = true;
            } else {
                bool escapeQuote = curr + 1 < s.size() && s[curr + 1] == '"';
                if (!escapeQuote) {
                    // must be closing quote of field end
                    return {res, curr + 2};
                } else {
                    // escape quote, just ignore
                    res += '"';
                    ++curr;
                }
            }
        } else if (s[curr] == ',' && !quoted) {
            // end
            return {res, curr + 1};
        } else {
            res += s[curr];
        }

        ++curr;
    }

    return {res, curr};
}

vector<string> getFields(string s) {
    vector<string> res;

    int start = 0;
    while (start < s.size()) {
        auto p = getOne(s, start);
        res.push_back(p.first);
        start = p.second;
    }

    return res;
}

void parse(string line) {
    // first_name,last_name,email,interests,notes,city,age
    vector<string> fields = getFields(line);
    string firstName = fields[0];
    string lastName = fields[1];
    string email = fields[2];
    string interests = fields[3];
    string notes = fields[4];
    string city = fields[5];
    string age = fields[6];

    cout << firstName << ", " << age << " years old, is from " << city << " and is interested in " << interests << "." << endl;
}

int main() {
    vector<string> lines({
        "Weronika,Zaborska,njkfdsv@dsgfk.sn,\"running, sci-fi\",new,Krakow,25",
        "Ryuichi,Akiyama,jkg@ljnsfd.fjn,music,guide,Tokyo,65",
        "Elena,Martinez,emrt@lsofnbr.rt,\"cooking, traveling\",superhost,Valencia,42",
        "\"John \"\"Mo\"\"\",Smith,sfn@flkaei.km,biking and hiking,,\"Seattle, WA\",23",
    });

    // for (string line; getline(cin, line); ) {
    //     parse(line);
    // }

    // testing
    for (string line : lines) {
        parse(line);
    }

    return 0;
}