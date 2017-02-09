package cc.holstr.wfu.sms.transaction;

import cc.holstr.wfu.google.stocking.Stockist;
import cc.holstr.wfu.model.Item;
import cc.holstr.wfu.model.Purchase;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by jason on 1/7/17.
 */
public class PurchaseManager {
	private Purchase purchase;

	public static Stockist stockist;

	public PurchaseManager() {
		build(null);
	}

	public PurchaseManager(Purchase purchase) {
		build(purchase);
	}

	public void build(Purchase purchase) {
		this.purchase = purchase;
		if(stockist==null) {
			stockist = new Stockist();
		}
	}

	public String cart() {
		return getPurchase().getCart().contents() + "\ntotal: $"+ getPurchase().getCart().getTotal();
	}

	public PurchaseResponse buy(String name, int quantity) {
		if(stockist.getStocks().get(name)!=null) {
			Item stockedItem = stockist.getStocks().get(name);
			for(Item item : getPurchase().getCart()) {
				if(StringUtils.containsIgnoreCase(item.getName(),name)) {
					//if(inCart==null) {
					if(quantity<0) {
						int newVal = item.getQuantity()+quantity;
						if(newVal > 0) {
							item.setQuantity(newVal);
							return PurchaseResponse.REMOVED;
						} else {
							getPurchase().getCart().remove(item);
							return PurchaseResponse.REMOVED_ALL;
						}

					}
				}
			}
			 if(stockedItem.getQuantity() >= quantity) {
				Item item = getPurchase().getCart().getByName(name);
				if(item==null) {
					item = new Item(name, stockedItem.getPrice(), quantity);
					purchase.getCart().add(item);
				} else {
					item.setQuantity(item.getQuantity()+quantity);
				}
				stockedItem.setQuantity(stockedItem.getQuantity() - quantity);
				return PurchaseResponse.ADDED;
			} else {
				return PurchaseResponse.OUT_OF_STOCK;
			}
		} else {
			return PurchaseResponse.NO_ITEM_FOUND;
		}
	}

	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}

	public Purchase getPurchase() {
		return purchase;
	}
}
