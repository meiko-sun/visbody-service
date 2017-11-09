package cn.lazy.service;

import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.StringUtil;

import cn.lazy.base.BaseExecuteResult;
import cn.lazy.base.BaseService;
import cn.lazy.base.RedisService;
import cn.lazy.base.ValidationResult;
import cn.lazy.config.AudienceConfig;
import cn.lazy.model.AccessToken;
import cn.lazy.model.Cipher;
import cn.lazy.model.SysUsers;
import cn.lazy.model.Visbody;
import cn.lazy.utils.ConstantUtil;
import cn.lazy.utils.JSONUtil;
import cn.lazy.utils.JwtHelper;
import cn.lazy.utils.SnowFlakeUtils;
import cn.lazy.utils.ValidationUtils;

/**获取登陆唯一性验证
 * @类名: GetAccessTokenService .
 * @描述: TODO .
 * @程序猿: chenjingwu .
 * @日期: 2017年3月4日 下午7:54:25 .
 * @版本号: V1.0 .
 */
@Service
public class LazyAccessTokenService  extends BaseService {

	@Autowired
	private RedisService redisService;
	
	@Autowired  
    private AudienceConfig audienceEntity; 
	
    /** 生成自定义token签名文件
     * @方法名: getAccessToken .
     * @描述: TODO .
     * @程序猿: chenjingwu .
     * @返回值: BaseExecuteResult<Object> .
     * @日期: 2017年3月14日 下午5:41:07 .
     * @throws
     */
	@Transactional
	public BaseExecuteResult<Object> getAccessToken(String json){
		info(IN_PARAMETER_FORMAT, this.getClass().getSimpleName(), "getAccessToken", json);
		BaseExecuteResult<Object> result = null;
    	try {
    		Cipher loginPara = JSONUtil.toBean(json, Cipher.class);
    		String userId = loginPara.getCode();
    		//拼装accessToken  
    		
            //返回accessToken  
            AccessToken accessTokenEntity = new AccessToken();
            accessTokenEntity.setId(String.valueOf(userId));
            accessTokenEntity.setAccessToken(UUID.randomUUID().toString().replace("-", ""));  
            accessTokenEntity.setExpiresIn(audienceEntity.getExpiresSecond());  
//            accessTokenEntity.setExpiresIn(-1);  
            accessTokenEntity.setTokenType("bearer");
            //设置第三方登陆token失效时间
//            if (loginPara.getMobile().indexOf("0") != -1) {
            	 String tokenSign = "visbodyToken_"+userId;
                 redisService.putAccessToken(tokenSign, accessTokenEntity);
//            }
    		result = new BaseExecuteResult<Object>(ConstantUtil.success, accessTokenEntity);
		} catch (Exception e) {
			e.printStackTrace();
			info(ERROR_FORMAT, this.getClass().getSimpleName(), "getAccessToken", e.getMessage());
			result = new BaseExecuteResult<Object>(
					ConstantUtil.failed, 
					ConstantUtil.ResponseError.INVALID_TOKEN.getCode(), ConstantUtil.ResponseError.INVALID_TOKEN.toString());
		}
    	info(OUT_PARAMETER_FORMAT, this.getClass().getSimpleName(), "getAccessToken", result);
    	return result;
	}
	
	public int iScheckVisbodyToken(String accessToken) {
		int status = -1;
		if (null == accessToken || StringUtil.isEmpty(accessToken)) {
			status = 1;
		} else {
			String tokenKey = "visbodyToken_2OvBvNmr7zMstA";
			boolean expire = redisService.expire(tokenKey);
			// token验证过期，失效
			if (expire == true) {
				status=0;
			}else {
				status=2;
			}
		}
		return status;
	}
	
	
	/**
	 * 
	  * @方法名: iScheckToken
	  * @描述: 判断app的token .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 上午9:58:24
	  * @返回值: int  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public int iScheckToken(String accessToken) {
		int status = -1;
		if (null == accessToken || StringUtil.isEmpty(accessToken)) {
			status = 1;
		} else {
			AccessToken token = new AccessToken();
			AccessToken redisToken = new AccessToken();
			token = JSONUtil.toBean(accessToken, AccessToken.class);
			String tokenKey = "token_" + token.getId();
			redisToken = redisService.findAccessToken(tokenKey, token);
			// token验证过期，失效
			if (null != token &&  null != redisToken && token.getAccessToken().length() > 100
					&& token.getAccessToken().equals(redisToken.getAccessToken())) {
				if (null != JwtHelper.parseJWT(token.getAccessToken(), audienceEntity.getBase64Secret())) {
					status = 0;
				} else {
					status = 2;
				}
			}

		}
		return status;
	}
	/**
	 * @方法名: iSTokenMessage .
	 * @描述: TODO .验证token值是否正常
	 * @程序猿: chenjingwu .
	 * @返回值: BaseExecuteResult<Object> .
	 * @日期: 2017年4月13日 下午3:44:17 .
	 * @throws
	 */
	public BaseExecuteResult<Object>  iSTokenMessage(String accessToken) {
		BaseExecuteResult<Object> resultMsg = null;
		if (iScheckToken(accessToken) == 1) {
			return new BaseExecuteResult<Object>(ConstantUtil.failed,
					ConstantUtil.ResponseError.INVALID_TOKEN.getCode(),
					ConstantUtil.ResponseError.INVALID_TOKEN.toString());
//			return new BaseExecuteResult<Object>(ConstantUtil.vfailed, resultMsg);
		}
		if (iScheckToken(accessToken) == 2) {
			return new BaseExecuteResult<Object>(ConstantUtil.failed,
					ConstantUtil.ResponseError.INVALID_CAPTCHA.getCode(),
					ConstantUtil.ResponseError.INVALID_CAPTCHA.toString());
//			return new BaseExecuteResult<Object>(ConstantUtil.vfailed, resultMsg);
		}
		if (iScheckToken(accessToken) == -1) {
			return new BaseExecuteResult<Object>(ConstantUtil.failed,
					ConstantUtil.ResponseError.INVALID_TOKEN.getCode(),
					ConstantUtil.ResponseError.INVALID_TOKEN.toString());
//			return new BaseExecuteResult<Object>(ConstantUtil.vfailed, resultMsg);
		}
		return resultMsg;
	}
	
	/**
	 * 
	  * @方法名: iSVisbodyTokenMessage
	  * @描述: 是否维塑token .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 上午10:02:55
	  * @返回值: BaseExecuteResult<Object>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	public JSON  iSVisbodyTokenMessage(String accessToken) {
		JSONObject jsonObject = new JSONObject();
		if (iScheckVisbodyToken(accessToken) == 1) {
			jsonObject.put("code", 40003);
			jsonObject.put("error", "token错误");
			return jsonObject;
//			return new BaseExecuteResult<Object>(ConstantUtil.vfailed, resultMsg);
		}
		if (iScheckVisbodyToken(accessToken) == 2) {
			jsonObject.put("code", 40004);
			jsonObject.put("error", "token失效");
			return jsonObject;

//			return new BaseExecuteResult<Object>(ConstantUtil.vfailed, resultMsg);
		}
		if (iScheckVisbodyToken(accessToken) == -1) {
			jsonObject.put("code", 40004);
			jsonObject.put("error", "token失效");
			return jsonObject;
//			return new BaseExecuteResult<Object>(ConstantUtil.vfailed, resultMsg);
		}
		return jsonObject;
	}
}
