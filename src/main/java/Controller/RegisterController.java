package Controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import MVCBoard.MemberDTO;
import MVCBoard.MemberDAO;

import utils.JSFunction;

public class RegisterController extends HttpServlet {
	
	//여기서  doGet, doPost를 만들겁니다
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			req.getRequestDispatcher("/01sangwon/main/register.jsp").forward(req, resp);
		}
		
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			//req.getRequestDispatcher("/member/Register.jsp").forward(req, resp);
			
			/*
			회원가입 전송 부분 
			*/
			
			
			String First_name = req.getParameter("First_name");
			String Last_name = req.getParameter("Last_name");
			String Id = req.getParameter("Id");
			String Pass = req.getParameter("Pass");
			
			//megister_date은 기본으로 sysdate를 지정하였으므로 할필요없습니다
			
			MemberDAO dao = new MemberDAO();
			MemberDTO dto = new MemberDTO();
			
			dto.setFirst_name(First_name);
			dto.setLast_name(Last_name);
			dto.setId(Id);
			dto.setPass(Pass);
			
			int joinResult = dao.memberInsert(dto);
			dao.close(); //반납
			
			if (joinResult == 1) {

				JSFunction.alertLocation(resp, "회원가입성공", "../../01sangwon/main/main.do");
			} else {
				
				JSFunction.alertBack(resp, "회원가입실패");

			}
		}
	}

