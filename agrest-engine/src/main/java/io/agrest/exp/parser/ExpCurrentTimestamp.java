/* Generated By:JJTree: Do not edit this line. ExpCurrentTimestamp.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=Exp,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package io.agrest.exp.parser;

public
class ExpCurrentTimestamp extends SimpleNode {
  public ExpCurrentTimestamp(int id) {
    super(id);
  }

  public ExpCurrentTimestamp(AgExpressionParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public <T> T jjtAccept(AgExpressionParserVisitor<T> visitor, T data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=058e439de0b0ca1a0f8b52d092fb5d76 (do not edit this line) */