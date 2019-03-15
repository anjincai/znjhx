package com.szzn.szznwebservice.Box.FeignClient.Hystric;

import com.szzn.szznwebservice.Box.FeignClient.BoxFegin;
import org.springframework.stereotype.Component;

@Component
public class BoxHystric  implements BoxFegin {

    @Override
    public String[] findBoxesByIP(String IP) {
        return new String[0];
    }

    @Override
    public String[] GetShouKongObjListForBBox(String hangbiaoshi) {
        return new String[0];
    }

    @Override
    public String[] GetBoxInfoByIP(String IP) {
        return new String[0];
    }

    @Override
    public String[] CheckBarcode(String BoxHangBiaoShi, String BarcodeInfo, String CardID) {
        return new String[0];
    }
}
