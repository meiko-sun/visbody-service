package cn.lazy.utils;



import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.springframework.util.CollectionUtils;


import cn.lazy.base.ValidationResult;



/**
 * 校验工具类
 * @author wdmcygah
 *
 */
public class ValidationUtils {

private static Validator validator =  Validation.buildDefaultValidatorFactory().getValidator();
	
	public static <T> ValidationResult validateEntity(T obj){
		ValidationResult result = new ValidationResult();
		 Set<ConstraintViolation<T>> set = validator.validate(obj,Default.class);
		 if( !CollectionUtils.isEmpty(set) ){
			 result.setHasErrors(true);
			 Map<String,String> errorMsg = new HashMap<String,String>();
			 for(ConstraintViolation<T> cv : set){
				 errorMsg.put(cv.getPropertyPath().toString(), cv.getMessage());
			 }
			 result.setErrorMsg(errorMsg);
		 }
		 return result;
	}
	
	public static <T> ValidationResult validateProperty(T obj,String propertyName){
		ValidationResult result = new ValidationResult();
		if (propertyName.indexOf(",") != -1) {
			String[] propertys = propertyName.split(",");
			 Map<String,String> errorMsg = new HashMap<String,String>();
			for (int i = 0; i < propertys.length; i++) {
				Set<ConstraintViolation<T>> set = validator.validateProperty(obj,propertys[i],Default.class);
				 if( !CollectionUtils.isEmpty(set) ){
					 result.setHasErrors(true);
					 for(ConstraintViolation<T> cv : set){
						 errorMsg.put(propertys[i], cv.getMessage());
					 }
					 result.setErrorMsg(errorMsg);
				 }
			}
		} else {
			Set<ConstraintViolation<T>> set = validator.validateProperty(obj,propertyName,Default.class);
			 if( !CollectionUtils.isEmpty(set) ){
				 result.setHasErrors(true);
				 Map<String,String> errorMsg = new HashMap<String,String>();
				 for(ConstraintViolation<T> cv : set){
					 errorMsg.put(propertyName, cv.getMessage());
				 }
				 result.setErrorMsg(errorMsg);
			 }
		}
		return result;
		
	}
}