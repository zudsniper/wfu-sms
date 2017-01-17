package cc.holstr.wfu.sms.transaction;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by jason on 1/7/17.
 */
public enum PaymentType {
	PAYPAL("PayPal (paypal)"),
	CARD("Credit/Debit Card (card)",new String[]{"credit","debit","card"}),
	CASH("Cash At Transaction (cash)",new String[]{"cash"});

	private final String displayName;

	private final String[] matchTerms;

	private PaymentType() {
		displayName="";
		matchTerms=null;
	}

	private PaymentType(String displayName) {
		this.displayName = displayName;
		matchTerms = new String[1];
		matchTerms[0] = this.displayName;
	}

	private PaymentType(String displayName, String[] matchTerms) {
		this.displayName = displayName;
		this.matchTerms = matchTerms;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String[] getMatchTerms() {
		return matchTerms;
	}

	public PaymentType match(String text) {
		for(PaymentType payment : PaymentType.values()) {
			if(payment.getMatchTerms()!=null) {
				for (String term : payment.getMatchTerms()) {
					if(StringUtils.containsIgnoreCase(text,term)) {
						return payment;
					}
				}
			}
		}
		return null;
	}

	public String toString() {
		return getDisplayName();
	}

	public static String prettyList() {
		String message = "";
		for(PaymentType type : values()) {
			message+=type.toString() + "\n";
		}
		return message;
	}
}
