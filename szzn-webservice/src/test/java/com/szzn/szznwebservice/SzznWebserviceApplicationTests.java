package com.szzn.szznwebservice;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.namespace.QName;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SzznWebserviceApplicationTests {

    @Test
    public static void main(String[] args) {
        JaxWsDynamicClientFactory clientFactory = JaxWsDynamicClientFactory.newInstance();
        Client client = clientFactory.createClient("http://localhost:8769/webservice/webservice/webservice?wsdl");
        try {
            Object[] result = client.invoke("SayHello","你好");
            System.out.println(result[0]);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("调用webservice成功!");
    }

}
