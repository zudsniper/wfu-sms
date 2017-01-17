package cc.holstr.wfu.model;

import cc.holstr.wfu.model.pickup.TimeAndPlace;
import cc.holstr.wfu.sms.transaction.PaymentType;

import java.util.Objects;

/**
 * Created by jason on 1/8/17.
 */
public class Purchase {

	private String number;
	private TimeAndPlace timeAndPlace;
	private Merchant merchant;
	private Cart cart;
	private PaymentType paymentType;

	public Purchase(String number) {
		build(number,null,null, new Cart(), null);
	}

	public Purchase(String number, TimeAndPlace timeAndPlace, Merchant merchant, Cart cart, PaymentType paymentType) {
		build(number, timeAndPlace, merchant, cart, paymentType);
	}

	public void build(String number, TimeAndPlace timeAndPlace, Merchant merchant, Cart cart, PaymentType paymentType) {
		this.number = number;
		this.timeAndPlace = timeAndPlace;
		this.merchant = merchant;
		this.cart = cart;
		this.paymentType = paymentType;
	}

	public boolean isFinal() {
		return number!=null && timeAndPlace!=null && merchant!=null && cart!=null && paymentType!=null;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public TimeAndPlace getTimeAndPlace() {
		return timeAndPlace;
	}

	public void setTimeAndPlace(TimeAndPlace timeAndPlace) {
		this.timeAndPlace = timeAndPlace;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	@Override
	public String toString() {
		return "Purchase{" +
				"number='" + number + '\'' +
				", timeAndPlace=" + timeAndPlace +
				", merchant=" + merchant +
				", cart=" + cart +
				", paymentType=" + paymentType +
				'}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(number, timeAndPlace, merchant, cart, paymentType);
	}
}
