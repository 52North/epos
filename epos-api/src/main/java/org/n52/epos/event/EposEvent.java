package org.n52.epos.event;

/**
 * The interface representing an internal event
 * object.
 * 
 * @author matthes rieke
 *
 */
public interface EposEvent {
	
	/**
	 * @param key the key to associate with the value
	 * @param value the value object to be associated with the key
	 */
	public void setValue(CharSequence key, Object value);

	/**
	 * @param key the associated key
	 * @return the value object associated with this key
	 */
	public Object getValue(CharSequence key);

	/**
	 * @return the original object of this event
	 */
	public Object getOriginalObject();

	/**
	 * @return the start time of this event. if it is a discrete event
	 * {@link #getStartTime()} == {@link #getEndTime()} shall be true.
	 */
	public long getStartTime();

	/**
	 * @return the end time of this event. if it is a discrete event
	 * {@link #getStartTime()} == {@link #getEndTime()} shall be true.
	 */
	public long getEndTime();

	/**
	 * adds a causal ancestor of this event
	 * 
	 * @param event the causal ancestor 
	 */
	public void addCausalAncestor(EposEvent event);

}
