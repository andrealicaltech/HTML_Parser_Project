package edu.caltech.cs2.project03;


import java.util.ArrayList;
import java.util.List;

/**
 * HTMLManager represents a single, possibly malformed segment of HTML as
 * a collection of HTMLTags. It provides a variety of methods for viewing and
 * manipulating this HTML.
 */


public class HTMLManager {
    private Queue<HTMLTag> q;

    /** 
     * Constructs the HTMLManager given the Queue of HTMLTags passed
     * as a parameter.  If the given Queue is null, throws an
     * IllegalArgumentException.  
     */
    public HTMLManager(Queue<HTMLTag> page) {
        Queue<HTMLTag> newQ = new Queue<>();

        if (page == null) {
            throw new IllegalArgumentException();
        } else {
            for (int i = 0; i < page.size(); i++) {
                HTMLTag curr = page.peek();
                newQ.enqueue(curr);
                page.dequeue();
                page.enqueue(curr);
            }
        }
        this.q = newQ;

    }//constructor

    /**
     * Adds the given tag to the end of the collection of stored tags. If the
     * given tag is null, throws an IllegalArgumentException.
     */
    public void add(HTMLTag tag) {
        if (tag != null) {
            q.enqueue(tag);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns a List of HTMLTags representing the current collection
     * of tags.
     */
    public List<HTMLTag> getTags() {
        List<HTMLTag> tag_list = new ArrayList<>();

        while (!this.q.isEmpty()) {
            HTMLTag curr = q.dequeue();
            tag_list.add(curr);
        }

        for (HTMLTag tag: tag_list) {
            q.enqueue(tag);
        }

        return tag_list;
    }

    /**
     * Returns a string version of the HTML page which is
     * indented by two spaces every time an open tag is
     * seen and unindenting by two spaces for every
     * closing tag.
     **/
    public String toString() {
        //nested tag increase 2 space
        List<HTMLTag> tags = getTags();
        int indent = 0;
        String result = "";

        for (HTMLTag tag: tags) {

            if (tag.isOpening()) {
                result += ("  ").repeat(indent);
                indent++;
            }

            else if (tag.isClosing()) {
                indent--;
                result += ("  ").repeat(indent);
            }//closing tag

            else if (tag.isSelfClosing() || tag.isNotTag()) {
                result += ("  ").repeat(indent);
            }
            result += tag + "\n";

        }
        return result;
    }

    /**
     * Fixes the current collection of HTMLTags to be valid HTML. When
     * an unexpected closing tag is found, the method will insert closing
     * tags for all unexpectedly closed tags at that point. The best use case
     * is for HTML where the author forgot to close their tags.
     */
    public void fixHTML() {
        Stack<HTMLTag> stack = new Stack<>();
        Queue<HTMLTag> output = new Queue<>();

        int max = q.size();

        for (int i = 0; i < max; i++) {
            HTMLTag curr = q.dequeue();
            if (curr.isSelfClosing() || curr.isNotTag()) {
                output.enqueue(curr);
            }//self-closing or comment add directly to output

            if (curr.isOpening()) {
                output.enqueue(curr);
                stack.push(curr);
            }//opening tag

            if (curr.isClosing()) {
                Boolean match = false;
                while (!match && !stack.isEmpty()) {
                    HTMLTag tag = stack.pop();
                    if (tag.matches(curr)) {
                        output.enqueue(curr);
                        match = true;
                    } else {
                        output.enqueue(tag.getMatching());
                    }//else
                }//while
            }//if closing
        }//for

        while (!stack.isEmpty()) {
            HTMLTag close = stack.pop().getMatching();
            output.enqueue(close);
        }//add closing tags for any remaining opening tags

        q = output;
    }//fixHTML

}//class
