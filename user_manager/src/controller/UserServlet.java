package controller;

import DAO.UserDAO;
import model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "UserServlet",urlPatterns = "/users")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        try {
            switch (action) {
                case "create":
                    insertUser(request, response);
                    break;
                case "edit":
                    updateUser(request, response);
                    break;
                case "select":
                    selectUser(request,response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "create":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                case "view":
                    viewUser(request,response);
                    break;
                case "select":
                    showSelectUserByCountryForm(request,response);
                    break;
                case "permision":
                    addUserPermision(request,response);
                    break;
                case "test-without-tran":
                    testWithoutTran(request,response);
                    break;
                case "test-use-tran":
                    testUseTran(request,response);
                    break;
                default:
                    listUser(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void listUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        List<User> listUser = userDAO.selectAllUsersStore();
        request.setAttribute("listUser", listUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/create.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        //User existingUser = userDAO.selectUser(id);
        User existingUser = userDAO.getUserById(id);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/edit.jsp");
        request.setAttribute("user", existingUser);
        dispatcher.forward(request, response);
    }

    private void showSelectUserByCountryForm(HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException{
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/selectByCountry.jsp");
        dispatcher.forward(request, response);
    }

    private void insertUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String country = request.getParameter("country");
        User newUser = new User(name, email, country);
        //userDAO.insertUser(newUser);
        userDAO.insertUserStore(newUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/create.jsp");
        dispatcher.forward(request, response);
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String country = request.getParameter("country");

        User book = new User(id, name, email, country);
        userDAO.updateUserStore(book);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/edit.jsp");
        dispatcher.forward(request, response);
    }

    private void selectUser(HttpServletRequest request,HttpServletResponse response) {
        String country = request.getParameter("country");
        List<User> existingUser = userDAO.selectUserByCountry(country);
        RequestDispatcher requestDispatcher;
        if (existingUser == null){
            requestDispatcher = request.getRequestDispatcher("error-404.jsp");
        }else{
            request.setAttribute("country",existingUser);
            requestDispatcher = request.getRequestDispatcher("user/selectByCountry.jsp");
        }
        try{
            requestDispatcher.forward(request,response);
        }catch (ServletException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        userDAO.deleteUserStore(id);

        List<User> listUser = userDAO.selectAllUsers();
        request.setAttribute("listUser", listUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/list.jsp");
        dispatcher.forward(request, response);
    }

    private void viewUser(HttpServletRequest request,HttpServletResponse response){
        int id = Integer.parseInt(request.getParameter("id"));
        User user = userDAO.selectUser(id);
        RequestDispatcher requestDispatcher;
        if (user == null){
            requestDispatcher = request.getRequestDispatcher("error-404.jsp");
        }else{
            request.setAttribute("user",user);
            requestDispatcher = request.getRequestDispatcher("user/view.jsp");
        }
        try{
            requestDispatcher.forward(request,response);
        }catch (ServletException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void addUserPermision(HttpServletRequest request, HttpServletResponse response) {
        User user = new User("kien", "kienhoang@gmail.com", "vn");
        int[] permision = {1, 2, 4};
        userDAO.addUserTransaction(user, permision);
    }

    private void testWithoutTran(HttpServletRequest request, HttpServletResponse response) {
        userDAO.insertUpdateWithoutTransaction();
    }

    private void testUseTran(HttpServletRequest request, HttpServletResponse response) {
        userDAO.insertUpdateUseTransaction();
    }
}
