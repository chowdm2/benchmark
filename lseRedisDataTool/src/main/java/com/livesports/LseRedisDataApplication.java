
package com.livesports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 
 * Spring Boot Main Class .
 *
 */

@SpringBootApplication
public class LseRedisDataApplication implements CommandLineRunner {
	/**
	 * RedisTemplate Bean.
	 * 
	 */
	@Autowired
	private StringRedisTemplate template;

	/**
	 * Number of User to be created in redis Server.
	 */
	private int numberOfUser = 0;

	/**
	 * Number of teams to be created for user in redis Server.
	 */
	private int numberOfTeam = 0;

	/**
	 * User Prefix.
	 */
	private String userPrefix = "user";

	/**
	 * Team Prefix.
	 */
	private String teamPrefix = "t";

	/**
	 * Segment Prefix.
	 */
	private String segmentPrefix = "s";

	/**
	 * teamsValue used as value for user Key.
	 */
	private String teamsValue = "";

	/**
	 * segmentValue used as value for team Key.
	 */
	private String segmentValue = "";

	@Override
	public void run(String... args) throws Exception {

		if (args.length < 2) {
			System.out.println(
					"################# Usage Format: java -jar <jar_file_name> arg1 arg2 where arg1 and arg2 are integers ##############");
			System.exit(1);
		}

		try {
			numberOfUser = Integer.parseInt(args[0]);
			numberOfTeam = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println("################# arg1 arg2 should be of type integer ######################");
			System.exit(1);
		}

		if (numberOfTeam != 0) {
			StringBuilder teams = new StringBuilder();

			StringBuilder segments = new StringBuilder();

			/**
			 * Logic to create teamValues as comma separated like "t1,t2,t3" for
			 * Redis Database.
			 */

			for (int i = 1; i <= numberOfTeam; i++) {

				teams.append(teamPrefix + i);
				teams.append(",");

			}

			teamsValue = (teams.substring(0, teams.length() - 1)).toString();

			System.out.println("teamValue String for Redis :- " + teamsValue);

			/**
			 * Logic to create segmentValues as comma separated like "s1,s2,s3"
			 * for Redis Database.
			 */

			for (int i = 1; i <= numberOfTeam; i++) {

				segments.append(segmentPrefix + i);
				segments.append(",");

			}

			segmentValue = (segments.substring(0, segments.length() - 1)).toString();

			System.out.println("segmentsValue String for Redis :-" + segmentValue);

		}

		insertRedisData(numberOfUser, numberOfTeam);
	}

	/**
	 * Insert Data into redis server.
	 * 
	 * @param numberOfUser
	 * @param numberOfTeam
	 */
	public void insertRedisData(int numberOfUser, int numberOfTeam) {
		
		for (int i = 1; i <= numberOfUser; ++i) {

			template.opsForValue().set(userPrefix + i, teamsValue);

		}

		System.out.println("Created-- " + numberOfUser + " No. of UserToTeam records");

		for (int j = 1; j <= numberOfTeam; ++j) {

			template.opsForValue().set(teamPrefix + j, segmentValue);

		}

		System.out.println("Created-- " + numberOfTeam + " No. of TeamToSegment records");

	}

	/**
	 * Spring Boot main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(LseRedisDataApplication.class, args);

	}

}
