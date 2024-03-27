package dao;

import static db.JdbcUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.sql.DataSource;

import vo.MemberVO;

public class MemberDAO {
	public static MemberDAO instance;
	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	DataSource ds;
	private MemberDAO() {}
	
	public static MemberDAO getInstance(){
		if(instance == null){
			instance = new MemberDAO();
		}
		return instance;
	}
	
	public void setConnection(Connection con){
		this.con = con;
	}
	
	public int insertMember(MemberVO member){
		String sql="INSERT INTO MEMBER VALUES (?,?,?,?,?,?,?,?)";
		int insertCount=0;
		int memberNo= 0;

		try{

			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, memberNo);
			pstmt.setString(2, member.getMemberId());
			pstmt.setString(3, member.getMemberPassword());
			pstmt.setString(4, member.getMemberName());
			pstmt.setString(5, member.getMemberIdNum());
			pstmt.setString(6, member.getMemberEamil());
			pstmt.setInt(7, member.getMemberAge());
			pstmt.setString(8, member.getMemberAddress());
			pstmt.setString(9, member.getMemberStatus());
			
			insertCount=pstmt.executeUpdate();
			
		}catch(Exception ex){
			System.out.println("joinMember 에러: " + ex);			
		}finally{
			close(pstmt);
		}
		
		return insertCount;
	}
	
	public ArrayList<MemberVO> selectMember(){
		String sql="SELECT * FROM MEMBER";
		ArrayList<MemberVO> memberList=new ArrayList<MemberVO>();
		MemberVO member = null;
		try{
			
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			if(rs.next()){
				do{
				member=new MemberVO();
				member.setMemberNo( rs.getInt("MemberNo"));
				member.setMemberId(rs.getString("MemberId"));
				member.setMemberPassword( rs.getString("MemberPassword"));
				member.setMemberName( rs.getString("MemberName"));
				member.setMemberIdNum( rs.getString("MemberIdNum"));
				member.setMemberEamil( rs.getString("MemberEamil"));
				member.setMemberAge(rs.getInt("MemberAge"));
				member.setMemberAddress(rs.getString("MemberAddress"));
				member.setMemberStatus(rs.getString("MemberStatus"));
				memberList.add(member);
				}while(rs.next());
			}
		}catch(Exception ex){
			System.out.println("getDeatilMember 에러: " + ex);			
		}finally{
			close(rs);
			close(pstmt);
		}
		return memberList;
	}

}
