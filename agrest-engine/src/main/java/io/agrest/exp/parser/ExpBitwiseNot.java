/* Generated By:JJTree: Do not edit this line. ExpBitwiseNot.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=Exp,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package io.agrest.exp.parser;

import io.agrest.exp.AgExpression;

public
class ExpBitwiseNot extends AgExpression {
  public ExpBitwiseNot(int id) {
    super(id);
  }

  public ExpBitwiseNot(AgExpressionParser p, int id) {
    super(p, id);
  }

  /** Accept the visitor. **/
  public <T> T jjtAccept(AgExpressionParserVisitor<T> visitor, T data) {

    return
    visitor.visit(this, data);
  }

  @Override
  protected AgExpression shallowCopy() {
    return new ExpBitwiseNot(id);
  }

  @Override
  public String toString() {
    return "~" + children[0];
  }
}
/* JavaCC - OriginalChecksum=19c490da33ec6688721f4959812e3984 (do not edit this line) */
