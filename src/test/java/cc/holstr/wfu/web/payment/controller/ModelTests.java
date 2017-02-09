package cc.holstr.wfu.web.payment.controller;

import cc.holstr.wfu.WfusmsInterfaceApplication;
import cc.holstr.wfu.services.PurchaseBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by jason on 1/17/17.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=WfusmsInterfaceApplication.class)
@SpringBootTest
//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ModelTests {

	/*@LocalServerPort
	private int port;

	@Autowired
	private PaymentController paymentController;*/

	private MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = standaloneSetup(new PaymentController(new PurchaseBuilder())).build();
	}

	@Test
	public void showPurchasePage() {

	}

}
