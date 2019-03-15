package com.szzn.szznwebservice.Box.Service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface BoxWebService {

    @WebMethod
    String  SayHello(String mm);

    @WebMethod
    String[] InitBoxFromService(String IP);

    @WebMethod
    String[] VerifyCard(String ip, String CardInfo);

    @WebMethod
    String[] CheckBarcode(String BoxHangBiaoShi, String BarcodeInfo, String CardID);

    @WebMethod
    String[] PutFileInBoxs(String BoxHangBiaoShi, String BarcodeInfo, String CardID, String IsJiJian);

    @WebMethod
    int UpdateDoorState(String BoxHangBiaoShi, String DoorState);

    @WebMethod
    String[] GetShouKongObjListForBBox(String BoxHangBiaoShi);

    @WebMethod
    String[] GetBoxInfoByIP(String IP);
}
