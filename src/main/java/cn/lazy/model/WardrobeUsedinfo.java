package cn.lazy.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

/**
 * 智能衣柜实体类
  * @类名: WardrobeUsedinfo
  * @描述: TODO .
  * @程序猿: chenjingwu .
  * @日期: 2017年10月25日 下午6:35:43
  * @版本号: V2.0 .
  *
 */
@Data
public class WardrobeUsedinfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8510243381921221315L;

	private int id;

	@NotNull(message = "会员UID不能为空！")
	private Long uid;

	//衣柜自增ID
	@NotNull(message = "衣柜自增ID不能为空！")
	private int wardrobeId;
	
	//会员头像
	@NotEmpty(message = "衣柜状态不能为空！")
	@NotNull(message = "衣柜状态不能为空！")
	private String wardStatus;

	//使用情况
	private int isUserd; //1.使用 2.续存 3.清空
	

}