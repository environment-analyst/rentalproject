package dao;

import static db.JdbcUtil.close;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import vo.MemberVO;
import vo.NtcVo;

public class NtcDAO {

    DataSource ds;
    Connection con;

    PreparedStatement pstmt;
    ResultSet rs;
    int cnt;

    private static NtcVo nvo;

    private static NtcDAO ntcDAO;

    public NtcDAO() {
    }

    public static NtcDAO getInstance() {
        if (ntcDAO == null) {
            ntcDAO = new NtcDAO();
        }
        return ntcDAO;
    }

    public void setConnection(Connection con) {
        this.con = con;
    }

    public int selectListCount() {
        int listCount = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement("select count(*) from NOTICE");
            rs = pstmt.executeQuery();

            if (rs.next()) {
                listCount = rs.getInt(1);
            }
        } catch (Exception ex) {
            System.out.println("getListCount 에러: " + ex);
        } finally {
            close(rs);
            close(pstmt);
        }
        return listCount;
    }

    public JSONArray selectArticleList(int page, int limit) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String list_sql = "select * from NOTICE order by NOTICENUM desc ";
        JSONArray articleList = new JSONArray();
        int startrow = (page - 1) * 10 + 1;
        int endrow = (page - 1) * 10 + 10;

        try {
            pstmt = con.prepareStatement(list_sql);
            rs = pstmt.executeQuery();
            int cnt = 0;
            while (rs.next()) {
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("NOTICENUM", rs.getString("NOTICENUM"));
                jsonobj.put("NOTICENAME", rs.getString("NOTICENAME"));
                jsonobj.put("NOTICETITLE", rs.getString("NOTICETITLE"));
                jsonobj.put("NOTICECONTENT", rs.getString("NOTICECONTENT"));
                jsonobj.put("NOTICEVIEWS", rs.getString("NOTICEVIEWS"));
                jsonobj.put("NOTICEFILE", rs.getString("NOTICEFILE"));
                jsonobj.put("NOTICEDATE", rs.getString("NOTICEDATE"));
                articleList.add(jsonobj);
                cnt++;
            }

        } catch (Exception ex) {
            System.out.println("오라클 데이터 List 에러 : " + ex);
        } finally {
            close(rs);
            close(pstmt);
        }

        return articleList;

    }

    public JSONArray selectConditionedArticleList(String sql) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        JSONArray articleList = new JSONArray();

        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("NOTICENUM", rs.getString("NOTICENUM"));
                jsonobj.put("NOTICENAME", rs.getString("NOTICENAME"));
                jsonobj.put("NOTICETITLE", rs.getString("NOTICETITLE"));
                jsonobj.put("NOTICECONTENT", rs.getString("NOTICECONTENT"));
                jsonobj.put("NOTICEVIEWS", rs.getString("NOTICEVIEWS"));
                jsonobj.put("NOTICEDATE", rs.getString("NOTICEDATE"));
                articleList.add(jsonobj);
            }

        } catch (Exception ex) {
            System.out.println("오라클 데이터 List 에러 : " + ex);
        } finally {
            close(rs);
            close(pstmt);
        }

        return articleList;

    }

    public NtcVo selectArticle(int NOTICENUM) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        NtcVo ntcVo = null;

        try {
            pstmt = con.prepareStatement("select * from NOTICE where NOTICENUM = ?");
            pstmt.setInt(1, NOTICENUM);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                ntcVo = new NtcVo();
                ntcVo.setNOTICENUM(rs.getInt("NOTICENUM"));
                ntcVo.setNOTICENAME(rs.getString("NOTICENAME"));
                ntcVo.setNOTICETITLE(rs.getString("NOTICETITLE"));
                ntcVo.setNOTICECONTENT(rs.getString("NOTICECONTENT"));
                ntcVo.setNOTICEVIEWS(rs.getInt("NOTICEVIEWS"));
                ntcVo.setNOTICEDATE(rs.getString("NOTICEDATE"));
            }
        } catch (Exception ex) {
            System.out.println("뷰 에러 : " + ex);
        } finally {
            close(rs);
            close(pstmt);
        }
        return ntcVo;
    }


    public int insertArticle(NtcVo ntcVo) {
        PreparedStatement pstmt = null;
        int insertCount = 0;

        try {
            String sql = "INSERT INTO NOTICE (NOTICENUM, NOTICENAME, NOTICETITLE, NOTICECONTENT, NOTICEDATE) VALUES((SELECT NVL(MAX(NOTICENUM),0)+1 FROM NOTICE  ),?,?,?, SYSDATE)";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, ntcVo.getNOTICENAME());
            pstmt.setString(2, ntcVo.getNOTICETITLE());
            pstmt.setString(3, ntcVo.getNOTICECONTENT());
            insertCount = pstmt.executeUpdate();

        } catch (Exception ex) {
            System.out.println("등록 에러 : " + ex);
        } finally {
            close(pstmt);
        }

        return insertCount;
    }

    public int upDateArticle(NtcVo ntcVo) {
        PreparedStatement pstmt = null;
        int isUpdateSuccess = 0;

        try {
            String sql = "UPDATE NOTICE SET  NOTICETITLE = ?, NOTICECONTENT = ? WHERE NOTICENUM = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, ntcVo.getNOTICETITLE()); 
            pstmt.setString(2, ntcVo.getNOTICECONTENT()); 
            pstmt.setInt(3, ntcVo.getNOTICENUM());
            isUpdateSuccess = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("글 수정 에러: " + ex);
        } finally {
            close(pstmt);
        }

        return isUpdateSuccess;
    }



    public int deleteArticle(int noticenum) {

        PreparedStatement pstmt = null;
        String delete_sql = "delete from notice where NOTICENUM=?";
        int deleteCount = 0;

        try {
            pstmt = con.prepareStatement(delete_sql);
            pstmt.setInt(1, noticenum);
            deleteCount = pstmt.executeUpdate();

            if (deleteCount > 0) {
            }
        } catch (Exception ex) {
            System.out.println("삭제 에러 : " + ex);
        } finally {
            close(pstmt);
        }

        return deleteCount;

    }

    public int updateReadCount(int NOTICENUM) {

        PreparedStatement pstmt = null;
        int updateCount = 0;
        String sql = "update notice set NOTICEVIEWS = " + "NOTICEVIEWS+1 where NOTICENUM = " + NOTICENUM;

        try {
            pstmt = con.prepareStatement(sql);
            updateCount = pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("조회수 에러 : " + ex);
        } finally {
            close(pstmt);
        }
        return updateCount;
    }

    public boolean isArticleBoardWriter(int NOTICENUM) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String promotion_sql = "select * from notice where NOTICENUM=?";
        boolean isWriter = false;

        try {
            pstmt = con.prepareStatement(promotion_sql);
            pstmt.setInt(1, NOTICENUM);
            rs = pstmt.executeQuery();
            rs.next();

        } catch (SQLException ex) {
            System.out.println("글쓴이 확인 에러 : " + ex);
        } finally {
            close(pstmt);
        }

        return isWriter;

    }

    public ArrayList<HashMap<String, Object>> listMember(String sql) {

        ArrayList<HashMap<String, Object>> memberList = new ArrayList<HashMap<String, Object>>();
        MemberVO member = null;
        try {
            sql = "Select * from NOTICE";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("NoticeNum", rs.getInt("NoticeNum"));
                map.put("NoticeName", rs.getString("NoticeName"));
                map.put("NoticeTitle", rs.getString("NoticeTitle"));
                map.put("NoticeContent", rs.getString("NoticeContent"));
                map.put("NoticeViews", rs.getInt("NoticeViews"));
                map.put("NOTICEDATE", rs.getDate("NOTICEDATE"));

                memberList.add(map);
            }
        } catch (Exception ex) {
            System.out.println("getDetail 에러: " + ex);
        } finally {
            close(rs);
            close(pstmt);
        }
        return memberList;
    }

}
