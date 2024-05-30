package compiler.ast;

import java.io.OutputStreamWriter;

import compiler.CompileEnvIntf;
import compiler.InstrIntf;
import compiler.Token;
import compiler.TokenIntf.Type;

public class ASTAndOrExpr extends ASTExprNode {
    ASTExprNode lhs, rhs;
    Token token;

    public ASTAndOrExpr(Token or, ASTExprNode left, ASTExprNode right) {
        this.lhs = left;
        this.token = or;
        this.rhs = right;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write("AndOr\n");
        outStream.write(indent);
        this.lhs.print(outStream, indent + indent);
        outStream.write("\n");
        outStream.write(indent);
        outStream.write(token.toString());
        outStream.write("\n");
        outStream.write(indent);
        this.rhs.print(outStream, indent + indent);
        outStream.write("\n");
    }

    @Override
    public int eval() {
        if (token.m_type == Type.OR) {
            return lhs.eval() == 1 || rhs.eval() == 1 ? 1 : 0;
        } else {
            return lhs.eval() == 1 && rhs.eval() == 1 ? 1 : 0;
        }
    }

    @Override
    public InstrIntf codegen(CompileEnvIntf env) {
        Integer constFold = this.constFold();
        compiler.InstrIntf resultExpr = null;
        if (constFold != null) {
            resultExpr = new compiler.instr.InstrIntegerLiteral(constFold.toString());
        } else {
            compiler.InstrIntf lhsExpr;
            Integer lhsConst = lhs.constFold();
            if(token.m_type == Type.OR){
                if (lhsConst == null){
                    lhsExpr = lhs.codegen(env);
                }else if(lhsConst == 0){
                    lhsExpr = new compiler.instr.InstrIntegerLiteral("0");
                } else { // 1
                    resultExpr = new compiler.instr.InstrIntegerLiteral("1");
                    lhsExpr = new compiler.instr.InstrIntegerLiteral("1"); // Compiler error without
                }
            } else { // AND
                if(lhsConst == null){
                    lhsExpr = lhs.codegen(env);
                }else if(lhsConst == 0 ){
                    lhsExpr = new compiler.instr.InstrIntegerLiteral("1"); // Compiler error without
                    resultExpr = new compiler.instr.InstrIntegerLiteral("0");
                }else { // 1
                    lhsExpr = new compiler.instr.InstrIntegerLiteral("1");
                }
            }
            
            if(resultExpr == null){
                Integer rhsConst = rhs.constFold();
                compiler.InstrIntf rhsExpr = null;
                if(rhsConst == null){
                    rhsExpr = rhs.codegen(env);
                } else {
                    rhsExpr = new compiler.instr.InstrIntegerLiteral(rhsConst.toString());
                }
                resultExpr = new compiler.instr.InstrAndOr(token.m_type, lhsExpr,rhsExpr);
            }
        }
        env.addInstr(resultExpr);
        return resultExpr;
    }

    public Integer constFold() {
        // NULL, wenn nicht konstant, sonst den wert
        Integer lhsConstFold = lhs.constFold();
        Integer rhsConstFold = rhs.constFold();

        if (token.m_type == Type.OR) {
            if ((lhsConstFold != null && lhsConstFold == 1) || (rhsConstFold != null && rhsConstFold == 1)) {
                return 1;
            } else if (lhsConstFold != null && rhsConstFold != null && lhsConstFold == 0 && rhsConstFold == 0) {
                return 0;
            }
        } else if (token.m_type == Type.AND) {
            if (lhsConstFold != null && rhsConstFold != null && lhsConstFold == 1 && rhsConstFold == 1) {
                return 1;
            } else if ((lhsConstFold != null && lhsConstFold == 0) || (rhsConstFold != null && rhsConstFold == 0)) {
                return 0;
            }
        }

        return null;
    }
}
