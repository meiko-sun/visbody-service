/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package cn.lazy.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jasypt.encryption.StringEncryptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
  * @类名: MyBatisConfig
  * @描述: MyBatis基础配置 .
  * @程序猿: sundefa .
  * @日期: 2017年10月28日 下午6:13:47
  * @版本号: V2.0 .
  *
 */
@Configuration
@Slf4j
public class MyBatisConfig {
    
	 /**     * mybatis 配置路径     */    
   private static String MYBATIS_CONFIG = "mybatis-config.xml";

   @Autowired
   StringEncryptor stringEncryptor;//密码解码器注入
   
	 @Value("${spring.datasource.username}")
	 private String username;
	 
	 @Value("${spring.datasource.password}")
	 private String password;
   
   
    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        reg.addInitParameter("loginUsername", "admin");
        reg.addInitParameter("loginPassword", "admin");
        return reg;
    }
	@Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        filterRegistrationBean.addInitParameter("principalCookieName", "USER_COOKIE");
        filterRegistrationBean.addInitParameter("principalSessionName", "USER_SESSION");
        return filterRegistrationBean;
    }
	
	
    
//    @Bean  
//    @ConfigurationProperties(prefix = "spring.datasource")  
//    public DataSource dataSource() {  
//        DruidDataSource druidDataSource = new DruidDataSource();  
////        return DataSourceBuilder.create().type(dataSourceType).build(); 
//        return druidDataSource;  
//    }  
//	@Bean(name="dataSource", destroyMethod = "close", initMethod="init")  
	@Bean(destroyMethod = "close", initMethod = "init")
	@ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
		log.info("------------------druid-加载mybitis- writeDataSource init ---------------------");  
		System.out.println("注入druid！！！");
		DruidDataSource datasource = new DruidDataSource();
//		System.out.println("解密后的数据库用户名:" + username);
//        System.out.println("解密后的数据库密码:" + password);
		datasource.setUsername(username);
		datasource.setPassword(password);
		try {  
            List<Filter> filterList=new ArrayList<>();
            filterList.add(wallFilter());
            datasource.setProxyFilters(filterList);
//            datasource.setFilters(filters);
            log.info("DruidConfig bean init success.");  
        } catch (Exception e) {  
        	log.error("druid configuration initialization filter", e);  
        }  
        return datasource; 
//        return new org.apache.tomcat.jdbc.pool.DataSource();//加载tomcat pool
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        //分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("dialect", "mysql");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        //通过设置pageSize=0或者RowBounds.limit = 0就会查询出全部的结果。
        properties.setProperty("pageSizeZero", "true");
        pageHelper.setProperties(properties);
        
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(MYBATIS_CONFIG));

        //添加插件
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
        log.info("MyBatisConfig bean init success.");  
        return sqlSessionFactoryBean.getObject();
    }


//    platformTransactionManager 为springboot默认初始化好的对象，，无需定义
    @Bean(name = "transactionInterceptor")
    public TransactionInterceptor transactionInterceptor(
            PlatformTransactionManager platformTransactionManager) {
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        // 事物管理器
        transactionInterceptor.setTransactionManager(platformTransactionManager);
        Properties transactionAttributes = new Properties();
        // 新增
        transactionAttributes.setProperty("insert*","PROPAGATION_REQUIRED,-Throwable");
        // 修改
        transactionAttributes.setProperty("update*","PROPAGATION_REQUIRED,-Throwable");
        // 删除
        transactionAttributes.setProperty("delete*","PROPAGATION_REQUIRED,-Throwable");
        //查询
        transactionAttributes.setProperty("select*","PROPAGATION_REQUIRED,-Throwable,readOnly");

        transactionInterceptor.setTransactionAttributes(transactionAttributes);
        System.out.println("----------------事务配置加载完成--------------");
        return transactionInterceptor;
    }
    //代理到ServiceImpl的Bean
    @Bean
    public BeanNameAutoProxyCreator transactionAutoProxy() {
        BeanNameAutoProxyCreator transactionAutoProxy = new BeanNameAutoProxyCreator();
        transactionAutoProxy.setProxyTargetClass(true);
        transactionAutoProxy.setBeanNames("*Service");
        transactionAutoProxy.setInterceptorNames("transactionInterceptor");
        return transactionAutoProxy;
    }
    
    /**
     * 配置事务管理器
     */
    @Bean
    @Primary
    public DataSourceTransactionManager transactionManager() throws Exception{
        return new DataSourceTransactionManager(dataSource());
    }

   

    @Bean
	public WallFilter wallFilter(){
	    WallFilter wallFilter=new WallFilter();
	    wallFilter.setConfig(wallConfig());
	    return  wallFilter;
	}
	@Bean
	public WallConfig wallConfig(){
	    WallConfig config =new WallConfig();
	    config.setMultiStatementAllow(true);//允许一次执行多条语句
	    config.setNoneBaseStatementAllow(true);//允许非基本语句的其他语句
	    return config;
	}
}
