package cn.lazy.utils;

import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableEncryptableProperties
@SpringBootTest(classes=Deserializers.Base.class)
@RunWith(SpringRunner.class)
public class TestBootTest {
    @Autowired
    StringEncryptor stringEncryptor;//密码解码器自动注入


    @org.junit.Test
    public void test() {
        System.out.println("生成加密后的数据库用户名："+stringEncryptor.encrypt("cjw20098@sina.com"));
        System.out.println("生成加密后的数据库密码："+stringEncryptor.encrypt("123456"));
    }
   
}