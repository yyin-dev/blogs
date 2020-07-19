#include <iostream>
#include <vector>
using namespace std;

/* https://www.hackerearth.com/practice/data-structures/trees/heapspriority-queues/tutorial/ */
class Heap {
    // Max heap
   private:
    vector<int> heap;
    int size;

    // heapify
    void heapify(int i) {
        int left = i * 2 + 1, right = i * 2 + 2;
        int largest = i;
        if (left <= size - 1 && heap[left] > heap[i]) {
            largest = left;
        }

        if (right <= size - 1 && heap[right] > heap[largest]) {
            largest = right;
        }

        if (largest != i) {
            int maxVal = heap[largest];
            heap[largest] = heap[i];
            heap[i] = maxVal;
            heapify(largest);
        }
    }

    // suppose this->heap has no order, make it a heap
    void buildHeap() {
        for (int i = ((size - 1) / 2) - 1; i >= 0; --i) {
            heapify(i);
        }
    }

   public:
    Heap(int N) : size(N) {}
    Heap(int N, vector<int> nums) : size(N), heap(nums) { buildHeap(); }

    // insert
    void insert(int x) {
        size += 1;
        heap.push_back(x);
        int index = size - 1;

        while (index >= 0) {
            int parentIndex = (index - 1) / 2;
            if (heap[index] > heap[parentIndex]) {
                int maxVal = heap[index];
                heap[index] = heap[parentIndex];
                heap[parentIndex] = maxVal;
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    // pop
    int pop() {
        int maxVal = heap[0];
        heap[0] = heap[size - 1];
        size -= 1;
        heapify(0);
        return maxVal;
    }

    // get max
    int maximum() {
        return heap[0];
    }

    void print() {
        for (auto i : heap) {
            cout << i << " ";
        }
        cout << endl;
    }
};

int main() {
    vector<int> v({1, 2, 3, 4, 5, 6, 7, 8, 9});
    Heap h(v.size(), v);
    h.print();
}
