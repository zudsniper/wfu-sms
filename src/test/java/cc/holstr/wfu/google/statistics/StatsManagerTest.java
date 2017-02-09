package cc.holstr.wfu.google.statistics;

import cc.holstr.wfu.model.*;
import cc.holstr.wfu.properties.Unpacker;
import cc.holstr.wfu.sms.transaction.PaymentType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jason on 2/4/17.
 */
public class StatsManagerTest {
	@Test
	public void addPurchase() throws Exception {
		Unpacker.STATS_SPREADSHEET_ID = "1IJWic-pWzfU5fAmaX1kLzswUIEZo3qzM3cxSE-qiLYU";
		StatsManager manager = new StatsManager();
		Purchase purchase = new Purchase("number");
		Cart cart = new Cart();
		cart.add(new Item("item",1,1));
		purchase.setCart(cart);
		purchase.setMerchant(new Merchant("name","number"));
		purchase.setPaymentType(PaymentType.CASH);
		purchase.setTimeAndPlace(TimeAndPlace.testCreate("time","place"));
		String timeSpent = manager.addPurchase(purchase);
		String[] expected = {
				"number",
				"item     $1.0   1n",
				"1",
				purchase.getMerchant().getName(),
				purchase.getTimeAndPlace().getTime(),
				purchase.getTimeAndPlace().getPlace(),
				purchase.getPaymentType().getDisplayName(),
				timeSpent
		};
		String[][] actualsMatrix = manager.getSheetsIO().getContents("stats");
		String fin = "";

		//trim timestamp
		String[] actual = new String[expected.length];
		for(int i = 0; i<actual.length; i++) {
			actual[i]=actualsMatrix[actualsMatrix.length-1][i+1];
		}

		for(int r = 0; r<actualsMatrix.length; r++) {
			for(int c = 0; c<actualsMatrix[r].length; c++) {
				fin += actualsMatrix[r][c]+", ";
			}
			fin+="\n";
		}
		System.out.println(fin);
		assertArrayEquals(expected, actual);
	}

}