package cc.holstr.wfu.servlet;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;

/**
 * Created by jason on 2/1/17.
 */
public class SMSShopServletTest {
	@Test
	public void service() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = new MockHttpSession();
		request.setSession(session);
		request.setParameter("To",SMSShopServlet.myNumber);
		request.setParameter("From","+11111111111");
		request.setParameter("Body","hello");

		service();
	}

}