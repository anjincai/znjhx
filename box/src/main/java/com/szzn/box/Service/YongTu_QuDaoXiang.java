package com.szzn.box.Service;

import an.com.entry.Box.Box;
import an.com.entry.Constant.Constant;
import an.com.entry.DanWei.DanWei;
import an.com.entry.GongWen.GongWen;
import an.com.entry.GongWen.GongWenXuHao;
import an.com.entry.LiuZhuan.LiuZhuan;
import an.com.entry.XinFeng.XinFengXinXi;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YongTu_QuDaoXiang {
    /**
     *
     * 【渠道箱信件投件操作】
     * @param BarcodeInfo
     * @param liuZhuan
     * @return
     * @return: String[]
     * @author: 【孔令海】
     * @update: [2012-12-24 下午04:58:51] [更改人姓名][变更描述]
     */
    public String[] quDaoXiangLetterTouJian(String BarcodeInfo,Box box,LiuZhuan liuZhuan){
        boolean bool;
        XinFengXinXi xinFengXinXi = xinJianHandler.findXinJianByTiaoMaInfo(BarcodeInfo,Constant.XINJIANZT_DENGJI);
        if(xinFengXinXi == null){
            if(BarcodeInfo.length() != 13){  //邮局
                return new String[]{"0","投件失败"};
            }else{
                xinFengXinXi = new XinFengXinXi();
                xinFengXinXi.setXinjianlx(Constant.XINJIANLX_FAXINEMS);
                xinFengXinXi.setXinfengbianhao(BarcodeInfo);
                xinFengXinXi.setMimidengjilx(0);
                xinFengXinXi.setMimidengjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("秘密等级", 0));
                xinFengXinXi.setHuanjilx(0);
                xinFengXinXi.setHuanjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("紧急程度", 0));
                xinFengXinXi.setXinfengtiaomaxx(BarcodeInfo);
                DanWei danwei = new DanWei();
                if(xinFengXinXi.getShoujiandanweibs() == null){
                    List<Integer> shoujiandanweihbs = commonHandler.findBoxShiYongDuiXiangByBoxID(box.getHangbiaoshi());
                    if(shoujiandanweihbs.size() == 1){
                        danwei = DanWeiUtils.getInstance().findDanWeiByHangBiaoShi(shoujiandanweihbs.get(0));
                        xinFengXinXi.setShoujiandanweibh(danwei.getDanweibianhao());
                        xinFengXinXi.setShoujiandanweibs(danwei.getHangbiaoshi());
                        xinFengXinXi.setShoujiandanweimc(danwei.getDanweimingcheng());
                    }
                }
                xinFengXinXi.setFajiandanweimc(liuZhuan.getCaozuodwmc());
                xinFengXinXi.setFajiandanweibs(liuZhuan.getCaozuodwbs());
                xinFengXinXi.setDengjirenbs(liuZhuan.getCaozuorenbs());
                xinFengXinXi.setDengjirenmc(liuZhuan.getCaozuorenmc());
                xinFengXinXi.setXinjianxz(1);
                xinFengXinXi.setXinjianzt(Constant.XINJIANZT_YITOU);

                xinFengXinXi.setDengjiriqi(new Date());
                xinFengXinXi.setXinfengbhlb(1);//信封编号类别 1为单函
                xinFengXinXi.setToudixiangtiyongtu(Constant.BOX_SHOUXINXIANG);//收信箱值是3
                CommonShouXinDao.getInstance().addXinJian(xinFengXinXi);//登记信件
            }
        }

        liuZhuan.setFenshu(1);
        liuZhuan.setJiaohuanxiangbs(box.getHangbiaoshi());
        liuZhuan.setWendanghbs(xinFengXinXi.getHangbiaoshi());
        liuZhuan.setWendanglx(Constant.WENDANGLX_XINJIAN);
        liuZhuan.setYewulx(Constant.YWLX_XINJIAN);
        //调用流转数据范围类操作数据库
        bool = wenDangLiuZhuan.processWenDangLiuZhuan(liuZhuan);
        if(bool)
            return new String[]{"1",""};
        else
            return new String[]{"0","投件失败"};
    }
    /**
     *
     * 【渠道箱公文裸文投件操作】
     * @param BarcodeInfo
     * @param liuZhuan
     * @return
     * @return: String[]
     * @author: 【孔令海】
     * @update: [2012-12-24 下午04:58:51] [更改人姓名][变更描述]
     */
    public String[] quDaoXiangGongWenTouJian(String BarcodeInfo,Box box,LiuZhuan liuZhuan){
        boolean bool;
        GongWen gongWen = gongWenHandler.findGongWenByXuHaoHBS(liuZhuan.getGongwenxuhaobs());
        if(gongWen == null){
            return new String[]{"0","投件失败"};
        }
        liuZhuan.setFenshu(1);
        liuZhuan.setJiaohuanxiangbs(box.getHangbiaoshi());
        liuZhuan.setWendanghbs(gongWen.getHangbiaoshi());
        liuZhuan.setWendanglx(Constant.WENDANGLX_GONGWEN);
        liuZhuan.setYewulx(Constant.YWLX_FENFA);
        //调用流转数据范围类操作数据库
        bool = wenDangLiuZhuan.processWenDangLiuZhuan(liuZhuan);

        if(bool)
            return new String[]{"1",""};
        else
            return new String[]{"0","投件失败"};
    }

    /**
     *
     * 【公文拟办单投箱处理方法】
     * @param BarcodeInfo
     * @param box
     * @param liuZhuan
     * @return
     * @return: String[]
     * @author: 【孔令海】
     * @update: [2013-03-05 下午02:39:34] [更改人姓名][变更描述]
     */
    public String[] gongWenNiBanTouJian(String BarcodeInfo,Box box,LiuZhuan liuZhuan) {
        boolean bool;
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(BarcodeInfo);;
        if(gongWenXuHaos.size() != 1){
            return new String[]{"0","投件失败"};
        }
        liuZhuan.setFenshu(1);
        liuZhuan.setJiaohuanxiangbs(box.getHangbiaoshi());
        liuZhuan.setWendanghbs(gongWenXuHaos.get(0).getGongwenhbs());
        liuZhuan.setWendanglx(Constant.WENDANGLX_GONGWEN);
        liuZhuan.setYewulx(gongWenXuHaos.get(0).getYewulx());
        //调用流转数据范围类操作数据库
        bool = wenDangLiuZhuan.processWenDangLiuZhuan(liuZhuan);
        if(bool){
            return new String[]{"1",""};
        }else{
            return new String[]{"0","投件失败"};
        }
    }

    /**
     *
     * <p>[公文分送投件业务]</p>
     * @param BarcodeInfo
     * @param box
     * @param liuZhuan
     * @return
     * @return: String[]
     * @author: 孔令海
     * @update: [2012-10-8 上午09:49:26] [更改人姓名][变更描述]
     */
    public String[] gongWenChuanYueTouJian(String BarcodeInfo, Box box, LiuZhuan liuZhuan) {
        boolean bool;
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(BarcodeInfo);
        if(gongWenXuHaos.size() != 1){
            return new String[]{"0","投件失败"};
        }
        liuZhuan.setFenshu(1);
        liuZhuan.setJiaohuanxiangbs(box.getHangbiaoshi());
        liuZhuan.setWendanghbs(gongWenXuHaos.get(0).getGongwenhbs());
        liuZhuan.setWendanglx(Constant.WENDANGLX_GONGWEN);
        liuZhuan.setYewulx(gongWenXuHaos.get(0).getYewulx());
        //调用流转数据范围类操作数据库
        bool = wenDangLiuZhuan.processWenDangLiuZhuan(liuZhuan);
        if(bool){
            return new String[]{"1",""};
        }else{
            return new String[]{"0","投件失败"};
        }
    }

    /**
     *
     * <p>[公文分发投普通箱业务]</p>
     * @param BarcodeInfo
     * @param box
     * @param liuZhuan
     * @return
     * @return: String[]
     * @author: 孔令海
     * @update: [2012-10-8 上午09:49:26] [更改人姓名][变更描述]
     */
    public String[] gongWenFenFaTouJian(String BarcodeInfo, Box box, LiuZhuan liuZhuan) {
        boolean bool;
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(BarcodeInfo);//获取当前投的文档序号DTO
        if(gongWenXuHaos.size() != 1){//一个条码号只对应GONGWENXUHAO表里一条数据，如果查询出多条说明UUID重复，生成规则有问题
            return new String[]{"0","投件失败"};
        }
        GongWen gongWen = gongWenHandler.findGongWenByXuHaoHBS(gongWenXuHaos.get(0).getHangbiaoshi());
        List<Integer> box_ShiYongDuiXiangs = commonHandler.findBoxShiYongDuiXiangByBoxID(box.getHangbiaoshi());
        if(box_ShiYongDuiXiangs.size() == 0){
            return new String[]{"0","投件失败"};
        }else if(box_ShiYongDuiXiangs.size() == 1){
            List<GongWenChuanYue> list_ChuanYue  = gongWenHandler.findGongWenChuanYueInfo(gongWenXuHaos.get(0).getHangbiaoshi(),box_ShiYongDuiXiangs.get(0),Constant.CHUANYUE_WEITOU);
            if(list_ChuanYue.size() != 1){
                return new String[]{"0","没有分发信息"};
            }
            liuZhuan.setFenshu(list_ChuanYue.get(0).getCyfenshu());
            liuZhuan.setWendanghbs(gongWenXuHaos.get(0).getGongwenhbs());
            liuZhuan.setWendanglx(Constant.WENDANGLX_GONGWEN);
            liuZhuan.setYewulx(gongWenXuHaos.get(0).getYewulx());
//			liuZhuan.setJijianzt(gongWen.getJinjichengdulx() >= 2 ? 1 : 0);
            bool = wenDangLiuZhuan.processWenDangLiuZhuan(liuZhuan);
            if(!bool)
                return new String[]{"0","投件失败"};
        }else if(box_ShiYongDuiXiangs.size() > 1){
            //假如多个单位使用一个箱子的时候...千年以后待续...
        }
        return new String[]{"1",""};
    }
}
