package org.bo.plugin.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class HttpClientUtil {
	
	/**
	 * get请求
	 * @param url
	 * @author chengbo
	 * @date 2016年1月19日 12:10:51
	 */
	public static String doGetExecute(String url) {
		if(url == null)
			return null;
		String content = null;
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		
		try {
			response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(httpclient != null)
					httpclient.close();
				if(response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	
	/**
	 * post请求
	 * @param url url
	 * @param paramMap
	 * @author chengbo
	 * @date 2016年1月19日 12:06:30
	 */
	public static String doPostExecute(String url, Map<String, String> paramMap) {
		if(url == null)
			return null;
		return sendHttpPost(buildHttpPost(url, paramMap), null);
	}
	
	/**
	 * post请求
	 * @param url
	 * @param param
	 * @return String
	 */
	public static String doPostExecute(String url,String param) {
		if(url == null)
			return null;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(param,Consts.UTF_8));
		return sendHttpPost(httpPost, null);
	}
	
	/**
	 * post请求
	 * @param url
	 * @param headers
	 * @param paramMap
	 * @return String
	 */
	public static String doPostExecute(String url, Map<String, String> headers, Map<String, String> paramMap) {
		return sendHttpPost(buildHttpPost(url, paramMap), headers);
	}
	
	/**
	 * 上传文件
	 * @param url
	 * @param headers
	 * @param paramMap
	 * @param fileMap
	 * @return String
	 */
	public static String doUploadFileExecute(String url,  Map<String, String> paramMap, Map<String, File> fileMap) {
		return sendHttpPost(buildHttpPost(url, paramMap,fileMap), null);
	}
	
	/**
	 * 构建HttpPost
	 * @param url
	 * @param paramMap
	 * @return HttpPost
	 */
	private static HttpPost buildHttpPost(String url, Map<String, String> paramMap) {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000).setConnectionRequestTimeout(15000).build());
		if(paramMap == null)
			return httpPost;
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (Entry<String, String> entry : paramMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			nvps.add(new BasicNameValuePair(key, value));
		}
		httpPost.setEntity(new UrlEncodedFormEntity(nvps,Consts.UTF_8));
		return httpPost;
	}
	
	/**
	 * 构建HttpPost
	 * @param url
	 * @param paramMap
	 * @param fileMap 文件Map
	 * @return HttpPost
	 */
	private static HttpPost buildHttpPost(String url, Map<String, String> paramMap, Map<String, File> fileMap) {
		HttpPost httpPost = buildHttpPost(url, paramMap);
		if(fileMap == null)
			return httpPost;
		
		MultipartEntityBuilder multiEntityBuilder = MultipartEntityBuilder.create();
		for (Entry<String, File> entry : fileMap.entrySet()) {
			multiEntityBuilder.addPart(entry.getKey(), new FileBody(entry.getValue(), ContentType.APPLICATION_OCTET_STREAM));
		}
		httpPost.setEntity(multiEntityBuilder.build());
		return httpPost;
	}
	
	/**
	 * 发送请求
	 * @param httpPost
	 * @param headers
	 * @return String
	 */
	private static String sendHttpPost(HttpPost httpPost, Map<String, String> headers){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		if(headers != null)
			httpPost.setHeaders(buildHeader(headers));
		String content = null;
		CloseableHttpResponse response = null; 
		
		try {
			response = httpclient.execute(httpPost);
			content = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(httpclient != null)
					httpclient.close();
				if(response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	
	/**
	 * 构建header
	 * @param params
	 * @return Header[]
	 */
	private static Header[] buildHeader(Map<String, String> params) {
        Header[] headers = null;
        if (params != null && params.size() > 0) {
            headers = new BasicHeader[params.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                headers[i] = new BasicHeader(entry.getKey(), entry.getValue());
                i++;
            }
        }
        return headers;
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
		return sendHttpPost(httpPost, null);
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
