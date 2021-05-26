#include <bits/stdc++.h>
using namespace std;

/**
 * Problem: 
 * https://1o24bbs.com/t/topic/12548 
 * https://www.glassdoor.com/Interview/You-re-given-an-array-of-CSV-strings-representing-search-results-Results-are-sorted-by-a-score-initially-A-given-host-may-QTN_2593687.htm 
 *
 * https://leetcode.com/discuss/interview-question/125518/Airbnb-or-Phone-screen-or-Paginate-Listings/936218
 * 
 * 1. Brute force:
 * Data structures:
 * Use list<struct Host> to store hosts; for O(1) removal from list.
 * Use unordered_set<int> to store hostIDs in current page, to avoid duplciate.
 * 
 * Keep scanning the input list, if there's a host whose hostID is not in the 
 * set, add it to the result. If the input list is exhausted, but current page
 * is not filled yet, add host from the head of input list.
 * 
 * Time complexity:
 * In the worse cast, the entire remaining input list need to be scanned for 
 * each page. Let k be the page size, and let n = t * k.
 * 
 * T(n) = T(n-k) + O(n)
 * T(0) = T(0)
 * 
 * => T(n) = O(n^2)
 * 
 * In the brute force approach, you might waste time traversing duplicates.
 * If the list contain many duplicates and resultPerPage is large, the algorithm
 * spends much time traversing duplicates.
 * Example: 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3. ResultPerPage = 5.
 * 
 * After picking the 1st host with hostID=1, can we diretly pick hostID=2?
 * The idea is as follows:
 * After picking hostID=x, skip all other hosts with hostID=x; Only when (1) a 
 * new page is started, or (2) there's no more unique hostID in the page, we
 * consider the duplicates.
 * 
 * 2. hashset + queue
 * Reference:
 * https://leetcode.com/discuss/interview-question/125518/Airbnb-or-Phone-screen-or-Paginate-Listings/296589
 * https://leetcode.com/discuss/interview-question/125518/Airbnb-or-Phone-screen-or-Paginate-Listings/302490
 * It seems wasteful to traverse the list from head, after we exhausted unique
 * hostIDs. Thus, a simple optimization is to add duplicate entries to a 
 * separate queue. This avoid restarting from the head of the list. However, 
 * the time complexity is the same: O(n^2) in the worst case: you may need to
 * access the entire remaining list to create a page. 
 * 
 * 3. priority_queue + temp store
 * Reference: https://leetcode.com/discuss/interview-question/125518/Airbnb-or-Phone-screen-or-Paginate-Listings/197216
 * 1. Read input and construct a separate list<struct Host> for each hostID.
 * 2. Store each list<struct Host> in a priority_queue, in decreasing order of score.
 * 3. Create a temporary store temp. Init count = 0;
 * 4. While priority_qeueue (pq) is not empty:
 *      l = pop from pq
 *      add l.front() to result
 *      ++count
 *      
 *      if (l is not empty)
 *        temp.add(l)
 * 
 *      if (pq.empty() || count == resultsPerPage)
 *        if (count == resultsPerPage)
 *          add page separator to result
 *          count = 0
 * 
 *        for (l : temp)
 *          pq.add(l)
 *        temp.clear()
 *
 * Idea: when removing list from pq to temp, the list won't be 
 * considered anymore, until being added into pq again, which only happens when:
 * (1) start another page, or (2) exhausted unique hostID.
 * Also, note that the decreasing order of score is preserved when a page 
 * contains duplicate.
 * The smartness of this design is that, when a list is removed from pq, you
 * will never consider adding it to the page anymore.
 * 
 * Time complexity:
 * consider the lifetime of a node in list<struct Host>. The same happens:
 * Added to result. Pop from pq, add to temp; add from temp to pq.
 * There're n nodes, so the while loop has complxity O(nlogn). 
 */ 

struct Host {
    string raw;

    int hostID;
    int listingID;
    double score;
    string city;
};

