package cn.lazy.service;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
import org.hamcrest.text.IsEmptyString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonNull;

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
import cn.lazy.model.VisbodyForAll;
import cn.lazy.model.VisbodyWeiduData;
import cn.lazy.utils.ConstantUtil;
import cn.lazy.utils.DateUtils;
import cn.lazy.utils.DoubleUtils;
import cn.lazy.utils.HttpClientUtils;
import cn.lazy.utils.Img2Base64Util;
import cn.lazy.utils.JSONUtil;
import cn.lazy.utils.QRCodeUtils;
import cn.lazy.utils.ValidationUtils;

/**
 * 
  * @类名: LazyVisbodyService
  * @描述: 维塑service .
  * @程序猿: sundefa .
  * @日期: 2017年11月6日 上午11:58:54
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
	
	@Value("${visbody.dataBind}")
    private String dataBind;
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
	public JSON getQrCode(String token,String json) {
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "getQrCode", json);
		JSON resultMsg = lazyAccessTokenService.iSVisbodyTokenMessage(token);
		if (resultMsg.toJSONString().trim().length() > 2) {
			return resultMsg;
		}
		JSONObject resultJson = new JSONObject();
		try {
			Visbody visbody = JSONUtil.toBean(json, Visbody.class);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("deviceid", visbody.getDeviceId());
			jsonObject.put("scanid", visbody.getScanId());
			jsonObject.put("company", "lazyhealth");
			String data = QRCodeUtils.encode(jsonObject.toString(), "", "", true);
			String uploadFileToQiNiu = qiNiuFileService.uploadFileToQiNiu(data);
			Map<String,Object> QrcMap = Maps.newHashMap();
			QrcMap.put("scanId", visbody.getScanId());
			QrcMap.put("qrcurl", uploadFileToQiNiu);
			if(visbody.getScanId() != null && !visbody.getScanId().trim().equals("")) {
				int findCountScanId = lazyVisbodyMapper.findCountScanId(QrcMap);
				if(findCountScanId == 0) {
					int insertNewScanId = lazyVisbodyMapper.insertNewScanId(QrcMap);
					if(insertNewScanId > 0) {
						resultJson.put("code", 0);
						resultJson.put("data", uploadFileToQiNiu);
						return resultJson;
					}
				}else {
					resultJson.put("code", 40005);
					resultJson.put("error", "参数错误");
					return resultJson;
				}
			}else {
				resultJson.put("code", 40001);
				resultJson.put("error", "参数错误");
				return resultJson;
			}
		} catch (Exception e) {
			e.printStackTrace();
			info(ERROR_FORMAT, this.getClass().getSimpleName(), "getQrCode", e.getMessage());
			//针对多条数据操作需要手动开启事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			resultJson.put("code", 40008);
			resultJson.put("error", "参数错误");
			return resultJson;
		}
    	return resultJson;
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
	public JSON notifyResult(String json) {
		// TODO Auto-generated method stub
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "notifyResult", json);
		Visbody visbody = JSONUtil.toBean(json, Visbody.class);
		JSON resultMsg = lazyAccessTokenService.iSVisbodyTokenMessage(visbody.getToken());
		if (resultMsg.toJSONString().trim().length() > 2) {
			return resultMsg;
		}
		JSONObject resultJson = new JSONObject();
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
					resultJson.put("code", 0);
					return resultJson;
				}
			}else {
				resultJson.put("code", 40002);
				resultJson.put("error","参数错误");
				return resultJson;
			}
		} catch (Exception e) {
			e.printStackTrace();
			info(ERROR_FORMAT, this.getClass().getSimpleName(), "notifyResult", e.getMessage());
			
			//针对多条数据操作需要手动开启事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			resultJson.put("code", 40002);
			resultJson.put("error","参数错误");
			return resultJson;
		}
		return resultJson;
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
					Map<String, Object> executePost = HttpClientUtils.executePost(dataBind, map);
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
					resultMap.put("bodyData", visbodyMap);
					resultMap.put("modelUrl", weiduVisbodyMap.get("modelObj"));
					resultMap.put("createTime", weiduVisbodyMap.get("createTime"));
					weiduVisbodyMap.remove("modelObj");
					weiduVisbodyMap.remove("createTime");
					resultMap.put("weiduData", weiduVisbodyMap);
				}else {
					result = new BaseExecuteResult<Object>(ConstantUtil.failed,
							ConstantUtil.ResponseError.OBJECTNULL.getCode(),
							ConstantUtil.ResponseError.OBJECTNULL.toString());
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

	public JSON getToken(String json) {
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "getToken", json);
		JSONObject jsonObject = new JSONObject();
		try {
			Cipher cipher = JSONUtil.toBean(json, Cipher.class);
			ValidationResult validateResult = ValidationUtils.validateProperty(cipher, "code,secret");
			if (validateResult.isHasErrors()) {
				jsonObject.put("code", 40005);
				jsonObject.put("error", "入参错误");
				return jsonObject;
			}
			if(cipher.getCode().equals("2OvBvNmr7zMstA") && cipher.getSecret().equals("SdvC4aEHeeKijMq2bciMQ")) {
				AccessToken accessToken = (AccessToken) lazyAccessTokenService.getAccessToken(json).getResult();
				jsonObject.put("code", 0);
				jsonObject.put("token",accessToken.getAccessToken() );
				jsonObject.put("expires_in", 5400);
			}else {
				jsonObject.put("code", 40007);
				jsonObject.put("error", "参数错误");
			}
			return jsonObject;
		} catch (Exception e) {
			e.printStackTrace();
			info(ERROR_FORMAT, this.getClass().getSimpleName(), "getToken", e.getMessage());
			jsonObject.put("code", 40005);
			jsonObject.put("error", "系统错误");
			//针对多条数据操作需要手动开启事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return jsonObject;
		}
	}




	/**
	 * 
	  * @方法名: compareData
	  * @描述: 比较数据 .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月6日 下午2:28:31
	  * @返回值: BaseExecuteResult<Object>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public BaseExecuteResult<Object> compareData(String token,String json) {
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "compareData", json);
		BaseExecuteResult<Object> result = null;
		try {
			Map<Object, Object> resultMap = Maps.newHashMap();
			Visbody visbody = JSONUtil.toBean(json, Visbody.class);
			//参数校验
			ValidationResult validateResult = ValidationUtils.validateProperty(visbody, "uid,scanId");
			if (validateResult.isHasErrors()) {
				return new BaseExecuteResult<Object>(ConstantUtil.vfailed, validateResult);
			}
			if(visbody.getScanId() != null && !visbody.getScanId().trim().equals("")) {
				Map<Object, Object> userMap = Maps.newHashMap();
				userMap.put("uid", visbody.getUid());
				VisbodyForAll newestData = lazyVisbodyMapper.findNewestData(userMap);
				userMap.put("scanId", visbody.getScanId());
				VisbodyForAll findbodyDataForScanId = lazyVisbodyMapper.findbodyDataForScanId(userMap);
				if(newestData != null && findbodyDataForScanId != null) {
					VisbodyData visbodyData = new VisbodyData();
					VisbodyWeiduData visbodyWeiduData = new VisbodyWeiduData();
					visbodyData.setBmi(DoubleUtils.getdouble(newestData.getBmi(), findbodyDataForScanId.getBmi()));
					visbodyData.setBodyFat(DoubleUtils.getdouble(newestData.getBodyFat(),findbodyDataForScanId.getBodyFat()));
					visbodyData.setFluid(DoubleUtils.getdouble(newestData.getFluid() ,findbodyDataForScanId.getFluid()));
					visbodyData.setKcal(DoubleUtils.getdouble(newestData.getKcal(),findbodyDataForScanId.getKcal()));
					visbodyData.setPercentBodyFat(DoubleUtils.getdouble(newestData.getPercentBodyFat(), findbodyDataForScanId.getPercentBodyFat()));
					visbodyData.setWaistToHip(DoubleUtils.getdouble(newestData.getWaistToHip() ,findbodyDataForScanId.getWaistToHip()));
					visbodyData.setWeight(DoubleUtils.getdouble(newestData.getWeight(),findbodyDataForScanId.getWeight()));
					
					visbodyWeiduData.setBustGirth(DoubleUtils.getdouble(newestData.getBustGirth(), findbodyDataForScanId.getBustGirth()));
					visbodyWeiduData.setHipGirth(DoubleUtils.getdouble(newestData.getHipGirth(),findbodyDataForScanId.getHipGirth()));
					visbodyWeiduData.setHeight(DoubleUtils.getdouble(newestData.getHeight(), findbodyDataForScanId.getHeight()));
					visbodyWeiduData.setLeftCalfGirth(DoubleUtils.getdouble(newestData.getLeftCalfGirth(), findbodyDataForScanId.getLeftCalfGirth()));
					visbodyWeiduData.setLeftThighGirth(DoubleUtils.getdouble(newestData.getLeftThighGirth() ,findbodyDataForScanId.getLeftThighGirth()));
					visbodyWeiduData.setLeftUpperArmGirth(DoubleUtils.getdouble(newestData.getLeftUpperArmGirth() ,findbodyDataForScanId.getLeftUpperArmGirth()));
					visbodyWeiduData.setRightCalfGirth(DoubleUtils.getdouble(newestData.getRightCalfGirth() ,findbodyDataForScanId.getRightCalfGirth()));
					visbodyWeiduData.setRightThighGirth(DoubleUtils.getdouble(newestData.getRightThighGirth() ,findbodyDataForScanId.getRightThighGirth()));
					visbodyWeiduData.setRightUpperArmGirth(DoubleUtils.getdouble(newestData.getRightUpperArmGirth() ,findbodyDataForScanId.getRightUpperArmGirth()));
					visbodyWeiduData.setWaistGirth(DoubleUtils.getdouble(newestData.getWaistGirth() ,findbodyDataForScanId.getWaistGirth()));
					resultMap.put("createTime", DateUtils.SHORT_DATE_FORMAT.format(findbodyDataForScanId.getCreateTime()));
					resultMap.put("bodyData", visbodyData);
					resultMap.put("weiduData", visbodyWeiduData);
				}else {
					result = new BaseExecuteResult<Object>(ConstantUtil.failed,
							ConstantUtil.ResponseError.OBJECTNULL.getCode(),
							ConstantUtil.ResponseError.OBJECTNULL.toString());
				}
			}else {
				result = new BaseExecuteResult<Object>(ConstantUtil.failed,
						ConstantUtil.ResponseError.SCANIDISNOTNOLL.getCode(),
						ConstantUtil.ResponseError.SCANIDISNOTNOLL.toString());
			}
			result = new BaseExecuteResult<Object>(ConstantUtil.success,resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			info(ERROR_FORMAT, this.getClass().getSimpleName(), "compareData", e.getMessage());
			result = new BaseExecuteResult<Object>(
					ConstantUtil.failed, 
					ConstantUtil.ResponseError.SYS_ERROR.getCode(), ConstantUtil.ResponseError.SYS_ERROR.toString());
			//针对多条数据操作需要手动开启事务
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
    	info(OUT_PARAMETER_FORMAT, this.getClass().getSimpleName(), "compareData", result);
    	return result;
	}
	
}
