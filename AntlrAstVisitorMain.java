

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class AntlrAstVisitorMain {

	public static void main(String[] args) throws Exception {
		// create input stream
		CharStream input = CharStreams.fromFileName("compiler/language.txt");
		// create lexer
		compiler.antlrcompiler.languageLexer lexer = new compiler.antlrcompiler.languageLexer(input);
		// create token stream
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		// create parser
		compiler.antlrcompiler.languageParser parser = new compiler.antlrcompiler.languageParser(tokens);
		parser.setBuildParseTree(true);
		// build parse tree
		ParseTree tree = parser.expr();
		// create visitor for expression evaluation
		compiler.antlrvisitor.ExprAstVisitor  exprEvalVisitor = new compiler.antlrvisitor.ExprAstVisitor();
		compiler.ast.ASTExprNode result = exprEvalVisitor.visit(tree);
		System.out.println(result);
	}

}
