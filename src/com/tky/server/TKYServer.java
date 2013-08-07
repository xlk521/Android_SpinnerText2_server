package com.tky.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.json.JSONException;
import org.json.JSONObject;

public class TKYServer extends HttpServlet {

	private static final long serialVersionUID = 4988624175878648807L;
	private static Connection conn;
	private static Statement st;
	private ServletFileUpload upload;
	private final long MAXSize = 4194304*2L;//4*2MB
	private String filedir=null;
    
	@Override
	public void init(ServletConfig config) throws ServletException {               //��ʼ��
		super.init();
		
		FileItemFactory factory = new DiskFileItemFactory();// Create a factory for disk-based file items
		this.upload = new ServletFileUpload(factory);// Create a new file upload handler
		this.upload.setSizeMax(this.MAXSize);// Set overall request size constraint 4194304
		filedir=config.getServletContext().getRealPath("images");
		System.out.println("filedir="+filedir);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String s = req.getParameter("json");// �õ��ͻ��˷��͵�����
		System.out.println("doPost();"+s);
		if(s==null){
			
		}
		else{
			PrintWriter out = null;
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(s);
				String flag = jsonObject.getString(JSONKey.FLAG);

				System.out.println("doPost();s is null and flag=" + flag);
				if (flag.equals("Welcom")) {
					jsonObject.put(JSONKey.WELCOM_LOG, "�����ɹ�");
				}else if(flag.equals("spinner")){
					System.out.println("spinner");
					searchForSpinner(jsonObject);
				}else{
					System.out.println("flag====error===>"+flag);
				}
				resp.setContentType("text/plain");
				resp.setCharacterEncoding("UTF-8");
				out = resp.getWriter();
				System.out.println("result =" + jsonObject.toString());
				out.write(jsonObject.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (out != null)
				out.close();
		}
	}

	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn =DriverManager.getConnection("jdbc:sqlserver://172.20.99.79:1433;DatabaseName=mms","sa","123456");// ����������������;
			System.out.println("connection create success");
		} catch (Exception e) {
			System.out.println("connection create failed");
		}
		return conn;
	}
	public static JSONObject searchForSpinner(JSONObject jsonObject){
		conn = getConnection();
		try {
			String sql = "select * from photo200 WHERE datatime = ( SELECT max([datatime]) FROM photo200)";
			// ��������ִ�о�̬sql����Statement����
			st = (Statement) conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			String id = null;
			int num;
			int classNum;
			String msg = null;
			if(rs.next()) {
				 id = rs.getString("id");
				 //����ָ��id����Ϣ
				 sql = "select * from photo200 where id = '" + id + "'";
				 ResultSet rs2 = st.executeQuery(sql);
				 //����Ϣ���ݵ�json��䵱��
				 while (rs2.next()){
					 System.out.println("=========================================================================>");
					 id = rs2.getString("id");
					 num = rs2.getInt("num");
					 classNum = rs2.getInt("class");
					 msg = rs2.getString("message");
					 try {
						 jsonObject.put("num", num);
						 jsonObject.put(""+classNum, msg);
					 } catch (JSONException e) {
						e.printStackTrace();
					 }
					 System.out.println("===>  id="+id+"  num="+num+"   classNum="+classNum +"    msg = "+msg);
				 }
			}
			System.out.println("sql==1===?"+jsonObject);
			// �ر����ݿ�����
			conn.close();
			return jsonObject;
		} catch (SQLException e) {
			System.out.println("��������ʧ��" + e.getMessage());
		}
		return null;
	}
}
