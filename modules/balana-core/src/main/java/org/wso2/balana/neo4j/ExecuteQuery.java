package org.wso2.balana.neo4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteQuery implements DBProperties {
	public static Connection createConnection() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(DBProperties.CONNECTION_STRING, DBProperties.USERNAME,
					DBProperties.PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	public static List<String> execute(Map<String, String> attributes) {
		List<String> policyIds = new ArrayList<String>();
		String query = "";
		for (String str : attributes.keySet()) {
			if (!str.contains("-")) {
				if (!query.equals("")) {
					query += " UNION ";
				}
				query += "MATCH (policy)-[:ASSOCIATED_WITH]->(target)-[:DEFINED_BY]->(" + str + ") WHERE " + str
						+ ".AttributeValue=\'" + attributes.get(str) + "\' RETURN policy.filename";
			}
		}
		try {
			ResultSet resultSet = createConnection().createStatement().executeQuery(query);
			while (resultSet.next()) {
				policyIds.add(resultSet.getString("policy.filename"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return policyIds;
	}

/*	public static void test() {
		String query = "MATCH (policy)-[:ASSOCIATED_WITH]->(target)-[:DEFINED_BY]->(resource) " +
				"WHERE resource.AttributeValue=\'http://medico.com/record/patient/BartSimpson\' " +
				"RETURN policy.filename UNION " +
				"MATCH (policy)-[:ASSOCIATED_WITH]->(target)-[:DEFINED_BY]->(subject) " +
				"WHERE subject.AttributeValue='Julius Hibbert' " +
				"RETURN policy.filename UNION MATCH (policy)-[:ASSOCIATED_WITH]->(target)-[:DEFINED_BY]->(action) " +
				"WHERE action.AttributeValue='read' RETURN policy.filename";
		try {
			ResultSet resultSet = createConnection().createStatement().executeQuery(query);
			while (resultSet.next()) {
				System.out.println(resultSet.getString("policy.filename"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void main(String args[]) {
		test();
	}*/
}
