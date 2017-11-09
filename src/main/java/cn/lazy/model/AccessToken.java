package cn.lazy.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;
import lombok.Getter;

/**
 * @类名: AccessToken .
 * @描述: token返回结果类
 * @程序猿: chenjingwu .
 * @日期: 2017年3月12日 上午11:24:34 .
 * @版本号: V1.0 .
 */
@Data
public class  AccessToken implements Serializable {

	private static final long serialVersionUID = -7898194272883238670L;
	@NotNull(message = "不能为空！")
	@Getter private String id;
	// token签名文件
	@NotNull(message = "不能为空！")
	@NotEmpty(message = "不能为空！")
	@Getter private String accessToken;
	// 类型
	@NotNull(message = "不能为空！")
	@NotEmpty(message = "不能为空！")
	@Getter private String tokenType;
	// 失效性
	@NotNull(message = "不能为空！")
	@NotEmpty(message = "不能为空！")
	@Getter private long expiresIn;

	
}
