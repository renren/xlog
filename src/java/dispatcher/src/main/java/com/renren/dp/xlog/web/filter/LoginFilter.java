package com.renren.dp.xlog.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.renren.dp.xlog.config.Configuration;
import com.sso.api.bean.LogonBean;
import com.sso.api.inter.SSOImpl;
import com.sso.api.inter.SSOInterface;
import com.sso.api.service.SSOService;

public class LoginFilter extends HttpServlet implements Filter {
	private static final long serialVersionUID = 1L;

	private static SSOService service = new SSOService();
	private static SSOInterface sso = new SSOImpl();

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		
		req.setCharacterEncoding("UTF-8");
		LogonBean bean=service.getLogonInfo(req, resp);
		if (bean == null || bean.getLogonstatus() == null
				|| !bean.getLogonstatus().equals("1")) {
			bean = sso.ppLogin(req, resp);
			if (bean == null) {
				return;
			}
		}

		String result = sso.isLogin(bean.getLogonname(), bean.getSid());
		if (bean != null && !result.equals("0")) {
			String admin=Configuration.getString("system.administrator","");
			if(admin.contains(bean.getLogonname())){
				chain.doFilter(req, resp);
			}else{
				RequestDispatcher dispatcher = req.getRequestDispatcher("/noPermission.jsp");
				dispatcher.forward(req, resp);
			}
		}
	}
}

