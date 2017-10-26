package cn.lazy.visbody.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时获取数据报告
 */
@Component
public class ReportTask {

    private int count=0;

//    @Scheduled(cron="*/6 * * * * ?")
//    private void process(){
//        System.out.println("this is scheduler task runing  "+(count++));
//    }
}
