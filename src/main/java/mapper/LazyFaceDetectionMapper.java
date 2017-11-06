package cn.lazy.mapper;

import java.util.List;
import java.util.Map;

/**
 * @类名: LazyCoachReleaseMapper .
 * @描述: TODO .
 * @程序猿: chenjingwu .
 * @日期: 2017年4月28日 下午6:02:16 .
 * @版本号: V1.0 .
 */
public interface LazyFaceDetectionMapper {
	
	// 当天团操课列表
	public List<Map<String, Object>> queryCourseList();
	
	// 当天私教课列表
	public List<Map<String, Object>> queryCourseOrderPrivateList(Map<String, Object> parameter);
	
	//查询场馆列表
	public List<Map<String, Object>> queryClubList();
	
	//查询人脸对象信息
	public Map<String, Object> queryFaceUserInfo(Map<String, Object> parameter);
	
	
	//存入会员进出记录
	public int saveFaceInOut(Map<String, Object> parameter);
	
}
