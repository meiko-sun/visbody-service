package cn.lazy.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.impl.HttpConnectionMetricsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.lazy.base.BaseExecuteResult;
import cn.lazy.base.BaseService;
import cn.lazy.base.QiNiuFileService;
import cn.lazy.base.RedisService;
import cn.lazy.base.ValidationResult;
import cn.lazy.mapper.LazyVisbodyMapper;
import cn.lazy.model.AccessToken;
import cn.lazy.model.Cipher;
import cn.lazy.model.Visbody;
import cn.lazy.model.VisbodyData;
import cn.lazy.model.VisbodyWeiduData;
import cn.lazy.utils.ConstantUtil;
import cn.lazy.utils.DateUtils;
import cn.lazy.utils.HttpClientUtils;
import cn.lazy.utils.JSONUtil;
import cn.lazy.utils.QRCodeUtils;
import cn.lazy.utils.ValidationUtils;

/**
 * 智能衣柜接口服务
  * @类名: LazyUserWardrobeService
  * @描述: TODO .
  * @程序猿: chenjingwu .
  * @日期: 2017年10月25日 下午5:55:40
  * @版本号: V2.0 .
  *
 */
@Service
public class LazyVisbodyService extends BaseService {
	

    @Autowired
    private RedisService redisService;
    
    @Autowired
    private LazyAccessTokenService lazyAccessTokenService;
    
	@Autowired
	private LazyVisbodyMapper lazyVisbodyMapper;
	
	@Autowired
	private QiNiuFileService qiNiuFileService;
	
	@Value("${visbody.bodysUrl}")
    private String bodysUrl;
	
	@Value("${visbody.datasUrl}")
    private String datasUrl;

	@Value("${visbody.progressUrl}")
    private String progressUrl;
	
	@Value("${visbody.modelsUrl}")
    private String modelsUrl;
	
	@Value("${visbody.recorde_url}")
    private String recorde_url;
	
