package com.szzn.szznwebservice.Box.FeignClient;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.fileupload.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class FeginTokenInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (null==getHttpServletRequest()){
            return;
        }
        requestTemplate.header("Token",getHeaders(getHttpServletRequest()).get("Token"));
    }

    private HttpServletRequest getHttpServletRequest(){
        try {
            return  ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }
    /**
     * Feign拦截器拦截请求获取Token对应的值
     * @param request
     * @return
     */
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}
