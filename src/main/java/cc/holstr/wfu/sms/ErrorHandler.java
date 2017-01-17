package cc.holstr.wfu.sms;

import cc.holstr.wfu.servlet.SMSShopServlet;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jason on 1/11/17.
 */
public class ErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

	public static void handleError(String number) {
		Twilio.init(cc.holstr.wfu.credentials.Twilio.ACCOUNT_SID, cc.holstr.wfu.credentials.Twilio.AUTH_TOKEN);

		logger.error("Resetting user account... (exception?)");

		Message message = Message.creator(new PhoneNumber(number), //to
				new PhoneNumber(SMSShopServlet.myNumber), //from
				"Something happened... Resetting your user session, please try your order again. " +
						"\nSorry for any inconvience!" ).create();
	}

}
