package com.szzn.danwei.Service;

import an.com.entry.DanWei.DanWei;
import com.szzn.danwei.Mapper.DanWeiMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DanWeiService {
    @Autowired
    private DanWeiMapper danWeiMapper;

    /**
     * 根据单位标识获取单位
     * @param danweibs
     * @return
     */
    public DanWei findDanWeiByHangBiaoShi(Integer danweibs){
        List<DanWei> list = danWeiMapper.findDanWeiByHangBiaoShi(danweibs);
        if(list.size() == 1){
            return list.get(0);
        }else{
            throw new TooManyResultsException("Class:【DanWeiUtils.java】;Method:【findDanWeiByHangBiaoShi()】. Discription:【返回多个返回值异常】");
        }
    }
}
