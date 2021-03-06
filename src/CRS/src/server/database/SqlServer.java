package CRS.src.server.database;

import java.sql.*;
import java.util.ArrayList;
import CRS.src.server.*;

/**
 * This was initially programmed for a MariaDB implementation of MYSql. It
 * should still work as expected, even for a Mysql server.
 *
 */
public class SqlServer {

	private String sqlServerLocation;
	private String schema;
	private Connection con;

	/**
	 * Initialize the connection to an SQL server.
	 * 
	 * @param sqlServerLocation The SQL address, for jdbc.
	 * @param schema            The SQL database/schema, the thing that contains the
	 *                          tables.
	 * @param username          The user of the SQL server.
	 * @param password          The password for the SQL server user.
	 * @throws SQLException Thrown when an SQL issue comes up.
	 */
	public SqlServer(String sqlServerLocation, String schema, String username, String password) throws SQLException {
		// note, passing the password as a string is insecure.
		// but it should not be that horrible for a demo product.
		// PLEASE DONT USE THIS IN PRODUCTION.
		this.sqlServerLocation = sqlServerLocation;
		this.schema = schema;
		con = DriverManager.getConnection(this.sqlServerLocation, username, password);
		this.setupServer(username, password);
//		con.close();
//		statement.executeUpdate("INSERT INTO `students` (`id`, `name`) VALUES ('4', 'aa')");

	}

	/**
	 * Connect to the server, if we need to create the schema/database, do so,
	 * otherwise load up the database.
	 * 
	 * @param username The user for the SQL server
	 * @param password The users password for the SQL server
	 * @throws SQLException Error, something has gone wrong if this happens.
	 */
	private void setupServer(String username, String password) throws SQLException {
		this.setupSchemaSQL(username, password);
		Statement statement = con.createStatement();
		this.setupStudentSQL(statement);
		this.setupCoursesSQL(statement);
		this.setupRequisitesSQL(statement);
		this.setupLecturesSQL(statement);
		this.setupRegistrationsSQL(statement);
		statement.close();
	}

	/**
	 * Create and/or connect to the schema/database.
	 * 
	 * @param username The sql server user
	 * @param password the sql server user password.
	 * @throws SQLException Error, unable to properly connect to server.
	 */
	public void setupSchemaSQL(String username, String password) throws SQLException {
		// create statement
		Statement statement = this.con.createStatement();
		// check how many of the schema exist. should be a binary thing, 1 it exists, 0
		// it does not.
		ResultSet rs = statement.executeQuery(
				"SELECT count(*) AS total FROM information_schema.schemata WHERE (SCHEMA_NAME ='" + this.schema + "')");
		int exists = 0;
		while (rs.next()) {
			// test if it exists.
			exists = rs.getInt("total");
		}
		// if the schema/database does not exist, create it.
		if (exists == 0) {
			System.out.println("Creating database/schema `" + this.schema + "`.");
			statement.executeUpdate("CREATE SCHEMA `" + this.schema + "`");
		}

		// close our temp statement
		statement.close();
		// and set the connection to our new schema/database
		this.con.close();
		this.con = DriverManager.getConnection(this.sqlServerLocation + this.schema, username, password);
	}

	/**
	 * Set up the student table in the database.
	 * 
	 * @param statement The statement that will execute our query and maybe execute
	 *                  an update
	 * @throws SQLException Error, not properly connected to SQL server.
	 */
	public void setupStudentSQL(Statement statement) throws SQLException {
		ResultSet rs = statement
				.executeQuery("SELECT count(*) AS total FROM information_schema.tables WHERE (TABLE_NAME ='students')");
		int exists = 0;
		while (rs.next()) {
			// 0 it does not exist, 1 it does.
			exists = rs.getInt("total");
		}
		// if the table does not exist, create it.
		if (exists == 0) {
			System.out.println("Creating TABLE 'students'.");
			// generated command from MySQL Workbench
			statement.executeUpdate(
					"CREATE TABLE `students` (`id` INT NOT NULL,`name` VARCHAR(45) NOT NULL, PRIMARY KEY (`id`));");
			// also add in some basic students, to show basics.
			this.addStudent(1, "test");
			this.addStudent(2, "long");
			this.addStudent(3, "cloud");
			this.addStudent(4, "aaaaaaaaa");
		}
	}

