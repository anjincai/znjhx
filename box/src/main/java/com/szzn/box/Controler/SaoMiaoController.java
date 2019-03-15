package com.szzn.box.Controler;

import an.com.entry.Barcode.BarCodeType;
import an.com.entry.Barcode.GongWen128BarcodeUtilTool;
import an.com.entry.Box.Box;
import an.com.entry.Constant.Constant;
import an.com.entry.GongWen.GongWen;
import an.com.entry.GongWen.GongWenXuHao;
import an.com.entry.GongWenPdf.GongWenBarcodeDTO;
import com.szzn.box.Fegin.GongWenHandler;
import com.szzn.box.Service.CommonHandlerService;
import com.szzn.box.Service.YongTu_PuTongXiang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SaoMiaoController {

    @Autowired
    private  YongTu_PuTongXiang yongTu_PuTongXiang;

    @Autowired
    private CommonHandlerService commonHandler;

    @Autowired
    private GongWenHandler gongWenHandler;

    @GetMapping("/saomiao")
    public String[] CheckBarcode(String BoxHangBiaoShi, String BarcodeInfo, String CardID) {
        // 判断条码合法性
        BarcodeInfo=BarcodeInfo.replaceAll("\\*", "");
        BarCodeType type = GongWen128BarcodeUtilTool.getType(BarcodeInfo);
        Box box = commonHandler.findBoxByHbs(Integer.parseInt(BoxHangBiaoShi));
        if(null==type){
            if(BarcodeInfo.indexOf("GB0626-2005") != -1){
                type = BarCodeType.GONGWEN;
            }else{
                type=BarCodeType.XINJIAN_EMS;
            }
        }
        if(!BarCodeType.XINJIAN_26.equals(type) && !BarCodeType.XINJIAN_17.equals(type)
                && !BarCodeType.XINJIAN_JY.equals(type) && !BarCodeType.XINJIAN_EMS.equals(type)){
            GongWenBarcodeDTO gongwenBarcode=gongWenHandler.parseGongWenBarcode(BarcodeInfo);
            List<GongWenXuHao> gongWenXuHaos = new ArrayList<GongWenXuHao>();
            gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(gongwenBarcode.getTiaoMaBianHao());
            /*
             * 当条码类型为空或者条码类型不为空并且该条码在本系统中没有处理信息，则视为通开条码对待。
             */
            if(type == null){
                type = BarCodeType.GONGWEN_TK;
            }

            if(box == null){
                return new String[] { "0", "交换箱未启用", "" };
            }

            //通开条码
            if(BarCodeType.GONGWEN_TK.equals(type)){
                List<Box> boxes = new ArrayList<Box>();
                if(Constant.BOX_TYPE_A.equals(box.getXiangtilx())){
                    Box boxtemp = commonHandler.findBoxByHbs(box.getHangbiaoshi());
                    if(boxtemp.getMingcheng().indexOf("备用") != -1){
                        return new String[] { "0", "交换箱还未启用，操作被拒绝", "" };
                    }
                    boxes.add(boxtemp);
                }else if(Constant.BOX_TYPE_B.equals(box.getXiangtilx())){
                    boxes = commonHandler.FINDCHILDRENBOXES(box.getHangbiaoshi() + "");
                }
                String[] IPS = new String[boxes.size()+2];
                IPS[0] = "3";
                IPS[1] = "";
                if(boxes.size()>0){
                    for (int i = 0; i < boxes.size(); i++) {
                        IPS[i + 2] = boxes.get(i).getIp();
                    }
                    return IPS;//组织好的IP串返回给CE程序
                }
            }

            //判断公文128码的业务类型         //           shenzhou/123
            if (BarCodeType.GONGWEN.equals(type)) {
                GongWenBarcodeDTO gongwenBar=gongWenHandler.parseGongWenBarcode(BarcodeInfo);
                String zidingyi = gongwenBar.getZiDingYi();
//				if(null!=zidingyi && !zidingyi.equals("")){
                if(zidingyi.indexOf("shenzhou") == -1){//外来条码
                    List<GongWen> gongwenList = gongWenHandler.findGongWenByGWBarcode(BarcodeInfo);
                    if(gongwenList.size()==0){//第一次投箱
                        List<Box> boxes = new ArrayList<Box>();
                        if(Constant.BOX_TYPE_A.equals(box.getXiangtilx())){
                            Box boxtemp = commonHandler.findBoxByHbs(box.getHangbiaoshi());
                            if(boxtemp.getMingcheng().indexOf("备用") != -1){
                                return new String[] { "0", "交换箱还未启用，操作被拒绝", "" };
                            }
                            boxes.add(boxtemp);
                        }else if(Constant.BOX_TYPE_B.equals(box.getXiangtilx())){
                            boxes = commonHandler.FINDCHILDRENBOXES(box.getHangbiaoshi() + "");
                        }
                        String[] IPS = new String[boxes.size()+2];
                        IPS[0] = "3";
                        IPS[1] = "";
                        if(boxes.size()>0){
                            for (int i = 0; i < boxes.size(); i++) {
                                if(boxes.get(i).getJiaohuanxiangyt()!=11){
                                    IPS[i + 2] = boxes.get(i).getIp();
                                }
                            }
                            return IPS;//组织好的IP串返回给CE程序
                        }else{
                            return new String[] { "0", "单位不在本箱组", "" };
                        }
                    }else{//已经投过箱
                        GongWen gw = gongwenList.get(0);
                        if(null!=gw.getWenjianzt() && !gw.getWenjianzt().equals("")){
                            List<Box> boxes = new ArrayList<Box>();
                            if(Constant.BOX_TYPE_A.equals(box.getXiangtilx())){
                                Box boxtemp = commonHandler.findBoxByHbs(box.getHangbiaoshi());
                                if(boxtemp.getMingcheng().indexOf("备用") != -1){
                                    return new String[] { "0", "交换箱还未启用，操作被拒绝", "" };
                                }
                                boxes.add(boxtemp);
                            }else if(Constant.BOX_TYPE_B.equals(box.getXiangtilx())){
                                boxes = commonHandler.FINDCHILDRENBOXES(box.getHangbiaoshi() + "");
                            }
                            String[] IPS = new String[boxes.size()+2];
                            IPS[0] = "3";
                            IPS[1] = "";
                            if(boxes.size()>0){
                                for (int i = 0; i < boxes.size(); i++) {
                                    if(boxes.get(i).getJiaohuanxiangyt()!=11){
                                        IPS[i + 2] = boxes.get(i).getIp();
                                    }
                                }
                                return IPS;//组织好的IP串返回给CE程序
                            }
                        }
                    }
                }else{//内部条码


                    if (Constant.YWLX_FENSONGFENCHUAN.equals(gongWenXuHaos.get(0).getYewulx())) {
                        List<GongWenNiBan> List_NiBan = gongWenHandler.findNiBanByYeWuHBS(gongWenXuHaos.get(0).getYewuhbs());//根据公文序号表里的业务行标识获取拟办表信息
                        if(List_NiBan.size() != 1){
                            return new String[] { "0", "拟办数据错误", "" };
                        }
                        //用拟办表里的fensongfenchuan字段区分其条码类型
                        type = List_NiBan.get(0).getFensongfenchuan().intValue() == Constant.NIBAN_FENSONG ? BarCodeType.GONGWEN_FF :BarCodeType.GONGWEN_CY;//分送||分传
                    } else if (Constant.YWLX_NIBAN.equals(gongWenXuHaos.get(0).getYewulx())) {
                        type = BarCodeType.GONGWEN_NB;//拟办
                    } else if(Constant.YWLX_FENFA.equals(gongWenXuHaos.get(0).getYewulx())) {
                        type = BarCodeType.GONGWEN_FF;//分送
                    }
                }
            }

        }
//		}

        if (Constant.BOX_TINGJUXIANG.equals(box.getJiaohuanxiangyt())) {// 普通箱
            if(BarCodeType.GONGWEN_NB.equals(type)){//拟办条码
                return yongTu_PuTongXiang.niBanBarCodeHandler(BarcodeInfo, box);
            }else if(BarCodeType.GONGWEN_FF.equals(type)){//分送条码
                return yongTu_PuTongXiang.fenFaBarCodeHandler(BarcodeInfo, box);
            }else if(BarCodeType.GONGWEN_CY.equals(type)){//分传条码
                return yongTu_PuTongXiang.chuanYueBarCodeHandler(BarcodeInfo, box);
            }else if (BarCodeType.XINJIAN_26.equals(type) || BarCodeType.XINJIAN_17.equals(type)
                    || BarCodeType.XINJIAN_JY.equals(type) || BarCodeType.XINJIAN_EMS.equals(type) ) {// 国办信件条码 || 央企信件条码
                return yongTu_PuTongXiang.xinJianBarCodeHandler(BarcodeInfo, box);
            }
        }

        return new String[] { "0", "扫描失败", "" };
    }
}
