<%@page import="MVCBoard.MemberDTO"%>
<%@page import="MVCBoard.MemberDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
//파라미터로 전달된 게시물의 일련번호를 받는다. 
String num = request.getParameter("Idx");   
//DB연결
MemberDAO dao = new MemberDAO();
//게시물의 조회수 증가
//일련번호에 해당하는 게시물 조회
MemberDTO dto = dao.selectView(num);
//자원해제
dao.close();                               
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원제 게시판</title>
<script>
function deletePost() {
    var confirmed = confirm("정말로 삭제하겠습니까?"); 
    if (confirmed) {
        var form = document.writeFrm;      
        form.method = "post";  
        form.action = "DeleteProcess.jsp";  
        form.submit();  
    }
}
</script>
</head>
<body>
<h2>회원제 게시판 - 상세 보기(View)</h2>

<!--  
-->
<form name="writeFrm">
<input type="hid-den" name="num" value="<%= num %>" />  
    <table border="1" width="90%">
        <tr>
            <td>번호</td>
            <td><%= dto.getIdx() %></td>
            <td>작성자 아이디</td>
            <td><%= dto.getId() %></td>
        </tr>
        <tr>
            <td>제목</td>
            <td colspan="3"><%= dto.getTitle() %></td>
        </tr>
        <tr>
            <td>내용</td>
            <td colspan="3" height="100">
	            <!-- 엔터키로 처리된 부분을 <br>태그로 변경해야지만 줄바꿈되어 출력된다. -->
                <%= dto.getContent().replace("\r\n", "<br/>") %>
            </td> 
        </tr>
        <tr>
            <td colspan="4" align="center">
            <%
            /*
            로그인이 된 상태에서, 세션영역에 저장된 아이디가 해당 게시물을 작성한
            아이디와 일치한다면 수정, 삭제 버튼을 보이게 처리한다. 
            즉, 작성자 본인이 해당 게시물을 조회했을때만 수정, 삭제 버튼이 보이게된다. 
            */
            if (session.getAttribute("Id") != null
                && session.getAttribute("Id").toString().equals(dto.getId())) {
            %>
	            <!-- 수정페이지에도 게시물의 일련번호를 파라미터로 전달한다. -->
                <button type="button"
                        onclick="location.href='Edit.jsp?num=<%= dto.getIdx() %>';">
                    수정하기</button>
                <button type="button" onclick="deletePost();">삭제하기</button> 
            <%
            }
            %>
                <button type="button" onclick="location.href='tables.jsp';">
                    목록 보기
                </button>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