	/**
	 * Set up the course table in the database
	 * 
	 * @param statement Statement to execute query/updates.
	 * @throws SQLException Error, most likely due to improper server connection.
	 */
	public void setupCoursesSQL(Statement statement) throws SQLException {
		ResultSet rs = statement
				.executeQuery("SELECT count(*) AS total FROM information_schema.tables WHERE (TABLE_NAME ='courses')");
		int exists = 0;
		while (rs.next()) {
			exists = rs.getInt("total");
		}
		// if the table does not exist, create it.
		if (exists == 0) {
			System.out.println("Creating TABLE 'courses'.");
			// generated command from MySQL Workbench
			statement.executeUpdate("CREATE TABLE `courses` (`name` VARCHAR(7) NOT NULL, PRIMARY KEY (`name`));");
			// also add in some courses, to show basics.
			this.addCourse("ENGG", 233);
			this.addCourse("ENSF", 409);
			this.addCourse("CPSC", 319);
			this.addCourse("TEST", 123);
			this.addCourse("ABCD", 101);
		}
	}

	/**
	 * Set up the prereq table in the database
	 * 
	 * @param statement Statement to execute query/updates.
	 * @throws SQLException Error, most likely due to improper server connection.
	 */
	public void setupRequisitesSQL(Statement statement) throws SQLException {
		ResultSet rs = statement.executeQuery(
				"SELECT count(*) AS total FROM information_schema.tables WHERE (TABLE_NAME ='requisites')");
		int exists = 0;
		while (rs.next()) {
			exists = rs.getInt("total");
		}
		// if the table does not exist, create it.
		if (exists == 0) {
			System.out.println("Creating TABLE 'requisites'.");
			// generated command from MySQL Workbench
			statement.executeUpdate(
					"CREATE TABLE `requisites` (`course` VARCHAR(7) NOT NULL, `requisite` VARCHAR(7) NOT NULL);");

			// also add in some basic prereqs, to show basics.
			this.addRequisite("ENSF", 409, "ENGG", 233);
			this.addRequisite("CPSC", 409, "ENGG", 233);
			this.addRequisite("ENGG", 233, "TEST", 123);
			this.addRequisite("ENGG", 233, "ABCD", 101);
		}
	}

	/**
	 * Setup the lectures (previously offering) table.
	 * 
	 * @param statement Statement to execute query/updates.
	 * @throws SQLException Error, most likely due to improper server connection.
	 */
	public void setupLecturesSQL(Statement statement) throws SQLException {
		ResultSet rs = statement
				.executeQuery("SELECT count(*) AS total FROM information_schema.tables WHERE (TABLE_NAME ='lectures')");
		int exists = 0;
		while (rs.next()) {
			exists = rs.getInt("total");
		}
		// if the table does not exist, create it.
		if (exists == 0) {
			System.out.println("Creating TABLE 'lectures'.");
			// generated command from MySQL Workbench
			statement.executeUpdate(
					"CREATE TABLE `lectures` (`id` INT NOT NULL, `course` VARCHAR(45) NOT NULL, `cap` INT NOT NULL);");

			// also add in some basic lectures, to show basics.
			this.addLecture("ENGG", 233, 1, 50);
			this.addLecture("ENGG", 233, 2, 200);
			this.addLecture("ENSF", 409, 1, 90);
			this.addLecture("CPSC", 319, 1, 30);
			this.addLecture("ABCD", 101, 1, 10);
			this.addLecture("TEST", 123, 1, 25);
		}
	}

	/**
	 * Setup the registrations table
	 * 
	 * @param statement Statement to execute query/updates.
	 * @throws SQLException Error, most likely due to improper server connection.
	 */
	public void setupRegistrationsSQL(Statement statement) throws SQLException {
		ResultSet rs = statement.executeQuery(
				"SELECT count(*) AS total FROM information_schema.tables WHERE (TABLE_NAME ='registrations')");
		int exists = 0;
		while (rs.next()) {
			exists = rs.getInt("total");
		}
		// if the table does not exist, create it.
		if (exists == 0) {
			System.out.println("Creating TABLE 'registrations'.");
			// generated command from MySQL Workbench
			statement.executeUpdate(
					"CREATE TABLE `registrations` (`student_id` INT NOT NULL, `course` VARCHAR(45) NOT NULL, `lecture_id` INT NOT NULL);");

			// also add in some basic registrations, to show basics.
			// If the lecture_id is 0, assume the student has already taken this course.
			this.addRegistration("ENGG", 233, 2, 0);
			this.addRegistration("ABCD", 101, 1, 0);
			this.addRegistration("ABCD", 101, 2, 0);
			this.addRegistration("ABCD", 101, 3, 0);
			this.addRegistration("ENGG", 233, 1, 1);
			this.addRegistration("ENGG", 233, 3, 2);
			this.addRegistration("ENSF", 409, 1, 1);
			this.addRegistration("CPSC", 319, 1, 1);
			this.addRegistration("TEST", 123, 3, 1);
		}
	}

