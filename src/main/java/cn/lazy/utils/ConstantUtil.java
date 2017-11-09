package cn.lazy.utils;

/**
 * 
  * @类名: ConstantUtil
  * @描述: 枚举类 .
  * @程序猿: sundefa .
  * @日期: 2017年10月28日 下午9:12:00
  * @版本号: V2.0 .
  *
 */
public class ConstantUtil {

	public static int success = 1;
	public static int failed = 0;
	public static int vfailed = -1;
	public static final String ENCRYPT_PWD = "A/S62x7'cr,FRK4vjO5s";//加密字符串，用于注册和登录

	//1.使用 2.续存 3.清空
	public enum ISUSERD  {
		USEING(1,"使用"),
		USED(2,"续存"),
		CLEAR(3,"清空");
	    	
	    	private Integer status;
	    	private String nameCn;
	    	ISUSERD(Integer status, String nameCn) {
	            this.status = status;
	            this.nameCn = nameCn;
	       	}
	    
	    public Integer getStatus() {
				return status;
			}

			public void setStatus(Integer status) {
				this.status = status;
			}

			public String getNameCn() {
				return nameCn;
			}

			public void setNameCn(String nameCn) {
				this.nameCn = nameCn;
			}
	    }
	
	
	/**
     * 消息
     */
    public enum ResponseMSG {
    	UP_OK("success", "30分定时扫描2天内的预约表记录,更新数据---%s条", "1000"),
    	UP_WAIT("success", "扫描结束，查无数据,继续等候处理--------", "1002"),
    	SAVE_OK("save_ok","存储成功！","1003"),
    	USEDING_OK("useing_ok","使用成功！","1004"),
    	USED_OK("used_ok","续存成功！","1005"),
    	CLEAR_OK("clear_ok","清空离场！","1006"),
    	UNBUND_OK("unbund_ok","解绑成功！","1007");
        private String msg_en;
        private String msg_cn;
        private String code;

        ResponseMSG(String msg_en, String msg_cn, String code) {
            this.msg_en = msg_en;
            this.msg_cn = msg_cn;
            this.code = code;
        }

        @Override
        public String toString() {
            return this.msg_cn;
        }

        public String getEn() {
            return msg_en;
        }

        public String getCn() {
            return msg_cn;
        }

        public String getCode() {
            return this.code;
        }
    }

    /**
     * 错误
     */
    public enum ResponseError {
    	FAILED("失败", "1111"),
    	SYS_ERROR("系统错误,入参格式有误!", "40005"),
    	INVALID_CLIENTID("无效的客户端标识","30003"),
    	INVALID_PASSWORD("手机号码输入有误，请重新输入","30004" ),
    	INVALID_CAPTCHA("无效的令牌或者令牌已过期","40004"),
    	INVALID_TOKEN("无效的令牌","40003"),
    	BINDINGFAILED("绑定id失败", "40006"),
    	NOTFOUNDUSER("获取用户信息失败", "40007"),
    	VALIDATE_COACH_TIME("字典数据为空，或教练未发布时间对应的时间，请联系管理员！","1003"),
    	SAVE_FAILED("存储失败！","1004"),
        VERIFY_CODE_ERR4("密码输入有误,请重新输入!","1011"),
        VERIFY_CODE_ERR5("上次离场，未清空衣柜，请联系管理员或前台进行解绑操作，谢谢配合!","1012"),
    	OBJECTNULL("查询不到该数据!","9998"),
    	SCANIDISHAVING("scanId已存在空","40009"),
    	SCANIDISNOTNOLL("scanId不能为空","40008");
        
        private String error;
        private String code;

        ResponseError(String error, String code) {
            this.error = error;
            this.code = code;
        }

        @Override
        public String toString() {
            return this.error;
        }

        public String getCode() {
            return this.code;
        }

        public String getError() {
            return this.error;
        }
    }

	
	
}
