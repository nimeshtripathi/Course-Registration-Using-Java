import java.sql.*;
import java.util.Scanner;
import java.util.Calendar;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.zip.CheckedOutputStream;

public class DataAccessLayer {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/project3-nudb";
    static final String USER = "root";
    static final String PASS = "rootroot";
    static Connection conn = null;
    static int userId;
    private static Object Year;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        System.out.println("Connecting to Caesar...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        checkUserCredentials();
    }

    public static void logout() throws SQLException
    {
        System.out.println("Press 1 to logout, any other integer for main menu");
        Scanner myInput = new Scanner(System.in);
        int b = myInput.nextInt();
        if(b==1)
        {
            checkUserCredentials();
        }
        else{
            menufunction();

        }
    }

    public static void checkUserCredentials() throws SQLException {
        Scanner myInput = new Scanner(System.in);
        System.out.println("Login Detail Required");
        System.out.println("Enter UserId: ");
        userId = myInput.nextInt();
        System.out.println("Enter password: ");
        myInput.nextLine();
        String password = myInput.nextLine();

        if (!isCredentialCorrect(userId, password)) {
            System.out.println("Either Username or Password is incorrect");
        } else {
            System.out.println("You have successfully logged into the system");
            int option = -1;
            while (option != 5) {

                option = menufunction();
                break;

            }

            doOperation(option);


        }
    }


    private static void doOperation(int option) throws SQLException {


        switch (option) {
            case 1:
                showTranscript();
                menufunction();
                break;
            case 2:
                showPersonalDetails();
                menufunction();
                break;
            case 3:
                logout();
                break;
            case 4:
                enroll();
                break;
            case 5:
                withdraw();
                break;
            default:
                handleInvalidEntry();

        }
    }



    public static boolean isCourseSelectionCorrect(String course) throws SQLException {
        String sql = "select count(*) count from transcript where StudId = ? and UoSCode=?";

        PreparedStatement p = conn.prepareStatement(sql);
        p.setInt(1, userId);
        p.setString(2, course);
        ResultSet rs = p.executeQuery();
        int count = 0;
        while (rs.next()) {
            count = rs.getInt("count");
        }


        if (count == 0)
            return false;

        return true;

    }

