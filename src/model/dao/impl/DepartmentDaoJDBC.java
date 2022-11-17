package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {
	
	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		
		ResultSet rs = null;
		
		 String sql = "INSERT INTO department " 
				 + "(Name) " + "VALUES "
				 + "(?)";
		 
		 try(PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			 
			 pstm.setString(1, obj.getName());
			 
			 int rowsAffected = pstm.executeUpdate();
			 
			 if(rowsAffected > 0) {
				rs = pstm.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
			 }
			  
		 } catch (SQLException e) {
			 throw new DbException(e.getMessage());
		 } finally {
			 
			 DB.closeResultSet(rs);
		 }
	}

	@Override
	public void update(Department obj) {
		
		String sql = "UPDATE department "
				 + "SET Name = ? "
				 + "WHERE Id = ?";
		
		try(PreparedStatement pstm = conn.prepareStatement(sql)) {
			
			pstm.setString(1, obj.getName());
			pstm.setInt(2, obj.getId());
			
			pstm.executeUpdate();
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage()); 
		}
	}

	@Override
	public void deleteById(Integer id) {
		
		String sql = "DELETE FROM department WHERE Id = ?";
		
		try(PreparedStatement pstm = conn.prepareStatement(sql)) {
			
			pstm.setInt(1, id);
			
			pstm.executeUpdate();
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

	@Override
	public Department findById(Integer id) {
		
		ResultSet rs = null;
		
		String sql = "SELECT * FROM department WHERE Id = ?";
		
		try(PreparedStatement pstm = conn.prepareStatement(sql)) {
			
			pstm.setInt(1, id);
			
			rs = pstm.executeQuery();
			
			if(rs.next()) {
				Department dep = instantiateDepartment(rs);
				return dep;
			}		
			return null;
			
		} catch(SQLException e){
			throw new DbException(e.getMessage());
		}
	}

	@Override
	public List<Department> findAll() {
		
		ResultSet rs = null;
		
		 String sql = "SELECT * FROM department";
		 
		 List<Department> list = new ArrayList<>();
		 
		 try(PreparedStatement pstm = conn.prepareStatement(sql)) {
			 
			rs = pstm.executeQuery();
			
			while(rs.next()) {
				Department dep = instantiateDepartment(rs);
				list.add(dep);
			}	
			return list;
			
			 
		 } catch(SQLException e) {
			 throw new DbException(e.getMessage());
		 }
	}
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("Id"));
		dep.setName(rs.getString("Name"));
		return dep;
	}
	
}