	/**
	 * Add a student directly to the sql database.
	 * 
	 * @param id      The id of the student to add.
	 * @param student The name of the student to add
	 * @throws SQLException Communication error / improper connection.
	 */
	public void addStudent(int id, String student) throws SQLException {
		Statement statement = con.createStatement();
		statement.executeUpdate("INSERT INTO `students` (`id`, `name`) VALUES ('" + id + "', '" + student + "')");
		statement.close();
	}

	/**
	 * Add a course directly to the sql database.
	 * 
	 * @param name Name of the course
	 * @param num  Course number
	 * @throws SQLException Communication error / improper connection
	 */
	public void addCourse(String name, int num) throws SQLException {
		Statement statement = con.createStatement();
		statement.executeUpdate("INSERT INTO `courses` (`name`) VALUES ('" + name.toUpperCase() + num + "')");
		statement.close();
	}

	/**
	 * Adds a new prereq for a course
	 * 
	 * @param courseName    The course name that will get a prereq
	 * @param courseNum     the course num that will get a prereq
	 * @param requisiteName the course name of the prereq
	 * @param requisiteNum  the course number of the prereq
	 * @throws SQLException Communication error / improper connection
	 */
	public void addRequisite(String courseName, int courseNum, String requisiteName, int requisiteNum)
			throws SQLException {
		Statement statement = con.createStatement();
		statement.executeUpdate("INSERT INTO `requisites` (`course`,`requisite`) VALUES ('" + courseName.toUpperCase()
				+ courseNum + "','" + requisiteName.toUpperCase() + requisiteNum + "')");
		statement.close();
	}

	/**
	 * Adds a new lecture for a course to the database
	 * 
	 * @param course    The course name that will get a new lecture
	 * @param courseNum The course number that will get a new lecture
	 * @param lecId     The lecture's id
	 * @param cap       the max amount of students in the lecture.
	 * @throws SQLException Communication error / improper connection
	 */
	public void addLecture(String course, int courseNum, int lecId, int cap) throws SQLException {
		Statement statement = con.createStatement();
		statement.executeUpdate("INSERT INTO `lectures` (`id`,`course`,`cap`) VALUES ('" + lecId + "','"
				+ course.toUpperCase() + courseNum + "','" + cap + "')");
		statement.close();
	}

	/**
	 * Adds a new registration to the database
	 * 
	 * @param course    The course name for the registration
	 * @param courseNum The course number for the registration
	 * @param studentId The student id, who is registered
	 * @param lectureId The lecture id that the student is registered to.
	 * @throws SQLException Communication error / improper connection
	 */
	public void addRegistration(String course, int courseNum, int studentId, int lectureId) throws SQLException {
		Statement statement = con.createStatement();
		statement.executeUpdate("INSERT INTO `registrations` (`student_id`,`course`,`lecture_id`) VALUES ('" + studentId
				+ "','" + course.toUpperCase() + courseNum + "','" + lectureId + "')");
		statement.close();
	}

	/**
	 * Removes a registration from the database
	 * 
	 * @param course    The course name that is registered to
	 * @param courseNum the course number that is registered to
	 * @param studentId The student that is registered
	 * @param lectureId The lecture that is registered to.
	 * @throws SQLException Communication error / improper connection
	 */
	public void removeRegistration(String course, int courseNum, int studentId, int lectureId) throws SQLException {
		Statement statement = con.createStatement();
		statement.executeUpdate(
				"DELETE FROM `registrations` WHERE (`student_id` = '" + studentId + "') AND (`course` = '"
						+ course.toUpperCase() + courseNum + "') AND (`lecture_id` = '" + lectureId + "')");
		statement.close();
	}

	/**
	 * Fetches all students from the database.
	 * 
	 * @return ArrayList<Student> is a list of all students in the database.
	 * @throws SQLException Communication error / improper connection
	 */
	public ArrayList<Student> getStudents() throws SQLException {
		ArrayList<Student> students = new ArrayList<Student>();
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `students`");
		while (rs.next()) {
			Student student = new Student(rs.getString("name"), rs.getInt("id"));
			students.add(student);
		}
		return students;
	}

