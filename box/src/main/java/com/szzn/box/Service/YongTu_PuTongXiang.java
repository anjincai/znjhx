package com.szzn.box.Service;

import an.com.entry.Box.Box;
import an.com.entry.Constant.Constant;
import an.com.entry.DanWei.DanWei;
import an.com.entry.GongWen.GongWen;
import an.com.entry.GongWen.GongWenChuanYue;
import an.com.entry.GongWen.GongWenXuHao;
import an.com.entry.GongWenPdf.GongWenBarcodeDTO;
import an.com.entry.LiuZhuan.LiuZhuan;
import an.com.entry.XinFeng.XinFengXinXi;
import com.szzn.box.Fegin.DanWeiUtil;
import com.szzn.box.Fegin.GongWenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class YongTu_PuTongXiang {

    @Autowired
    private CommonHandlerService commonHandler;

    @Autowired
    private GongWenHandler gongWenHandler;

    @Autowired
    private DanWeiUtil DanWeiUtils;

    /**
     *
     * <p>【拟办条码扫描普通箱业务处理方法】</p>
     * @param BarcodeInfo
     * @param
     * @return
     * @return: String[]
     * @author:【 孔令海】
     * @update: [2012-9-28 下午06:14:08] [更改人姓名][变更描述]
     */
    public String[] niBanBarCodeHandler(String BarcodeInfo, Box box) {
        GongWenBarcodeDTO gongwenBarcode=gongWenHandler.parseGongWenBarcode(BarcodeInfo);
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(gongwenBarcode.getTiaoMaBianHao());
        if(gongWenXuHaos.size() != 1){//一个条码号只对应GONGWENXUHAO表里一条数据，如果查询出多条说明UUID重复，生成规则有问题
            return new String[] {"0","非法投件，拒绝投箱", ""};
        }
        if(Constant.XUHAOZT_BANJIE.equals(gongWenXuHaos.get(0).getWenjianzt())){//办结
            return new String[] {"0","该处理单已经传阅完毕，操作被拒绝", ""};
        }
//		else if(Constant.XUHAOZT_YITOU.equals(gongWenXuHaos.get(0).getWenjianzt())){//已投
//			return new String[] {"0","该处理单已经投件，拒绝投箱", ""};
//		}
        // 根据拟办信息组织成开箱子的IP串返回给CE程序
        List<GongWenChuanYue> List_ChuanYue =  gongWenHandler.findGongWenChuanYueInfo(gongWenXuHaos.get(0).getHangbiaoshi(),null,Constant.CHUANYUE_WEITOU);
        if(List_ChuanYue.size() == 0){
            return new String[] {"0","该条码没有拟办信息，拒绝投箱", ""};
        }
        //计算下面数组的长度
        List<Box> box_list = new ArrayList<Box>();
        DanWei danWei = null;
        for (int i = 0; i < List_ChuanYue.size(); i++) {
            List<Box> boxes = commonHandler.findBoxByShiYongDanWeiHbs(List_ChuanYue.get(i).getCydxbs(),box.getHangbiaoshi());
            if(boxes.size()>0){
                box_list.add(boxes.get(0));
            }
            //当拟办单位有外单位的时候需要把外发箱归为可投箱体
            danWei = new DanWei();
            danWei = DanWeiUtils.findDanWeiByHangBiaoShi(List_ChuanYue.get(i).getCydxbs());
            if(!Constant.DWLX_NEIBU.equals(danWei.getDanweileixing())){
                List<Box> childrenBoxes = commonHandler.findBoxByJiaHuanXiangBS(box.getHangbiaoshi());
                for (int k = 0; k < childrenBoxes.size(); k++) {
                    if(Constant.BOX_QUDAOXIANG.equals(childrenBoxes.get(k).getJiaohuanxiangyt())){
                        box_list.add(childrenBoxes.get(k));
                    }
                }
            }
        }

        // 根据批分信息组织成开箱子的IP串返回给CE程序
        String[] result = new String[box_list.size() + 2];
        result[0]="3";
        result[1]="";
        StringBuffer SB = null;
        for (int k = 0; k < box_list.size(); k++) {
            SB = new StringBuffer();
            SB.append(box_list.get(k).getIp() + "#1份 ");
            result[k+2] = SB.toString();
        }
        if(box_list.size() == 0){
            return new String[] {"0", "单位不在本箱组", ""};
        }
        return result;
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
        GongWenBarcodeDTO gongwenBarcode=gongWenHandler.parseGongWenBarcode(BarcodeInfo);
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(gongwenBarcode.getTiaoMaBianHao());;
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


    public String gongWenNiBanTouJianMoNiTou(String BarcodeInfo,Box box,LiuZhuan liuZhuan) {
        boolean bool;
        GongWenBarcodeDTO gongwenBarcode=gongWenHandler.parseGongWenBarcode(BarcodeInfo);
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(gongwenBarcode.getTiaoMaBianHao());;
        if(gongWenXuHaos.size() != 1){
            return "公文序号不唯一";
        }
        liuZhuan.setFenshu(1);
        liuZhuan.setJiaohuanxiangbs(box.getHangbiaoshi());
        liuZhuan.setWendanghbs(gongWenXuHaos.get(0).getGongwenhbs());
        liuZhuan.setWendanglx(Constant.WENDANGLX_GONGWEN);
        liuZhuan.setYewulx(gongWenXuHaos.get(0).getYewulx());
        //调用流转数据范围类操作数据库
        bool = wenDangLiuZhuan.processWenDangLiuZhuan(liuZhuan);
        if(bool){
            return "投件成功";
        }else{
            return "投件失败";
        }
    }

    /**
     *
     * <p>【传阅条码扫描交换箱时的业务处理方法】</p>
     * @param BarcodeInfo
     * @param BoxHangBiaoShi
     * @return
     * @return: String[]
     * @author: 【孔令海】
     * @update: [2013-03-04 下午13:13:13] [更改人姓名][变更描述]
     */
    public String[] chuanYueBarCodeHandler(String BarcodeInfo,Box box) {
        GongWenBarcodeDTO gongwenBarcode=gongWenHandler.parseGongWenBarcode(BarcodeInfo);
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(gongwenBarcode.getTiaoMaBianHao());
        if(gongWenXuHaos.size() != 1){//一个条码号只对应GONGWENXUHAO表里一条数据，如果查询出多条说明UUID重复，生成规则有问题
            return new String[] {"0","非法投件，拒绝投箱", ""};
        }
        if(Constant.XUHAOZT_BANJIE.equals(gongWenXuHaos.get(0).getWenjianzt())){//办结
            return new String[] {"0","该传阅单已经传阅完毕，操作被拒绝", ""};
        }else if(Constant.XUHAOZT_YITOU.equals(gongWenXuHaos.get(0).getWenjianzt())){//已投
            return new String[] {"0","非法投件，拒绝投箱", ""};
        }
        // 根据拟办信息组织成开箱子的IP串返回给CE程序
        List<GongWenChuanYue> List_ChuanYue =  gongWenHandler.findGongWenChuanYueInfo(gongWenXuHaos.get(0).getHangbiaoshi(),null,Constant.CHUANYUE_WEITOU);
        if(List_ChuanYue.size() == 0){
            return new String[] {"0","该条码没有传阅信息，拒绝投箱", ""};
        }
        //计算下面数组的长度
        List<Box> box_list = new ArrayList<Box>();
        for (int i = 0; i < List_ChuanYue.size(); i++) {
            List<Box> boxes = commonHandler.findBoxByShiYongDanWeiHbs(List_ChuanYue.get(i).getCydxbs(),box.getHangbiaoshi());
            if(boxes.size()>0){
                box_list.add(boxes.get(0));
            }
        }
        // 根据批分信息组织成开箱子的IP串返回给CE程序
        String[] result = new String[box_list.size() + 2];
        result[0]="3";
        result[1]="";
        StringBuffer SB = null;
        for (int k = 0; k < box_list.size(); k++) {
            SB = new StringBuffer();
            SB.append(box_list.get(k).getIp() + "#1份 ");
            result[k+2] = SB.toString();
        }
        if(box_list.size() == 0){
            return new String[] {"0", "单位不在本箱组", ""};
        }
        return result;
    }

    /**
     *
     * <p>【分送条码扫描普通箱业务处理方法】</p>
     * @param BarcodeInfo
     * @return
     * @return: String[]
     * @author: 【孔令海】
     * @update: [2012-9-28 下午06:15:54] [更改人姓名][变更描述]
     */
    public String[] fenFaBarCodeHandler(String BarcodeInfo, Box box) {
        GongWenBarcodeDTO gongwenBarcode=gongWenHandler.parseGongWenBarcode(BarcodeInfo);
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(gongwenBarcode.getTiaoMaBianHao());
        if(gongWenXuHaos.size() != 1){//一个条码号只对应GONGWENXUHAO表里一条数据，如果查询出多条说明UUID重复，生成规则有问题
            return new String[] {"0","数据错误，拒绝投箱", ""};
        }
        if(Constant.XUHAOZT_YITOU.equals(gongWenXuHaos.get(0).getWenjianzt())){
            return new String[] {"0","文件已投，拒绝操作", ""};
        }
        List<GongWenChuanYue> gongWenChuanYue_list = gongWenHandler.findGongWenChuanYueInfo(gongWenXuHaos.get(0).getHangbiaoshi(),null,null);
        // 根据分发信息组织成开箱子的IP串返回给CE程序
        if(gongWenChuanYue_list.size() != 1){
            return new String[] {"0","数据错误，拒绝投箱", ""};
        }
        //如果文件传阅状态为未投，表示该条码是第一次投箱，第一次投箱为定向投箱
        if(gongWenChuanYue_list.get(0).getChuanyuezt().equals(Constant.CHUANYUE_WEITOU)){
            List<Box> boxes = commonHandler.findBoxByShiYongDanWeiHbs(gongWenChuanYue_list.get(0).getCydxbs(),box.getHangbiaoshi());
            //分发单位不在本箱组
            if(boxes.size() == 0){
                DanWei danWei = DanWeiUtils.findDanWeiByHangBiaoShi(gongWenChuanYue_list.get(0).getCydxbs());
                //当分送单位为外单位的时候开渠道箱
                if(!Constant.DWLX_NEIBU.equals(danWei.getDanweileixing())){
                    return openFunctionBox(box,Constant.BOX_QUDAOXIANG);//开渠道箱
                }
                return new String[]{"0","单位不在本箱组，操作被拒绝",""};
            }
            //交换箱用途  为11时，代表秘书修改了领导为出差状态
            //扫描的时候，用途为11的时候，直接拒绝接下来的操作
            if(boxes.get(0).getJiaohuanxiangyt()==11){
                return new String[] { "0", boxes.get(0).getMingcheng()+"，拒绝投件！", "" };
            }
            return new String[]{"2","",boxes.get(0).getIp()};//组织好的IP串返回给CE程序
        }
        //其他情况，表示该条码已经流转，此时此条码可以投任意箱体
        else{
            //计算下面数组的长度
            List<Box> boxes = new ArrayList<Box>();

            boxes = commonHandler.FINDCHILDRENBOXES(box.getHangbiaoshi() + "");
            String[] IPS = new String[boxes.size()+2];
            IPS[0] = "3";
            IPS[1] = "";
            if(boxes.size()>0){
                for (int i = 0; i < boxes.size(); i++) {
                    IPS[i + 2] = boxes.get(i).getIp();
                }
                return IPS;//组织好的IP串返回给CE程序
            }else{
                return new String[]{"0","该组交换箱尚未启用",""};
            }


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
        boolean bool2;
        GongWenBarcodeDTO gongwenBarcode=gongWenHandler.parseGongWenBarcode(BarcodeInfo);
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(gongwenBarcode.getTiaoMaBianHao());//获取当前投的文档序号DTO
        if(gongWenXuHaos.size() != 1){//一个条码号只对应GONGWENXUHAO表里一条数据，如果查询出多条说明UUID重复，生成规则有问题
            return new String[]{"0","投件失败"};
        }
//		GongWen gongWen = gongWenHandler.findGongWenByXuHaoHBS(gongWenXuHaos.get(0).getHangbiaoshi());
        List<Integer> box_ShiYongDuiXiangs = commonHandler.findBoxShiYongDuiXiangByBoxID(box.getHangbiaoshi());
        if(box_ShiYongDuiXiangs.size() == 0){
            return new String[]{"0","投件失败"};
        }else if(box_ShiYongDuiXiangs.size() == 1){
            List<GongWenChuanYue> list_ChuanYue  = gongWenHandler.findGongWenChuanYueInfo(gongWenXuHaos.get(0).getHangbiaoshi(),null,null);
            if(list_ChuanYue.size() != 1){
                return new String[]{"0","没有分发信息"};
            }
            liuZhuan.setFenshu(list_ChuanYue.get(0).getCyfenshu());
            liuZhuan.setWendanghbs(gongWenXuHaos.get(0).getGongwenhbs());
//			liuZhuan.setGongwenxuhaobs(gongWenXuHaos.get(0).getHangbiaoshi());
            liuZhuan.setWendanglx(Constant.WENDANGLX_GONGWEN);
            liuZhuan.setYewulx(gongWenXuHaos.get(0).getYewulx());
//			liuZhuan.setJijianzt(gongWen.getJinjichengdulx() >= 1 ? 1 : 0);
            bool = wenDangLiuZhuan.processWenDangLiuZhuan(liuZhuan);
            bool2 = gongWenHandler.updateGongWenBeiYong10(liuZhuan.getWendanghbs());
            if(!bool || !bool2)
                return new String[]{"0","投件失败"};
        }else if(box_ShiYongDuiXiangs.size() > 1){
            //假如多个单位使用一个箱子的时候...千年以后待续...
        }

        return new String[]{"1",""};
    }


    public String gongWenFenFaTouJianMoNiTou(String BarcodeInfo, Box box, LiuZhuan liuZhuan) {
        boolean bool;
        boolean bool2;
        GongWenBarcodeDTO gongwenBarcode=gongWenHandler.parseGongWenBarcode(BarcodeInfo);
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(gongwenBarcode.getTiaoMaBianHao());//获取当前投的文档序号DTO
        if(gongWenXuHaos.size() != 1){//一个条码号只对应GONGWENXUHAO表里一条数据，如果查询出多条说明UUID重复，生成规则有问题
            return "公文序号不唯一";
        }
//		GongWen gongWen = gongWenHandler.findGongWenByXuHaoHBS(gongWenXuHaos.get(0).getHangbiaoshi());
        List<Integer> box_ShiYongDuiXiangs = commonHandler.findBoxShiYongDuiXiangByBoxID(box.getHangbiaoshi());
        if(box_ShiYongDuiXiangs.size() == 0){
            return "交换箱使用对象不唯一";
        }else if(box_ShiYongDuiXiangs.size() == 1){
            List<GongWenChuanYue> list_ChuanYue  = gongWenHandler.findGongWenChuanYueInfo(gongWenXuHaos.get(0).getHangbiaoshi(),box_ShiYongDuiXiangs.get(0),null);
            if(list_ChuanYue.size() != 1){
                return "没有分发信息";
            }
            liuZhuan.setFenshu(list_ChuanYue.get(0).getCyfenshu());
            liuZhuan.setWendanghbs(gongWenXuHaos.get(0).getGongwenhbs());
//			liuZhuan.setGongwenxuhaobs(gongWenXuHaos.get(0).getHangbiaoshi());
            liuZhuan.setWendanglx(Constant.WENDANGLX_GONGWEN);
            liuZhuan.setYewulx(gongWenXuHaos.get(0).getYewulx());
            liuZhuan.setTiaomabianhao(gongwenBarcode.getTiaoMaBianHao());
//			liuZhuan.setJijianzt(gongWen.getJinjichengdulx() >= 1 ? 1 : 0);
            bool = wenDangLiuZhuan.processWenDangLiuZhuan(liuZhuan);
            bool2 = gongWenHandler.updateGongWenBeiYong10(liuZhuan.getWendanghbs());
            if(!bool || !bool2)
                return "投件失败";
        }else if(box_ShiYongDuiXiangs.size() > 1){
            //假如多个单位使用一个箱子的时候...千年以后待续...
        }
        return "投件成功";
    }

    /**
     *
     * <p>【信件条码扫描普通箱业务处理方法】</p>
     * @param BarcodeInfo
     * @param BoxHangBiaoShi
     * @return
     * @return: String[]
     * @author: 【孔令海】
     * @update: [2012-9-28 下午06:16:20] [更改人姓名][变更描述]
     */
    public String[] xinJianBarCodeHandler(String BarcodeInfo, Box box) {
        String[] str = xinJian_Validata(BarcodeInfo);//验证该信件状态是否可投箱
        if(str != null)
            return str;
        //取得已登记的信件
        XinFengXinXi xinFengXinXi = xinJianHandler.findXinJianByTiaoMaInfo(BarcodeInfo,null);
        if(xinFengXinXi == null){
            xinFengXinXi = new XinFengXinXi();
            if(BarcodeInfo.length() == 26){
                xinFengXinXi = CommonParseXinJian.parseBarCodeToXinJian(BarcodeInfo);//信件条码转化成信件DTO
            }
            //当收件单位未解析出来的时候
            if(xinFengXinXi.getShoujiandanweibs() == null){
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
        }
        else{
            LiuZhuan isHaveLZ = commonHandler.findLiuZhuanByXinFengBs(xinFengXinXi.getHangbiaoshi(), Constant.WENDANGLX_XINJIAN);
            if(null!=isHaveLZ &&xinFengXinXi.getXinjianzt()!=11 ){
                // 根据批分信息组织成开箱子的IP串返回给CE程序
                List<Box> boxes = commonHandler.FINDCHILDRENBOXES(box.getHangbiaoshi() + "");
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
        }



        //获取在本组控收件单位
        DanWei danWei =new DanWei();
        if(null !=xinFengXinXi&&xinFengXinXi.getXinjianzt()==11){//退信
            danWei = DanWeiUtils.findDanWeiByHangBiaoShi(xinFengXinXi.getFajiandanweibs());
        }else{
            danWei = DanWeiUtils.findDanWeiByHangBiaoShi(xinFengXinXi.getShoujiandanweibs());
        }
        //获取本组控下所有的箱子
        List<Box> childrenBoxes = commonHandler.FINDCHILDRENBOXES(box.getHangbiaoshi() + "");
        //开渠道箱【条件：信件类别是发信、信件状态是未投、收件单位不在厅里】
        if((Constant.XINJIANLX_FAXINGUOBAN.equals(xinFengXinXi.getXinjianlx()) || Constant.XINJIANLX_FAXINYANGQI.equals(xinFengXinXi.getXinjianlx())) && !Constant.DWLX_NEIBU.equals(danWei.getDanweileixing())){
            //退信為11
            if(null !=xinFengXinXi&&xinFengXinXi.getXinjianzt()==11){
                return new String[] {"0","该信件为退信！请重新登记！", ""};
            }
            else{
                for (int i = 0; i < childrenBoxes.size(); i++) {
                    if(Constant.BOX_QUDAOXIANG.equals(childrenBoxes.get(i).getJiaohuanxiangyt())){
                        if(childrenBoxes.get(i).getJiaohuanxiangyt().equals(7)&&danWei.getDanweileixing()==7)
                            return new String[]{"2","",childrenBoxes.get(i).getIp()};
                    }else if(Constant.BOX_GUOBANXIANG.equals(childrenBoxes.get(i).getJiaohuanxiangyt())){
                        if(childrenBoxes.get(i).getJiaohuanxiangyt().equals(9)&&danWei.getDanweileixing()==9||danWei.getDanweileixing()==7)
                            return new String[]{"2","",childrenBoxes.get(i).getIp()};
                    }
                }
            }
        }
        // 根据批分信息组织成开箱子的IP串返回给CE程序
        List<Box> boxes=new ArrayList<Box>();
        if(null !=xinFengXinXi&&xinFengXinXi.getXinjianzt()==11){//退信
            boxes = commonHandler.findBoxByShiYongDanWeiHbs(xinFengXinXi.getFajiandanweibs(),box.getHangbiaoshi());
        }else{
            boxes = commonHandler.findBoxByShiYongDanWeiHbs(xinFengXinXi.getShoujiandanweibs(),box.getHangbiaoshi());
        }
        StringBuffer SB = new StringBuffer();
        if(boxes.size()>0  ){
            if(Constant.BOX_QUDAOXIANG.equals(boxes.get(0).getJiaohuanxiangyt())){
                return new String[] {"0","该单位不收件！请联系机要室！", ""};
            }else{
                SB.append(boxes.get(0).getIp() + "#信件 1 封");
                return new String[]{"2","",SB.toString()};//组织好的IP串返回给CE程序
            }
        }else{
            return new String[] {"0","单位不在本箱组", ""};
        }
    }

    /**
     *
     * <p>[信件条码投普通箱操作]</p>
     * @param BoxHangBiaoShi
     * @param BarcodeInfo
     * @param liuZhuan
     * @return
     * @return: String[]
     * @author: 孔令海
     * @update: [2012-10-8 上午09:57:50] [更改人姓名][变更描述]
     */
    public String[] xinJianInfo(String BoxHangBiaoShi, String BarcodeInfo,LiuZhuan liuZhuan) {
        boolean bool;
        XinFengXinXi xinFengXinXi = xinJianHandler.findXinJianByTiaoMaInfo(BarcodeInfo,null);
        if(xinFengXinXi == null){
            DanWei danwei = new DanWei();
            xinFengXinXi = new XinFengXinXi();
            if(BarcodeInfo.length() == 26){
                xinFengXinXi = CommonParseXinJian.parseBarCodeToXinJian(BarcodeInfo);//信件条码转化成信件DTO
                xinFengXinXi.setXinjianlx(Constant.XINJIANLX_SHOUXINGUOBAN);//收信
            }else if(BarcodeInfo.length() == 17){
                xinFengXinXi = new XinFeng17BarcodeParser().parse17BarcodeDTO(BarcodeInfo);
                xinFengXinXi.setXinjianlx(Constant.XINJIANLX_SHOUXINYANGQI);//收信
                xinFengXinXi.setFajiandanweimc(liuZhuan.getCaozuodwmc());
                xinFengXinXi.setFajiandanweibs(liuZhuan.getCaozuodwbs());
            }else if(BarcodeInfo.length() == 12){ //机要局
                xinFengXinXi = new XinFengXinXi();
                xinFengXinXi.setJiyaohao("机"+BarcodeInfo.substring(0, 4));
                xinFengXinXi.setMimidengjilx(0);
                xinFengXinXi.setMimidengjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("秘密等级", 0));
                xinFengXinXi.setHuanjilx(0);
                xinFengXinXi.setXinfengbianhao("机"+(BarcodeInfo.substring(0, 4)));
                xinFengXinXi.setHuanjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("紧急程度", 0));
                xinFengXinXi.setXinjianlx(Constant.XINJIANLX_SHOUXINJY);//收信
                xinFengXinXi.setXinfengtiaomaxx(BarcodeInfo);
                xinFengXinXi.setFajiandanweimc(liuZhuan.getCaozuodwmc());
                xinFengXinXi.setFajiandanweibs(liuZhuan.getCaozuodwbs());
            }else if(BarcodeInfo.length() == 13){  //邮局
                xinFengXinXi.setXinjianlx(Constant.XINJIANLX_SHOUXINEMS);
                xinFengXinXi.setXinfengbianhao(BarcodeInfo);
                xinFengXinXi.setMimidengjilx(0);
                xinFengXinXi.setMimidengjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("秘密等级", 0));
                xinFengXinXi.setHuanjilx(0);
                xinFengXinXi.setHuanjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("紧急程度", 0));
                xinFengXinXi.setXinfengtiaomaxx(BarcodeInfo);
                xinFengXinXi.setFajiandanweimc(liuZhuan.getCaozuodwmc());
                xinFengXinXi.setFajiandanweibs(liuZhuan.getCaozuodwbs());

            }else
            {
                xinFengXinXi.setXinjianlx(Constant.XINJIANLX_SHOUXINEMS);
                xinFengXinXi.setXinfengbianhao(BarcodeInfo);
                xinFengXinXi.setMimidengjilx(0);
                xinFengXinXi.setMimidengjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("秘密等级", 0));
                xinFengXinXi.setHuanjilx(0);
                xinFengXinXi.setHuanjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("紧急程度", 0));
                xinFengXinXi.setXinfengtiaomaxx(BarcodeInfo);
                xinFengXinXi.setFajiandanweimc(liuZhuan.getCaozuodwmc());
                xinFengXinXi.setFajiandanweibs(liuZhuan.getCaozuodwbs());
            }
            //当收件单位未解析出来的时候[000的时候]

            if(xinFengXinXi.getShoujiandanweibs() == null){
                List<Integer> shoujiandanweihbs = commonHandler.findBoxShiYongDuiXiangByBoxID(Integer.parseInt(BoxHangBiaoShi));
                if(shoujiandanweihbs.size() == 1){
                    danwei = DanWeiUtils.findDanWeiByHangBiaoShi(shoujiandanweihbs.get(0));
                    xinFengXinXi.setShoujiandanweibh(danwei.getDanweibianhao());
                    xinFengXinXi.setShoujiandanweibs(danwei.getHangbiaoshi());
                    xinFengXinXi.setShoujiandanweimc(danwei.getDanweimingcheng());
                }

            }

            xinFengXinXi.setDengjirenbs(liuZhuan.getCaozuorenbs());
            xinFengXinXi.setDengjirenmc(liuZhuan.getCaozuorenmc());
            xinFengXinXi.setXinjianxz(1);

            xinFengXinXi.setXinjianzt(Constant.XINJIANZT_YITOU);

            xinFengXinXi.setDengjiriqi(new Date());
            xinFengXinXi.setXinfengbhlb(1);//信封编号类别 1为单函
            xinFengXinXi.setToudixiangtiyongtu(Constant.BOX_SHOUXINXIANG);//收信箱值是3
            CommonShouXinDao.getInstance().addXinJian(xinFengXinXi);//登记信件
        }
