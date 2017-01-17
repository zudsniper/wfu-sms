package cc.holstr.wfu.sms.transaction;

import cc.holstr.wfu.model.Merchant;
import cc.holstr.wfu.model.Purchase;
import cc.holstr.wfu.properties.Unpacker;
import cc.holstr.wfu.services.PurchaseBuilder;
import cc.holstr.wfu.servlet.SMS;
import cc.holstr.wfu.servlet.SMSInternalServlet;
import cc.holstr.wfu.servlet.SMSShopServlet;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jason on 1/8/17.
 */
public class MerchantValidator {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static HashMap<String, Integer> rejects = new HashMap<>();
	public static List<Merchant> merchants;

	private static HashMap<String, Merchant> merchantsMap;

	public void askForMerchant(Purchase purchase) {
		Twilio.init(cc.holstr.wfu.credentials.Twilio.ACCOUNT_SID, cc.holstr.wfu.credentials.Twilio.AUTH_TOKEN);
		if(merchants==null) {
			merchants = Unpacker.STATIC_INFO.getMerchants();
		}

		PurchaseBuilder.purchases.put(purchase.getNumber(), purchase);

		for(Merchant merchant : merchants) {
			PhoneNumber merchantNumber = new PhoneNumber(merchant.getNumber());
			PhoneNumber myNumber = new PhoneNumber(SMSInternalServlet.myNumber);
			Message message = Message
					.creator(merchantNumber,  // to
							myNumber,  // from
							"ORDER AT " + new Date().toString() + "\n"
							+ purchase.getCart().contents()
							+ "\n $"+purchase.getCart().getTotal()
							+ "\n at " + purchase.getTimeAndPlace().toString()
							+ "\n\nRespond with ACCEPT and then the orderee number to accept. You will recieve the orderee number shortly."
							+"\nIf you cannot take this order, please send REJECT and then the orderee number.")
					.create();

			Message messageNumber = Message.creator(merchantNumber,
					myNumber,
					purchase.getNumber())
					.create();
		}
	}



	public void giveMerchant(String number, Merchant merchant) {
		Twilio.init(cc.holstr.wfu.credentials.Twilio.ACCOUNT_SID, cc.holstr.wfu.credentials.Twilio.AUTH_TOKEN);

		Purchase p = PurchaseBuilder.purchases.get(number);
		p.setMerchant(merchant);
		PurchaseBuilder.purchases.put(number,p);

		if(p!=null) {
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

	public void fail(String number) {
		Twilio.init(cc.holstr.wfu.credentials.Twilio.ACCOUNT_SID, cc.holstr.wfu.credentials.Twilio.AUTH_TOKEN);

		Message message = Message.creator(new PhoneNumber(number),
				new PhoneNumber(SMSShopServlet.myNumber),
				"we're very sorry, but no merchant could take your order at that time. Please try again later. " +
						"\n Reply \"reset\" to start over.").create();
	}

	public static HashMap<String, Merchant> getMerchantsMap() {
		if(merchantsMap==null) {
			merchantsMap = new HashMap<>();
			for (Merchant merchant : merchants) {
				merchantsMap.put(merchant.getNumber(), merchant);
			}
		}
		return merchantsMap;
	}
}
