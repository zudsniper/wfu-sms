package cc.holstr.wfu.servlet;

import cc.holstr.util.RegExp;
import cc.holstr.wfu.model.Merchant;
import cc.holstr.wfu.services.PurchaseBuilder;
import cc.holstr.wfu.sms.transaction.MerchantValidator;
import com.twilio.twiml.Body;
import com.twilio.twiml.Message;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.TwiMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by jason on 1/10/17.
 */
public class SMSInternalServlet extends HttpServlet {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String myNumber = "+15122336872";

	public final PurchaseBuilder service;

	public MerchantValidator merchantValidator;

	public SMSInternalServlet(PurchaseBuilder service, MerchantValidator merchantValidator) {
		this.service = service;
		this.merchantValidator = merchantValidator;
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		String fromNumber = request.getParameter("From");
		String toNumber = request.getParameter("To");
		String body = request.getParameter("Body");

		String message = null;
			logger.info("TO INTERNAL: " +fromNumber);
			String accept_unvalidated = RegExp.find(body, "ACCEPT \\+[0-9]+");
			String reject_unvalidated =  RegExp.find(body, "REJECT \\+[0-9]+");
			if(accept_unvalidated!=null) {
				if(MerchantValidator.getMerchantsMap().containsKey(fromNumber)) {
					String orderee = accept_unvalidated.substring(accept_unvalidated.indexOf("ACCEPT ") + "ACCEPT ".length(),accept_unvalidated.length());
					Merchant merchant = MerchantValidator.getMerchantsMap().get(fromNumber);
					merchantValidator.giveMerchant(orderee,merchant);
				}
			} else if(reject_unvalidated!=null) {
				if(MerchantValidator.getMerchantsMap().containsKey(fromNumber)) {
					String orderee = accept_unvalidated.substring(accept_unvalidated.indexOf("REJECT ") + "REJECT ".length(), accept_unvalidated.length());
					HashMap<String, Integer> rejects = merchantValidator.rejects;
					Integer numRejects = rejects.get(orderee);
					if (numRejects == null) {
						rejects.put(orderee, 1);
					} else {
						rejects.put(orderee, numRejects++);
					}
					if (numRejects >= merchantValidator.getMerchantsMap().size()) {

					}
				}
			}


		MessagingResponse twiml = new MessagingResponse.Builder()
				.message(new Message.Builder()
						.body(new Body(message))
						.build())
				.build();

		response.setContentType("application/xml");
		try {
			response.getWriter().print(twiml.toXml());
		} catch (TwiMLException e) {
			logger.error("ERROR: "+e.getMessage());
			//e.printStackTrace();
		}

	}
}
