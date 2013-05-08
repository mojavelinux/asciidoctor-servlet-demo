package org.asciidoctor.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.asciidoctor.bean.AsciidoctorProcessor;

@WebServlet(name = "AsciiDoc Tester", urlPatterns = "/asciidoctor")
public class AsciidoctorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	AsciidoctorProcessor processor;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		writer.write("<!DOCTYPE html>\n<html>\n<body>\n");
		writer.write("<h1>AsciiDoc Tester powered by Asciidoctor</h1>\n");
		writer.write("<form method=\"post\">\n");
		writer.write("<textarea name=\"source\" style=\"width: 600px; height: 400px; display: block;\">");
		writer.write(readFromStream(request.getServletContext()
				.getResourceAsStream("/WEB-INF/sample.ad")));
		writer.write("</textarea>\n");
		writer.write("<input type=\"submit\" name=\"submit\" value=\"Render\">\n");
		writer.write("</form>\n");
		writer.write("</body>\n</html>");
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String source = request.getParameter("source");
		long start = System.currentTimeMillis();
		String result = processor.renderAsDocument(source, request.getServletContext().getRealPath("/include-root"));
		System.out.println("Rendered document in " + (System.currentTimeMillis() - start) + "ms");
		response.setContentType("text/html");
		response.setContentLength(result.length());
		PrintWriter writer = response.getWriter();
		writer.write(result);
		request.setAttribute("source", source);
		request.setAttribute("result", result);
	}

	public String readFromStream(final InputStream is) {
		if (is == null) {
			return "";
		}
		final char[] buffer = new char[1024];
		final StringBuilder out = new StringBuilder();
		try {
			final Reader in = new InputStreamReader(is, "UTF-8");
			try {
				for (;;) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
						break;
					out.append(buffer, 0, rsz);
				}
			} finally {
				in.close();
			}
		} catch (UnsupportedEncodingException ex) {
			/* ... */
		} catch (IOException ex) {
			/* ... */
		}
		return out.toString();
	}

}
