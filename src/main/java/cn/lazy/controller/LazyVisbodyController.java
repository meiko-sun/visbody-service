package cn.lazy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.ApiOperation;

import cn.lazy.base.BaseExecuteResult;
import cn.lazy.service.LazyVisbodyService;

/**
 * 
  * @类名: LazyVisbodyController
  * @描述: 维塑设备控制层 .
  * @程序猿: sundefa .
  * @日期: 2017年11月6日 上午11:06:31
  * @版本号: V2.0 .
  *
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/lazyVisbody")
public class LazyVisbodyController {

	/*
	 * http://localhost:8080/swagger/index.html
	 */
	@Autowired
	LazyVisbodyService lazyVisbodyService;

	/**
	 * 
	  * @方法名: getQrCode
	  * @描述: 维塑获取二维码接口 .
	  * @程序猿: sundefa .
	  * @日期: 2017年10月28日 下午11:11:53
	  * @返回值: BaseExecuteResult<?>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	@ApiOperation(value = "获取二维码", notes = "入参：{\"deviceId\": \"设备ID\", \"scanId\": \"扫描id\",\"token\": \"接口凭证\"}")
	@RequestMapping(value = "/getQrCode", method = RequestMethod.POST)
	public BaseExecuteResult<?> getQrCode(@RequestHeader("Authorization") String token,@RequestParam("json") String json) {
		BaseExecuteResult<?> result = lazyVisbodyService.getQrCode(token,json);
		return result;
	}
	
	/**
	 * 
	  * @方法名: notifyResult
	  * @描述: 维塑合成的结果通知 .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 上午9:53:11
	  * @返回值: BaseExecuteResult<?>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	@ApiOperation(value = "维塑合成的结果通知", notes = "{}")
	@RequestMapping(value = "/notifyResult", method = RequestMethod.POST)
	public BaseExecuteResult<?> notifyResult(@RequestHeader("Authorization") String token, @RequestParam("json") String json) {
		BaseExecuteResult<?> result = lazyVisbodyService.notifyResult(token, json);
		return result;
	}
	
	
	/**
	 * 
	  * @方法名: scan
	  * @描述: app扫描二维码接口 .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 上午9:54:07
	  * @返回值: BaseExecuteResult<?>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	@ApiOperation(value = "app扫描二维码接口", notes = "{uid:1506545446546,scanid:,deviceid}")
	@RequestMapping(value = "/scan", method = RequestMethod.POST)
	public BaseExecuteResult<?> scan(@RequestHeader("Authorization") String token, @RequestParam("json") String json) {
		BaseExecuteResult<?> result = lazyVisbodyService.scan(token, json);
		return result;
	}
	
	/**
	 * 
	  * @方法名: recordList
	  * @描述: 体测记录 .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 上午10:15:27
	  * @返回值: BaseExecuteResult<?>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	@ApiOperation(value = "uid 体测记录", notes = "{uid:1506545446546,scanid:,deviceid}")
	@RequestMapping(value = "/recordList", method = RequestMethod.POST)
	public BaseExecuteResult<?> recordList(@RequestHeader("Authorization") String token, @RequestParam("json") String json) {
		BaseExecuteResult<?> result = lazyVisbodyService.recordList(token, json);
		return result;
	}
	
	/**
	 * 
	  * @方法名: progress
	  * @描述: h5获取合成进度 .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 上午11:45:08
	  * @返回值: BaseExecuteResult<?>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	@ApiOperation(value = "scanid进程", notes = "{uid，scanid}")
	@RequestMapping(value = "/progress", method = RequestMethod.POST)
	public BaseExecuteResult<?> progress(@RequestHeader("Authorization") String token, @RequestParam("json") String json) {
		BaseExecuteResult<?> result = lazyVisbodyService.progress(token, json);
		return result;
	}
	
	/**
	 * 
	  * @方法名: recordDetails
	  * @描述: h5获取体测详情 .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 上午11:56:39
	  * @返回值: BaseExecuteResult<?>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	@ApiOperation(value = "h5获取体测详情 ", notes = "{uid:1506545446546,scanid:,deviceid}")
	@RequestMapping(value = "/recordDetails", method = RequestMethod.POST)
	public BaseExecuteResult<?> recordDetails(@RequestHeader("Authorization") String token, @RequestParam("json") String json) {
		BaseExecuteResult<?> result = lazyVisbodyService.recordDetails(token, json);
		return result;
	}
	
	
	/**
	 * 
	  * @方法名: recordEntry
	  * @描述: app记录入口 .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 下午3:02:47
	  * @返回值: BaseExecuteResult<?>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	@ApiOperation(value = "app记录入口 ", notes = "{uid:1506545446546}")
	@RequestMapping(value = "/recordEntry", method = RequestMethod.POST)
	public BaseExecuteResult<?> recordEntry(@RequestHeader("Authorization") String token, @RequestParam("json") String json) {
		BaseExecuteResult<?> result = lazyVisbodyService.recordEntry(token, json);
		return result;
	}
	
	/**
	 * 
	  * @方法名: getToken
	  * @描述: 维塑后台获取token .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月2日 下午2:29:25
	  * @返回值: BaseExecuteResult<?>  
	  * @版本号: V2.0 .
	  * @throws
	 */
	@ApiOperation(value = "app记录入口 ", notes = "{uid:1506545446546}")
	@RequestMapping(value = "/getToken", method = RequestMethod.POST)
	public BaseExecuteResult<?> getToken( @RequestParam("json") String json) {
		BaseExecuteResult<?> result = lazyVisbodyService.getToken(json);
		return result;
	}
	
}
