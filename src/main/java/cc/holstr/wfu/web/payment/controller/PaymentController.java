package cc.holstr.wfu.web.payment.controller;

import cc.holstr.wfu.google.statistics.StatsManager;
import cc.holstr.wfu.model.Purchase;
import cc.holstr.wfu.services.PurchaseBuilder;
import cc.holstr.wfu.sms.transaction.PurchaseManager;
import cc.holstr.wfu.web.payment.util.PurchaseURLCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by jason on 1/8/17.
 */
@Controller
@RequestMapping("purchase")
public class PaymentController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final PurchaseBuilder builder;

	public static final String PAYPAL_SUCCESS_URL = "success";
	public static final String PAYPAL_CANCEL_URL = "cancel";
	public static final String PAYPAL_NOTIFY_URL = "notify";

	public PaymentController(PurchaseBuilder builder) {
		this.builder = builder;
	}

	@GetMapping("{purchase_id}")
	String purchase(@PathVariable String purchase_id, ModelMap model) {
		Purchase p = builder.purchases.get(purchase_id);
		if(p!=null) {
			model.put("purchase_id", purchase_id);
			model.put("cart", p.getCart());
			model.put("timeAndPlace", p.getTimeAndPlace());
			model.put("merchant", p.getMerchant());
			model.put("number", p.getNumber());
			model.put("total", p.getCart().getTotal());
			if (p.getCart().size() > 1) {
				model.put("checkout_message", "Your order including " + p.getCart().get(0).getName() + " and more");
			} else {
				model.put("checkout_message", "Your order for " + p.getCart().get(0).getName());
			}
			model.put("return_url", PurchaseURLCreator.create(PurchaseBuilder.myUrl,purchase_id,p));
			model.put("cancel_return_url",PurchaseBuilder.myUrl + "/purchase/" + purchase_id + "/" + PAYPAL_CANCEL_URL);
			model.put("notify_url",PurchaseBuilder.myUrl + "/purchase/" + purchase_id + "/" + PAYPAL_NOTIFY_URL);
			return "purchase";
		} else {
			return "no_purchase";
		}

	}

	@GetMapping("{purchase_id}/" + PAYPAL_CANCEL_URL)
	public String cancelPay(@PathVariable String purchase_id, ModelMap model){
		builder.purchases.remove(purchase_id);
		return "cancel";
	}

	@GetMapping("{purchase_id}/" + PAYPAL_SUCCESS_URL)
	public String successPay(@PathVariable String purchase_id,
							 @RequestParam String time,
							 @RequestParam String place,
							 @RequestParam String merchant_name,
							 @RequestParam String merchant_number,
							 @RequestParam String total,
							 ModelMap model){
		try {
			model.put("time", URLDecoder.decode(time,"UTF-8"));
			model.put("place",URLDecoder.decode(place,"UTF-8"));
			model.put("merchant_name",URLDecoder.decode(merchant_name,"UTF-8"));
			model.put("merchant_number",URLDecoder.decode(merchant_number,"UTF-8"));
			model.put("total",URLDecoder.decode(total,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "success";
	}

	@PostMapping("{purchase_id}/" + PAYPAL_NOTIFY_URL)
	public void notifyPay(@PathVariable String purchase_id) {
		builder.statsManager.addPurchase(builder.purchases.get(purchase_id));
		builder.purchases.remove(purchase_id);
		logger.info("SUCCESSFUL PURCHASE WITH UUID \n"+purchase_id);
		PurchaseManager.stockist.toStockument();
	}
}
