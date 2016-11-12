package de.x3ro.mariosarsa;

import java.util.LinkedList;
import java.util.List;


public class SarsaQueue<T> {

    LinkedList<T> elems;
    int num;

    public SarsaQueue(int num) {
        this.num = num;
        elems = new LinkedList<T>();
    }

    public void push(T elem) {
        assert(elems.size() <= num);

        if(elems.size() == num) {
            elems.removeFirst();
        }

        elems.add(elem);
    }

    /**
     * Returns at maximum the number of elements specified, or less if the Queue holds less.
     */
    public List<T> maxLast(int num) {
        int size = elems.size();
        int start = size - num;
        if(start < 0) {
            start = 0;
        }
        return elems.subList(start, size);
    }

}
