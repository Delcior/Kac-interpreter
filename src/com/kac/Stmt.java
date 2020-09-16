package com.kac;

import java.util.List;

abstract class Stmt {
	interface Visitor<T> {
		T visitExpressionStmt(Expression stmt);
		T visitPrintStmt(Print stmt);
		T visitVarDeclarationStmt(VarDeclaration stmt);
		T visitScopeStmt(Scope stmt);
	}
    static class Expression extends Stmt{
        final Expr expression;

		Expression(Expr expression) {
			this.expression = expression;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitExpressionStmt(this);
		}
	}
    static class Print extends Stmt{
        final Expr expression;

		Print(Expr expression) {
			this.expression = expression;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitPrintStmt(this);
		}
	}
	static class VarDeclaration extends Stmt{
		final Token name;
		final Expr initialValue;

		VarDeclaration(Token name, Expr initialValue){
			this.name = name;
			this.initialValue = initialValue;
		}
		@Override
		<T> T accept(Visitor<T> visitor){ return visitor.visitVarDeclarationStmt(this);}
	}
	static class Scope extends Stmt{
		final List<Stmt> statements;

		Scope(List<Stmt> statements){
			this.statements = statements;
		}
		@Override
		<T> T accept(Visitor<T> visitor){ return visitor.visitScopeStmt(this);}
	}
	abstract <T> T accept(Visitor<T> visitor);
}
