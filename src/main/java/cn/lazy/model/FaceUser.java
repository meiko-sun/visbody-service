package cn.lazy.model;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

/**
 * 人脸用户认证类
  * @类名: FaceUser
  * @描述: TODO .
  * @程序猿: chenjingwu .
  * @日期: 2017年10月23日 下午8:01:12
  * @版本号: V2.0 .
  *
 */
@Data
public class FaceUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8510243381921221315L;

	private Long id;

	@NotNull(message = "会员UID不能为空！")
	private Long uid;

	// 人脸识别服务器对应用户唯一ID
	@NotNull(message = "人脸识别唯一码不能为空！")
	private int faceKey;

	//用户名称
	@NotEmpty(message = "用户昵称不能为空！")
	@NotNull(message = "用户昵称不能为空！")
	private String name;
	
	//会员头像
	private String avatar;

	//会员头像ids
	private List<Integer> photoIds;
	
	//会员头像
	private String photo;
	
	//性别{0: 未知, 1: 男, 2: 女} 
	private Integer gender;
	
	//用户类型 {0:会员, 1:员工, 2: 访客} 
	private Integer subjectType;
	
	private Integer subjectId;
	

}