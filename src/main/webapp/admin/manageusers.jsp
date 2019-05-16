 <%@ include file="/header.jsp" %>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.SQLException"%>
<%@page import="org.cysecurity.cspf.jvl.model.DBConnect"%>
<%@page import="java.sql.Connection"%>

<%
 if(session.getAttribute("privilege")!=null && session.getAttribute("privilege").equals("admin"))
{

	   Connection con=new DBConnect().connect(getServletContext().getRealPath("/WEB-INF/config.properties"));
	 if(request.getParameter("delete")!=null)
	 {
		 PreparedStatement stmt = con.prepareStatement(
		     "Delete from users where username=?"
		 );
		 String user=request.getParameter("user");
		 stmt.setString(1, user);
		 stmt.executeUpdate();
	 }
	 %>
	<form action="manageusers.jsp" method="POST">
	<%
	 ResultSet rs=con.createStatement().executeQuery("select * from users where privilege='user'");
	 while(rs.next())
	 {
		 out.print("<input type='radio' name='user' value='"+rs.getString("username")+"'/> "+rs.getString("username")+"<br/>");
	 }
	 %>
	<br/>
	<input type="submit" value="Delete" name="delete"/>

	</form>
	<br/>
	<a href="admin.jsp"> Back to Admin Panel</a>
	<%
}
else
{
out.print("<b class='fail'> x You Are not Authorized to view this Page x </b>");
}
%>
 <%@ include file="/footer.jsp" %>
