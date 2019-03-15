package com.szzn.box.Service;

import an.com.entry.Box.Box;
import com.szzn.box.Mapper.BoxMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonHandlerService {
    @Autowired
    private BoxMapper boxMapper;

    /**
     * 根据IP获取整组箱子
     * @param IP
     * @return
     */
    public List<Box> findBoxByIP(String IP){
        List<Box> boxList =new ArrayList<Box>();
        boxList=boxMapper.findBoxByIP(IP);
        return  boxList;
    }

    /**
     * 根据IP获取箱存数量
     * @param IP
     * @return
     */
    public  Integer sum_xiangCunNum(String IP){
        Integer num=boxMapper.sum_xiangCunNum(IP);
        return  num;
    }

    /**
     * 根据航标识获取整组箱子
     * @param hangbiaoshi
     * @return
     */
    public  List<Box> findBoxByJiaHuanXiangBS(Integer hangbiaoshi){
        List<Box>  boxList=boxMapper.findBoxByJiaHuanXiangBS(hangbiaoshi);
        return  boxList;
    }

    /**
     * 根据航标识获取箱子
     * @param hangbiaoshi
     * @return
     */
    public  Box  findBoxByHbs(Integer hangbiaoshi){
        List<Box> boxList=boxMapper.findBoxByJiaHuanXiangBS(hangbiaoshi);
        if(boxList.size()>0){
            return boxList.get(0);
        }
        return null;
    }

    /**
     * 根据航标识获得该组控所控制的所有交换箱
     * @param hangBiaoShi
     * @return
     */
    public  List<Box> FINDCHILDRENBOXES(String hangBiaoShi){
        List<Box> boxList=boxMapper.FINDCHILDRENBOXES(hangBiaoShi);
        return  boxList;
    }

    public  List<Box> findBoxByShiYongDanWeiHbs(Integer chuanyuedxbs,Integer boxbs){
        List<Box> boxList=boxMapper.findBoxByShiYongDanWeiHbs(chuanyuedxbs,boxbs);
        return  boxList;
    }
}
