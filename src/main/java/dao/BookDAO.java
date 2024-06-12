package dao;

import static db.JdbcUtil.close;

import java.awt.print.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;
import javax.websocket.Session;

import vo.BookVO;
import vo.CarVO;
import vo.MemberVO;

public class BookDAO {

    private static BookDAO instance;
	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	DataSource ds;
	int cnt;
	
    private BookDAO() {}

    public static BookDAO getInstance(){
        if(instance == null){
            instance = new BookDAO();
        }
        return instance;
    }

    public void setConnection(Connection con){
        this.con = con;
    }

 
    public int insertBook(BookVO bookVo, CarVO carVo, MemberVO memberVo) {
        
        int cnt = 0;

        try {
           String sql = "SELECT * FROM member where memberId = ?";
           pstmt = con.prepareStatement(sql);
           pstmt.setString(1, memberVo.getMemberId());
           
           rs = pstmt.executeQuery();
           
           if(rs.next()) {
              int memberNum = rs.getInt("memberNo"); 
              
              sql = "select * from car where carname = ?";
              pstmt = con.prepareStatement(sql);
              pstmt.setString(1, carVo.getCarName());
              
              rs = pstmt.executeQuery();
              
              if(rs.next()) {
                 int carnum =  rs.getInt("carNumber");
                 String status = "예약중";
                 sql = "insert into book values(BOOk_seq.nextval, ?, ? ,? ,?, ?, sysdate, ?, ?, ?)"; 
                 pstmt = con.prepareStatement(sql);
                 pstmt.setInt(1, memberNum);
                 pstmt.setString(2, memberVo.getMemberId());
                 pstmt.setString(3, memberVo.getMemberName());
                 pstmt.setInt(4, carnum);
                 pstmt.setString(5, carVo.getCarName());
                 pstmt.setString(6, bookVo.getBookDate());
                 pstmt.setString(7, bookVo.getReturnDate());
                 pstmt.setString(8, status);                 
                 pstmt.executeUpdate();   
                 
                  String sqlUpdateCar = "UPDATE car SET carstatus = ? WHERE carnumber = ?";
                  PreparedStatement pstmtUpdateCar = con.prepareStatement(sqlUpdateCar);
                  pstmtUpdateCar.setString(1, status);
                  pstmtUpdateCar.setInt(2, carnum);
                  cnt = pstmtUpdateCar.executeUpdate();
                 if(cnt > 0) {
                    
                    return 1;
                 }
              }else {
                 return 0;
              }
           }else{
              cnt = 0;
           }
        }catch(Exception e) {
           e.printStackTrace();
           System.out.println("예약 오류");
        }finally {
        	close(rs);
        	close(pstmt);
        }
        return cnt;
    }
    
