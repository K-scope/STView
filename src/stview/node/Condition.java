package stview.node;

/**
 * Abstract Condition class.
 */
public abstract class Condition extends STVNode{
	private static final long serialVersionUID = -6104944525221882861L;
	protected String condition;
	
	/**
	 * Setter for condition.
	 * @param c A condition
	 */
	public void setCondition(String c){
		condition = c;
	}
	
	/**
	 * Getter for condition.
	 * @return The condition
	 */
	public String getCondition(){
		return condition;
	}
	
}

