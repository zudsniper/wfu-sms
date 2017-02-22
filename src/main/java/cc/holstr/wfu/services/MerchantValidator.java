package cc.holstr.wfu.services;

import cc.holstr.wfu.model.Merchant;
import cc.holstr.wfu.model.Purchase;
import cc.holstr.wfu.properties.Unpacker;
import cc.holstr.wfu.services.PurchaseBuilder;
import cc.holstr.wfu.servlet.SMS;
import cc.holstr.wfu.servlet.SMSInternalServlet;
import cc.holstr.wfu.servlet.SMSShopServlet;
import cc.holstr.wfu.sms.transaction.PaymentType;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jason on 1/8/17.
 */
@Service
public class MerchantValidator {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final static long REJECT_INTERVAL = 180000; //180000;

	private static List<Merchant> merchants;

	private static HashMap<String, Merchant> merchantsMap;

	public MerchantValidator() {
	}

	public void askForMerchant(Purchase purchase){
		Twilio.init(cc.holstr.wfu.credentials.Twilio.ACCOUNT_SID, cc.holstr.wfu.credentials.Twilio.AUTH_TOKEN);
		if (merchants == null) {
			merchants = Unpacker.STATIC_INFO.getMerchants();
		}

		PurchaseBuilder.purchases.put(purchase.getNumber(), purchase);
		for (Merchant merchant : merchants) {
			PhoneNumber merchantNumber = new PhoneNumber(merchant.getNumber());
			PhoneNumber myNumber = new PhoneNumber(SMSInternalServlet.myNumber);
			Message message = Message
					.creator(merchantNumber,  // to
							myNumber,  // from
							"ORDER AT " + new Date().toString() + "\n"
									+ purchase.getCart().contents()
									+ "\n $" + purchase.getCart().getTotal()
									+ "\n at " + purchase.getTimeAndPlace().toString()
									+ "\n\nRespond with ACCEPT and then the orderee number to accept. You will recieve the orderee number in a separate message shortly."
									+ "\n"+purchase.getNumber())
					.create();

			Message messageNumber = Message.creator(merchantNumber,
					myNumber,
					purchase.getNumber())
					.create();
		}
	}

	@Async
	public void rejectTimer(Purchase purchase) throws InterruptedException{
		long start = System.currentTimeMillis();
		boolean rejected = false;
		timerLoop:
		while(System.currentTimeMillis() < start+REJECT_INTERVAL) {
			if(System.currentTimeMillis()%60000==0) {
				logger.debug("merchant: " + purchase.getMerchant());
			}
			if(purchase.getMerchant()!=null) {
				rejected = false;
				break timerLoop;
			} else {
				rejected = true;
			}
		}
		if(rejected) {
			Message message = Message.creator(new PhoneNumber(purchase.getNumber()),
					new PhoneNumber(SMSShopServlet.myNumber),
					"I'm sorry, but we couldn't find a merchant for your order at this time. Please try again later.").create();
		}
	}

	public void giveMerchant(String number, Merchant merchant) {
		logger.info("Giving merchant...");
		Purchase p = PurchaseBuilder.purchases.get(number);
		p.setMerchant(merchant);
		PurchaseBuilder.purchases.put(number, p);

		if (p != null) {
			Message message = Message.creator(new PhoneNumber(number),
					new PhoneNumber(SMSShopServlet.myNumber),
					"Success! \nYour merchant is " + merchant.getName()
							+ "\nContact at " + merchant.getNumber() + " if necessary."
							+ "\nMEETING: \n. . . . . . . . "
							+ "\nLOCATION: " + p.getTimeAndPlace().getPlace()
							+ "\nTIME: " + p.getTimeAndPlace().getTime()
							+ "\n\nPlease reply with a payment type."
							+ "\n" + PaymentType.prettyList())
					.create();
		}
	}

	public static HashMap<String, Merchant> getMerchantsMap() {
		if (merchantsMap == null) {
			merchantsMap = new HashMap<>();
			for(Merchant merchant : merchants) {
				merchantsMap.put(merchant.getNumber(),merchant);
			}
		}
		return merchantsMap;
	}

	public static List<Merchant> getMerchants() {
		return merchants;
	}

	public static void setMerchants(List<Merchant> merchants) {
		MerchantValidator.merchants = merchants;
	}
}
