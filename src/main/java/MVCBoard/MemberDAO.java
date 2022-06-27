package MVCBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;

import common.DBConnPool;

public class MemberDAO extends DBConnPool {

	// 생성자의 super()을 통해 커넥션풀을 활성화 시켜줍시다
	public MemberDAO() {
		super();
	}

	// 로그인
	public int memberInsert(MemberDTO dao) {
		int result = 0;

		try {
			String sql = " insert into register values " + "(seq_board_num.nextval, ?, ?, ?, ?) ";

			// query문 확인

			psmt = con.prepareStatement(sql);
			psmt.setString(1, dao.getFirst_name());
			psmt.setString(2, dao.getLast_name());
			psmt.setString(3, dao.getId());
			psmt.setString(4, dao.getPass());

			result = psmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("회원가입에러" + e.getMessage());
		}
		return result;
	}

	// 회원정보(추후에 로그인 및 아이디 체크등 정보확인용으로 작업할겁니다)
	public MemberDTO memberSelect(String id, String pw) {
		MemberDTO dto = new MemberDTO(); // 객체생성
		try {
			System.out.println("id:" + id);
			System.out.println("pw:" + pw);

			if (pw.equals("")) {
				// 아이디가 맞는지 확인용
				String sql = "select * from register where id = ?";

				psmt = con.prepareStatement(sql);
				psmt.setString(1, id);
				rs = psmt.executeQuery();

				// rs.next(); // 해당결과는 한행뿐이기 때문에 if문을 쓰지않음
				if (rs.next()) {

					dto.setId(rs.getString(1));
					dto.setPass(rs.getString(2));
					dto.setFirst_name(rs.getString(3));
					dto.setLast_name(rs.getString(4));

				}

			} else {
				// 아이디와 비번이 맞는지 확인용
				String sql = "select * from register where id = ? and pass = ?";

				psmt = con.prepareStatement(sql);
				psmt.setString(1, id);
				psmt.setString(2, pw);
				rs = psmt.executeQuery();

				// rs.next(); // 해당결과는 한행뿐이기 때문에 if문을 쓰지않음
				if (rs.next()) {

					dto.setId(rs.getString(1));
					dto.setPass(rs.getString(2));
					dto.setFirst_name(rs.getString(3));
					dto.setLast_name(rs.getString(4));

				}
			}

		} catch (Exception e) {
			System.out.println("로그인에러" + e.getMessage());
			e.getStackTrace();

		}

		return dto;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////

	public int selectCount(Map<String, Object> map) {
		int totalCount = 0;
		String query = "SELECT COUNT(*) FROM title";
		if (map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField") + " " + " LIKE '%" + map.get("searchWord") + "%'";
		}
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			rs.next();
			totalCount = rs.getInt(1);
		} catch (Exception e) {
			System.out.println("게시물 수를 구하는 중 예외 발생");
			e.printStackTrace();
		}
		return totalCount;
	}

	public List<MemberDTO> selectList(Map<String, Object> map) {

		/*
		 * select한 게시물의 목록은 다수의 레코드가 포함되므로 이를 저장하기 위해 순서를 보장하는 List계열의 컬렉션이 필요하다.
		 * Set컬렉션은 순서를 보장하지 못하므로 게시판 목록을 출력하는 용도로는 사용할 수 없다.
		 */
		// List<BoardDTO> bbs = new Vector<BoardDTO>();
		List<MemberDTO> bbs = new ArrayList<MemberDTO>();

		/*
		 * 목록에 출력할 게시물을 추출하기 위한 쿼리문으로 항상 일련번호(작성순)의 역순(내림차순)으로 정렬해야 한다. 게시판의 목록은 최근 게시물이
		 * 제일 위쪽에 노출되기 때문이다.
		 */
		String query = "SELECT * FROM title ";
		if (map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField") + " " + " LIKE '%" + map.get("searchWord") + "%' ";
		}
		query += " ORDER BY idx DESC ";

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			// 추출된 결과에 따라 갯수만큼 반복한다.
			while (rs.next()) {
				// 하나의 레코드를 읽어서 추출한 후 DTO객체에 저장한다.
				// DTO객체를 생성한 후..
				MemberDTO dto = new MemberDTO();
				// 각 멤버변수에 해당하는 컬럼을 매칭하여 데이터를 저장한다.
				dto.setIdx(rs.getString("Idx"));
				dto.setId(rs.getString("id"));
				dto.setTitle(rs.getString("title"));
				dto.setContent(rs.getString("content"));

				// 하나의 레코드를 DTO에 저장한 후 List컬렉션에 추가한다.
				bbs.add(dto);
			}
		} catch (Exception e) {
			System.out.println("게시물 조회 중 예외 발생");
			e.printStackTrace();
		}

		// List.jsp로 컬렉션을 반환한다.
		return bbs;
	}

	/*
	 * 게시판 만들때바다 테이블명 오더바이 변경 해줘야함
	 */
	public List<MemberDTO> selectListPage(Map<String, Object> map) {
		List<MemberDTO> board = new Vector<MemberDTO>();

		String query = " SELECT * FROM ( " + "    SELECT Tb.*, ROWNUM rNum FROM ( " + "        SELECT * FROM title ";
		if (map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField") + " LIKE '%" + map.get("searchWord") + "%' ";
		}
		query += "      ORDER BY idx DESC " + "     ) Tb " + " ) " + " WHERE rNum BETWEEN ? AND ?";

		try {
			psmt = con.prepareStatement(query);
			psmt.setString(1, map.get("start").toString());
			psmt.setString(2, map.get("end").toString());
			rs = psmt.executeQuery();

			while (rs.next()) {
				MemberDTO dto = new MemberDTO();
				// 테이블 변경되었으므로 저장하는 부분은 수정이 필요함

				dto.setIdx(rs.getString(1));
				dto.setId(rs.getString(2));
				dto.setTitle(rs.getString(3));
				dto.setContent(rs.getString(4));

				board.add(dto);
			}
		} catch (Exception e) {
			System.out.println("게시물 조회 중 예외 발생");
			e.printStackTrace();
		}

		return board;
	}

	public MemberDTO selectView(String idx) {
		// 조회된 레코드를 DTO객체 저장한 후 반환한다.
		MemberDTO dto = new MemberDTO();
		// 인파라미터가 있는 퀴리문 작성
		String query = " SELECT * FROM title WHERE idx=? ";
		try {
			// 쿼리실행을 위한 객체생성 및 인파라미터 설정
			psmt = con.prepareStatement(query);
			psmt.setString(1, idx);
			rs = psmt.executeQuery();

			// 조회된 레코드가 있을때 DTO 객체에 각 컬럼값을 저장한다.
			if (rs.next()) {
				dto.setIdx(rs.getString(1));
				dto.setId(rs.getString(2));
				dto.setTitle(rs.getString(3));
				dto.setContent(rs.getString(4));
			}
		} catch (Exception e) {
			System.out.println("게시물 상세보기 중 예외 발생");
			e.printStackTrace();
		}
		return dto;
	}

}