    public ArrayList<BookVO> userSelectBook(int bookNum) {
        ArrayList<BookVO> userBookList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM BOOK WHERE BOOKNUM = ? AND BOOKSTATUS = '예약중'";
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, bookNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                BookVO book = new BookVO();
                book.setBookNum(rs.getString("BOOKNUM"));
                book.setBookMember(rs.getInt("BOOKMEMBER"));
                book.setCarNumber(rs.getInt("CARNUMBER"));
                book.setMemberName(rs.getString("MEMBERNAME"));
                book.setToday(rs.getString("TODAY"));
                book.setBookDate(rs.getString("BOOKDATE"));
                book.setReturnDate(rs.getString("RETURNDATE"));
                book.setBookStatus(rs.getString("BOOKSTATUS"));
                userBookList.add(book);
            }
        } catch (SQLException ex) {
            System.out.println("userSelectBook 에러: " + ex);
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userBookList;
    }
    
    public ArrayList<HashMap<String, Object>> myBook(String Id) {
        ArrayList<HashMap<String, Object>> userBookList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT * FROM BOOK WHERE MEMBERID = ?";
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, Id);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("BookNum", rs.getInt("BOOKNUM"));
                map.put("MemberName", rs.getString("MEMBERNAME"));
                map.put("CarName", rs.getString("CARNAME"));
                map.put("ToDay", rs.getString("TODAY"));
                map.put("BookDate", rs.getString("BOOKDATE"));
                map.put("ReturnDate", rs.getString("RETURNDATE"));
                map.put("BookStatus", rs.getString("BOOKSTATUS"));
                userBookList.add(map);
            }
        } catch (SQLException ex) {
            System.out.println("userSelectBook 에러: " + ex);
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userBookList;
    }
    
    public ArrayList<BookVO> AllSelect(Connection con) { // Connection 파라미터 추가
        ArrayList<BookVO> allBookList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT * FROM BOOK";
        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                BookVO book = new BookVO();
                book.setBookNum(rs.getString("BOOKNUM"));
                book.setBookMember(rs.getInt("BOOKMEMBER"));
                book.setCarNumber(rs.getInt("CARNUMBER"));
                book.setMemberName(rs.getString("MEMBERNAME"));
                book.setToday(rs.getString("TODAY"));
                book.setBookDate(rs.getString("BOOKDATE"));
                book.setReturnDate(rs.getString("RETURNDATE"));
                book.setBookStatus(rs.getString("BOOKSTATUS"));
                book.setReturnStatus(rs.getString("RETURNRESULT"));
                allBookList.add(book);
            }
        } catch (SQLException ex) {
            System.out.println("AllSelect 에러: " + ex);
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return allBookList;
    }
    
    public ArrayList<HashMap<String, Object>> bookList(String sql) {

		ArrayList<HashMap<String, Object>> bookList = new ArrayList<HashMap<String, Object>>();
		BookVO book = null;
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("bookNum",rs.getInt("bookNum"));
				map.put("memberNo",rs.getInt("memberNo"));
				map.put("memberName",rs.getString("memberName"));
				map.put("carNumber",rs.getInt("carNumber"));
				map.put("carName",rs.getString("carName"));
				map.put("today",rs.getString("today"));
				map.put("bookDate",rs.getString("bookDate"));
				map.put("returnDate",rs.getString("returnDate"));
				map.put("bookStatus",rs.getString("bookStatus"));
				bookList.add(map);
			}
		} catch (Exception ex) {
			System.out.println("bookList 에러: " + ex);
		} finally {
			close(rs);
			close(pstmt);
		}
		return bookList;
	}
    
    public int bookUpdate(Connection con, BookVO book) throws SQLException {
        PreparedStatement pstmt = null;
        int count = 0;

        try {
            if (book.getMemberName() != null && !book.getMemberName().trim().isEmpty()) {
            	
                String sql1 = "UPDATE BOOK SET MEMBERNAME = ? WHERE BOOKNUM = ?";
                pstmt = con.prepareStatement(sql1);
                pstmt.setString(1, book.getMemberName());
                pstmt.setString(2, book.getBookNum());
                
                count += pstmt.executeUpdate();
                pstmt.close(); 
            }
            if (book.getBookDate() != null && !book.getBookDate().trim().isEmpty()) {
                String sql2 = "UPDATE BOOK SET BOOKDATE = ? WHERE BOOKNUM = ?";
                pstmt = con.prepareStatement(sql2);
                pstmt.setString(1, book.getBookDate());
                pstmt.setString(2, book.getBookNum());
                count += pstmt.executeUpdate();
                pstmt.close(); 
            }
            if (book.getReturnDate() != null && !book.getReturnDate().trim().isEmpty()) {
                String sql3 = "UPDATE BOOK SET RETURNDATE = ? WHERE BOOKNUM = ?";
                pstmt = con.prepareStatement(sql3);
                pstmt.setString(1, book.getReturnDate());
                pstmt.setString(2, book.getBookNum());
                count += pstmt.executeUpdate();
                pstmt.close(); 
            }
        } finally {
            if (pstmt != null) {
                pstmt.close(); 
            }
        }
        return count; 
    }
   
    public int cancel(BookVO book) {
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int updateCount = 0;

        try {
            String selectSql = "SELECT * FROM BOOK WHERE BOOKNUM = ?";
            pstmtSelect = con.prepareStatement(selectSql);
            pstmtSelect.setString(1, book.getBookNum());
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                String carNumber = rs.getString("CARNUMBER");

                String sql = "UPDATE CAR SET CARSTATUS = '예약가능' WHERE CARNUMBER = ?";
                pstmtUpdate = con.prepareStatement(sql);
                pstmtUpdate.setString(1, carNumber);
                updateCount = pstmtUpdate.executeUpdate();
                
                if(updateCount > 0) {
                        sql = "DELETE BOOK WHERE BOOKNUM = ?";
                        pstmt = con.prepareStatement(sql);
                        pstmt.setString(1, book.getBookNum());
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("cancel 에러: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtSelect != null) pstmtSelect.close();
                if (pstmtUpdate != null) pstmtUpdate.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return updateCount;
    }
    public int Delete(BookVO book) {
    	PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int updateCount = 0;

        try {
            String selectSql = "SELECT * FROM BOOK WHERE BOOKNUM = ?";
            pstmtSelect = con.prepareStatement(selectSql);
            pstmtSelect.setString(1, book.getBookNum());
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                String carNumber = rs.getString("CARNUMBER");

                String sql = "UPDATE CAR SET CARSTATUS = '예약가능' WHERE CARNUMBER = ?";
                pstmtUpdate = con.prepareStatement(sql);
                pstmtUpdate.setString(1, carNumber);
                updateCount = pstmtUpdate.executeUpdate();
                
                if(updateCount > 0) {
                        sql = "DELETE BOOK WHERE BOOKNUM = ?";
                        pstmt = con.prepareStatement(sql);
                        pstmt.setString(1, book.getBookNum());
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("cancel 에러: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtSelect != null) pstmtSelect.close();
                if (pstmtUpdate != null) pstmtUpdate.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return updateCount;
    }
    public boolean LoginBook(BookVO book, String memberId) {
        boolean success = false;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM BOOK WHERE BOOKNUM = ? AND MEMBERID = ? ";

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, book.getBookNum());
            pstmt.setString(2, memberId);
            rs = pstmt.executeQuery();

            if (rs.next()) { 
                success = true;
            } 
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace(); 
            }
        }

        return success;
    }
}