package cn.lazy.visbody.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供给第三方的的接口
 */
@RestController
public class ThirdClientController {


    /**
     * 第三方访问懒人接口，获取token令牌
     * @param appId
     * @param appSecret
     * @return
     */
    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    public String getToken(@RequestParam(value = "appId") String appId,
                           @RequestParam(value = "appSecret") String appSecret) {

        String token = "";

        //过期时间
        int expireIn = 7200;

        //保存在redis中

        return token;
    }

    /**
     * 生成二维码给设备服务商进行展示
     * @param scanId
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/visbody/getQRcode", method = RequestMethod.GET)
    public String getQRCode(@RequestParam(value = "scanId", required = true) String scanId,
                            @RequestParam(value = "deviceId", required = true) String deviceId) {

        //生成二维码
        String qrCodeContent = "company=懒人易健";
        qrCodeContent += "&scanId=" + scanId;
        qrCodeContent += "&deviceId=" + deviceId;

        //找到logo文件

        //生成带logo的二维码并进行base64转化


        String qrCodeData = "";
        //返回json

        return "";
    }


    /**
     * 设备商通知体侧结果合成成功
     * @param scanId
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/visbody/resultNotify", method = RequestMethod.POST)
    public String resultNotify(@RequestParam(value = "scanId", required = true) String scanId,
                            @RequestParam(value = "deviceId", required = true) String deviceId) {


        //拿到model_url



        return "";
    }

}
