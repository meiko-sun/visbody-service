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
public class VisbodyForAll implements Serializable {
	private double weight;
	private double bodyFat;
	private double percentBodyFat;
	private double bmi;
	private double kcal;
	private double waistToHip;
	private double fluid;
	private double leftUpperArmGirth;
	private double rightUpperArmGirth;
	private double bustGirth;
	private double waistGirth;
	private double hipGirth;
	private double rightThighGirth;
	private double leftThighGirth;
	private double rightCalfGirth;
	private double leftCalfGirth;
	private double height;
	

}