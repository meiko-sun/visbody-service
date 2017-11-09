package cn.lazy.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import cn.lazy.model.SysUsers;
import cn.lazy.model.VisbodyForAll;

/**用户接口类
 * @类名: LazyUserWardrobeMapper .
 * @描述: TODO .
 * @程序猿: chenjingwu .
 * @日期: 2017年3月3日 下午1:54:32 .
 * @版本号: V1.0 .
 */
@Transactional(rollbackFor=NumberFormatException.class)
public interface LazyVisbodyMapper {
	
    //保存scanId
	public int insertNewScanId(Map<String, Object> qrcMap);

	//查询维塑
	public Map<String, Object> queryVisbodyInfo(Map<String, Object> parameterMap);

	//查询用户维塑体测列表信息
	public Map<String, Object> queryVisBody(Map<String, Object> parameterMap);

	//更新维塑信息
	public int updateVisbodyInfo(Map<String, Object> executeGetBodys);

	//查询用户信息
	public Map<String, Object> findSysUsersByParameter(Map<String, Object> visbodyMap);

	//查询用户维塑体测维度数据map
	public Map<String, Object> queryWeiduVisBody(Map<String, Object> parameterMap);

	//查询历史
	public List<Map<String, Object>> queryVisBodyList(Map<String, Object> parameterMap);

	//查找用户身体数据
	public VisbodyForAll findNewestData(Map<Object, Object> userMap);

	//身体数据返回scanid
	public VisbodyForAll findbodyDataForScanId(Map<Object, Object> scanId);

	//返回scanid个数
	public int findCountScanId(Map<String, Object> qrcMap);

}