	@Value("${visbody.entry_url}")
    private String entry_url;
	/**
	 * 
	  * @方法名: getQrCode
	  * @描述: 获取二维码 .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午11:01:43
	  * @返回值: BaseExecuteResult<?>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public BaseExecuteResult<Object> getQrCode(String token,String json) {
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "getQrCode", json);
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "notifyResult", json);
		BaseExecuteResult<Object> resultMsg = lazyAccessTokenService.iSVisbodyTokenMessage(token);
		if (null != resultMsg)
			return resultMsg;
		BaseExecuteResult<Object> result = null;
		try {
			Visbody visbody = JSONUtil.toBean(json, Visbody.class);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("deviceid", visbody.getDeviceId());
			jsonObject.put("scanid", visbody.getScanId());
			jsonObject.put("company", "lazyhealth");
			String data = QRCodeUtils.encode(jsonObject.toString(), "", "C:\\data\\", true);
			String uploadFileToQiNiu = qiNiuFileService.uploadFileToQiNiu(data);
			Map<String,Object> QrcMap = Maps.newHashMap();
			QrcMap.put("scanId", visbody.getScanId());
			QrcMap.put("qrcurl", uploadFileToQiNiu);
			int insertNewScanId = lazyVisbodyMapper.insertNewScanId(QrcMap);
			if(insertNewScanId > 0) {
				Map<String,Object> resultMap = Maps.newHashMap();
				resultMap.put("data", uploadFileToQiNiu);
				result = new BaseExecuteResult<Object>(ConstantUtil.success,resultMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			info(ERROR_FORMAT, this.getClass().getSimpleName(), "getQrCode", e.getMessage());
			result = new BaseExecuteResult<Object>(
					ConstantUtil.failed, 
					ConstantUtil.ResponseError.SYS_ERROR.getCode(), ConstantUtil.ResponseError.SYS_ERROR.toString());
			//针对多条数据操作需要手动开启事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
    	info(OUT_PARAMETER_FORMAT, this.getClass().getSimpleName(), "getQrCode", result);
    	return result;
	}


	
	
	

/**
 * 
  * @方法名: notifyResult
  * @描述: TODO .
  * @程序猿: sundefa .
  * @日期: 2017年10月30日 下午3:33:13
  * @返回值: BaseExecuteResult<Object>  
  * @版本号: V2.0 .
  * @throws
 */
	public BaseExecuteResult<Object> notifyResult(String token, String json) {
		// TODO Auto-generated method stub
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "notifyResult", json);
		BaseExecuteResult<Object> resultMsg = lazyAccessTokenService.iSVisbodyTokenMessage(token);
		if (null != resultMsg)
			return resultMsg;
		Visbody visbody = JSONUtil.toBean(json, Visbody.class);
		BaseExecuteResult<Object> result = null;
		try {
			if(visbody.getStatus() == 1) {
				//合成成功开始调数据
				String responseToken = HttpClientUtils.getToken();
				System.out.println(responseToken);
				String modelsInfoUrl=modelsUrl+"?token="+responseToken+"&scanid="+visbody.getScanId();
				//获取model.obj
				Map<String, Object> executeGetModels = HttpClientUtils.executeGet(modelsInfoUrl);
				System.out.println(executeGetModels);
				String url = executeGetModels.get("model_url").toString();
				String executeGetForModel = HttpClientUtils.executeGetForModel(url);
				String uploadFileToQiNiu = qiNiuFileService.uploadFileToQiNiu(executeGetForModel);
				System.out.println(uploadFileToQiNiu);
				//获取身体数据
				String bodysInfoUrl=bodysUrl+"?token="+responseToken+"&scanid="+visbody.getScanId();
				Map<String, Object> executeGetBodys = HttpClientUtils.executeGet(bodysInfoUrl);
				System.out.println(executeGetBodys);
				//获取维度数据
				String datasInfoUrl=datasUrl+"?token="+responseToken+"&scanid="+visbody.getScanId();
				Map<String, Object> executeGetDatas = HttpClientUtils.executeGet(datasInfoUrl);
				//拼装map
				for(Entry<String, Object> entry : executeGetBodys.entrySet()) {
					String key = entry.getKey();
					if(!executeGetDatas.containsKey(key)) {
						executeGetDatas.put(key, entry.getValue());
					}
				}
				executeGetDatas.put("modelObj", uploadFileToQiNiu);
				int updateVisbodyInfo = lazyVisbodyMapper.updateVisbodyInfo(executeGetDatas);
				if(updateVisbodyInfo > 0) {
					result = new BaseExecuteResult<Object>(ConstantUtil.success, "成功");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			info(ERROR_FORMAT, this.getClass().getSimpleName(), "notifyResult", e.getMessage());
			result = new BaseExecuteResult<Object>(ConstantUtil.failed, ConstantUtil.ResponseError.SYS_ERROR.getCode(),
					ConstantUtil.ResponseError.SYS_ERROR.toString());
			//针对多条数据操作需要手动开启事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		info(OUT_PARAMETER_FORMAT, this.getClass().getSimpleName(), "notifyResult", result);
		return result;
	}


/**
 * 
  * @方法名: scan
  * @描述: app 扫码返回h5 url .
  * @程序猿: sundefa .
  * @日期: 2017年11月1日 上午10:13:57
  * @返回值: BaseExecuteResult<Object>  
  * @版本号: V2.0 .
  * @throws
 */
	public BaseExecuteResult<Object> scan(String token,String json) {
			info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "scan", json);
			BaseExecuteResult<Object> resultMsg = lazyAccessTokenService.iSTokenMessage(token);
			if (null != resultMsg)
				return resultMsg;
			BaseExecuteResult<Object> result = null;
			try {
				Visbody visbody = JSONUtil.toBean(json, Visbody.class);
				ValidationResult validateResult = ValidationUtils.validateProperty(visbody, "uid,scanId,deviceId");
				if (validateResult.isHasErrors()) {
					return new BaseExecuteResult<Object>(ConstantUtil.vfailed, validateResult);
				}
				Map<String, Object> visbodyMap = JSONUtil.toMap(json);
				visbodyMap.put("uid", visbody.getUid());
				Map<String, Object> sysUserMap = lazyVisbodyMapper.findSysUsersByParameter(visbodyMap);
				visbodyMap.put("ScanId", visbody.getScanId());
				int updateVisbodyInfo = lazyVisbodyMapper.updateVisbodyInfo(visbodyMap);
				if(sysUserMap != null && updateVisbodyInfo > 0) {
					String responseToken = HttpClientUtils.getToken();
					String sex = sysUserMap.get("sex").toString();
					String age = sysUserMap.get("age").toString();
					String height = sysUserMap.get("height").toString();
					String mobile = sysUserMap.get("mobile").toString();
					Map<String,String> map = Maps.newHashMap();
					map.put("deviceid", visbody.getDeviceId());
					map.put("scanid", visbody.getScanId());
					map.put("token", responseToken);
					map.put("sex", sex);
					map.put("age", age);
					map.put("height", height);
					map.put("mobile", mobile);
					Map<String, Object> executePost = HttpClientUtils.executePost("https://api.visbodyfit.com:30000/v1/dataBind", map);
					Map<Object, Object> resultMap = Maps.newHashMap();
					String url=recorde_url+"?scanid="+visbody.getScanId()+"&uid="+visbody.getUid();
					resultMap.put("recorde_url", url);//设置全局url
					if(executePost.get("status").toString().equals("0.0")) {
						result=new BaseExecuteResult<Object>(ConstantUtil.success, resultMap);
					}else if(executePost.get("status").toString().equals("-1.0")){
						result=new BaseExecuteResult<Object>(ConstantUtil.success, resultMap);
					}
				}else {
					result = new BaseExecuteResult<Object>(
							ConstantUtil.failed, 
							ConstantUtil.ResponseError.NOTFOUNDUSER.getCode(), ConstantUtil.ResponseError.NOTFOUNDUSER.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				info(ERROR_FORMAT, this.getClass().getSimpleName(), "scan", e.getMessage());
				result = new BaseExecuteResult<Object>(
						ConstantUtil.failed, 
						ConstantUtil.ResponseError.SYS_ERROR.getCode(), ConstantUtil.ResponseError.SYS_ERROR.toString());
				//针对多条数据操作需要手动开启事务
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			}
	    	info(OUT_PARAMETER_FORMAT, this.getClass().getSimpleName(), "scan", result);
	    	return result;
		}


/**
 * 
  * @方法名: recordList
  * @描述: h5访问页面数据 .
  * @程序猿: sundefa .
  * @日期: 2017年11月1日 上午11:39:39
  * @返回值: BaseExecuteResult<Object>  
  * @版本号: V2.0 .
  * @throws
 */
	public BaseExecuteResult<Object> recordList(String token,String json) {
			info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "recordList", json);
//			BaseExecuteResult<Object> resultMsg = lazyAccessTokenService.iSTokenMessage(token);
//			if (null != resultMsg)
//				return resultMsg;
			BaseExecuteResult<Object> result = null;
			try {
				Visbody visbody = JSONUtil.toBean(json, Visbody.class);
				//参数校验
				ValidationResult validateResult = ValidationUtils.validateProperty(visbody, "uid");
				if (validateResult.isHasErrors()) {
					return new BaseExecuteResult<Object>(ConstantUtil.vfailed, validateResult);
				}
				Map<String, Object> parameterMap = JSONUtil.toMap(json);
				List<Map<String,Object>> visBodyList = lazyVisbodyMapper.queryVisBodyList(parameterMap);
				result=new BaseExecuteResult<Object>(ConstantUtil.success,visBodyList);
			} catch (Exception e) {
				e.printStackTrace();
				info(ERROR_FORMAT, this.getClass().getSimpleName(), "recordList", e.getMessage());
				result = new BaseExecuteResult<Object>(
						ConstantUtil.failed, 
						ConstantUtil.ResponseError.SYS_ERROR.getCode(), ConstantUtil.ResponseError.SYS_ERROR.toString());
				//针对多条数据操作需要手动开启事务
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			}
	    	info(OUT_PARAMETER_FORMAT, this.getClass().getSimpleName(), "recordList", result);
	    	return result;
		}

/**
 * 
  * @方法名: progress
  * @描述: H5进程 .
  * @程序猿: sundefa .
  * @日期: 2017年11月1日 上午11:45:56
  * @返回值: BaseExecuteResult<Object>  
  * @版本号: V2.0 .
  * @throws
 */
	public BaseExecuteResult<Object> progress(String token,String json) {
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "progress", json);
//		BaseExecuteResult<Object> resultMsg = lazyAccessTokenService.iSTokenMessage(token);
//		if (null != resultMsg)
//			return resultMsg;
		BaseExecuteResult<Object> result = null;
		try {
			Visbody visbody = JSONUtil.toBean(json, Visbody.class);
			String responseToken = HttpClientUtils.getToken();
			System.out.println(responseToken);
			String nowProgressUrl=progressUrl+"?token="+responseToken+"&scanid="+visbody.getScanId();
			Map<String, Object> executeGet = HttpClientUtils.executeGet(nowProgressUrl);
			if(executeGet != null) {
//				executeGet.put("progress", "");
				result = new BaseExecuteResult<Object>(ConstantUtil.success,executeGet);
			}else {
				result = new BaseExecuteResult<Object>(ConstantUtil.failed, 
						ConstantUtil.ResponseError.NOTFOUNDUSER.getCode(), ConstantUtil.ResponseError.NOTFOUNDUSER.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			info(ERROR_FORMAT, this.getClass().getSimpleName(), "progress", e.getMessage());
			result = new BaseExecuteResult<Object>(
					ConstantUtil.failed, 
					ConstantUtil.ResponseError.SYS_ERROR.getCode(), ConstantUtil.ResponseError.SYS_ERROR.toString());
			//针对多条数据操作需要手动开启事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		info(OUT_PARAMETER_FORMAT, this.getClass().getSimpleName(), "progress", result);
		return result;
	}
	
	
	/**
	 * 
	  * @方法名: recordDetails
	  * @描述: h5获取体测详情  .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 上午11:57:10
	  * @返回值: BaseExecuteResult<Object>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public BaseExecuteResult<Object> recordDetails(String token,String json) {
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "recordDetails", json);
//		BaseExecuteResult<Object> resultMsg = lazyAccessTokenService.iSTokenMessage(token);
//		if (null != resultMsg)
//			return resultMsg;
		BaseExecuteResult<Object> result = null;
		try {
			Visbody visbody = JSONUtil.toBean(json, Visbody.class);
			//参数校验
			ValidationResult validateResult = ValidationUtils.validateProperty(visbody, "scanid");
			if (validateResult.isHasErrors()) {
				return new BaseExecuteResult<Object>(ConstantUtil.vfailed, validateResult);
			}
			String scanId = visbody.getScanId();
			System.out.println(visbody.getScanId());
			if(scanId !=null && !scanId.trim().equals("") ) {
				Map<String, Object> parameterMap = JSONUtil.toMap(json);
				Map<String, Object> visbodyMap=lazyVisbodyMapper.queryVisBody(parameterMap);
				Map<String, Object> weiduVisbodyMap=lazyVisbodyMapper.queryWeiduVisBody(parameterMap);
				Map<Object,Object> resultMap = Maps.newHashMap();
				if(visbodyMap.size() > 0 && weiduVisbodyMap.size() > 0) {//如果size大于0
					resultMap.put("VisbodyData", visbodyMap);
					resultMap.put("modelUrl", weiduVisbodyMap.get("modelObj"));
					resultMap.put("createTime", weiduVisbodyMap.get("createTime"));
					weiduVisbodyMap.remove("modelObj");
					weiduVisbodyMap.remove("createTime");
					resultMap.put("visbodyWeiduData", weiduVisbodyMap);
				}
				result = new BaseExecuteResult<Object>(ConstantUtil.success,resultMap);
			}else {
				result = new BaseExecuteResult<Object>(ConstantUtil.failed,
						ConstantUtil.ResponseError.SCANIDISNOTNOLL.getCode(),
						ConstantUtil.ResponseError.SCANIDISNOTNOLL.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			info(ERROR_FORMAT, this.getClass().getSimpleName(), "recordDetails", e.getMessage());
			result = new BaseExecuteResult<Object>(
					ConstantUtil.failed, 
					ConstantUtil.ResponseError.SYS_ERROR.getCode(), ConstantUtil.ResponseError.SYS_ERROR.toString());
			//针对多条数据操作需要手动开启事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
    	info(OUT_PARAMETER_FORMAT, this.getClass().getSimpleName(), "recordDetails", result);
    	return result;
	}


	/**
	 * 
	  * @方法名: recordEntry
	  * @描述: aapp记录入口  .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 下午3:04:21
	  * @返回值: BaseExecuteResult<Object>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public BaseExecuteResult<Object> recordEntry(String token,String json) {
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "recordEntry", json);
		BaseExecuteResult<Object> resultMsg = lazyAccessTokenService.iSTokenMessage(token);
		if (null != resultMsg)
			return resultMsg;
		BaseExecuteResult<Object> result = null;
		try {
			Visbody visbody = JSONUtil.toBean(json, Visbody.class);
			Map<Object, Object> resultMap = Maps.newHashMap();
			String url=entry_url+"?uid="+visbody.getUid();
			resultMap.put("entry_url", url);//设置全局url
			result=new BaseExecuteResult<Object>(ConstantUtil.success, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			info(ERROR_FORMAT, this.getClass().getSimpleName(), "recordEntry", e.getMessage());
			result = new BaseExecuteResult<Object>(
					ConstantUtil.failed, 
					ConstantUtil.ResponseError.SYS_ERROR.getCode(), ConstantUtil.ResponseError.SYS_ERROR.toString());
			//针对多条数据操作需要手动开启事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
    	info(OUT_PARAMETER_FORMAT, this.getClass().getSimpleName(), "recordEntry", result);
    	return result;
	}




	/**
	 * 
	  * @方法名: getToken
	  * @描述:维塑后台获取token.
	  * @程序猿: sundefa .
	  * @日期: 2017年11月2日 下午2:33:43
	  * @返回值: BaseExecuteResult<Object>  
	  * @版本号: V2.0 .
	  * @throws
	 */

	public BaseExecuteResult<Object> getToken(String json) {
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "getToken", json);
		BaseExecuteResult<Object> result = null;
		try {
			Cipher cipher = JSONUtil.toBean(json, Cipher.class);
			ValidationResult validateResult = ValidationUtils.validateProperty(cipher, "code,secret");
			if (validateResult.isHasErrors()) {
				return new BaseExecuteResult<Object>(ConstantUtil.vfailed, validateResult);
			}
			Map<String,Object> resultMap = Maps.newHashMap();
			if(cipher.getCode().equals("2OvBvNmr7zMstA") && cipher.getSecret().equals("SdvC4aEHeeKijMq2bciMQ")) {
				AccessToken accessToken = (AccessToken) lazyAccessTokenService.getAccessToken(json).getResult();
				resultMap.put("token", accessToken);
			}
			result=new BaseExecuteResult<Object>(ConstantUtil.success, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			info(ERROR_FORMAT, this.getClass().getSimpleName(), "getToken", e.getMessage());
			result = new BaseExecuteResult<Object>(
					ConstantUtil.failed, 
					ConstantUtil.ResponseError.SYS_ERROR.getCode(), ConstantUtil.ResponseError.SYS_ERROR.toString());
			//针对多条数据操作需要手动开启事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
    	info(OUT_PARAMETER_FORMAT, this.getClass().getSimpleName(), "getToken", result);
    	return result;
	}
	
	
	
}
