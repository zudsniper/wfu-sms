package cc.holstr.wfu.web.payment.controller;

import cc.holstr.wfu.WfusmsInterfaceApplication;
import cc.holstr.wfu.model.Cart;
import cc.holstr.wfu.model.Item;
import cc.holstr.wfu.model.Merchant;
import cc.holstr.wfu.model.Purchase;
import cc.holstr.wfu.model.pickup.TimeAndPlace;
import cc.holstr.wfu.properties.Unpacker;
import cc.holstr.wfu.services.PurchaseBuilder;
import cc.holstr.wfu.sms.transaction.PaymentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

/**
 * Created by jason on 1/11/17.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=WfusmsInterfaceApplication.class)
@SpringBootTest
public class PurchaseBuilderTests{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Mock
	private PurchaseBuilder builder;

	@Mock
	private PaymentController paymentController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void TestGenerateURLFromUUID() {
		Unpacker.unpack();
		Cart c = new Cart();
		c.add(new Item("Test Item", 3.50, 1));
		Purchase p = new Purchase("+11111111111",
				TimeAndPlace.create("8:00-8:30","PAC"),
				new Merchant("test","+22222222222"),
				 c,
				PaymentType.CARD);
		String url = builder.generateURL(p);

		logger.info(url);


		assertTrue(""=="");
	}

}
