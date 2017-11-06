package cn.lazy.base;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;

import sun.misc.BASE64Decoder;

@SuppressWarnings("restriction")
@Service
public class QiNiuFileService {

	@Value("${qiniu.lazy.accesskey}")
    private String accessKey;
	
	@Value("${qiniu.lazy.secretkey}")
    private String secretKey;
	
	@Value("${qiniu.lazy.bucketname}")
    private String bucketName;
	
	@Value("${qiniu.lazy.buckethostname}")
	private String bucketHostName;
	
	/**
	 * // 要求url可公网正常访问BucketManager.fetch(url, bucketName, key);
    // @param url 网络上一个资源文件的URL
    // @param bucketName 空间名称
    // @param key 空间内文件的key[唯一的]
	 * @方法名: uploadFileToQiNiu .
	 * @描述: TODO .
	 * @程序猿: chenjingwu .
	 * @返回值: String .
	 * @日期: 2017年3月17日 下午2:59:42 .
	 * @throws
	 */
	public String uploadFileToQiNiu(String base64Str) {
		String upFileUrl = null;
//		byte[] uploadBytes = null;
		// 构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone0());
		// ...其他参数参考类注释
		UploadManager uploadManager = new UploadManager(cfg);
		// ...生成上传凭证，然后准备上传
//		String accessKey = "_7U6qErggmO5sccmX1KSEaISWLXnPhjb1SOqZPGY";
//		String secretKey = "Z_fr4sJnnQH-mqepeFcYbwDp-6p1PyCF4j6Lf7g6";
//		String bucketName = "lazyhealth";
		// 默认不指定key的情况下，以文件内容的hash值作为文件名
//		try {
//			uploadBytes = FileUtils.getContent(originalUrl);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		BASE64Decoder decoder = new BASE64Decoder();
		// Base64解码
		byte[] uploadBytes = null;
		try {
			uploadBytes = decoder.decodeBuffer(base64Str);
			for (int i = 0; i < uploadBytes.length; ++i) {
					if (uploadBytes[i] < 0) {// 调整异常数据
						uploadBytes[i] += 256;
					}
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(uploadBytes);
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucketName);
		try {
			Response response = uploadManager.put(byteInputStream, null, upToken, null, null);
			// 解析上传成功的结果
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//			System.out.println(putRet.key);
//			System.out.println(putRet.hash);
			upFileUrl = bucketHostName + putRet.key;
		} catch (QiniuException ex) {
			Response r = ex.response;
			System.err.println(r.toString());
			try {
				System.err.println(r.bodyString());
			} catch (QiniuException ex2) {
				// ignore
			}
		}
		return upFileUrl;
	}
	
	/**
	 * 删除指定文件
	 * @方法名: delQiniuFile .
	 * @描述: TODO .
	 * @程序猿: chenjingwu .
	 * @返回值: Response .
	 * @日期: 2017年3月17日 下午4:45:33 .
	 * @throws
	 */
	public Response delQiniuFile(String key){
		Response response = null;
	    //构造一个带指定Zone对象的配置类
	    Configuration cfg = new Configuration(Zone.zone0());
	    //...其他参数参考类注释
//		String accessKey = "_7U6qErggmO5sccmX1KSEaISWLXnPhjb1SOqZPGY";
//		String secretKey = "Z_fr4sJnnQH-mqepeFcYbwDp-6p1PyCF4j6Lf7g6";
//		String bucket = "lazyhealth";
	    Auth auth = Auth.create(accessKey, secretKey);
	    BucketManager bucketManager = new BucketManager(auth, cfg);
	    try {
	    	response = bucketManager.delete(bucketName, key);
	        
	    } catch (QiniuException ex) {
	        //如果遇到异常，说明删除失败
	        System.err.println(ex.code());
	        System.err.println(key+"------"+ex.response.toString());
	    }
	    return response;
	}
	
	
	/** 上传视频文件至七牛空间
	 * @方法名: uploadVideoToQiNiu .
	 * @描述: TODO .
	 * @程序猿: chenjingwu .
	 * @返回值: String .
	 * @日期: 2017年3月29日 下午6:30:37 .
	 * @throws
	 */
	public String uploadVideoToQiNiu(MultipartFile file) {
		String upFileUrl = null;
//		byte[] uploadBytes = null;
		ByteArrayInputStream byteInputStream = null;
		// 构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone0());
		// ...其他参数参考类注释
		UploadManager uploadManager = new UploadManager(cfg);
		// ...生成上传凭证，然后准备上传
//		String accessKey = "_7U6qErggmO5sccmX1KSEaISWLXnPhjb1SOqZPGY";
//		String secretKey = "Z_fr4sJnnQH-mqepeFcYbwDp-6p1PyCF4j6Lf7g6";
//		String bucketName = "lazyhealth";
		// 默认不指定key的情况下，以文件内容的hash值作为文件名
//		try {
//			uploadBytes = FileUtils.getContent(originalUrl);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucketName);
		try {
			byteInputStream = new ByteArrayInputStream(file.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Response response = uploadManager.put(byteInputStream, null, upToken, null, null);
			// 解析上传成功的结果
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//			System.out.println(putRet.key);
//			System.out.println(putRet.hash);
			upFileUrl = bucketHostName + putRet.key;
		} catch (QiniuException ex) {
			Response r = ex.response;
			System.err.println(r.toString());
			try {
				System.err.println(r.bodyString());
			} catch (QiniuException ex2) {
				// ignore
			}
		}
		return upFileUrl;
	}
	
	
	/** 返回客户端上传token
	 * @方法名: upTokenByQiniu .
	 * @描述: TODO .
	 * @程序猿: chenjingwu .
	 * @返回值: String .
	 * @日期: 2017年3月30日 上午10:50:19 .
	 * @throws
	 */
	public String upTokenByQiniu(String key) {
		String upToken = null;
	    Auth auth = Auth.create(accessKey, secretKey);
	    if(StringUtils.isNullOrEmpty(key)) {
	    	 upToken = auth.uploadToken(bucketName);
	    } else {
	    	 upToken = auth.uploadToken(bucketName, key);
	    }
	    return upToken;
	}
}
