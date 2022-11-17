package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement pstm = null;

		try {
			String sql = "INSERT INTO seller " + "(Name, Email, BirthDate, BaseSalary, DepartmentId) " + "VALUES "
					+ "(?, ?, ?, ?, ?)";

			pstm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

			pstm.setString(1, obj.getName());
			pstm.setString(2, obj.getEmail());
			pstm.setDate(3, new Date(obj.getBirthDate().getTime()));
			pstm.setDouble(4, obj.getBaseSalary());
			pstm.setInt(5, obj.getDepartment().getId());

			int rowsAffected = pstm.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = pstm.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unexpected error! No rows affected!");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(pstm);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement pstm = null;

		try {
			String sql = "UPDATE seller "
					 + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					 + "WHERE Id = ?";

			pstm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

			pstm.setString(1, obj.getName());
			pstm.setString(2, obj.getEmail());
			pstm.setDate(3, new Date(obj.getBirthDate().getTime()));
			pstm.setDouble(4, obj.getBaseSalary());
			pstm.setInt(5, obj.getDepartment().getId());
			pstm.setInt(6, obj.getId());

			pstm.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(pstm);
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement pstm = null;
		
		try {
			String sql = "DELETE FROM seller "
					+ "WHERE Id = ?";
			
			pstm = conn.prepareStatement(sql);
			
			pstm.setInt(1, id);
			
			pstm.executeUpdate();
			
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstm);
		}

	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {

			String sql = "SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id " + "WHERE seller.Id = ?";

			pstm = conn.prepareStatement(sql);

			pstm.setInt(1, id);

			rs = pstm.executeQuery();
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller obj = instantiateSeller(rs, dep);
				return obj;
			}
			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(pstm);
			DB.closeResultSet(rs);
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {

			String sql = "SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id " + "ORDER BY Name";

			pstm = conn.prepareStatement(sql);

			rs = pstm.executeQuery();

			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {

				Department dep = map.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}

				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);
			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(pstm);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {

			String sql = "SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id " + "WHERE DepartmentId = ? " + "ORDER BY Name";

			pstm = conn.prepareStatement(sql);

			pstm.setInt(1, department.getId());

			rs = pstm.executeQuery();

			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {

				Department dep = map.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}

				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);
			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(pstm);
			DB.closeResultSet(rs);
		}
	}

}
