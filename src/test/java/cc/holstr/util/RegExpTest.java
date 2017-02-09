package cc.holstr.util;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created by jason on 2/5/17.
 */
public class RegExpTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Before
	public void setUp() {

	}

	@Test
	public void searchWith1Flag() throws Exception {
		String total = "this THIS thIs is a test string";
		String exp = "THIS";
		List<String> ret = RegExp.search(total,exp, Pattern.CASE_INSENSITIVE);
		logger.debug("1Flag");
		for(String str : ret) {
			logger.debug(str);
		}
		assertTrue(ret.size()==3);
	}

	@Test
	public void searchWithEmbeddedFlag() throws Exception {
		String total = "this THIS thIs is a test string";
		String exp = "(?i)THIS";
		List<String> ret = RegExp.search(total,exp);
		logger.debug("EmbeddedFlag");
		for(String str : ret) {
			logger.debug(str);
		}
		assertTrue(ret.size()==3);
	}

	@Test
	public void search() throws Exception {
		String total = "this THIS thIs is a test string";
		String exp = "THIS";
		List<String> ret = RegExp.search(total,exp);
		logger.debug("search");
		for(String str : ret) {
			logger.debug(str);
		}
		assertTrue(ret.size()==1);
	}

	@Test
	public void find1() throws Exception {

	}

	@Test
	public void find2() throws Exception {

	}

	@Test
	public void contains1() throws Exception {

	}

	@Test
	public void contains2() throws Exception {

	}

	@Test
	public void find() throws Exception {

	}

	@Test
	public void contains() throws Exception {

	}

}