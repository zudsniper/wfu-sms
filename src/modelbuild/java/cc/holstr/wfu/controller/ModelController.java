package cc.holstr.wfu.controller;

import cc.holstr.wfu.model.Cart;
import cc.holstr.wfu.model.Item;
import cc.holstr.wfu.model.Merchant;
import cc.holstr.wfu.model.Purchase;
import cc.holstr.wfu.model.TimeAndPlace;
import cc.holstr.wfu.properties.Unpacker;
import cc.holstr.wfu.services.PurchaseBuilder;
import cc.holstr.wfu.sms.transaction.PaymentType;
import cc.holstr.wfu.web.payment.util.PurchaseURLCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ModelController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String PAYPAL_SUCCESS_URL = "success";
	public static final String PAYPAL_CANCEL_URL = "cancel";
	public static final String PAYPAL_NOTIFY_URL = "notify";

	public ModelController() {
	}

	@GetMapping("")
	String purchase(ModelMap model) {
		Unpacker.unpack();
		Purchase p = new Purchase("+model_testing");
		p.setCart(new Cart());
		p.getCart().add(new Item("test item",1,1));
		p.getCart().add(new Item("test item 2", 1,1));
		p.getCart().add(new Item("test item 3", 1,1));
		p.getCart().add(new Item("test item 4", 1,1));
		p.setTimeAndPlace(TimeAndPlace.create("temp 1","PAC"));
		p.setMerchant(new Merchant("jaoson","+jaoson_number"));
		p.setPaymentType(PaymentType.CARD);

		if(p!=null) {
			model.put("purchase_id", "");
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
			model.put("return_url", PurchaseURLCreator.create(PurchaseBuilder.myUrl,"",p));
			model.put("cancel_return_url",PurchaseBuilder.myUrl + "/purchase/"+PAYPAL_CANCEL_URL);
			model.put("notify_url",PurchaseBuilder.myUrl + "/purchase/" + PAYPAL_NOTIFY_URL);
			return "modelbuild/purchase";
		} else {
			return "modelbuild/no_purchase";
		}

	}

	@GetMapping("no_purchase")
	public String noPurchase() {
		return "modelbuild/no_purchase";
	}

	@GetMapping(PAYPAL_CANCEL_URL)
	public String cancelPay(ModelMap model){
		return "modelbuild/cancel";
	}

	@GetMapping(PAYPAL_SUCCESS_URL)
	public String successPay(@RequestParam String time,
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
		return "modelbuild/success";
	}
}
