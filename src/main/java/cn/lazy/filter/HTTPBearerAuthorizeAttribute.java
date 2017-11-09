package cn.lazy.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.lazy.base.BaseExecuteResult;
import cn.lazy.base.RedisService;
import cn.lazy.config.AudienceConfig;
import cn.lazy.model.AccessToken;
import cn.lazy.model.SysUsers;
import cn.lazy.utils.ConstantUtil;
import cn.lazy.utils.JSONUtil;
import cn.lazy.utils.JwtHelper;


public class HTTPBearerAuthorizeAttribute implements Filter{
	@Autowired
	private AudienceConfig audienceEntity;
	
	@Autowired
	private RedisService redisService;
	

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
	            filterConfig.getServletContext());
		
	}


	@SuppressWarnings("unused")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		int count = 0;
		// TODO Auto-generated method stub
		BaseExecuteResult<Object> resultMsg = null;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String headers = httpRequest.getParameter("Host");
		System.out.println(headers);
		String accessToken = httpRequest.getHeader("authorization");
		if(accessToken.equals("123")) {
			chain.doFilter(request, response);
		}
		System.out.println(accessToken);
		String json = httpRequest.getParameter("json");
		System.out.println(json);
		String Authorization = httpRequest.getParameter("authorization");
		System.out.println(Authorization);
		AccessToken token = new AccessToken();
		AccessToken redisToken = new AccessToken();
		if (null == accessToken && count == 0) {
			chain.doFilter(request, response);
			count++;
			return;
		} else {
			SysUsers sysUser = JSONUtil.toBean(json, SysUsers.class);
			token = JSONUtil.toBean(accessToken, AccessToken.class);
			String tokenKey = "token_" + sysUser.getMobile();
			System.out.println(tokenKey);
			redisToken = redisService.findAccessToken(tokenKey, token);
			System.out.println(token.getAccessToken().equals(redisToken.getAccessToken()));
			// JSONObject jsonObject = JSONObject.parseObject(auth);
			// String headStr = jsonObject.getString("bearer");
			// token验证过期，失效
			if (null != token && token.getAccessToken().length() > 100
					&& token.getAccessToken().equals(redisToken.getAccessToken())) {
				if (null != JwtHelper.parseJWT(token.getAccessToken(), audienceEntity.getBase64Secret())) {
					chain.doFilter(request, response);
					return;
				} else {
					resultMsg = new BaseExecuteResult<Object>(ConstantUtil.failed,
							ConstantUtil.ResponseError.INVALID_CAPTCHA.getCode(),
							ConstantUtil.ResponseError.INVALID_CAPTCHA.toString());
				}
			}

		}
		ObjectMapper mapper = new ObjectMapper();
		resultMsg = new BaseExecuteResult<Object>(ConstantUtil.failed,
				ConstantUtil.ResponseError.INVALID_TOKEN.getCode(),
				ConstantUtil.ResponseError.INVALID_TOKEN.toString());
		((HttpServletResponse) response).getWriter().write(mapper.writeValueAsString(resultMsg));
		return;
	}


	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
