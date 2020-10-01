package com.kac;

import java.util.List;

abstract class Stmt {
	interface Visitor<T> {
		T visitExpressionStmt(Expression stmt);
		T visitPrintStmt(Print stmt);
		T visitVarDeclarationStmt(VarDeclaration stmt);
		T visitScopeStmt(Scope stmt);
		T visitIfStmt(If stmt);
		T visitWhileStmt(While stmt);
		T visitForStmt(For stmt);
		T visitFunctionDeclarationStmt(FunctionDeclaration stmt);
		T visitReturnStmt(Return stmt);
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
	static class While extends Stmt{
		final Expr condition;
		final Stmt body;

		While(Expr condition, Stmt body){
			this.condition = condition;
			this.body = body;
		}
		@Override
		<T> T accept(Visitor<T> visitor){return visitor.visitWhileStmt(this);}
	}
	static class For extends Stmt{
		final Stmt initializer;
		final Expr condition;
		final Stmt stateModifier;
		final Stmt body;

		For(Stmt initializer, Expr condition, Stmt stateModifier, Stmt body){
			this.initializer = initializer;
			this.condition = condition;
			this.stateModifier = stateModifier;
			this.body = body;
		}
		@Override
		<T> T accept(Visitor<T> visitor){ return visitor.visitForStmt(this);}
	}
	static class If extends  Stmt{
		Expr condition;
		Stmt ifBranch;
		Stmt elseBranch;

		If(Expr condition, Stmt ifBranch, Stmt elseBranch){
			this.condition = condition;
			this.ifBranch = ifBranch;
			this.elseBranch = elseBranch;
		}

		@Override
		<T> T accept(Visitor<T> visitor){return visitor.visitIfStmt(this);}
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
	static class FunctionDeclaration extends Stmt{
		final Token name;
		final List<Token> arguments;
		final Stmt body;

		FunctionDeclaration(Token name, List<Token> arguments, Stmt body){
			this.name = name;
			this.arguments = arguments;
			this.body = body;
		}
		<T> T accept(Visitor<T> visitor){return visitor.visitFunctionDeclarationStmt(this);}
	}
	static class Return extends Stmt{
		final Expr value;

		Return(Expr value){
			this.value = value;
		}
		<T> T accept(Visitor<T> visitor){return visitor.visitReturnStmt(this);}
	}
	abstract <T> T accept(Visitor<T> visitor);
}
