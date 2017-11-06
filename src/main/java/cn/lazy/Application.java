package cn.lazy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import cn.lazy.config.AudienceConfig;
import lombok.extern.java.Log;

/**
 * 
  * @类名: Application
  * @描述: TODO .
  * @程序猿: sundefa .
  * @日期: 2017年10月28日 下午5:52:16
  * @版本号: V2.0 .
  *
 */
@EnableAutoConfiguration
@ServletComponentScan
@EnableScheduling
@ComponentScan
@EnableConfigurationProperties({AudienceConfig.class}) 
@Log
@MapperScan("cn.lazy.mapper")
public class Application{

/**
 *  程序入口
  * @方法名: main
  * @描述: TODO .
  * @程序猿: sundefa .
  * @日期: 2017年10月28日 下午5:51:56
  * @返回值: void  
  * @版本号: V2.0 .
  * @throws
 */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("SpringBoot Start Success");
    }
 
}
