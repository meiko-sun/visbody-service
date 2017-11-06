package cn.lazy.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * pojo类
 * 
 * @类名: User .
 * @描述: TODO .
 * @程序猿: chenjingwu .
 * @日期: 2017年3月3日 下午2:29:46 .
 * @版本号: V1.0 .
 */
@Data
public class SysUsers implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8510243381921221315L;

	@NotNull(message = "用户ID不能为空！")
	private Long uid;

	// 创建时间
	private Date createTime;

	//模糊查询字段
	private String memberName;
	// 用户昵称
	@NotEmpty(message = "用户昵称不能为空！")
	@NotNull(message = "用户昵称不能为空！")
	private String petName;

	// 注册入口：1 pc 2 android 3 ios 4 wechat
	@NotNull(message = "登陆方式不能为空！")
	private Integer toRegister;

	// 自定义图像
	@NotNull(message = "头像不能为空！")
	private String definedPhoto;

	// 真实姓名
	@NotEmpty(message = "真实姓名不能为空！")
	@NotNull(message = "真实姓名不能为空！")
	private String realityName;

	// 登陆密码
	@NotNull(message = "登陆密码不能为空！")
	private String password;

	// 上次退出时间
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date lastLogoutTime;

	// 邮箱
	@Pattern(regexp = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$", message = "email格式不正确")
	private String email;

	// 手机号码
	@NotNull(message = "手机号码不能为空！")
	@Pattern(regexp = "^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$", message = "手机号格式不正确")
	private String mobile;

	// 性别 1 男 2 女 3 未知
	@NotNull(message = "性别不能为空！")
	private Integer sex;

	private Integer age; // 通过身份证信息计算出年龄

	// 身高
	private String height;

	// 体重
	private String weight;

	// 生日
	@NotNull(message = "生日不能为空！")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date birthday;

	// 推荐时间
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date recommendTime;

	// 推荐者编号
	private Integer recommendUserId;

	// 职业
	private String profession;

	// 兴趣爱好
	private String tastesDiffer;

	// 手机验证码
	@NotEmpty(message = "验证码不能为空！")
	@NotNull(message = "验证码不能为空！")
	private String verifCode;

	// 身份证
	@NotEmpty(message = "身份证号码不能为空")
	@NotNull(message = "身份证号码不能为空")
	@Pattern(regexp = "([1-9]\\d{13,16}[a-zA-Z0-9]{1})", message = "身份证号码格式不正确")
	private String identityCard;

	@NotEmpty(message = "openID不能为空")
	@NotNull(message = "openID不能为空")
	private String activityOpenId;

	// 是否认证
	private Integer identityCardVerified;
	// 用户名
	@NotNull(message = "用户编号不能为空！")
	@NotEmpty(message = "用户编号不能为空！")
	private String userId;
	// code键
	private String captchaCode;
	// 值队
	private String captchaValue;

	@NotNull(message = "地址不能为空")
	private String address;

	// 手机绑定唯一码
	@NotNull(message = "手机绑定唯一码不能为空！")
	@NotEmpty(message = "手机绑定唯一码不能为空！")
	private String mobileCode;

	AccessToken token;

	//管理员用户名
	private String userName;
	
	//支付密码是否为空 0 空  1 已设置
	private String isPayPwd;
	
	

}