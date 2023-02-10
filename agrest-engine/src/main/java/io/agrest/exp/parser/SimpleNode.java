/* Generated By:JJTree: Do not edit this line. SimpleNode.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=Exp,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package io.agrest.exp.parser;

import java.util.Arrays;
import java.util.Objects;

public class SimpleNode implements Node {

    protected Node parent;
    protected Node[] children;
    protected int id;
    protected Object value;
    protected AgExpressionParser parser;

    public SimpleNode(int i) {
        id = i;
    }

    public SimpleNode(AgExpressionParser p, int i) {
        this(i);
        parser = p;
    }

    @Override
    public void jjtOpen() {
    }

    @Override
    public void jjtClose() {
    }

    @Override
    public void jjtSetParent(Node n) {
        parent = n;
    }

    @Override
    public Node jjtGetParent() {
        return parent;
    }

    @Override
    public void jjtAddChild(Node n, int i) {
        if (children == null) {
            children = new Node[i + 1];
        } else if (i >= children.length) {
            Node[] c = new Node[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }
        children[i] = n;
    }

    @Override
    public Node jjtGetChild(int i) {
        return children[i];
    }

    @Override
    public int jjtGetNumChildren() {
        return (children == null) ? 0 : children.length;
    }

    @Override
    public void jjtSetValue(Object value) {
        this.value = value;
    }

    @Override
    public Object jjtGetValue() {
        return value;
    }

    /**
     * Accept the visitor.
     **/
    @Override
    public <T> T jjtAccept(AgExpressionParserVisitor<T> visitor, T data) {
        return visitor.visit(this, data);
    }

    /**
     * Accept the visitor.
     **/
    public <T> T childrenAccept(AgExpressionParserVisitor<T> visitor, T data) {
        if (children != null) {
            for (Node child : children) {
                child.jjtAccept(visitor, data);
            }
        }
        return data;
    }

  /* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

    @Override
    public String toString() {
        return AgExpressionParserTreeConstants.jjtNodeName[id];
    }

    public String toString(String prefix) {
        return prefix + this;
    }

  /* Override this method if you want to customize how the node dumps
     out its children. */

    public void dump(String prefix) {
        System.out.println(toString(prefix));
        if (children != null) {
            for (Node child : children) {
                SimpleNode n = (SimpleNode) child;
                if (n != null) {
                    n.dump(prefix + " ");
                }
            }
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleNode that = (SimpleNode) o;
        return id == that.id && Arrays.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result + Arrays.hashCode(children);
        return result;
    }
}

/* JavaCC - OriginalChecksum=5b582160ae0ddf8fccabd52a1f82953e (do not edit this line) */