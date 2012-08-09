package com.renren.dp.xlog.web.action;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.renren.dp.xlog.dispatcher.SystemManager;
import com.sso.api.inter.SSOImpl;
import com.sso.api.inter.SSOInterface;

public class DispatcherController extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String forward = "http://dap.d.xiaonei.com";
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	        throws ServletException, IOException{
		String operator=req.getParameter("operator");
		SystemManager sm=new SystemManager();
		boolean result=false;
		if(operator.equals("start")){
			result=sm.start();
		}else if(operator.equals("stop")){
			result=sm.stop();
		}else if(operator.equals("logout")){
			HttpSession session = req.getSession();
			session.removeAttribute("username");
			session.invalidate();
			session = null;
			SSOInterface sso = new SSOImpl();
			sso.ppClearLocalLogin(req, res);
			sso.ppLogout(req, res, forward);
			return ;
		}
		RequestDispatcher dispatcher =null;
		if(result){
			dispatcher = req.getRequestDispatcher("/index.jsp");
		}else{
			String tip=null;
			if(operator.equals("start")){
				tip="启动dispatcher失败!";
			}else if(operator.equals("stop")){
				tip="停止dispatcher失败!";
			}
			dispatcher = req.getRequestDispatcher("/error.jsp?errTip="+tip);
		}

		dispatcher.forward(req, res);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	        throws ServletException, IOException{
		doPost(req,res);
	}
}
