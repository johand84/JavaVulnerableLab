<%@ include file="/header.jsp" %>
<%
	if(session.getAttribute("privilege") != null &&
		session.getAttribute("privilege").equals("admin")
	)
	{
		response.sendRedirect("admin.jsp");
	}
	else
	{
		out.print("<b class='fail'> x You Are not Authorized to view this Page x </b>");
	}
%>
<%@ include file="/footer.jsp" %>