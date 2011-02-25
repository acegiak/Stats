package com.nidefawl.Stats.ItemResolver;

public interface itemResolver {
	/**
	 * Returns item id corresponding to item name
	 * 
	 * @param name
	 * @return item id
	 */
	public abstract int getItem(String name);

	/**
	 * Returns the name of the item corresponding to the ID
	 * 
	 * @param id
	 *            id of item
	 * @return name of item
	 */
	public abstract String getItem(int id);

}