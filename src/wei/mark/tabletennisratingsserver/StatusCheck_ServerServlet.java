package wei.mark.tabletennisratingsserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class StatusCheck_ServerServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String response = null;

		try {
			response = "OK";
		} catch (Exception ex) {
			log(ex.getMessage());
			response = "ERROR";
		}

		resp.setContentType("text/plain");
		resp.getWriter().println(response);
	}
}
