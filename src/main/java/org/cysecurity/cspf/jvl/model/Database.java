package org.cysecurity.cspf.jvl.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
	private final Connection con;

	public Database(Connection con) {
		this.con = con;
	}

	public void insertUser(
		String username,
		String password,
		String email,
		String about,
		String avatar,
		boolean isAdmin,
		String secret
	)
		throws SQLException, ClassNotFoundException, IOException
	{
		PreparedStatement stm = con.prepareStatement(
			"INSERT INTO users" +
			"(" +
				"username," +
				"password," +
				"salt," +
				"email," +
				"About," +
				"avatar," +
				"privilege," +
				"secretquestion," +
				"secret" +
			")" +
			"VALUES" +
			"(" +
				"?," +
				"?," +
				"?," +
				"?," +
				"?," +
				"?," +
				"?," +
				"?," +
				"?" +
			")"
		);

		stm.setString(1, username);
		String salt = Salt.generateSalt();
		stm.setString(2, HashMe.hashMe(password,salt));
		stm.setString(3, salt);
		stm.setString(4, email);
		stm.setString(5, about);
		stm.setString(6, avatar);
		stm.setString(7, isAdmin ? "admin" : "user");
		stm.setInt(8, secret != null ? 1 : 0);
		stm.setString(9, secret);

		stm.executeUpdate();
	}
}
