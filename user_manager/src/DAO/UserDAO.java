package DAO;

import model.User;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private String jdbcURL = "jdbc:mysql://localhost:3306/demo?useSSL=false";
    private String jdbcUser = "root";
    private String jdbcPassword = "123456";
    private static final String INSERT_USERS_SQL = "INSERT INTO users" + "(name,email,country)VALUES" + "(?,?,?);";
    private static final String SELECT_USER_BY_ID = "select id,name,email,country from users where id=?;";
    private static final String SELECT_ALL_USERS_AND_SORT = "SELECT * FROM users ORDER BY name ;";
    private static final String DELETE_USERS_SQL = "delete from users where id=?;";
    private static final String UPDATE_USERS_SQL = "update users set name=?,email=?,country=? where id=?;";
    private static final String SELECT_USER_BY_COUNTRY = "select id,name,email,country from users where country=?;";

    private static final String SQL_INSERT = "INSERT INTO EMPLOYEE (NAME, SALARY, CREATED_DATE) VALUES (?,?,?)";

    private static final String SQL_UPDATE = "UPDATE EMPLOYEE SET SALARY=? WHERE NAME=?";

    private static final String SQL_TABLE_CREATE = "CREATE TABLE EMPLOYEE"

            + "("

            + " ID serial,"

            + " NAME varchar(100) NOT NULL,"

            + " SALARY numeric(15, 2) NOT NULL,"

            + " CREATED_DATE timestamp,"

            + " PRIMARY KEY (ID)"

            + ")";

    private static final String SQL_TABLE_DROP = "DROP TABLE IF EXISTS EMPLOYEE";


    public UserDAO() {
    }

    protected Connection getConnection(){
        Connection connection = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL,jdbcUser,jdbcPassword);
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return connection;
    }

    private void printSQLException(SQLException ex){
        for (Throwable e:ex){
            if (e instanceof SQLException){
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null){
                    System.out.println("Cause: " + t);
                    t=t.getCause();
                }
            }
        }
    }

    @Override
    public void insertUser(User user) throws SQLException {
        System.out.println("INSERT_USER_SQL");

        try{
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL);
            preparedStatement.setString(1,user.getName());
            preparedStatement.setString(2,user.getEmail());
            preparedStatement.setString(3,user.getCountry());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            printSQLException(e);
        }
    }

    @Override
    public User selectUser(int id) {
        User user = null;
        try(Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);){
            preparedStatement.setInt(1,id);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String country = resultSet.getString("country");
                user = new User(id,name,email,country);
            }
        }catch (SQLException e){
            printSQLException(e);
        }
        return user;
    }

    @Override
    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS_AND_SORT)){
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String country = resultSet.getString("country");
                users.add(new User(id, name, email, country));
            }
        }catch (SQLException e){
            printSQLException(e);
        }
        return users;
    }

    @Override
    public boolean deleteUser(int id) throws SQLException {
        boolean rowDelete;
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USERS_SQL);){
            preparedStatement.setInt(1,id);
            rowDelete = preparedStatement.executeUpdate()>0;
        }
        return rowDelete;
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USERS_SQL);) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());
            statement.setInt(4, user.getId());

            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }

    @Override
    public List<User> selectUserByCountry(String country) {
        List<User> list = new ArrayList<>();
        try(Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_COUNTRY);){
            preparedStatement.setString(1,country);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                list.add(new User(id,name,email,country));
            }
        }catch (SQLException e){
            printSQLException(e);
        }
        return list;
    }

    @Override
    public User getUserById(int id) {
        User user = null;
        String query = "{CALL get_user_by_id(?)}";
        try(Connection connection = getConnection();
        CallableStatement callableStatement = connection.prepareCall(query)){
            callableStatement.setInt(1,id);
            ResultSet resultSet = callableStatement.executeQuery();
            while (resultSet.next()){
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String country = resultSet.getString("country");
                user = new User(id,name,email,country);
            }
        }catch (SQLException e){
            printSQLException(e);
        }
        return user;
    }

    @Override
    public void insertUserStore(User user){
        String query = "{Call insert_user(?,?,?)}";
        try(Connection connection = getConnection();
        CallableStatement callableStatement = connection.prepareCall(query)){
            callableStatement.setString(1,user.getName());
            callableStatement.setString(2,user.getEmail());
            callableStatement.setString(3,user.getCountry());
            callableStatement.executeUpdate();
        }catch (SQLException e){
            printSQLException(e);
        }
    }

    @Override
    public void addUserTransaction(User user, int[] permisions) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtAssignment = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(INSERT_USERS_SQL, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getCountry());
            int rowAffected = pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            int userId = 0;
            if (rs.next())
                userId = rs.getInt(1);
            if (rowAffected == 1) {
                String sqlPivot = "INSERT INTO User_Permision(user_id,permision_id) "
                        + "VALUES(?,?)";
                pstmtAssignment = conn.prepareStatement(sqlPivot);
                for (int permisionId : permisions) {
                    pstmtAssignment.setInt(1, userId);
                    pstmtAssignment.setInt(2, permisionId);
                    pstmtAssignment.executeUpdate();
                }
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException ex) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (pstmtAssignment != null) pstmtAssignment.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void insertUpdateWithoutTransaction() {

        try (Connection conn = getConnection();

             Statement statement = conn.createStatement();

             PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT);

             PreparedStatement psUpdate = conn.prepareStatement(SQL_UPDATE)) {



            statement.execute(SQL_TABLE_DROP);

            statement.execute(SQL_TABLE_CREATE);



            // Run list of insert commands

            psInsert.setString(1, "Quynh");

            psInsert.setBigDecimal(2, new BigDecimal(10));

            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            psInsert.execute();



            psInsert.setString(1, "Ngan");

            psInsert.setBigDecimal(2, new BigDecimal(20));

            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            psInsert.execute();



            // Run list of update commands



            // below line caused error, test transaction

            // org.postgresql.util.PSQLException: No value specified for parameter 1.

            psUpdate.setBigDecimal(2, new BigDecimal(999.99));



            //psUpdate.setBigDecimal(1, new BigDecimal(999.99));

            psUpdate.setString(2, "Quynh");

            psUpdate.execute();



        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @Override
    public void insertUpdateUseTransaction() {

        try (Connection conn = getConnection();

             Statement statement = conn.createStatement();

             PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT);

             PreparedStatement psUpdate = conn.prepareStatement(SQL_UPDATE)) {

            statement.execute(SQL_TABLE_DROP);

            statement.execute(SQL_TABLE_CREATE);

            // start transaction block

            conn.setAutoCommit(false); // default true

            // Run list of insert commands

            psInsert.setString(1, "Quynh");

            psInsert.setBigDecimal(2, new BigDecimal(10));

            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            psInsert.execute();



            psInsert.setString(1, "Ngan");

            psInsert.setBigDecimal(2, new BigDecimal(20));

            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            psInsert.execute();



            // Run list of update commands



            // below line caused error, test transaction

            // org.postgresql.util.PSQLException: No value specified for parameter 1.

            psUpdate.setBigDecimal(2, new BigDecimal(999.99));



            //psUpdate.setBigDecimal(1, new BigDecimal(999.99));

            psUpdate.setString(2, "Quynh");

            psUpdate.execute();



            // end transaction block, commit changes

            conn.commit();

            // good practice to set it back to default true

            conn.setAutoCommit(true);



        } catch (Exception e) {

            System.out.println(e.getMessage());

            e.printStackTrace();

        }

    }
}
