package org.headroyce.sean.link;

import java.util.ArrayList;

/**
 * @author Sean Cavalieri
 */

public class BST<D extends Comparable<D>> {

    //root of the tree, links to all other nodes in the Binary Search Tree
    private BSTNode<D> root;

    //class for nodes in the tree
    private class BSTNode<T extends Comparable<T>> {

        //the data stored in the node
        public T data;

        //pointers to the next two nodes in the tree
        public BSTNode<T> left;
        public BSTNode<T> right;
    }

    //New Add after Lecture
    //adds the node and returns the object in the tree which is the same as what is being put in if there is a duplicate
    //O(n)
    public D add(D thingi) {
        BSTNode<D> tbAdded = new BSTNode<D>();
        tbAdded.data = thingi;
        if(root == null) { root = tbAdded; return null; }
        BSTNode<D> cur = root;
        BSTNode<D> par = null;
        D data = null;	//stores the data of the duplicate to be returned
        while(cur != null) {	//finds parent of where to add, and gets data of duplicate
            if(tbAdded.data.compareTo(cur.data) == 0) {		//store data
                data = cur.data;
            }
            if(tbAdded.data.compareTo(cur.data) <= 0) {		//move left
                par = cur;
                cur = cur.left;
            } else {										//move right
                par = cur;
                cur = cur.right;
            }
        }
        if(tbAdded.data.compareTo(par.data) <= 0) {		//add the data tbAdded where it goes
            par.left = tbAdded;
        } else {
            par.right = tbAdded;
        }
        return data;	//return duplicate data to use in main class
    }


    //Traverse inOrder and Return list
    //O(n)
    public ArrayList<D> inOrder() {
        if(root == null) {
            return null;
        }
        //arraylist to store the list of nodes as the traversal acts on them
        ArrayList<D> list = new ArrayList<D>();
        //passes the list through so every cycle can add to it
        helpInOrder(root, list);
        return list;
    }
    //help traverse
    private void helpInOrder(BSTNode<D> cur, ArrayList<D> ls) {
        //base case
        if(cur == null) {
            return;
        }
        //left
        helpInOrder(cur.left, ls);

        //center THIS IS WHERE STUFF HAPPENS WITH A NODE
        //In this case, adds the node's data to the list
        ls.add(cur.data);

//		System.out.println("name: " + cur.name + ", stock: " + cur.stock + ", cost: $" + cur.cost);
        //right
        helpInOrder(cur.right, ls);
    }


    //Better remove method after lecture
    //This method finds the node and its parent to be removed
    //O(n)
    public D remove(D Drem) {
        BSTNode<D> cur = root;
        BSTNode<D> par = null;
        while(cur != null) {	//find the correct node by navigating tree
            if(cur.data.compareTo(Drem) < 0) {
                par = cur;
                cur = cur.right;
            } else if(cur.data.compareTo(Drem) > 0) {
                par = cur;
                cur = cur.left;
            } else {
                return removeNode(par, cur).data;	//call helper function when node is found
            }
        }
        return null;	//if object is not in the tree it returns null
    }
    //helper actually removes
    //O(n)
    private BSTNode<D> removeNode(BSTNode<D> p, BSTNode<D> r){
        if(r.left != null && r.right != null) {		//two children case
            BSTNode<D> rML = r.left;
            BSTNode<D> pRML = r;
            while(rML.right != null) {	//finds right most left node to swap data with for 2 children case
                pRML = rML;
                rML = rML.right;
            }
            D temp = r.data;
            r.data = rML.data;
            rML.data = temp;
            return(removeNode(pRML, rML));	//now that this is simplified to the one/no child case, call method again
        } else {								//one or no children case
            BSTNode<D> s = r.left;
            if(s == null) { s = r.right; }
            if(p == null) {						//if p is null, this is the root being removed
                BSTNode<D> t = root;
                root = s;
                return t;
            }
            if(p.left == r) {
                p.left = s;
            } else {
                p.right = s;
            }
            return r;		//return data of removed node

        }
    }

}