    private static void showAllEnrolledCourses() throws SQLException {
       String query = "select UoSCode,Grade from transcript \n" +
               "  where StudId = ? and Grade is null ";

        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, userId);
        ResultSet rs = p.executeQuery();
        System.out.println("Your enrolled courses");
        while(rs.next()){
            System.out.println("Course Code: "+ rs.getString("UoSCode"));
        }

    }


    private static void handleInvalidEntry() throws SQLException {
        System.out.println("Error");
        menufunction();
    }


    private static void Question2helper() throws SQLException {

        Calendar now = Calendar.getInstance();
        int a = (now.get(Calendar.MONTH) + 1);
        int b = (now.get(Calendar.YEAR));

        String semester = a > 6 && b<2020 ? "Q2" : "Q1";

        String sql = "select UoSCode, Year from transcript where StudId=? and Semester=? ";
        PreparedStatement p = conn.prepareStatement(sql);
        p.setInt(1, userId);
        p.setString(2, semester);

        ResultSet rs = p.executeQuery();

        System.out.println("StudId: " + userId);
        System.out.println(", Semester: " + semester);

        while (rs.next()) {

            System.out.println(" UoSCode: " + rs.getString("UoSCode"));
            System.out.println(" Year: " + rs.getString("Year"));

        }


    }


    public static boolean isCredentialCorrect(int userId, String password) throws SQLException {
        String sql = "select count(*) count from student where id = ? and Password=?";
        PreparedStatement p = conn.prepareStatement(sql);
        p.setInt(1, userId);
        p.setString(2, password);
        ResultSet rs = p.executeQuery();
        int count = 0;
        while (rs.next()) {
            count = rs.getInt("count");
        }


        if (count == 0)
            return false;

        return true;

    }


    public static int menufunction() throws SQLException {

        Scanner myInput = new Scanner(System.in);
        Question2helper();
        System.out.println("Select 1 of the following options \n");
        System.out.println("1 for Transcript\n");
        System.out.println("2 for Personal Detail\n");
        System.out.println("3 for Logout\n");
        System.out.println("4 for Enrol \n");
        System.out.println("5 for Withdraw\n");

        int option = myInput.nextInt();
        return option;

    }

    private static void showTranscript() throws SQLException {


        String sql = "select UoSCode,Grade from transcript where StudId=?";
        PreparedStatement p = conn.prepareStatement(sql);
        p.setInt(1, userId);

        ResultSet rs = p.executeQuery();

        while (rs.next()) {


            System.out.println(" UoSCode: " + rs.getString("UoSCode"));
            System.out.println(" Grade: " + rs.getString("Grade"));

        }

        System.out.println("Enter 1 to view course details ");
        System.out.println("Enter 2 to go back to main menu ");
        Q3helper();

    }


    private static void Q3helper() throws SQLException {
        Scanner myInput = new Scanner(System.in);
        int option1 = myInput.nextInt();
        if (option1 == 2) {
            menufunction();
        }
        if (option1 == 1) {
            String sql = "SELECT  DISTINCT unitofstudy.UoSName," +
                    "transcript.UoSCode, " +
                    "transcript.Semester,\n" +
                    " transcript.Year, " +
                    "transcript.Grade, " +
                    "transcript.StudId,\n" +
                    "    uosoffering.Enrollment, uosoffering.MaxEnrollment,\n" +
                    "    faculty.Name\n" +
                    "    from transcript\n" +
                    "    JOIN unitofstudy on transcript.UoSCode = unitofstudy.UoSCode\n" +
                    "    JOIN uosoffering on transcript.UoSCode = uosoffering.UoSCode\n" +
                    "    JOIN faculty on uosoffering.InstructorId = faculty.Id\n" +
                    "    WHERE\n" +
                    "    transcript.Year = uosoffering.Year\n" +
                    "    AND transcript.Semester = uosoffering.Semester AND transcript.StudID=? ";
            PreparedStatement p = conn.prepareStatement(sql);
            p.setInt(1, userId);
            ResultSet rs = p.executeQuery();
            System.out.println("StudId: " + userId);
            while (rs.next()) {

                System.out.println(" UoSName: " + rs.getString("unitofstudy.UoSName"));
                System.out.print(" UoSCode: " + rs.getString("transcript.UoSCode"));
                System.out.print(" Semester: " + rs.getString("transcript.Semester"));
                System.out.print(" Year: " + rs.getString("transcript.Year"));
                System.out.print(" Grade: " + rs.getString("transcript.Grade"));
                System.out.print(" Enrollment: " + rs.getString("uosoffering.Enrollment"));
                System.out.print(" Max Enrollment: " + rs.getString("uosoffering.MaxEnrollment"));
                System.out.print(" Faculty: " + rs.getString("faculty.Name"));


            }
        } else {
            System.out.println(" Invalid Entry, Select 1 or 2 ");
            Q3helper();
            menufunction();
        }


    }

    private static void showPersonalDetails() throws SQLException {
        String sql = "select Id, Name, Password, Address from student where Id = ?";
        PreparedStatement p = conn.prepareStatement(sql);
        p.setInt(1, userId);

        ResultSet rs = p.executeQuery();

        while (rs.next()) {
            System.out.println(" ID: " + rs.getString("Id"));
            System.out.println(" Name: " + rs.getString("Name"));
            System.out.println(" Password: " + rs.getString("Password"));
            System.out.println(" Address: " + rs.getString("Address"));
        }

        UpdatePersonalDetails();
    }

    private static void UpdatePersonalDetails() throws SQLException {

        System.out.println("Do you want to edit your Personal Details?");
        System.out.println("Enter 1 to edit and 2 to go to Main menu");
        Scanner myInput = new Scanner(System.in);
        int option2 = myInput.nextInt();
        if (option2 == 2) {
            menufunction();
        }
        if (option2 == 1) {
            System.out.println("Enter New Password");
            Scanner myInput1 = new Scanner(System.in);
            //myInput1.nextLine();
            String password_new = myInput1.nextLine();
            //password_new = "'"+ password_new + "'";

            System.out.println("Enter New Address");
            Scanner myInput2 = new Scanner(System.in);
            //myInput2.nextLine();
            String address_new = myInput2.nextLine();
            //address_new = "'"+ address_new + "'";

            String sql = "UPDATE Student SET Password=?,  Address=? WHERE Id=?";
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, password_new);
            p.setString(2, address_new);
            p.setInt(3, userId);

            int numberOfRecordUpdated = p.executeUpdate();
            System.out.println("Number of record updated" + numberOfRecordUpdated);

        }

    }
    private static void enroll() throws SQLException{

        listAllCourses();
        String course = getCourseToEnroll();
        if(isPrerequisiteSatisfied(course)){
            if(SeatsAreAvailable(course)){
                registerForCourse(course);
            }else{
                System.out.println("Seats are not available for the course"+ course);
            }
        }else{
           // System.out.println("Your prerequisite requirement is not fulfilled  for the selected course");
            showPreRequisiteForTheCouse(course);
            menufunction();


        }
    }

    private static void registerForCourse(String course) throws SQLException {

        Calendar now = Calendar.getInstance();
        int a = (now.get(Calendar.MONTH) + 1);
        int b = (now.get(Calendar.YEAR));

        String semester = a > 6 ? "Q2" : "Q1";

        String withdrawCourseQuery = "{CALL enroll_to_course(?,?,?,?)}";
        CallableStatement stmt = conn.prepareCall(withdrawCourseQuery);
        stmt.setInt(2, userId);
        stmt.setString(1, course);
        stmt.setString(3, semester);
        stmt.setInt(4, b);
        stmt.executeQuery();
    }


    private static boolean SeatsAreAvailable(String course) throws SQLException {
        String query = "select MaxEnrollment-Enrollment seatsAvailable from uosOffering where UoSCode= ?";
        PreparedStatement p = conn.prepareStatement(query);
        p.setString(1, course);
        ResultSet rs = p.executeQuery();
        int isSeatAvailable = 0;
        while (rs.next()) {

            isSeatAvailable = rs.getInt("seatsAvailed");
        }


        if (isSeatAvailable == 0)
            return false;


        return true;
    }

    private static void showPreRequisiteForTheCouse(String course) throws SQLException {

        String query = "select r.PrereqUoSCode PrereqUoSCode\n" +
                "  from transcript t\n" +
                "  inner join requires r on r.UoSCode = t.UoSCode and t.StudId = ? and t.UoSCode = ?";

        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, userId);
        p.setString(2, course);
        ResultSet rs = p.executeQuery();
        System.out.println("Please complete the below prerequisite for course: "+ course);
        while (rs.next()) {
            System.out.println("Prerequisite Course: "+ rs.getString("PrereqUoSCode"));
        }
    }

    private static boolean isPrerequisiteSatisfied(String course) throws SQLException {
        String query = "select count(*) count\n" +
                "  from transcript t\n" +
                "  inner join requires r on r.PrereqUoSCode = t.UoSCode and t.StudId = ? and t.UoSCode = ?";

        PreparedStatement p = conn.prepareStatement(query);
        p.setInt(1, userId);
        p.setString(2, course);
        ResultSet rs = p.executeQuery();
        int count = 0;
        while (rs.next()) {
            count = rs.getInt("count");
        }


        if (count == 0)
            return false;

        return true;
    }

    private static String getCourseToEnroll() throws SQLException {
        System.out.println("Enter the course id which you want to enroll: ");
        Scanner myInput2 = new Scanner(System.in);

        String course = myInput2.nextLine();

        String query = "{CALL enrollstudent("+id","+ "coursecode"+ "," + year, grade)}";
        CallableStatement stmt = conn.prepareCall(query);
        ResultSet rs = stmt.executeQuery();

