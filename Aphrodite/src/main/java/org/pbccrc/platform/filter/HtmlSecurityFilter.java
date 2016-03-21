package org.pbccrc.platform.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.pbccrc.platform.util.Constant;

public class HtmlSecurityFilter implements Filter {

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		HttpSession session = httpRequest.getSession();
		
		if (byPass(httpRequest)) {
			chain.doFilter(request, response);
		} else {
			if (session.getAttribute(Constant.SESSION_USER_KEY) != null) {
		        chain.doFilter(request, response);
		    } else {
				httpResponse.sendRedirect("login.html");
		    }
		}
		
	}
	
	protected boolean byPass(HttpServletRequest request) {

		String requestURI = request.getServletPath();
		
		if ("/login.html".equals(requestURI)) {
			return true;
		}
		
		return false;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
