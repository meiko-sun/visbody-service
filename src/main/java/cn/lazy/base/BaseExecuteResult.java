package cn.lazy.base;

import java.io.Serializable;

/**
 * 	输出基类
  * @类名: BaseExecuteResult
  * @描述: TODO .
  * @程序猿: sundefa .
  * @日期: 2017年10月28日 下午5:52:45
  * @版本号: V2.0 .
  *
 */

public class BaseExecuteResult<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int isSuccess;// -1 验证失败 0:未成功 1:成功
	private String errorCode;
	private String errorMsg;
	/**
	 * 执行结果类别
	 */
	private String executeType;
	private T result;

	public BaseExecuteResult() {
		super();
	}

	public BaseExecuteResult(String executeType) {
		this.executeType = executeType;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public String getExecuteType() {
		return executeType;
	}

	public int getIsSuccess() {
		return isSuccess;
	}

	public T getResult() {
		return result;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public void setExecuteType(String executeType) {
		this.executeType = executeType;
	}

	public void setIsSuccess(int isSuccess) {
		this.isSuccess = isSuccess;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public BaseExecuteResult(int isSuccess, T result) {
		super();
		this.isSuccess = isSuccess;
		this.result = result;
	}

	public BaseExecuteResult(int isSuccess, String errorCode, String errorMsg) {
		super();
		this.isSuccess = isSuccess;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
	
}
