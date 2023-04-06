/* Generated By:JJTree: Do not edit this line. ExpGreater.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=Exp,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package io.agrest.exp.parser;

import io.agrest.exp.AgExpression;

public
class ExpGreater extends AgExpression {
  public ExpGreater(int id) {
    super(id);
  }

  public ExpGreater(AgExpressionParser p, int id) {
    super(p, id);
  }

  /** Accept the visitor. **/
  public <T> T jjtAccept(AgExpressionParserVisitor<T> visitor, T data) {

    return
    visitor.visit(this, data);
  }

  @Override
  protected AgExpression shallowCopy() {
    return new ExpGreater(id);
  }

  @Override
  public String toString() {
    return children[0] + " > " + children[1];
  }
}
/* JavaCC - OriginalChecksum=ea0ce31b9d9db21857900434d14bd004 (do not edit this line) */
