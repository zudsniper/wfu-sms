package cc.holstr.wfu.web.payment.util;

import cc.holstr.wfu.model.Purchase;
import cc.holstr.wfu.web.payment.controller.PaymentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jason on 1/16/17.
 */
public class PurchaseURLCreator {

	private static final Logger logger = LoggerFactory.getLogger(PurchaseURLCreator.class);

	public static String create(String url, String purchase_id, Purchase purchase) {
		try {
			url = url + "/purchase/" + purchase_id + "/" + PaymentController.PAYPAL_SUCCESS_URL
			+ "?time=" + URLEncoder.encode(purchase.getTimeAndPlace().getTime(),"UTF-8")
			+ "&place=" + URLEncoder.encode(purchase.getTimeAndPlace().getPlace(), "UTF-8")
			+ "&merchant_name=" + URLEncoder.encode(purchase.getMerchant().getName(), "UTF-8")
			+ "&merchant_number=" + URLEncoder.encode(purchase.getMerchant().getNumber(), "UTF-8")
			+ "&total=" + URLEncoder.encode(purchase.getCart().getTotal()+"", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		return url;
	}

}
