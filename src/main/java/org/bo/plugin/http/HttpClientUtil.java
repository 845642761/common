package org.bo.plugin.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class HttpClientUtil {
	
	/**
	 * get请求
	 * @param: uri
	 * @author: chengbo
	 * @date: 2016年1月19日 12:10:51
	 */
	public static String doGetExecute(String uri) {
		if(uri == null)
			return null;
		String content = null;
		HttpGet httpGet = new HttpGet(uri);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	
	/**
	 * post请求
	 * @param: hm 参数
	 * @param: uri uri
	 * @author: chengbo
	 * @date: 2016年1月19日 12:06:30
	 */
	public static String doPostExecute(HashMap<String, String> hm,String url) {
		if(url == null)
			return null;
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if(hm != null){
			for (Entry<String, String> entry : hm.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				nvps.add(new BasicNameValuePair(key, value));
			}
		}
		HttpPost httpPost = new HttpPost(url);
		String content = null;
		httpPost.setEntity(new UrlEncodedFormEntity(nvps,Consts.UTF_8));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		try {
			CloseableHttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	
	/**
	 * post请求
	 * @param url
	 * @param param
	 * @return
	 */
	public static String doPostExecute(String url,String param) {
		if(url == null)
			return null;
		
		HttpPost httpPost = new HttpPost(url);
		String content = null;
		httpPost.setEntity(new StringEntity(param,Consts.UTF_8));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		try {
			CloseableHttpResponse response = httpclient.execute(httpPost);
			content = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	
	/**
	 * post请求
	 * @param: hm 参数
	 * @param: uri uri
	 * @param: header header信息
	 * @author: chengbo
	 * @date: 2016年1月20日 18:03:43
	 */
	public String doPostExecute(HashMap<String, String> hm,String uri,HashMap<String, String> header) {
		if(uri == null)
			return null;
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if(hm != null){
			for (Entry<String, String> entry : hm.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				nvps.add(new BasicNameValuePair(key, value));
			}
		}
		HttpPost httpPost = new HttpPost(uri);
		String content = null;
		httpPost.setEntity(new UrlEncodedFormEntity(nvps,Consts.UTF_8));
		CookieStore cookieStore = new BasicCookieStore();
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		try {
			CloseableHttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	
	/**
	 * WSDL请求
	 * @param: hm 参数
	 * @param: uri uri
	 * @param: methodName: 方法名称
	 * @param: nameSpace: 命名空间
	 * @author: chengbo
	 * @date: 2016年1月20日 16:13:30
	 */
	public String doWSDLExecute(HashMap<String, String> hm,String uri,String methodName,String nameSpace) {
		HttpPost httpPost = new HttpPost(uri);
		String content = null;
		String wsdlRequestData = makeWSDLXML(hm, methodName, nameSpace);
		byte[] bytes = null;
		try {
			bytes = wsdlRequestData.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		InputStream inputStream = new ByteArrayInputStream(bytes, 0, bytes.length);
		HttpEntity requestEntity = new InputStreamEntity(inputStream,bytes.length,ContentType.create("application/soap+xml", Consts.UTF_8));
		httpPost.setEntity(requestEntity);
		httpPost.setHeader("Content-Type","application/soap+xml; charset=utf-8");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity,Consts.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	
	/**
	 * 创建WSDLxml
	 * @param: hm 参数
	 * @param: uri uri
	 * @param: methodName: 方法名称
	 * @param: nameSpace: 命名空间
	 * @author: chengbo
	 * @date: 2016年1月20日 16:14:28
	 */
	private String makeWSDLXML(HashMap<String, String> hm,String methodName,String nameSpace) {
		if(hm == null || methodName == null || nameSpace == null)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append("<soapenv:Envelope");
		sb.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
		sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		sb.append(" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
		sb.append("<soapenv:Body>\n");
		sb.append("	<ns0:");
		sb.append(methodName);
		sb.append(" xmlns:ns0=\"");
		sb.append(nameSpace);
		sb.append("\">\n");
		
		int i = 0;
		for (Entry<String, String> entry : hm.entrySet()) {
			String value = entry.getValue();
			sb.append("		<arg");
			sb.append(i);
			sb.append(">");
			sb.append(value);
			sb.append("</arg");
			sb.append(i);
			sb.append(">\n");
			i++;
		}
		sb.append("	</ns0:");
		sb.append(methodName);
		sb.append(">\n");
		sb.append("</soapenv:Body>\n");
		sb.append("</soapenv:Envelope>");
		return sb.toString();
	}
}
