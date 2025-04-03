package edu.caltech.cs2.project03;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a string of tags into HTMLTags. Iterates
 * over the given source String
 */
public class HTMLParser implements Iterator<HTMLTag> {
    private static final Pattern TAG_PATTERN = Pattern.compile("^<\\s*(?<closing>/)?\\s*(?<tagData>[^>]*[^/> ])\\s*(?<selfclosing>/)?\\s*>");
    private static final Pattern TAG_COMMENT = Pattern.compile("^<!--(?<comment>[^>]*[^/>])?-->", Pattern.DOTALL);
    private static final Pattern TAG_SCRIPT = Pattern.compile("<\\s*/\\s*script\\s*>");
    private static final Pattern TAG_CLOSE = Pattern.compile("<\\s*/\\s*(?<tagData>[^>]+)\\s*>");

    private String page;
    private String prevTag;

    /**
     * Creates an HTMLParser based off the given source String
     */
    public HTMLParser(String page) {
        this.page = page.trim();
        this.prevTag = "";
    }

    /**
     * Parses and returns a "normal" tag (any opening, closing, or self-closing
     * tag that is not a comment or content) based on the contents at the
     * beginning of the current page.
     *
     * In all cases, this method updates the field prevTag.  If the tag
     * being parsed is NOT an opening tag, it sets prevTag to an empty
     * String.  Otherwise, it gets the element (tag.getElement()) and
     * sets prevTag to that element name.
     **/
    public HTMLTag findNormalTag() {

        Matcher m = TAG_PATTERN.matcher(this.page);
        HTMLTag tag;

        if (m.find()) {
            if (m.group("closing") != null) {
                tag = new HTMLTag(m.group("tagData"), HTMLTagType.CLOSING);
                prevTag = "";
            } else if (m.group("selfclosing") != null) {
                tag = new HTMLTag(m.group("tagData"), HTMLTagType.SELF_CLOSING);
                prevTag = "";
            } else {
                tag = new HTMLTag(m.group("tagData"), HTMLTagType.OPENING);
                prevTag = tag.getElement();
            }
        } else {
            return null;
        }

        this.page = this.page.substring(m.group(0).length());

        return tag;
    }

    /**
     * Parses and returns a comment tag based on the contents
     * at the beginning of the current page. Always sets prevTag
     * to an empty String
     **/
    public HTMLTag findCommentTag() {
        Matcher m = TAG_COMMENT.matcher(this.page);
        HTMLTag tag;

        if (m.find()) {
            if (m.group("comment") != null) { //comment pattern
                tag = new HTMLTag(m.group("comment"), HTMLTagType.COMMENT);
                prevTag = "";
            } else {
                return null;
            }
        } else {
            return null;
        }

        this.page = this.page.substring(m.group(0).length());

        return tag;
    }

    /**
     * Parses and returns "content" (that is, the non-tag text
     * inside a innermost opening tag, that is before the corresponding
     * closing tag.
     *
     * To do this, this method:
     * 1) Special cases when the prevTag is a script tag and skips
     *    to the very next closing script tag.
     * 2) Otherwise, finds the index of the very next closing tag
     *    which does not have to match any other tag.
     * 3) Sets prevTag to an empty String.
     * 4) Returns a new content tag with the contents from the
     *    beginning of the remainder of the page all the way
     *    to the index found in (1) or (2).
     **/
    public HTMLTag findContent() {

        HTMLTag tag;

        if (prevTag.equals("script")) {
            Matcher pt = TAG_SCRIPT.matcher(this.page);

            if (pt.find()) {
                tag = new HTMLTag(this.page.substring(0, pt.start()), HTMLTagType.CONTENT);
                prevTag = "";
            } else {
                return null;
            }

            this.page = this.page.substring(pt.start());

        } else {
            Matcher cl = TAG_CLOSE.matcher(this.page);

            if (cl.find()) {
                tag = new HTMLTag(this.page.substring(0, cl.start()), HTMLTagType.CONTENT);
                prevTag = "";
            } else {
                return null;
            }

            this.page = this.page.substring(cl.start());
        }

        return tag;
    }

    /**
     * Returns the next HTMLTag in the source String
     */
    public HTMLTag next() {
        this.page = this.page.trim();
        if (this.page.startsWith("<!--")) {
            return findCommentTag();
        }
        else if (this.page.startsWith("<") && !this.prevTag.equals("script")) {
            return findNormalTag();
        }
        else {
            return findContent();
        }
    }

    /**
     * Returns true if there is another HTMLTag in the source String
     * returns false otherwise.
     */
    public boolean hasNext() {
        return !this.page.isEmpty();
    }

    /**
     * Throws UnsupportedOperationException
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}