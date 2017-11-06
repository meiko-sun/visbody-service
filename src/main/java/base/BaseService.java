package cn.lazy.base;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 	Service基类
  * @类名: BaseService
  * @描述: TODO .
  * @程序猿: sundefa .
  * @日期: 2017年10月28日 下午5:53:13
  * @版本号: V2.0 .
  *
 */
@Slf4j
public class BaseService {
    public static final ObjectMapper jsonMapper = new ObjectMapper();
    public static final String IN_PARAMETER_FORMAT = "========>>>>{}.{}->in params:{}";
    public static final String OUT_PARAMETER_FORMAT = "========>>>>{}.{}->out params:{}";
    public static final String ERROR_FORMAT = "========>>>>{}.{}->error:{}";

    public static void debug(String format, String className, String methodName, Object params) {
        try {
            log.debug(format, className, methodName, jsonMapper.writeValueAsString(params));
        } catch (Exception e) {
            log.debug(e.toString());
        }
    }

    public static void info(String format, String className, String methodName, Object params) {
        try {
            log.info(format, className, methodName, jsonMapper.writeValueAsString(params));
        } catch (Exception e) {
            log.debug(e.toString());
        }
    }
}
