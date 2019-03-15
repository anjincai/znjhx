package com.szzn.box.Fegin;

import an.com.entry.DanWei.DanWei;
import com.szzn.box.Fegin.Hystric.DanWeiUtilHystric;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Repository
@FeignClient(value = "danweiserver",fallback = DanWeiUtilHystric.class)
public interface DanWeiUtil {
    @RequestMapping(value = "/danwei/utils/danweibs",method = RequestMethod.GET)
    DanWei findDanWeiByHangBiaoShi(@Param("danweibs") Integer danweibs);
}
