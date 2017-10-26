package cn.lazy.visbody.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReportController {


    /**
     * 获取用户体侧记录列表
     * @param uid
     * @return
     */
    @RequestMapping(value = "/visbody/report/list", method = RequestMethod.POST)
    public String getList(@RequestParam(value = "uid", required = true) String uid){

        return "";
    }

    /**
     * 显示用户体侧报告，通过html显示
     * @param name
     * @param model
     * @return
     */
    @RequestMapping(value = "/visbody/report/info", method = RequestMethod.POST)
    public String info(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "report";
    }
}
