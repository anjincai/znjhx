package com.szzn.szznwebservice.Box.FeignClient;

import com.szzn.szznwebservice.Box.FeignClient.Hystric.BoxHystric;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "boxserver",fallback = BoxHystric.class)
public interface BoxFegin {

    /**
     *
     * <p>
     * [箱体初始化方法]
     * </p>
     *
     * @param IP
     * @return
     * @return: String[]
     */
    @RequestMapping(value = "/box/CS/Ip",method = RequestMethod.GET)
    String[] findBoxesByIP(@RequestParam("IP") String IP);

    /**
     *
     * <p>
     * [获得受控分箱]
     * </p>
     *
     * @param hangbiaoshi
     * @return
     * @return: String[]
     */
    @RequestMapping(value = "/box/CS/hangbiaoshi",method = RequestMethod.GET)
    String[] GetShouKongObjListForBBox(@RequestParam("hangbiaoshi") String hangbiaoshi);
    /**
     *
     * <p>
     * [获得交换箱信息]
     * </p>
     *
     * @param IP
     * @return
     * @return: String[]
     */
    @RequestMapping(value = "/box/CS/BOX/IP",method = RequestMethod.GET)
    String[] GetBoxInfoByIP(@RequestParam("IP") String IP);

    /**
     *
     * <p>
     * [扫描]
     * </p>
     *
     * @param BoxHangBiaoShi
     * @return
     * @return: String[]
     */
    @RequestMapping(value = "/saomiao",method = RequestMethod.GET)
    String[] CheckBarcode(@RequestParam("BoxHangBiaoShi") String BoxHangBiaoShi, @RequestParam("BarcodeInfo")String BarcodeInfo, @RequestParam("CardID")String CardID);
}
