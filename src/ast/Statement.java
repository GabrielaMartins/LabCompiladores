/*
 * Gabriela de Jesus Martins	- 489689
 * Valdeir Soares Perozim		- 489786
 */

package ast;

abstract public class Statement {

	abstract public void genC(PW pw);
	abstract public StatementType getType();
}
