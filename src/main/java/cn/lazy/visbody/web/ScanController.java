package cn.lazy.visbody.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "scan")
@RestController
public class ScanController {

    /**
     * 用户扫描体侧设备二维码
     * @param scanId
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/")
    public String scan(@RequestParam(value = "scanId", required = true) String scanId,
                       @RequestParam(value = "deviceId", required = true) String deviceId) {

        //保存到数据库生成一个报告记录

        //返回成功

        return "";
    }
}
