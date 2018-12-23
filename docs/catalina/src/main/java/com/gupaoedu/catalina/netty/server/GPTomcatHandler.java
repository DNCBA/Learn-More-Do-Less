package com.gupaoedu.catalina.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.core.common.config.CustomConfig;

import org.apache.log4j.Logger;

import com.gupaoedu.catalina.http.GPRequest;
import com.gupaoedu.catalina.http.GPResponse;
import com.gupaoedu.catalina.http.GPServlet;

public class GPTomcatHandler extends ChannelInboundHandlerAdapter {

	private Logger LOG = Logger.getLogger(GPTomcatHandler.class);
	
    private static final Map<Pattern,Class<?>> servletMapping = new HashMap<Pattern,Class<?>>();
    
    static{
    	
    		CustomConfig.load("web.properties");
    	
    		for (String key : CustomConfig.getKeys()) {
			if(key.startsWith("servlet")){
				String name = key.replaceFirst("servlet.", "");
				if(name.indexOf(".") != -1){
					name = name.substring(0,name.indexOf("."));
				}else{
					continue;
				}
				String pattern = CustomConfig.getString("servlet." + name + ".urlPattern");
				pattern = pattern.replaceAll("\\*", ".*");
				String className = CustomConfig.getString("servlet." + name + ".className");
				if(!servletMapping.containsKey(pattern)){
					try {
						servletMapping.put(Pattern.compile(pattern), Class.forName(className));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
    }
  
    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
        if (msg instanceof HttpRequest) {  
        		HttpRequest r = (HttpRequest) msg;
        		GPRequest request = new GPRequest(ctx,r);
        		GPResponse response = new GPResponse(ctx,r);
            String uri = request.getUri();
            String method = request.getMethod();
  
            LOG.info(String.format("Uri:%s method %s", uri, method));  
  
            boolean hasPattern = false;
            for (Entry<Pattern, Class<?>> entry : servletMapping.entrySet()) {
            		if (entry.getKey().matcher(uri).matches()) {  
	            		GPServlet servlet = (GPServlet)entry.getValue().newInstance();
	            		if("get".equalsIgnoreCase(method)){
	            			servlet.doGet(request, response);
	            		}else{
	            			servlet.doPost(request, response);
	            		}
	            		hasPattern = true;
                }
			}
            
            if(!hasPattern){  
                String out = String.format("404 NotFound URL%s for method %s", uri,method);  
                response.write(out,404);  
                return;  
            }  
        }  
    }  
  
    @Override  
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {  
        ctx.flush();  
    }  
  
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {  
        cause.printStackTrace();
        ctx.close();  
    }  
}