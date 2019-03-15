package com.szzn.box.Mapper;

import an.com.entry.Box.Box;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@MapperRepository
@
public interface BoxMapper {
    @Select("SELECT * FROM JIAOHUANXIANG WHERE IP=#{IP}")
    List<Box> findBoxByIP(@Param("IP") String IP);

    @Select("SELECT COUNT(*) FROM XIANGCUNXINXI WHERE IP=#{IP}")
    Integer sum_xiangCunNum(@Param("IP") String IP);

    @Select("SELECT * FROM JIAOHUANXIANG WHERE HANGBIAOSHI = #{HANGBIAOSHI}")
    List<Box> findBoxByJiaHuanXiangBS(@Param("HANGBIAOSHI") Integer HANGBIAOSHI);

    @Select("SELECT * FROM JIAOHUANXIANG WHERE ZUKONGHBS = #{HANGBIAOSHI} AND ISZUKONG != 1 ORDER BY FENXIANGHAO")
    List<Box> FINDCHILDRENBOXES(@Param("HANGBIAOSHI") String HANGBIAOSHI);

    @Select("SELECT * FROM JIAOHUANXIANG J LEFT JOIN XIANGTISHIYONGDX X ON J.HANGBIAOSHI=X.JIAOHUANXIANGHBS "
            + "WHERE X.SHIYONGDUIXIANGBS = #{danweibs} "+
            "<isNotNull property=\"zukongbs\" prepend=\"AND\">" +
                "J.ZUKONGHBS = #{zukongbs}" +
            "</isNotNull>")
    List<Box> findBoxByShiYongDanWeiHbs(@Param("danweibs")Integer danweibs,@Param("zukongbs") Integer zukongbs);
}
