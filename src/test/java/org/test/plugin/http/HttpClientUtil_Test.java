package org.test.plugin.http;

import org.apache.log4j.Logger;
import org.bo.plugin.http.HttpClientUtil;
import org.junit.Test;

public class HttpClientUtil_Test {
	private Logger log = Logger.getLogger(HttpClientUtil_Test.class);
	
	/**
	 * 测试HttpClientUtil访问url
	 */
	@Test
	public void testUrl() {
		String url = "https://www.baidu.com";
		String content = HttpClientUtil.doGetExecute(url);
		log.error(content);
	}
}
