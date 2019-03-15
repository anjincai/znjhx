package com.szzn.danwei.Mapper;

import an.com.entry.DanWei.DanWei;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DanWeiMapper {

    /**
     * 根据单位标识获取单位
     * @param HangBiaoShi
     * @return
     */
    @Select("SELECT HANGBIAOSHI,DANWEIMINGCHENG,DANWEIBIANHAO,DANWEIQUANMA,DANWEILEIXING,P_HANGBIAOSHI,CHENGSHI,CIXU,DIANHUA,DANWEIJIANCHENG,SHIFOUYOUZJD FROM ORG WHERE HANGBIAOSHI=#{HangBiaoShi} ORDER BY CIXU")
    List<DanWei> findDanWeiByHangBiaoShi(@Param("HangBiaoShi") Integer HangBiaoShi);
}