return course;
    }

    private static void listAllCourses() throws SQLException {
        //enroll_to_course
        String query = "{CALL displayCourses()}";
        CallableStatement stmt = conn.prepareCall(query);
        ResultSet rs = stmt.executeQuery();
        while(rs.next())
        {
            System.out.println();
            System.out.print(" Course Code: " + rs.getString("uoscode"));
            System.out.print(" Semester: " + rs.getString("semester"));
            System.out.print(" Year: " + rs.getString("year"));
        }
    }

    private static void withdraw() throws SQLException {
        showAllEnrolledCourses();
        System.out.println("Enter the course id which you want to drop: ");
        Scanner myInput2 = new Scanner(System.in);
        String course = myInput2.nextLine();
        if(isCourseSelectionCorrect(course)){
            updateWithdraw(course);

        }else{
            System.out.println("You have not entered the correct course");
        }


    }

    private static void updateWithdraw(String course) throws SQLException {
        Calendar now = Calendar.getInstance();
        int a = (now.get(Calendar.MONTH) + 1);
        int b = (now.get(Calendar.YEAR));

        String semester = a > 6 ? "Q2" : "Q1";

        String withdrawCourseQuery = "{CALL withdrawCourse(?,?,?)}";
        CallableStatement stmt = conn.prepareCall(withdrawCourseQuery);
        stmt.setInt(1, userId);
        stmt.setString(2, course);
        stmt.setString(3, semester);
        stmt.executeQuery();
    }

}
