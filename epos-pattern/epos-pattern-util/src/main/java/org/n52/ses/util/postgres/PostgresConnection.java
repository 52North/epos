/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.ses.util.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection to a postgres data base.
 * 
 * Singleton
 * 
 */
public class PostgresConnection {

	private Connection connection;
	private static PostgresConnection _instance;

	/**
	 * Object serves as lock. Needed in the constructor as
	 * 'synchronized(_instance)' does block permanently. (Re-entrant seems not
	 * working between static and non static context)
	 */
	private static Object lock = new Object();

	private String user;
	private String password;
	private String database;
	private Integer port;
	private String host;

	private static final Logger logger = LoggerFactory
			.getLogger(PostgresConnection.class);

	/**
	 * 
	 * private Constructor
	 * 
	 */
	private PostgresConnection(String host, Integer port, String user,
			String password, String database) {
		this.user = user;
		this.password = password;
		this.database = database;
		this.port = port;
		this.host = host;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			PostgresConnection.logger.warn(e.getMessage());

			StringBuilder sb = new StringBuilder();

			for (StackTraceElement ste : e.getStackTrace()) {
				sb.append("\n" + ste.toString());
			}

			PostgresConnection.logger.warn(sb.toString());
		}
	}

	/**
	 * 
	 * @return the single instance of this class
	 */
	public static PostgresConnection getInstance() {
		synchronized (lock) {
			if (_instance == null) {
				throw new IllegalStateException(
						"Instance not available. Create one!");
			}
			return _instance;
		}
	}

	/**
	 * 
	 * @return the single instance of this class
	 */
	public static PostgresConnection getInstance(String host, int port,
			String user, String password, String database) {
		synchronized (lock) {
			if (_instance == null) {
				_instance = new PostgresConnection(host, port, user, password,
						database);
			}
			return _instance;
		}
	}

	/**
	 * 
	 * @return the connection to the data base
	 */
	public Connection getConnection() {
		if (this.connection == null) {
			String url = "";
			if (this.port == null) {
				url = "jdbc:postgresql://" + this.host + "/" + this.database;
			} else {
				url = "jdbc:postgresql://" + this.host + ":" + this.port + "/"
						+ this.database;
			}

			Properties props = new Properties();
			props.setProperty("user", this.user);
			props.setProperty("password", this.password);

			try {
				PostgresConnection.logger
						.info("try to open connection to DB\n\turl: " + url
								+ "\n\tuser: " + this.user + "\n\tpassword: "
								+ this.password);
				this.connection = DriverManager.getConnection(url, props);
			} catch (SQLException e) {
				PostgresConnection.logger.warn(e.getMessage(), e);
			}
		}
		return this.connection;
	}
}