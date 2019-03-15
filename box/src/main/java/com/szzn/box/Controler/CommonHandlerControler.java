package com.szzn.box.Controler;

import an.com.entry.Box.Box;
import com.szzn.box.Service.CommonHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/box")
public class CommonHandlerControler {

    @Autowired
    private CommonHandlerService commonHandlerService;

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
    @GetMapping("/CS/Ip")
    public  String[] findBoxesByIP(String IP){
        System.out.println(IP);
        String[] boxInfo = new String[12];
        List<Box> boxes=commonHandlerService.findBoxByIP(IP);
        if(boxes.size()==0){
            return new String[]{};
        }
        boxInfo[0] = boxes.get(0).getGongzuozt() + "";
        boxInfo[1] = boxes.get(0).getMingcheng();
        boxInfo[2] = boxes.get(0).getHangbiaoshi() + "";
        Integer num = commonHandlerService.sum_xiangCunNum(boxes.get(0).getIp());//获取箱存里最新的文件总数量作为计数器的最新数据
        boxInfo[3] = num + "";
        boxInfo[4] = boxes.get(0).getGuzhangdengzt() + "";
        boxInfo[5] = boxes.get(0).getJijiandengzt() + "";
        boxInfo[6] = boxes.get(0).getTouqudengzt() + "";
        boxInfo[7] = boxes.get(0).getXiangtilx() == 1 ? "B":"A";
        boxInfo[8] = boxes.get(0).getIszukong() + "";
        boxInfo[9] = "8";
        if(boxes.get(0).getXiangtilx() == 1 && boxes.get(0).getIszukong() == 0){//B箱
            List<Box> list =commonHandlerService.findBoxByJiaHuanXiangBS( boxes.get(0).getZukonghbs());
            boxInfo[10] = list.get(0).getIp() + "";
        }else if(boxes.get(0).getXiangtilx() == 2){//A箱
            boxInfo[10] = boxes.get(0).getIp();
        }else if(boxes.get(0).getXiangtilx() == 1 && boxes.get(0).getIszukong() == 1){//B11组控
            boxInfo[10] = boxes.get(0).getIp() + "";
        }
        if(boxes.get(0).getXiangtilx() == 1){
            boxInfo[11] = "4";
        }else if(boxes.get(0).getXiangtilx() == 2){
            boxInfo[11] = "12";
        }
        return boxInfo;
    }

    /**
     *
     * <p>
     * [获得受控分箱]
     * </p>
     *
     * @param boxhangbiaoshi
     * @return
     * @return: String[]
     */
    @GetMapping("/CS/hangbiaoshi")
    public  String[] GetShouKongObjListForBBox(String boxhangbiaoshi){
        List<Box> list = commonHandlerService.FINDCHILDRENBOXES(boxhangbiaoshi);
        if(list.size() == 0){
            return new String[]{};
        }
        String[] boxInfo = new String[list.size()];
        StringBuffer SB = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            SB.append(list.get(i).getMingcheng() == null ? "" : list.get(i).getMingcheng());
            SB.append("#" + (list.get(i).getIp() == null ? "" : list.get(i).getIp()));
            SB.append("#" + (list.get(i).getFenxianghao() == null ? "" : list.get(i).getFenxianghao()));
            SB.append("#" + (list.get(i).getJiaohuanxiangyt() == null ? "" : list.get(i).getJiaohuanxiangyt()));
            boxInfo[i] = SB.toString();
            SB = new StringBuffer();
        }
        return boxInfo;
    }

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
    @GetMapping("/CS/BOX/IP")
    public  String[] GetBoxInfoByIP(String IP){
        List<Box> boxes = commonHandlerService.findBoxByIP(IP);
        if(boxes.size()==0){
            return new String[]{"","","","","","",""};
        }
        String[] boxInfo = new String[7];
        boxInfo[0] = boxes.get(0).getMingcheng();
        boxInfo[1] = boxes.get(0).getHangbiaoshi() + "";
        Integer num = commonHandlerService.sum_xiangCunNum(boxes.get(0).getIp());//获取箱存里最新的文件总数量作为计数器的最新数据
        boxInfo[2] = num + "";
        boxInfo[3] = boxes.get(0).getGuzhangdengzt() + "";
        boxInfo[4] = boxes.get(0).getJijiandengzt() + "";
        boxInfo[5] = boxes.get(0).getTouqudengzt() + "";
        boxInfo[6] = boxes.get(0).getJiaohuanxiangyt() + "";
        return boxInfo;
    }
}
