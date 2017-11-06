package cn.lazy.config;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.stereotype.Component;

@Component  
public class MyCustomizationBean implements EmbeddedServletContainerCustomizer  {  
    /** 
     * @param container 
     * @see org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer#customize(org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer) 
     */  
    public void customize(ConfigurableEmbeddedServletContainer container) {  
         //container.setContextPath("/SpringBoot");  
//         container.setPort(8888);  
//         container.setSessionTimeout(30);
    }  
       
}  