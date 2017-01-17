package cc.holstr.wfu.services;

import cc.holstr.wfu.model.Cart;
import cc.holstr.wfu.model.Purchase;
import cc.holstr.wfu.model.pickup.TimeAndPlace;
import cc.holstr.wfu.sms.transaction.MerchantValidator;
import cc.holstr.wfu.sms.transaction.PaymentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
