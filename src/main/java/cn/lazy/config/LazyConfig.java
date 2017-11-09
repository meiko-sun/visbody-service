package cn.lazy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

import lombok.extern.java.Log;

/**
 * 
  * @类名: LazyConfig
  * @描述: 配置 .
  * @程序猿: sundefa .
  * @日期: 2017年10月28日 下午6:07:31
  * @版本号: V2.0 .
  *
 */
@Configuration
@EnableSwagger
@EnableAutoConfiguration
@Log
public class LazyConfig
{    
    private SpringSwaggerConfig springSwaggerConfig;
    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation(){
    	log.info("springSwaggerConfig bean init success.");  
        return  new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(new ApiInfo("api", "desc", null, null, null, null))
                .useDefaultResponseMessages(false)
                .includePatterns(".*?");
    }
}