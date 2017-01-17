package cc.holstr.wfu.model;

/**
 * Created by jason on 1/7/17.
 */
public class Item implements Comparable<Item>{

	private long id;

	private String name;

	private double price;

	private int quantity;

	public Item() {
	}

	public Item(String name, double price, int quantity) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public int compareTo(Item o) {
		if(this.price>o.getPrice()) {
			return 1;
		} else if(this.price==o.getPrice()) {
			return 0;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return "Item{" +
				"id=" + id +
				", name='" + name + '\'' +
				", price=" + price +
				", quantity=" + quantity +
				'}';
	}
}