vector<string> split(string s, string delim) {
    // https://stackoverflow.com/a/14266139/9057530
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

struct Host parseHost(string& s) {
    vector<string> words = split(s, ",");

    int h, l;
    double sc;
    string c;
    stringstream(words[0]) >> h;
    stringstream(words[1]) >> l;
    stringstream(words[2]) >> sc;
    stringstream(words[3]) >> c;

    struct Host host = {s, h, l, sc, c};
    return host;
}

// Brute-force
// Time: O(n^2)
// Space: O(n)
vector<string> displayPages(vector<string> hostStrings, int resultsPerPage) {
    list<struct Host> hosts;
    for (string s : hostStrings)
        hosts.push_back(parseHost(s));

    vector<string> res;
    while (!hosts.empty()) {
        bool exhausted = false;
        list<struct Host>::iterator curr = hosts.begin();

        unordered_set<int> thisPage;
        for (int i = 0; i < resultsPerPage && !hosts.empty(); ++i) {
            bool inserted = false;

            while (!exhausted && !inserted) {
                list<struct Host>::iterator temp = curr;
                list<struct Host>::iterator next = ++temp;
                if (thisPage.count(curr->hostID) == 0) {
                    res.push_back(curr->raw);
                    thisPage.insert(curr->hostID);

                    inserted = true;
                    hosts.erase(curr);
                }

                curr = next;

                if (curr == hosts.end()) {
                    exhausted = true;
                }
            }

            if (!inserted) {
                res.push_back(hosts.front().raw);
                hosts.pop_front();
            }
        }

        res.push_back("");
    }

    return res;
}

// Time: O(nlogn)
vector<string> displayPagesFast(vector<string> hostStrings, int resultsPerPage) {
    unordered_map<int, list<struct Host>> hostMap;
    for (string& s : hostStrings) {
        struct Host host = parseHost(s);
        hostMap[host.hostID].push_back(host);
    }

    auto comp = [](list<struct Host> l1, list<struct Host> l2) {
        return l1.front().score < l2.front().score;
    };
    priority_queue<list<struct Host>, vector<list<struct Host>>, decltype(comp)> pq(comp); 
    for (auto iter = hostMap.begin(); iter != hostMap.end(); ++iter) {
        pq.push(iter->second);
    }

    vector<string> res;
    vector<list<struct Host>> used; // temporarily holding used list<struct Host> in current page
    int count = 0;
    while (!pq.empty()) {
        list<struct Host> l = pq.top(); pq.pop();
        struct Host h = l.front(); l.pop_front();
        res.push_back(h.raw);
        ++count;

        if (!l.empty())
            used.push_back(l);

        if (pq.empty() || count == resultsPerPage) {
            // exhausted unique hostIDs

            if (count == resultsPerPage) {
                res.push_back("");
                count = 0;
            }

            // Move `used` into `pq`
            for (list<struct Host> l : used) {
                pq.push(l);
            }
            used.clear();
        }
    }

    if (count > 0) {
        res.push_back("");
    }

    return res;
}

void printRes(vector<string>& v) {
    cout << "[" << endl;
    for (string s : v) {
        cout << "  " << s << endl;
    }
    cout << "]" << endl;
}

int main() {
    vector<string> input = {
        "1,28,300.1,San Francisco",
        "4,5,209.1,San Francisco",
        "20,7,208.1,San Francisco",
        "23,8,207.1,San Francisco",
        "16,10,206.1,Oakland",
        "1,16,205.1,San Francisco",
        "1,31,204.6,San Francisco",
        "6,29,204.1,San Francisco",
        "7,20,203.1,San Francisco",
        "8,21,202.1,San Francisco",
        "2,18,201.1,San Francisco",
        "2,30,200.1,San Francisco",
        "15,27,109.1,Oakland",
        "10,13,108.1,Oakland",
        "11,26,107.1,Oakland",
        "12,9,106.1,Oakland",
        "13,1,105.1,Oakland",
        "22,17,104.1,Oakland",
        "1,2,103.1,Oakland",
        "28,24,102.1,Oakland",
        "18,14,11.1,San Jose",
        "6,25,10.1,Oakland",
        "19,15,9.1,San Jose",
        "3,19,8.1,San Jose",
        "3,11,7.1,Oakland",
        "27,12,6.1,Oakland",
        "1,3,5.1,Oakland",
        "25,4,4.1,San Jose",
        "5,6,3.1,San Jose",
        "29,22,2.1,San Jose",
        "30,23,1.1,San Jose"};
    vector<string> output = displayPagesFast(input, 12);
    printRes(output);
}
