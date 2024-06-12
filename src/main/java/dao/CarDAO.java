package dao;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.sql.DataSource;
import vo.CarVO;
import static db.JdbcUtil.*;

public class CarDAO {
	public static CarDAO instance;
	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	DataSource ds;
	private CarDAO() {
		
	}
	public static CarDAO getInstance(){
		if(instance == null){
			instance = new CarDAO();
		}
		return instance;
	}
	public void setConnection(Connection con){
		this.con = con;
	}
	
	public String selectCar(CarVO car){
		String loginId = null;
		String sql="SELECT * FROM CAR";
		
		try{
			pstmt=con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				
			}
		}catch(Exception ex){
			System.out.println(" 에러: " + ex);			
		}finally{
			close(rs);
			close(pstmt);
		}
		
		return loginId;
	}
	
	public int insertCar(CarVO car){
		String sql="INSERT INTO CAR VALUES (?,?,?,?,?,?,?,?,?)";
		
		int insertCount=0;
		
		try{

			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, car.getCarNumber());
			pstmt.setString(2, car.getCarName());
			pstmt.setString(3,car.getCarCompany());
			pstmt.setInt(4, car.getNumber());
			pstmt.setInt(5,car.getCarSeater());
			pstmt.setString(6,car.getCarType());
			pstmt.setInt(7,car.getCarInsuranceAge());
			pstmt.setString(8,car.getCarFuel());
			pstmt.setString(9,car.getCarPrice());
			
			insertCount=pstmt.executeUpdate();
			
		}catch(Exception ex){
			System.out.println("에러: " + ex);			
		}finally{
			close(pstmt);
		}
		
		return insertCount;
	}
	
	
	
}