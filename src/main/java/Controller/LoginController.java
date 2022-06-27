package Controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import MVCBoard.MemberDAO;
import MVCBoard.MemberDTO;

import common.JDBConnect;
import utils.JSFunction;

public class LoginController extends HttpServlet {

	// 여기서 doGet, doPost를 만들겁니다
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/01sangwon/main/login.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// 아이디와 비번을 가져옵니다.
		String Id = req.getParameter("Id");
		String Pass = req.getParameter("Pass");

		// DAO객체를 생성합니다
		MemberDAO mDao = new MemberDAO();

		// DAO의 memberSelect()의 결과값을 DTO에 저장합니다

		MemberDTO dto = mDao.memberSelect(Id, Pass);
		// System.out.println(dto.getUser_name());

		mDao.close(); // 반납

		// 회원정보를 찾으면...
		if (dto.getId() != null) {
			// 세션생셩
			HttpSession session = req.getSession();
			session.setAttribute("ID", Id);
			session.setAttribute("NAME", dto.getFirst_name());

			JSFunction.alertLocation(resp, "로그인성공", "../../01sangwon/main/main.do");
		} else {
			JSFunction.alertBack(resp, "맞는 회원정보가 없습니다");
		}
	}
}