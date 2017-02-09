package cc.holstr.wfu.services;

import cc.holstr.wfu.google.statistics.StatsManager;
import cc.holstr.wfu.model.Purchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by jason on 1/8/17.
 */
@Service
public class PurchaseBuilder {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String myUrl = "https://localhost:8443";

	public static HashMap<String, Purchase> purchases = new HashMap<>();

	public StatsManager statsManager;

	public PurchaseBuilder() {
		statsManager = new StatsManager();
	}

	public String generateURL(Purchase purchase) {
		//int id = purchase.hashCode();
		UUID uuid = UUID.randomUUID();
		purchases.put(uuid.toString(), purchase);

		logger.warn("NUMBER: " +purchase.getNumber());
		logger.warn("UUID: " +uuid.toString());

		return myUrl + "/purchase/" + uuid.toString() + "/";
	}

	public void finalisePurchase(Purchase purchase) {

	}
}
