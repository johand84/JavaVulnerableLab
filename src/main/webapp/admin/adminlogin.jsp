<%@page import="org.cysecurity.cspf.jvl.model.HashMe"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.SQLException"%>
<%@page import="org.cysecurity.cspf.jvl.model.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%
	if(request.getParameter("Login")!=null)
	{
		Connection con=new DBConnect().connect(
			getServletContext().getRealPath("/WEB-INF/config.properties")
		);
		String user=request.getParameter("username");
		try
		{
			if(con!=null && !con.isClosed())
			{
				PreparedStatement stmt1 = con.prepareStatement(
					"select salt from users where username=?"
				);
				stmt1.setString(1, user);
				ResultSet rs1 = stmt1.executeQuery();
				if (rs1 != null && rs1.next()) {
					String pass = HashMe.hashMe(
						request.getParameter("password"),
						rs1.getString("salt")
					); // Hashed/salted Password

					PreparedStatement stmt2 = con.prepareStatement(
						"select * from users where username=? and password=? and privilege='admin'"
					);
					stmt2.setString(1, user);
					stmt2.setString(2, pass);
					ResultSet rs2 = stmt2.executeQuery();

					if(rs2 != null && rs2.next()){
						session.setAttribute("isLoggedIn", "1");
						session.setAttribute("userid", rs2.getString("id"));
						session.setAttribute("user", rs2.getString("username"));
						session.setAttribute("avatar", rs2.getString("avatar"));
						session.setAttribute("privilege", rs2.getString("privilege"));

						Cookie privilege=new Cookie("privilege","admin");
						privilege.setPath(request.getContextPath());
						response.addCookie(privilege);

						response.sendRedirect("admin.jsp");
					}
					else
					{
						response.sendRedirect("adminlogin.jsp?err=<span style='color:red'>Username/Password is wrong</span>");
					}
				}
				else {
					response.sendRedirect("adminlogin.jsp?err=<span style='color:red'>Something went wrong</span>");
				}
			}
		}
		catch(SQLException ex)
		{
			response.sendRedirect("adminlogin.jsp?err=<span style='color:red'>Something went wrong</span>");
		}
		catch(Exception e)
		{
			response.sendRedirect("adminlogin.jsp?err="+e);
		}
	}
%>
<%@ include file="/header.jsp" %>
<b>Admin Login Page:</b><br/>
<form action="adminlogin.jsp" method="post">
	<table>
		<tr>
			<td>UserName: </td>
			<td><input type="text" name="username" /></td>
		</tr>
		<tr>
			<td>Password :</td>
			<td><input type="password" name="password"/></td>
		</tr>
		<tr>
			<td><input type="submit" name="Login" value="Login"/></td>
		</tr>
		<tr>
			<td></td>
			<td class="fail">
				<%
					if(request.getParameter("err") != null)
					{
						out.print(request.getParameter("err"));
					}
				%>
			</td>
		</tr>
	</table>
</form>

 <%@ include file="/footer.jsp" %>
