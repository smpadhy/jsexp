/*
 * Copyright 2009 Julian Mendez
 *
 *
 * This file is part of jsexp.
 *
 * jsexp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jsexp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jsexp.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.tudresden.inf.lat.jsexp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Represents a non-atomic S-expression.
 * 
 * @author Julian Mendez
 */
public class SexpList implements Sexp {

	protected static final String newLine = "\n";
	protected static final String tabulation = "  ";
	private int depth = 0;
	private List<Sexp> rep = new ArrayList<Sexp>();

	protected SexpList() {
	}

	/**
	 * @see de.tudresden.inf.lat.jsexp.Sexp#add(Sexp)
	 */
	public void add(Sexp item) {
		if (getDepth() < item.getDepth() + 1) {
			this.depth = item.getDepth() + 1;
		}
		this.rep.add(item);
	}

	/**
	 * Creates a non-atomic S-expression using a list of tokens.
	 * 
	 * @param tokenList
	 *            list of tokens to parse, this list must be not null and have
	 *            balanced parenthesis.
	 */
	protected SexpList(List<Token> tokenList) {
		SexpList ret = null;
		Stack<SexpList> stack = new Stack<SexpList>();
		SexpList currentList = new SexpList();
		boolean firstTime = true;
		for (Token tok : tokenList) {
			if (tok.isLeftParenthesis()) {
				if (!firstTime) {
					stack.push(currentList);
					currentList = new SexpList();
				}
				firstTime = false;
			} else if (tok.isRightParenthesis()) {
				if (stack.empty()) {
					ret = currentList;
				} else {
					SexpList lastList = currentList;
					currentList = stack.pop();
					currentList.add(lastList);
				}
			} else if (!tok.isComment()) {
				currentList.add(new SexpString(tok.getText()));
			}
		}
		this.rep = ret.rep;
	}

	/**
	 * @see de.tudresden.inf.lat.jsexp.Sexp#get(int)
	 */
	public Sexp get(int index) {
		return this.rep.get(index);
	}

	/**
	 * @see de.tudresden.inf.lat.jsexp.Sexp#getDepth()
	 */
	public int getDepth() {
		return this.depth;
	}

	/**
	 * @see de.tudresden.inf.lat.jsexp.Sexp#getLength()
	 */
	public int getLength() {
		return this.rep.size();
	}

	/**
	 * @see de.tudresden.inf.lat.jsexp.Sexp#isAtomic()
	 */
	public boolean isAtomic() {
		return false;
	}

	/**
	 * @see de.tudresden.inf.lat.jsexp.Sexp#iterator()
	 */
	public Iterator<Sexp> iterator() {
		return this.rep.iterator();
	}

	/**
	 * @see de.tudresden.inf.lat.jsexp.Sexp#toIndentedString()
	 */
	public String toIndentedString() {
		return toIndentedString(this, 0).trim();
	}

	/**
	 * Auxiliary function for showing an indented string representing an
	 * S-expression or subexpression at a certain depth. The depth determines
	 * the indentation.
	 * 
	 * @param expr
	 *            the S-expression or subexpression.
	 * @param depth
	 *            the depth where a S-subexpression is.
	 * @return an indented string of the passed S-(sub)expression at a certain
	 *         depth.
	 */
	protected String toIndentedString(Sexp expr, int depth) {
		StringBuffer ret0 = new StringBuffer();
		if (expr.isAtomic()) {
			ret0.append(expr.toIndentedString());
		} else if (expr.getLength() == 0) {
			ret0.append(Token.leftParenthesisChar);
			ret0.append(Token.rightParenthesisChar);
		} else {
			ret0.append(newLine);
			for (int i = 0; i < depth; i++) {
				ret0.append(tabulation);
			}
			ret0.append(Token.leftParenthesisChar);
			for (Iterator<Sexp> it = expr.iterator(); it.hasNext();) {
				Sexp current = it.next();
				ret0.append(toIndentedString(current, depth + 1));
				if (it.hasNext()) {
					ret0.append(' ');
				}
			}
			ret0.append(Token.rightParenthesisChar);
		}
		return ret0.toString();
	}

	public boolean equals(Object o) {
		boolean ret = false;
		if (o instanceof SexpList) {
			SexpList other = (SexpList) o;
			ret = this.rep.equals(other.rep);
		}
		return ret;
	}

	public int hashCode() {
		return this.rep.hashCode();
	}

	public String toString() {
		StringBuffer ret0 = new StringBuffer();
		ret0.append(Token.leftParenthesisChar);
		for (Iterator<Sexp> it = this.rep.iterator(); it.hasNext();) {
			Sexp expr = it.next();
			ret0.append(expr.toString());
			if (it.hasNext()) {
				ret0.append(' ');
			}
		}
		ret0.append(Token.rightParenthesisChar);
		return ret0.toString();
	}
}
