package cn.lazy.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Visbody {
	
	private String deviceId="00011709080040";//设备id
	
	private String scanId;//扫描id
	
	private String uid;//用户id
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date time;//接收时间
	
	private Integer type;//检测类型
	
	private Integer status;//合成状态
	
	private String msg;//消息
	
	private String token;//token的string
}
