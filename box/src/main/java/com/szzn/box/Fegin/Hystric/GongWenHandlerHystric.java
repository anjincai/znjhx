package com.szzn.box.Fegin.Hystric;

import an.com.entry.GongWen.GongWen;
import an.com.entry.GongWen.GongWenChuanYue;
import an.com.entry.GongWen.GongWenXuHao;
import an.com.entry.GongWenPdf.GongWenBarcodeDTO;
import com.szzn.box.Fegin.GongWenHandler;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class GongWenHandlerHystric implements GongWenHandler {
    /**
     * 通过条码编号获取公文序号
     *
     * @param tiaomabiaohao
     * @return
     */
    @Override
    public List<GongWenXuHao> findGongWenXuHaoByTiaoMa(String tiaomabiaohao) {
        return null;
    }

    /**
     * 根据拟办信息组织成开箱子的IP串返回给CE程序
     *
     * @param gongWenXHHangBiaoShi
     * @param danWeiHangBiaoShi
     * @param chuanyuezt
     * @return
     */
    @Override
    public List<GongWenChuanYue> findGongWenChuanYueInfo(Integer gongWenXHHangBiaoShi, Integer danWeiHangBiaoShi, Integer chuanyuezt) {
        return null;
    }

    /**
     * 解析公文条码
     *
     * @param Barcode
     * @return
     */
    @Override
    public GongWenBarcodeDTO parseGongWenBarcode(String Barcode) {
        return null;
    }

    /**
     * 通过公文条码查找公文
     *
     * @param Barcode
     * @return
     */
    @Override
    public List<GongWen> findGongWenByGWBarcode(String Barcode) {
        return null;
    }
}
