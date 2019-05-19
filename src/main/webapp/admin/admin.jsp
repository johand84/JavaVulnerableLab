<%@ include file="/header.jsp" %>
<%
	if(session.getAttribute("privilege") != null &&
		session.getAttribute("privilege").equals("admin")
	)
	{
%>

Welcome to the Admin Panel<br/><br/>
<ul>
	<li><b><a href='manageusers.jsp'>Manage Users </a></b></li>
	<li><b><a href='AddPage.jsp'>Add Page </a></b></li>
	<li><b><a href='Configure.jsp'>Change Configuration </a></b></li>
</ul>
<%
	}
	else
	{
		out.print("<b class='fail'> x You Are not Authorized to view this Page x </b>");
	}
%>
<%@ include file="/footer.jsp" %>
