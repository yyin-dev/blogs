#include <bits/stdc++.h>
using namespace std;

// // Very easy problem.

class node {
   public:
    int val;
    node *right, *left;
};

int isPresent(node* root, int val) {
    /*
   The structure of the node is as follows:
      class node {
         public:
             node * left, *right;
             int val;
      };
*/
    if (!root)
        return 0;

    if (root->val == val)
        return 1;
    else if (root->val < val)
        return isPresent(root->right, val);
    else
        return isPresent(root->left, val);
}
