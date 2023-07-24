/* Generated By:JJTree: Do not edit this line. ExpEqual.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=Exp,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package io.agrest.exp.parser;

import io.agrest.exp.AgExpression;

public
class ExpEqual extends AgExpression {
  public ExpEqual(int id) {
    super(id);
  }

  public ExpEqual(AgExpressionParser p, int id) {
    super(p, id);
  }

  public ExpEqual() {
    super(AgExpressionParserTreeConstants.JJTEQUAL);
  }

  public static ExpEqual of(ExpObjPath path, ExpGenericScalar<?> value) {
    ExpEqual equal = new ExpEqual(AgExpressionParserTreeConstants.JJTEQUAL);
    equal.jjtAddChild(path, 0);
    equal.jjtAddChild(value, 1);
    return equal;
  }

  /** Accept the visitor. **/
  public <T> T jjtAccept(AgExpressionParserVisitor<T> visitor, T data) {

    return
    visitor.visit(this, data);
  }

  @Override
  protected AgExpression shallowCopy() {
    return new ExpEqual(id);
  }

  @Override
  public String toString() {
    return "(" + children[0] + ") = (" + children[1] + ")";
  }
}
/* JavaCC - OriginalChecksum=5d0a19fd37d62fae2718cc73973efde7 (do not edit this line) */
