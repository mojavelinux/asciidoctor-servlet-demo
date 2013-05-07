package org.asciidoctor.test.servlet.helper;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter(urlPatterns = "/*")
public class RequestHolderFilter implements Filter {
	public static final ThreadLocal<HttpServletRequest> REQUEST_INSTANCE = new ThreadLocal<HttpServletRequest>();

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(final ServletRequest request,
			final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		REQUEST_INSTANCE.set((HttpServletRequest) request);
		try {
			chain.doFilter(request, response);
		} finally {
			REQUEST_INSTANCE.remove();
		}
	}

	@Override
	public void destroy() {
	}
	
	public static HttpServletRequest currentInstance() {
		return REQUEST_INSTANCE.get();
	}
}