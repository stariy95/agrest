/* Generated By:JJTree: Do not edit this line. ExpFalse.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=Exp,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package io.agrest.exp.parser;

import io.agrest.exp.AgExpression;

public
class ExpFalse extends AgExpression {
  public ExpFalse(int id) {
    super(id);
  }

  public ExpFalse(AgExpressionParser p, int id) {
    super(p, id);
  }

  /** Accept the visitor. **/
  public <T> T jjtAccept(AgExpressionParserVisitor<T> visitor, T data) {

    return
    visitor.visit(this, data);
  }

  @Override
  protected AgExpression shallowCopy() {
    return new ExpFalse(id);
  }

  @Override
  public String toString() {
    return "false";
  }
}
/* JavaCC - OriginalChecksum=f5ab544d76028320b4b1dbb970751376 (do not edit this line) */
