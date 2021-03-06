/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cysecurity.cspf.jvl.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author breakthesec
 */
public class AddPage extends HttpServlet {
	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();
		if (session.getAttribute("privilege") != null &&
			session.getAttribute("privilege").equals("admin")
		) {
			response.setContentType("text/html;charset=UTF-8");
			try {
				String fileName = request.getParameter("filename");
				String content = request.getParameter("content");
				if (fileName != null && content != null) {
					String pagesDir = getServletContext().getRealPath("/pages") + "/";
					new File(pagesDir).mkdirs();

					String filePath = new File(pagesDir + fileName).getCanonicalPath();

					if (filePath.startsWith(pagesDir)) {
						File f = new File(filePath);
						if (f.exists()) {
							f.delete();
						}
						if (f.createNewFile()) {
							BufferedWriter bw = new BufferedWriter(
								new FileWriter(f.getAbsoluteFile())
							);
							bw.write(content);
							bw.close();
							out.print(
								"Successfully created the file: <a href='../pages/" +
								fileName +
								"'>" +
								fileName
								+ "</a>"
							);
						} else {
							out.print("Failed to create the file");
						}
					} else {
						// TODO Better error handling
						out.print("premission denied");
					}
				} else {
					out.print("filename or content Parameter is missing");
				}
			} catch (Exception e) {
				out.print(e);
			} finally {
				out.close();
			}
		} else {
			out.print("premission denied");
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
	// + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
