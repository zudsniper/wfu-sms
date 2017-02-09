package cc.holstr.wfu.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by jason on 1/7/17.
 */
public class Cart extends ArrayList<Item> {

	public Cart() {
		super();
	}

	public double getTotal() {
		double total = 0;
		for(Item item : this) {
			total+= item.getPrice()*item.getQuantity();
		}
		return total;
	}

	public String contents() {
		String contents = "";
		for(Item item : this) {
			contents+= item.getName() + "     $" + item.getPrice()+ "   "+item.getQuantity() + "\n";
		}
		return contents;
	}

	public Item getByName(String name) {
		for(Item item : this) {
			if(StringUtils.containsIgnoreCase(item.getName(),name)) {
				return item;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		String output = "Cart{\nitems=";

		for(Item item : this) {
			output+= item.toString() + ", ";
		}

		output += "}";
		return output;
	}
}
