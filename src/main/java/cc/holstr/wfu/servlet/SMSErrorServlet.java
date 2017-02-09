package cc.holstr.wfu.servlet;

import cc.holstr.wfu.services.MerchantValidator;
import cc.holstr.wfu.services.PurchaseBuilder;
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

/**
 * Created by jason on 2/4/17.
 */
public class SMSErrorServlet extends HttpServlet {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public final PurchaseBuilder builder;

	public SMSErrorServlet(PurchaseBuilder builder) {
		this.builder = builder;
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		String fromNumber = request.getParameter("From");
		String toNumber = request.getParameter("To");
		String body = request.getParameter("Body");
		String message;

		if(SMSInternalServlet.myNumber.equals(toNumber)) {
			message = "ERROR " + fromNumber + "\nWith Message: \n" + body
					+ "\n\n This DOESN'T mean whatever you did didn't go through, so don't try again." ;
		} else {
			message = "hold on, we're having some technical issues... You should recieve your message shortly.";
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
