package com.szzn.szznwebservice.Box.Service;


import com.szzn.szznwebservice.Box.FeignClient.BoxFegin;
import com.szzn.szznwebservice.Box.Service.BoxWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service
@WebService(serviceName = "BoxWebService", // 与接口中指定的name一致
        targetNamespace = "http://Service.Box.szznwebservice.szzn.com/", // 与接口中的命名空间一致,一般是接口的包名倒
        endpointInterface = "com.szzn.szznwebservice.Box.Service.BoxWebService" // 接口地址
)
public class BoxWebServiceImpl implements BoxWebService {

    @Autowired
    private BoxFegin boxFegin;

    public String  SayHello(String mm) {
        return mm;
    }

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
    public String[] InitBoxFromService(String IP) {
        return boxFegin.findBoxesByIP(IP);
    }

    /**
     * <p>
     * [刷卡]
     * </p>
     *
     * @param
     * @return: void
     */
    public String[] VerifyCard(String ip, String CardInfo) {
        return new String[0];
    }

    /**
     *
     * <p>
     * [扫描条码]
     * </p>
     *
     * @param
     * @return
     * @return: String[]
     */
    public String[] CheckBarcode(String BoxHangBiaoShi, String BarcodeInfo, String CardID) {
        return new String[0];
    }
    /**
     *
     * <p>
     * [投件]
     * </p>
     *
     * @param BoxHangBiaoShi
     * @param BarcodeInfo
     * @param CardID
     * @param IsJiJian
     * @return
     * @return: String[]
     */
    public String[] PutFileInBoxs(String BoxHangBiaoShi, String BarcodeInfo, String CardID, String IsJiJian) {
        return new String[0];
    }
    // 箱门状态通知
    public int UpdateDoorState(String BoxHangBiaoShi, String DoorState) {
        return 0;
    }

    /**
     *
     * <p>
     * [获得受控分箱]
     * </p>
     *
     * @param BoxHangBiaoShi
     * @return
     * @return: String[]
     */
    public String[] GetShouKongObjListForBBox(String BoxHangBiaoShi) {
        return boxFegin.GetShouKongObjListForBBox(BoxHangBiaoShi);
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
    public String[] GetBoxInfoByIP(String IP) {
        return boxFegin.GetBoxInfoByIP(IP);
    }

}
