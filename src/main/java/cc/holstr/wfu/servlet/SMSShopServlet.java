package cc.holstr.wfu.servlet;

import cc.holstr.util.RegExp;
import cc.holstr.wfu.model.Item;
import cc.holstr.wfu.model.Purchase;
import cc.holstr.wfu.model.TimeAndPlace;
import cc.holstr.wfu.properties.Unpacker;
import cc.holstr.wfu.services.MerchantValidator;
import cc.holstr.wfu.services.PurchaseBuilder;
import cc.holstr.wfu.sms.ErrorHandler;
import cc.holstr.wfu.sms.transaction.*;
import com.twilio.twiml.Body;
import com.twilio.twiml.Message;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.TwiMLException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by jason on 1/5/17.
 */
@Component
public class SMSShopServlet extends HttpServlet {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String myNumber = "+15122336569";

	public final PurchaseBuilder builder;

	@Autowired
	public MerchantValidator merchantValidator;
	//private PurchaseManager manager;

	public SMSShopServlet(PurchaseBuilder builder) {
		this.builder = builder;
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PurchaseManager manager = new PurchaseManager();
		HttpSession session = request.getSession(true);
		String fromNumber = request.getParameter("From");
		String toNumber = request.getParameter("To");
		String body = request.getParameter("Body");
		manager.setPurchase((Purchase)session.getAttribute("purchase"));
		//Boolean confirm = (Boolean) session.getAttribute("checkoutConfirm");
		CheckoutStep step = (CheckoutStep) session.getAttribute("checkoutStep");

		String message = null;

		/*Map<String, String[]> pm = request.getParameterMap();

		for(String param_name : pm.keySet()) {
			if (param_name.contains("payment")) {
				logger.debug("parameter \"payment\" parsed");
			}
			logger.debug("param: " + param_name);
		}*/

		logger.debug("merchantValidator : "+merchantValidator);

		if(SMSInternalServlet.myNumber.equals(toNumber)) {
			logger.warn("internal handled incorrectly.");
			//smsInternalServlet.builder(request,response);
		} else {
			logger.info("TO EXTERNAL: " + fromNumber);
			if (manager.getPurchase() == null) {
				logger.info("NEW USER: No purchase in session.");
				step = CheckoutStep.IN_PROGRESS;
				//confirm = false;
				manager.setPurchase(new Purchase(fromNumber));
				message = Unpacker.STATIC_INFO.getWelcome() + "\n";
				message += PurchaseManager.stockist.listStock() + "\n\n";
				message += "To buy, type the name and an amount of an item. \n";
				message += "example: five gum mint 2 \n";
				message += "To remove from cart, type the name and a negative amount of an item. \n";
				message += "example: five gum mint -2 \n";
				message += "To view your cart, send \"cart\" \n";
				message += "To clear your cart, send \"clear\" \n";
				message += "To checkout, send \"checkout\"";
			} else {
				logger.info("OLD USER: Purchase & Step in session.");
				logger.debug(manager.getPurchase().toString());
				if (StringUtils.containsIgnoreCase(body, "reset")) {
					message = "Resetting your user session. ";
					manager.setPurchase(null);
					//TODO: Remember to do this
					//builder.purchases.remove();
				} else {
					switch (step) {
						case IN_PROGRESS: {
							logger.info("Step = IN PROGRESS");
							if (StringUtils.containsIgnoreCase(body, "cart")) {
								message = manager.cart();
							} else if (StringUtils.containsIgnoreCase(body, "checkout")) {
 								if (!manager.getPurchase().getCart().isEmpty()) {
									/*if(!confirm.booleanValue()) {
										message = "Your cart is \n" + manager.cart();
										message+= "\n\nType \"checkout\" again to confirm.";
									} else {*/
										message = "please choose a time and place for your order pickup. \n" +
												". . . . . . . .\n" + TimeAndPlace.list();
										step = CheckoutStep.TIME_AND_PLACE;
									//}
								} else {
									message = "Your cart is empty!";
								}
							} else if(StringUtils.containsIgnoreCase(body,"clear")) {
								if (!manager.getPurchase().getCart().isEmpty()) {
									manager.getPurchase().getCart().clear();
									message = "Cart cleared.";
								} else {
									message = "Your cart is empty!";
								}
							} else {
								String name = null;
								int quantity = 0;

								purchaseloop:
								for(Item item : manager.stockist.getStocks().values()) {
									if(containsOnceIgnoreCase(body, item.getName())) {
										name = item.getName();
										String unparsedQuantity = RegExp.find(body,"((-)|())[0-9]+");
										if(unparsedQuantity !=null) {
											quantity = Integer.parseInt(unparsedQuantity);
											break purchaseloop;
										} else {
											quantity = 1;
											break purchaseloop;
										}
									}
								}
								if(name!=null && quantity!=0) {
									logger.debug("REQUEST: Parsed name=" + name
											+ "\nParsed quantity=" + quantity);
									PurchaseResponse res = manager.buy(name, quantity);
									if (res == PurchaseResponse.NO_ITEM_FOUND) {
										message = "No product named " + name + " found.";
									} else if (res == PurchaseResponse.OUT_OF_STOCK) {
										if (manager.stockist.getStocks().get(name).getQuantity() < quantity) {
											message = "sorry, we only have " + manager.stockist.getStocks().get(name).getQuantity() + " " + name + ". You wanted " + quantity + ". ";
										} else {
											message = "product named " + name + " is out of stock.";
										}
									} else if(res == PurchaseResponse.ADDED) {
										Item inCart = manager.getPurchase().getCart().getByName(name);
										if(inCart==null) {
											message = "Added " + quantity + " " + name + " to your cart.";
										} else {
											message = "Added " + quantity + " " + name + " to your cart. You now have " + inCart.getQuantity() + " in your cart.";
										}
									} else if(res == PurchaseResponse.REMOVED){
										message = "Removed " + Math.abs(quantity) + " " + name + " from you cart.";
									} else {
										message = "Removed all " + name + " from your cart.";
									}
								} else {
										message = "Invalid request, try again.";
								}
							}
						}
						break;
						case TIME_AND_PLACE: {
							logger.info("Step = TIME AND PLACE");

							message = "Invalid time and place, please try again.";

							TimeAndPlace timeAndPlace = null;

							timeplaceloop:
							for(String place : TimeAndPlace.validPlaces) {
								if(StringUtils.containsIgnoreCase(body, place)) {
									for(String time : TimeAndPlace.validTimes) {
										if(StringUtils.containsIgnoreCase(body, time)) {
											logger.debug("time: " + time
													+ "\n place: " + place);
											timeAndPlace = new TimeAndPlace(time,place);
											break timeplaceloop;
										}
									}
								}
							}

							if (timeAndPlace == null) {
								message = "Invalid time or place. Please try again.";
							} else {
								message = "Please wait while we find you a merchant...";
								manager.getPurchase().setTimeAndPlace(timeAndPlace);
								step = CheckoutStep.PAYMENT;
								merchantValidator.askForMerchant(manager.getPurchase());
								try {
									merchantValidator.rejectTimer(manager.getPurchase());
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						break;
						case TIME_AND_PLACE_VALIDATE: {
							/**
							 * This step is @deprecated
							 */
							logger.info("Step = TIME AND PLACE VALIDATE");
							/*merchantValidator.askForMerchant(manager.getPurchase());*/
							//TODO: THIS ISN'T SCALABLE.

						}
						break;
						case PAYMENT: {
							Purchase p = builder.purchases.get(fromNumber);
							if(StringUtils.containsIgnoreCase(body, "cash")) {
								message = "Ok, meet " + p.getMerchant().getName()
										+ "\nat: " + p.getTimeAndPlace().getTime()
										+ "\nin: " + p.getTimeAndPlace().getPlace()
										+ "\n\nYour total is $" + p.getCart().getTotal() + ". "
										+ "\nSee you then!";
								p.setPaymentType(PaymentType.CASH);
								builder.statsManager.addPurchase(p);
							} else {
								paymentloop:
								for (PaymentType type : PaymentType.values()) {
									for (String term : type.getMatchTerms()) {
										if (StringUtils.containsIgnoreCase(body, term)) {
  											p.setPaymentType(type);
											message = "Click here to checkout: " +
													builder.generateURL(p);
											break paymentloop;
										} else {
											message = "No payment method for \""+body+"\" please try again.";
										}
									}
								}
							}
						}
						break;
						default: {

						}
						break;
					}
				}
			}
			//session.setAttribute("checkoutConfirm", confirm);
			session.setAttribute("checkoutStep", step);
			session.setAttribute("purchase", manager.getPurchase());
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
			if(!SMSInternalServlet.myNumber.equals(toNumber)) {
				logger.error("EXCEPTION: User didn't receive message?");
				ErrorHandler.handleError(fromNumber);
				manager.setPurchase(null);
			}
			//e.printStackTrace();
		}

		//System.out.println(request.getParameter("Body"));
	}

	private boolean containsOnceIgnoreCase(String total, String search) {
		total = total.toLowerCase();
		return total.split(search.toLowerCase()).length==2;
	}

}
