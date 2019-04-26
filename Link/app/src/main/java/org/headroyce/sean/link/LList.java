package org.headroyce.sean.link;

/**
 * @author Sean Cavalieri
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class LList<T> implements List<T> {

    private Node<T> head;

    public class Node<D> {

        private D data;
        private Node<D> next;

        //getters
        public Node<D> getNext() { return next; }
        public D getData() { return data; }

        //setters
        public void setNext(Node<D> n) { next = n; }
        public void setData(D d) { data = d; }
    }

    //O(n)
    @Override
    public int size() {
        // TODO Auto-generated method stub
        int size = 0;
        Node<T> cur = head;
        while(cur != null) {
            size++;
            cur = cur.getNext();
        }
        return size;
    }
    //O(1)
    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return head == null;
    }

    @Override
    public boolean contains(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public Iterator<T> iterator() {
        // TODO Auto-generated method stub
        return new iter();
    }

    //Iterator class for object returned above. Allows for For Each loops!
    public class iter implements Iterator<T> {

        Node<T> cur = head;

        //O(1)
        @Override
        public boolean hasNext() {

            // TODO Auto-generated method stub
            if(cur == null) { return false; }
            return cur != null;

        }

        //O(1)
        @Override
        public T next() {
            // TODO Auto-generated method stub
            Node<T> temp = cur;
            cur = cur.getNext();

            return temp.getData();
        }

    }


    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public Object[] toArray(Object[] a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    //O(n)
    @Override
    public boolean add(T o) {
        // TODO Auto-generated method stub

        if(head == null) {
            Node<T> next1 = new Node<T>();
            next1.setData(o);
            head = next1;
            return true;
        }
        Node<T> cur = head;
        while(cur.getNext() != null) {
            cur = cur.getNext();
        }
        Node<T> next = new Node<T>();
        next.setData(o);
        cur.setNext(next);
        return true;
    }

    //O(n)
    @Override
    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        if(head == null) { return false; }
        if(head.getData().equals(o)){
            head = head.getNext();
            return true;
        }
        for(Node<T> cur = head; cur.getNext() != null; cur = cur.getNext()) {
            if(cur.getNext().getData().equals(o)) {
                if(cur.getNext().getNext() != null) {
                    cur.setNext(cur.getNext().getNext());
                } else {
                    cur.setNext(null);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public boolean addAll(Collection c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public boolean addAll(int index, Collection c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public boolean removeAll(Collection c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public boolean retainAll(Collection c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        head = null;
    }

    //O(n)
    @Override
    public T get(int index) {
        // TODO Auto-generated method stub
        if(head == null) { return null; }
        if(index >= this.size() || index < 0) {
            throw new IndexOutOfBoundsException("index does not exist in LList");
        }
        int ind = 0;
        Node<T> cur = head;
        while(cur.getNext() != null && ind != index) {
            ind++;
            cur = cur.getNext();
        }
        return cur.getData();

    }

    @Override
    public Object set(int index, Object element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void add(int index, Object element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    //O(n)
    @Override
    public T remove(int index) {
        // TODO Auto-generated method stub
        if(head == null || index >= this.size()) { return null; }
        if(index == 0) {
            Node<T> temp0 = head;
            if(head.getNext() == null) {
                head = null;
            } else {
                head = head.getNext();
            }
            return temp0.getData();
        }
        Node<T> cur = head;
        int ind = 0;
        while(cur.getNext() != null) {
            if(ind == index - 1) {
                Node<T> temp = cur.getNext();
                if(cur.getNext().getNext() == null) {
                    cur.setNext(null);
                    return temp.getData();
                }
                cur.setNext(cur.getNext().getNext());
                return temp.getData();
            }
            ind++;
            cur = cur.getNext();
        }
        return null;

    }

    @Override
    public int indexOf(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public int lastIndexOf(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public ListIterator listIterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public ListIterator listIterator(int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unsupported Operation");
    }



}