//		else if(null!=xinFengXinXi){
//			if(BarcodeInfo.length() == 26 &&xinFengXinXi.getXinjianzt()==11){//退信
//				return new String[]{"0","投件失败"};
//			}
//		}
        if(xinFengXinXi.getXinjianzt()==11){
            liuZhuan.setBeiyong("11");
        }
        liuZhuan.setFenshu(1);
        liuZhuan.setJiaohuanxiangbs(Integer.parseInt(BoxHangBiaoShi));
        liuZhuan.setWendanghbs(xinFengXinXi.getHangbiaoshi());
        liuZhuan.setWendanglx(Constant.WENDANGLX_XINJIAN);
        liuZhuan.setYewulx(Constant.YWLX_XINJIAN);
//		//只有国办收发信才有缓急
//		if(Constant.XINJIANLX_FAXINGUOBAN.equals(xinFengXinXi.getXinjianlx())||Constant.XINJIANLX_SHOUXINGUOBAN.equals(xinFengXinXi.getXinjianlx())){
//			liuZhuan.setJijianzt(xinFengXinXi.getHuanjilx() >= 2 ? 1 : 0);
//		}
        //调用流转数据范围类操作数据库
        bool = wenDangLiuZhuan.processWenDangLiuZhuan(liuZhuan);
        if(bool)
            return new String[]{"1",""};
        else
            return new String[]{"0","投件失败"};
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
        GongWenBarcodeDTO gongwenBarcode=new GongWenBarcodeParser().parseGongWenBarcode(BarcodeInfo);
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(gongwenBarcode.getTiaoMaBianHao());
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

    public String gongWenChuanYueTouJianMoNiTou(String BarcodeInfo, Box box, LiuZhuan liuZhuan) {
        boolean bool;
        GongWenBarcodeDTO gongwenBarcode=new GongWenBarcodeParser().parseGongWenBarcode(BarcodeInfo);
        List<GongWenXuHao> gongWenXuHaos = gongWenHandler.findGongWenXuHaoByTiaoMa(gongwenBarcode.getTiaoMaBianHao());
        if(gongWenXuHaos.size() != 1){
            return "投件失败";
        }
        liuZhuan.setFenshu(1);
        liuZhuan.setJiaohuanxiangbs(box.getHangbiaoshi());
        liuZhuan.setWendanghbs(gongWenXuHaos.get(0).getGongwenhbs());
        liuZhuan.setWendanglx(Constant.WENDANGLX_GONGWEN);
        liuZhuan.setYewulx(gongWenXuHaos.get(0).getYewulx());
        //调用流转数据范围类操作数据库
        bool = wenDangLiuZhuan.processWenDangLiuZhuan(liuZhuan);
        if(bool){
            return "投件成功";
        }else{
            return "投件失败";
        }
    }

    /**
     *
     * <p>【判断信件现在的状态是否可以投箱】</p>
     * @param BarcodeInfo
     * @return
     * @return: String[]
     * @author: 【孔令海】
     * @update: [2012-9-19 下午12:26:17] [更改人姓名][变更描述]
     */
    private String[] xinJian_Validata(String BarcodeInfo){
        //判断信件是否已投箱
        XinFengXinXi xinJian_validata2 = xinJianHandler.findXinJianByTiaoMaInfo(BarcodeInfo,Constant.XINJIANZT_YITOU);
        if(xinJian_validata2 != null){
            return new String[] { "0", "信件已投箱，操作被拒绝", "" };
        }
        //判断信件是已取件状态
//		XinFengXinXi xinJian_validata3 = xinJianHandler.findXinJianByTiaoMaInfo(BarcodeInfo,Constant.XINJIANZT_YIQU);
//		if(xinJian_validata3 != null){
//			return new String[] { "0", "信件是已取件状态，操作被拒绝", "" };
//		}
        //判断信件是已分拣
//		XinFengXinXi xinJian_validata4 = xinJianHandler.findXinJianByTiaoMaInfo(BarcodeInfo,Constant.XINJIANZT_YIFENJIAN);
//		if(xinJian_validata4 != null){
//			return new String[] { "0", "信件已分拣，操作被拒绝", "" };
//		}
        //取得已登记的信件
        return null;
    }

    /**
     * 开功能箱子
     * @author 孔令海
     * @param box
     * @return
     */
    private String[] openFunctionBox(Box box,Integer boxYongTu) {
        List<Box> childrenBoxes = commonHandler.FINDCHILDRENBOXES(box.getHangbiaoshi() + "");
        for (int i = 0; i < childrenBoxes.size(); i++) {
            if(boxYongTu.equals(childrenBoxes.get(i).getJiaohuanxiangyt())){
                return new String[]{"2","",childrenBoxes.get(i).getIp()};
            }
        }
        return new String[]{"0","单位不在本箱组", ""};
    }

    public String[] OAGongWenBarcodeToBox(String barcodeInfo, Box box, LiuZhuan liuZhuan, GongWen gw) {
        boolean bool;
        boolean bool2;
        liuZhuan.setFenshu(1);
        liuZhuan.setJiaohuanxiangbs(box.getHangbiaoshi());
        liuZhuan.setWendanghbs(gw.getHangbiaoshi());
        liuZhuan.setWendanglx(Constant.WENDANGLX_GONGWEN);
        liuZhuan.setYewulx(Constant.YWLX_NIBAN);
        //调用流转数据范围类操作数据库
        bool = wenDangLiuZhuan.processWenDangLiuZhuan2(liuZhuan);
        bool2 = gongWenHandler.updateGongWenZTYiTou(gw);
        if(bool && bool2){
            return new String[]{"1",""};
        }else{
            return new String[]{"0","投件失败"};
        }
    }


    public String OAGongWenBarcodeToBoxMoNiTouJian(String barcodeInfo, Box box, LiuZhuan liuZhuan, GongWen gw) {
        boolean bool;
        boolean bool2;
        liuZhuan.setFenshu(1);
        liuZhuan.setJiaohuanxiangbs(box.getHangbiaoshi());
        liuZhuan.setWendanghbs(gw.getHangbiaoshi());
        liuZhuan.setWendanglx(Constant.WENDANGLX_GONGWEN);
        liuZhuan.setYewulx(Constant.YWLX_NIBAN);
        liuZhuan.setTiaomabianhao(barcodeInfo);
        //调用流转数据范围类操作数据库
        bool = wenDangLiuZhuan.processWenDangLiuZhuan2(liuZhuan);
        bool2 = gongWenHandler.updateGongWenZTYiTou(gw);
        if(bool && bool2){
            return "投件成功";
        }else{
            return "error";
        }
    }


    /**
     * 信件模拟投箱
     * @param BoxHangBiaoShi
     * @param BarcodeInfo
     * @param liuZhuan
     * @param danweibs
     * @return
     */
    public String xinJianInfoMoNiTouXiang(String BoxHangBiaoShi, String BarcodeInfo,LiuZhuan liuZhuan, Integer danweibs) {
        boolean bool;
        //判断信封表有无此条码
        XinFengXinXi xinFengXinXi = xinJianHandler.findXinJianByTiaoMaInfo(BarcodeInfo,null);
        if(xinFengXinXi == null){
            xinFengXinXi = new XinFengXinXi();
            if(BarcodeInfo.length() == 26){
                xinFengXinXi = CommonParseXinJian.parseBarCodeToXinJian(BarcodeInfo);//信件条码转化成信件DTO
                xinFengXinXi.setXinjianlx(Constant.XINJIANLX_SHOUXINGUOBAN);//收信
            }else if(BarcodeInfo.length() == 17){
                xinFengXinXi = new XinFeng17BarcodeParser().parse17BarcodeDTO(BarcodeInfo);
                xinFengXinXi.setXinjianlx(Constant.XINJIANLX_SHOUXINYANGQI);//收信
            }else if(BarcodeInfo.length() == 12){ //机要局
                xinFengXinXi = new XinFengXinXi();
                xinFengXinXi.setJiyaohao("机"+BarcodeInfo.substring(0, 4));
                xinFengXinXi.setMimidengjilx(0);
                xinFengXinXi.setMimidengjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("秘密等级", 0));
                xinFengXinXi.setHuanjilx(0);
                xinFengXinXi.setXinfengbianhao("机"+(BarcodeInfo.substring(0, 4)));
                xinFengXinXi.setHuanjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("紧急程度", 0));
                xinFengXinXi.setXinjianlx(Constant.XINJIANLX_SHOUXINJY);//收信
                xinFengXinXi.setXinfengtiaomaxx(BarcodeInfo);
            }else if(BarcodeInfo.length() == 13){  //邮局
                xinFengXinXi.setXinjianlx(Constant.XINJIANLX_SHOUXINEMS);
                xinFengXinXi.setXinfengbianhao(BarcodeInfo);
                xinFengXinXi.setMimidengjilx(0);
                xinFengXinXi.setMimidengjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("秘密等级", 0));
                xinFengXinXi.setHuanjilx(0);
                xinFengXinXi.setHuanjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("紧急程度", 0));
                xinFengXinXi.setXinfengtiaomaxx(BarcodeInfo);
            }else
            {
                xinFengXinXi.setXinjianlx(Constant.XINJIANLX_SHOUXINEMS);
                xinFengXinXi.setXinfengbianhao(BarcodeInfo);
                xinFengXinXi.setMimidengjilx(0);
                xinFengXinXi.setMimidengjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("秘密等级", 0));
                xinFengXinXi.setHuanjilx(0);
                xinFengXinXi.setHuanjimc(ToolUtilSystemImpl.getInstance().getShuJuZiDianMingCheng("紧急程度", 0));
                xinFengXinXi.setXinfengtiaomaxx(BarcodeInfo);
            }

            DanWei danwei = new DanWei();
            if(null != danweibs){
                danwei = DanWeiUtils.findDanWeiByHangBiaoShi(danweibs);
                xinFengXinXi.setShoujiandanweibh(danwei.getDanweibianhao());
                xinFengXinXi.setShoujiandanweibs(danwei.getHangbiaoshi());
                xinFengXinXi.setShoujiandanweimc(danwei.getDanweimingcheng());
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
        liuZhuan.setFenshu(1);
        liuZhuan.setJiaohuanxiangbs(Integer.parseInt(BoxHangBiaoShi));
        liuZhuan.setWendanghbs(xinFengXinXi.getHangbiaoshi());
        liuZhuan.setWendanglx(Constant.WENDANGLX_XINJIAN);
        liuZhuan.setYewulx(Constant.YWLX_XINJIAN);
//		//只有国办收发信才有缓急
//		if(Constant.XINJIANLX_FAXINGUOBAN.equals(xinFengXinXi.getXinjianlx())||Constant.XINJIANLX_SHOUXINGUOBAN.equals(xinFengXinXi.getXinjianlx())){
//			liuZhuan.setJijianzt(xinFengXinXi.getHuanjilx() >= 2 ? 1 : 0);
//		}
        //调用流转数据范围类操作数据库
        bool = wenDangLiuZhuan.processWenDangLiuZhuan(liuZhuan);
        if(bool)
            return "投件成功";
        else
            return "投件失败";
    }
}
