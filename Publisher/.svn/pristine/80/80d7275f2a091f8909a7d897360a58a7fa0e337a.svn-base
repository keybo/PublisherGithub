package com.example.pubfeed;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class JSONFeed implements Serializable {
	private static final long serialVersionUID = 1L;
	private int itemcount = 0;
	private List<JSONItem> itemlist;

	public JSONFeed() {
		itemlist = new Vector<JSONItem>(0);
	}

	public void addItem(JSONItem item) {
		itemlist.add(item);
		itemcount++;
	}

	public JSONItem getItem(int location) {
		return itemlist.get(location);
	}

	public int getItemCount() {
		return itemcount;
	}

}
