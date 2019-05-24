package org.cysecurity.cspf.jvl.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class Database {
	private final Connection con;

	public Database(Connection con) {
		this.con = con;
	}

	public void createCards() throws SQLException {
		con.createStatement().executeUpdate(
			"CREATE TABLE cards" +
			"(" +
				"id INT," +
				"cardno VARCHAR(80)," +
				"cvv VARCHAR(6)," +
				"expirydate DATE," +
				"FOREIGN KEY (id) REFERENCES users(ID)" +
			")"
		);
	}

	public int insertCard(
		int id,
		String cardnumber,
		String cvv,
		Date expdate
	)
		throws SQLException
	{
		for (char c : cardnumber.toCharArray())
			if (!Character.isDigit(c))
				return 0;

		for (char c : cvv.toCharArray())
			if (!Character.isDigit(c))
				return 0;

		PreparedStatement stm = con.prepareStatement(
			"INSERT INTO cards" +
			"(" +
				"id," +
				"cardno," +
				"cvv," +
				"expirydate" +
			")" +
			"VALUES" +
			"(" +
				"?," +
				"?," +
				"?," +
				"?" +
			")"
		);

		stm.setInt(1, id);
		stm.setString(2, cardnumber);
		stm.setString(3, cvv);
		stm.setDate(4, expdate);

		return stm.executeUpdate();
	}

	public void createPosts() throws SQLException {
		con.createStatement().executeUpdate(
			"CREATE TABLE posts" +
			"(" +
				"postid INT NOT NULL AUTO_INCREMENT," +
				"content TEXT," +
				"title VARCHAR(100)," +
				"user INT," +
				"PRIMARY KEY (postid)," +
				"FOREIGN KEY (user) REFERENCES users(ID)" +
			")"
		);
	}

	public void createSelectUserMessage() throws SQLException {
		con.createStatement().executeUpdate(
			"CREATE PROCEDURE SelectUserMessage(in messageid int)" +
			"BEGIN " +
			"SELECT " +
				"msgid," +
				"(SELECT username FROM users WHERE id = (" +
					"SELECT sender FROM UserMessages WHERE msgid = messageid)" +
				") AS sender," +
				"(SELECT username FROM users WHERE id = (" +
					"SELECT recipient FROM UserMessages WHERE msgid = messageid)" +
				") AS recipient," +
				"subject," +
				"msg " +
			"FROM UserMessages WHERE msgid = messageid;" +
			"END"
		);
	}

	public void createUserMessages() throws SQLException {
		con.createStatement().executeUpdate(
			"CREATE TABLE UserMessages" +
			"(" +
				"msgid INT NOT NULL AUTO_INCREMENT," +
				"recipient INT," +
				"sender INT," +
				"subject VARCHAR(60)," +
				"msg TEXT," +
				"PRIMARY KEY (msgid)," +
				"FOREIGN KEY (recipient) REFERENCES users(ID)," +
				"FOREIGN KEY (sender) REFERENCES users(ID)" +
			")"
		);
	}

	public void insertPost(
		String content,
		String title,
		String user
	)
		throws SQLException
	{
		PreparedStatement stm = con.prepareStatement(
			"INSERT INTO posts" +
			"(" +
				"content," +
				"title," +
				"user" +
			")" +
			"VALUES" +
			"(" +
				"?," +
				"?," +
				"(SELECT id FROM users WHERE username = ?)" +
			")"
		);

		stm.setString(1, content);
		stm.setString(2, title);
		stm.setString(3, user);

		stm.executeUpdate();
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

	public int insertUserMessage(
		String recipient,
		String sender,
		String subject,
		String message
	)
		throws SQLException
	{
		PreparedStatement stm = con.prepareStatement(
			"INSERT INTO UserMessages" +
			"(" +
				"recipient," +
				"sender," +
				"subject," +
				"msg" +
			")" +
			"VALUES" +
			"(" +
				"(SELECT ID FROM users WHERE username = ?)," +
				"(SELECT ID FROM users WHERE username = ?)," +
				"?," +
				"?" +
			")"
		);
		stm.setString(1, recipient);
		stm.setString(2, sender);
		stm.setString(3, subject); // TODO XSS sanitation
		stm.setString(4, message); // TODO XSS sanitation

		return stm.executeUpdate();
	}

	public ResultSet selectPost(int postid) throws SQLException {
		PreparedStatement statement = con.prepareStatement(
			"SELECT " +
				"p.postid," +
				"p.content," +
				"p.title," +
				"u.username AS user " +
			"FROM " +
				"posts p " +
			"LEFT JOIN " +
				"users u " +
			"ON " +
				"u.ID = p.user " +
			"WHERE " +
				"p.postid = ?"
		);

		statement.setInt(1, postid);

		return statement.executeQuery();
	}

	public ResultSet selectPosts() throws SQLException {
		return con.createStatement().executeQuery(
			"SELECT " +
				"p.postid," +
				"p.content," + // TODO Perhaps skip content?
				"p.title," +
				"u.username AS user " +
			"FROM " +
				"posts p " +
			"LEFT JOIN " +
				"users u " +
			"ON " +
				"u.ID = p.user"
		);
	}

	public int selectUserId(String username) throws SQLException {
		PreparedStatement stm = con.prepareStatement(
			"SELECT ID FROM users WHERE username = ?"
		);
		stm.setString(1, username);
		ResultSet result = stm.executeQuery();

		return result.next() ? result.getInt("ID") : -1;
	}

	public ResultSet selectUserMessage(int msgid) throws SQLException {
		PreparedStatement stm = con.prepareStatement("CALL SelectUserMessage(?)");
		stm.setInt(1,msgid);
		return stm.executeQuery();
	}
}