	/**
	 * Fetches all courses from the database
	 * 
	 * @return ArrayList<Course> is a list of all the courses in the database.
	 * @throws SQLException Communication error / improper connection
	 */
	public ArrayList<Course> getCourses() throws SQLException {
		ArrayList<Course> courses = new ArrayList<Course>();
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `courses`");
		while (rs.next()) {
			String courseName = rs.getString("name");
			Course course = new Course(courseName.substring(0, 4), Integer.parseInt(courseName.substring(4, 7)));
			courses.add(course);
		}
		return courses;
	}

	/**
	 * Loads all of the lectures from the database for the given course
	 * 
	 * @param course The course to load all of the lectures for
	 * @throws SQLException Communication error / improper connection
	 */
	public void setCourseLectures(Course course) throws SQLException {
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `lectures` WHERE course ='"
				+ course.getCourseName().toUpperCase() + course.getCourseNum() + "'");
		while (rs.next()) {
//			System.out.println(rs.getInt("id") + " " + rs.getInt("cap"));
			Lecture lec = new Lecture(rs.getInt("id"), rs.getInt("cap"));
			course.addLecture(lec);
		}
	}

	/**
	 * Connects a given course to all of the prereqs defined in the database
	 * 
	 * @param course  The course to load all of the lectures for
	 * @param courses The course list, which must contain the prereq course.
	 * @throws SQLException Communication error / improper connection
	 */
	public void setCourseRequisites(Course course, ArrayList<Course> courses) throws SQLException {
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `requisites` WHERE course ='"
				+ course.getCourseName().toUpperCase() + course.getCourseNum() + "'");
		while (rs.next()) {
			String requisiteName = rs.getString("requisite");
			for (Course c : courses) {
				if (c.getCourseName().equals(requisiteName.substring(0, 4))
						&& c.getCourseNum() == Integer.parseInt(requisiteName.substring(4, 7))) {
					course.addPreReq(c);
				}
			}
		}
	}

	/**
	 * Registers for the course, all students that are taking it.
	 * 
	 * @param course   The course, should have some lectures contained.
	 * @param students The student list, which should contain some students that are
	 *                 taking this course.
	 * @throws SQLException Communication error / improper connection
	 */
	public void setCourseRegistrations(Course course, ArrayList<Student> students) throws SQLException {
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `registrations` WHERE course ='"
				+ course.getCourseName().toUpperCase() + course.getCourseNum() + "'");
		while (rs.next()) {
			for (Student s : students) {
				if (s.getStudentId() == rs.getInt("student_id")) {
					if (rs.getInt("lecture_id") == 0) {
						s.addTaken(course);
					} else {
						Registration r = new Registration();
						r.completeRegistration(s, course.getLectureSection(rs.getInt("lecture_id")));
					}
				}
			}
		}
	}

	/**
	 * Registers a student for the courses that they might be taking.
	 * 
	 * @param student The student to register for
	 * @param courses The course list, which should have some courses that the
	 *                student will take.
	 * @throws SQLException Communication error / improper connection
	 */
	public void setStudentRegistrations(Student student, ArrayList<Course> courses) throws SQLException {
		Statement statement = con.createStatement();
		ResultSet rs = statement
				.executeQuery("SELECT * FROM `registrations` WHERE student_id ='" + student.getStudentId() + "'");
		while (rs.next()) {
			for (Course c : courses) {
				if ((c.getCourseName() + c.getCourseNum()).equals(rs.getString("course"))) {
					if (rs.getInt("lecture_id") == 0) {
						student.addTaken(c);
						continue;
					} else {
						Registration r = new Registration();
						r.completeRegistration(student, c.getLectureSection(rs.getInt("lecture_id")));
					}
				}
			}
		}
	}

	/**
	 * Shutdown the sql server.
	 */
	public void close() {
		try {
			this.con.close();
		} catch (SQLException e) {
			System.err.println("Error! Something went wrong with closing the SQL server connection.");
			e.printStackTrace();
		}
	}
	// debug code! uncomment to test the sql server.
//	public static void main(String[] args) {
//		System.out.println("Debugging Sql Server.");
//		try {
//			SqlServer serv = new SqlServer("jdbc:mysql://localhost:3306/", "school_long_cloud", "mysql", "");
//			ArrayList<Student> students = serv.getStudents();
//			ArrayList<Course> courses = serv.getCourses();
//			for (Course c : courses) {
//				serv.setCourseLectures(c);
//			}
//			for (Course c : courses) {
//				serv.setCourseRequisites(c, courses);
//				System.out.println(c);
//			}
//			for (Student s : students) {
//				serv.setStudentRegistrations(s, courses);
//				System.out.println(s);
//				System.out.println(s.listRegistered());
//				System.out.println(s.listTaken());
//			}
//			serv.removeRegistration("ENGG", 233, 2, 0);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
