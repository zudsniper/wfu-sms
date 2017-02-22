package cc.holstr.wfu.servlet;

import cc.holstr.wfu.model.Merchant;
import cc.holstr.wfu.model.StaticInfo;
import cc.holstr.wfu.properties.Unpacker;
import cc.holstr.wfu.services.MerchantValidator;
import cc.holstr.wfu.services.PurchaseBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by jason on 2/9/17.
 */
@SpringBootTest
public class SMSInternalServletTest {

	@Before
	public void before() {
	}

	@Test
	public void service() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		when(request.getParameter("Body")).thenReturn("ACCEPT +15121111111");

		PrintWriter writer = new PrintWriter("temp.txt");
		when(response.getWriter()).thenReturn(writer);

		MerchantValidator validator = mock(MerchantValidator.class);

		validator.setMerchants(new ArrayList<>());

		new SMSInternalServlet(mock(PurchaseBuilder.class), validator).service(request,response);

		verify(request.getParameter("Body"), atLeast(1));
		writer.flush();
		assertTrue(FileUtils.readFileToString(new File("temp.txt"),"UTF-8").contains("Ok, you accepted +15121111111 's order. Thanks!"));
	}

}