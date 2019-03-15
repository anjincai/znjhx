package com.szzn.danwei.Controller.DanWei;

import an.com.entry.DanWei.DanWei;
import com.szzn.danwei.Service.DanWeiService;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DanWeiUtils {

    @Autowired
    private DanWeiService danWeiService;
    /**
     *
     * 【根据单位行标识获取单位】
     * @param danWeiHangBiaoShi
     * @return
     * @return: DanWei
     * @author: 【孔令海】
     * @update: [2012-12-20 上午10:59:24] [更改人姓名][变更描述]
     */
    @GetMapping("/danwei/utils/danweibs")
    public DanWei findDanWeiByHangBiaoShi(Integer danWeiHangBiaoShi){
        DanWei danWei = danWeiService.findDanWeiByHangBiaoShi(danWeiHangBiaoShi);
        return  danWei;
    }
}
