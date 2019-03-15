package com.szzn.box.Fegin;

import an.com.entry.GongWen.GongWen;
import an.com.entry.GongWen.GongWenChuanYue;
import an.com.entry.GongWen.GongWenXuHao;
import an.com.entry.GongWenPdf.GongWenBarcodeDTO;
import com.szzn.box.Fegin.Hystric.GongWenHandlerHystric;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Repository
@FeignClient(value = "gongwenserver",fallback = GongWenHandlerHystric.class)
public interface GongWenHandler {
    /**
     * 通过条码编号获取公文序号
     * @param tiaomabiaohao
     * @return
     */
    @RequestMapping(value = "/gongwen/gongwenxuhao/tiaomabianhao",method = RequestMethod.GET)
    List<GongWenXuHao> findGongWenXuHaoByTiaoMa(@RequestParam("tiaomabiaohao") String tiaomabiaohao);

    /**
     * 根据拟办信息组织成开箱子的IP串返回给CE程序
     * @param gongWenXHHangBiaoShi
     * @param danWeiHangBiaoShi
     * @param chuanyuezt
     * @return
     */
    @RequestMapping(value = "/gongwen/gongwenchuanyue/findGongWenChuanYueInfo",method = RequestMethod.GET)
    List<GongWenChuanYue> findGongWenChuanYueInfo(@RequestParam("gongWenXHHangBiaoShi") Integer gongWenXHHangBiaoShi,@RequestParam("danWeiHangBiaoShi")Integer danWeiHangBiaoShi,@RequestParam("chuanyuezt")Integer chuanyuezt);

    /**
     * 解析公文条码
     * @param Barcode
     * @return
     */
    @RequestMapping(value = "/gongwen/barcode/parseGongWenBarcode",method = RequestMethod.GET)
    GongWenBarcodeDTO parseGongWenBarcode(@Param("Barcode") String Barcode);

    /**
     * 通过公文条码查找公文
     * @param Barcode
     * @return
     */
    @RequestMapping(value = "/gongwen/barcode/findGongWenByGWBarcode",method = RequestMethod.GET)
    List<GongWen> findGongWenByGWBarcode(@Param("Barcode") String Barcode);
}
