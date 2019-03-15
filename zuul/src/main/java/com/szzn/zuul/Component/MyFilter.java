package com.szzn.zuul.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class MyFilter extends ZuulFilter {

    @Autowired
    private  HttpServletRequest request;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
//        HttpServletRequest request = ctx.getRequest();
        String remoteAddr = request.getRemoteAddr();
        if (!request.getHeader("User-Agent").startsWith("Mozilla"))
            {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(400);
                ctx.getResponse().setCharacterEncoding("UTF-8");
                try {
                    ctx.getResponse().getWriter().write("");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        return null;
    }

}
