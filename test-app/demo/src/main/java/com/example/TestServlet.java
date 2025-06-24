package com.example;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;

@WebServlet(name = "TestServlet", urlPatterns = { "/npe" })
public class TestServlet extends HttpServlet {

    public static class User {
        private String name;

        public String getName() {
            return name; // Retourne null si name n'est pas initialisé
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Simulation NullPointerException</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println(".case { margin-bottom: 30px; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }");
            out.println(".error { color: red; background-color: #ffeeee; padding: 10px; border-radius: 5px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Simulation de NullPointerException</h1>");

            // Cas 1: Méthode sur un objet null
            out.println("<div class='case'>");
            out.println("<h2>Cas 1: Méthode sur un objet null</h2>");
            try {
                User user = null;
                out.println("<p>Longueur du nom: " + user.getName().length() + "</p>");
            } catch (NullPointerException e) {
                out.println("<div class='error'>");
                out.println("<p><strong>Exception générée (cas 1):</strong></p>");
                e.printStackTrace(out);
                out.println("</div>");
            }
            out.println("</div>");

            // Cas 2: Champ non initialisé
            out.println("<div class='case'>");
            out.println("<h2>Cas 2: Champ non initialisé</h2>");
            try {
                User user2 = new User();
                out.println("<p>Longueur du nom: " + user2.getName().length() + "</p>");
            } catch (NullPointerException e) {
                out.println("<div class='error'>");
                out.println("<p><strong>Exception générée (cas 2):</strong></p>");
                e.printStackTrace(out);
                out.println("</div>");
            }
            out.println("</div>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}