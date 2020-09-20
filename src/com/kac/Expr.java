package com.kac;


abstract class Expr {
	interface Visitor<T> {
		T visitBinaryExpr(Binary expr);
		T visitGroupingExpr(Grouping expr);
		T visitLiteralExpr(Literal expr);
		T visitUnaryExpr(Unary expr);
		T visitVariableExpr(Variable var);
		T visitAssignmentExpr(Assignment expr);
		T visitLogicalOrExpr(LogicalOR expr);
		T visitLogicalAndExpr(LogicalAND expr);
	}
	static class Assignment extends Expr{
		final Variable variable;
		final Expr value;

		Assignment(Variable variable, Expr value){
			this.variable = variable;
			this.value = value;
		}

		@Override
		<T> T accept(Visitor<T> visitor){return visitor.visitAssignmentExpr(this);}
	}
    static class Binary extends Expr{
        final Expr left;
        final Token operator;
        final Expr right;

		Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitBinaryExpr(this);
		}
	}
    static class Grouping extends Expr{
        final Expr expression;

		Grouping(Expr expression) {
			this.expression = expression;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitGroupingExpr(this);
		}
	}
    static class Literal extends Expr{
        final Object value;

		Literal(Object value) {
			this.value = value;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitLiteralExpr(this);
		}
	}
    static class Unary extends Expr{
        final Token operator;
        final Expr right;

		Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		<T> T accept(Visitor<T> visitor) {
			return visitor.visitUnaryExpr(this);
		}
	}
	static class Variable extends Expr{
		final Token name;

		Variable(Token name){
			this.name = name;
		}

		@Override
		<T> T accept(Visitor<T> visitor){return visitor.visitVariableExpr(this);}
	}
	static class LogicalOR extends Expr{
		final Expr left;
		final Expr right;

		LogicalOR(Expr left, Expr right){
			this.left = left;
			this.right = right;
		}
		@Override
		<T> T accept(Visitor<T> visitor){return visitor.visitLogicalOrExpr(this);}
	}
	static class LogicalAND extends Expr{
		final Expr left;
		final Expr right;

		LogicalAND(Expr left, Expr right){
			this.left = left;
			this.right = right;
		}
		@Override
		<T> T accept(Visitor<T> visitor){return visitor.visitLogicalAndExpr(this);}
	}
	abstract <T> T accept(Visitor<T> visitor);
}
