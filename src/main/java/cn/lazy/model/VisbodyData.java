package cn.lazy.model;

import lombok.Data;
/**
 * 
  * @类名: VisbodyData
  * @描述: 维塑身体数据模型 .
  * @程序猿: sundefa .
  * @日期: 2017年11月1日 上午11:12:01
  * @版本号: V2.0 .
  *
 */
@Data
public class VisbodyData {
	private double weight;
	private double bodyFat;
	private double percentBodyFat;
	private double bmi;
	private double kcal;
	private double waistToHip;
	private double fluid;
}
