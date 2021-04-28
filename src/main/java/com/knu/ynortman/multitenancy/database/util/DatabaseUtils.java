package com.knu.ynortman.multitenancy.database.util;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Component;

import com.knu.ynortman.multitenancy.util.aop.TrackExecutionTime;

@Component
@ConditionalOnProperty(name = "multitenancy.strategy", havingValue = "database")
public class DatabaseUtils {

	@TrackExecutionTime
	public boolean createDatabase(String db, JdbcTemplate jdbcTemplate) {
		return jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("CREATE DATABASE " + db));
	}
	
	@TrackExecutionTime
	public boolean createUser(String user, String password, JdbcTemplate jdbcTemplate) {
		return jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt
				.execute("CREATE USER " + user + " WITH ENCRYPTED PASSWORD '" + password + "'"));
	}
	
	@TrackExecutionTime
	public boolean grantPrivileges(String db, String user, JdbcTemplate jdbcTemplate) {
		return jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt
				.execute("GRANT ALL PRIVILEGES ON DATABASE " + db + " TO " + user));
	}
}
