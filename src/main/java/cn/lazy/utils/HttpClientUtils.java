package cn.lazy.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;




public class HttpClientUtils {
	
	
	private static HttpClient client = new HttpClient();
	private static CloseableHttpClient postClient = HttpClients.createDefault();
	
	public static String getToken() throws Exception {
//		GetMethod method = new GetMethod("https://testmodeldataapi.visbodyfit.com:30000/v1/token?visid=vf5a0168cac31a9&secret=9091caca39c30870608200ce5ccd02a1");
		String token=null;
		try {  
			org.apache.http.client.HttpClient httpClient = CertificateValidationIgnored.getNoCertificateHttpClient(
					"https://api.visbodyfit.com:30000/v1/token?visid=vf59eac478eb6d9&secret=82d20916b968432534875330aa60e51b");
			HttpGet request = new HttpGet("https://api.visbodyfit.com:30000/v1/token?visid=vf59eac478eb6d9&secret=82d20916b968432534875330aa60e51b");
//			HttpClientUtils.client.executeMethod(method);
          HttpResponse httpResponse = httpClient.execute(request);
          HttpEntity entity = httpResponse.getEntity();
          String responseJson =EntityUtils.toString(entity,"utf-8");  
//          httpResponse.toString();
//          String responseJson = method.getResponseBodyAsString();
          Map<String, Object> responseMap = JSONUtil.toMapFastJson(responseJson);
           token = responseMap.get("token").toString();
//	        System.out.println(method.getStatusLine());//返回的状态  
//	        System.out.println(method.getResponseBodyAsString());//返回的参数 
	      } catch (HttpException e) {
	          e.printStackTrace();  
	      } catch (IOException e) {  
	          e.printStackTrace();  
	      } 
		return token;
	}
	
	/**
	 * 
	  * @方法名: executeGet
	  * @描述: TODO .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 下午3:41:23
	  * @返回值: Map<String,Object>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public static Map<String, Object> executeGet(String url) {
//		GetMethod method = new GetMethod(url);
		Map<String, Object> responseMap=null;
		 try {
			 org.apache.http.client.HttpClient httpClient = CertificateValidationIgnored.getNoCertificateHttpClient(url);
			 HttpGet request = new HttpGet(url);
			 HttpResponse httpResponse = httpClient.execute(request);
	         HttpEntity entity = httpResponse.getEntity();
	         String responseJson =EntityUtils.toString(entity,"utf-8"); 
//			HttpClientUtils.client.executeMethod(method);
//			String responseJson = method.getResponseBodyAsString();
//			System.out.println(responseJson);
			responseMap = JSONUtil.toMapFastJson(responseJson);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseMap;
	}
	
	/**
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * 
	  * @方法名: executePost
	  * @描述: post请求 .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 下午4:16:13
	  * @返回值: Map<String,Object>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public static Map<String, Object> executePost(String url, Map<String, String> map) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		
		    //采用绕过验证的方式处理https请求  
		    SSLContext sslcontext = createIgnoreVerifySSL();  
		      
		       // 设置协议http和https对应的处理socket链接工厂的对象  
		       Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
		           .register("http", PlainConnectionSocketFactory.INSTANCE)  
		           .register("https", new SSLConnectionSocketFactory(sslcontext))  
		           .build();  
		       PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
		       HttpClients.custom().setConnectionManager(connManager);  
		  
		       //创建自定义的httpclient对象  
		    CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
//		    CloseableHttpClient client = HttpClients.createDefault();
		
		
		//创建post方式请求对象  
        HttpPost httpPost = new HttpPost(url); 
        String responseString=null;
        //装填参数  
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
        if(map!=null){  
            for (Entry<String, String> entry : map.entrySet()) {  
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
            }  
        }  
        //设置参数到请求对象中  
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));  
  
        System.out.println("请求地址："+url);  
        System.out.println("请求参数："+nvps.toString());  
          
        //设置header信息  
        //指定报文头【Content-type】、【User-Agent】  
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");  
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
          
        //执行请求操作，并拿到结果（同步阻塞）  
        CloseableHttpResponse response = client.execute(httpPost);  
        //获取结果实体  
        HttpEntity entity = response.getEntity(); 
        if(response != null){  
            HttpEntity resEntity = response.getEntity();  
            if(resEntity != null){  
            	responseString= EntityUtils.toString(resEntity,"utf-8");  
            	System.out.println(responseString);
            }  
        }
        System.out.println(entity);
        EntityUtils.consume(entity);  
        //释放链接  
        response.close(); 
        return JSONUtil.toMap(responseString);
	}
	
	
	
	/**
	 * 
	  * @方法名: executeGetForModel
	  * @描述: 模型base64 .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 下午5:47:09
	  * @返回值: String  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public static String executeGetForModel(String url) {
//		GetMethod method = new GetMethod(url);
		String encodeBuffer=null;
		 try {
			 org.apache.http.client.HttpClient httpClient = CertificateValidationIgnored.getNoCertificateHttpClient(url);
			 HttpGet request = new HttpGet(url);
			 HttpResponse httpResponse = httpClient.execute(request);
	         HttpEntity entity = httpResponse.getEntity();
	         String responseJson =EntityUtils.toString(entity,"utf-8");  
//			HttpClientUtils.client.executeMethod(method);
//			String responseJson = method.getResponseBodyAsString();
//			System.out.println(responseJson);
			encodeBuffer= QRCodeUtils.encoder.encodeBuffer(responseJson.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encodeBuffer;
	}
	
	
	
	/** 
	 * 绕过验证 
	 *   
	 * @return 
	 * @throws NoSuchAlgorithmException  
	 * @throws KeyManagementException  
	 */  
	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {  
	    SSLContext sc = SSLContext.getInstance("SSLv3");  
	  
	    // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法  
	    X509TrustManager trustManager = new X509TrustManager() {  
	        @Override  
	        public void checkClientTrusted(  
	                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,  
	                String paramString) throws CertificateException {  
	        }  
	  
	        @Override  
	        public void checkServerTrusted(  
	                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,  
	                String paramString) throws CertificateException {  
	        }  
	  
	        @Override  
	        public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
	            return null;  
	        }  
	    };  
	  
	    sc.init(null, new TrustManager[] { trustManager }, null);  
	    return sc;  
	} 
}
