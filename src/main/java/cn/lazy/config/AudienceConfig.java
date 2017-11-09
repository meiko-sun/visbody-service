package cn.lazy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "audience", locations = "classpath:jwt.properties")
public class AudienceConfig {
	
	private String clientId;
	private String base64Secret;
	private String name;
	private int expiresSecond;

}