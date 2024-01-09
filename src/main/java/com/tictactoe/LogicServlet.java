package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession currentSession = request.getSession();

        Field field = getField(currentSession);

        int selectedIndex = getSelectedIndex(request);

        Sign sign = field.getField().get(selectedIndex);

        if (Objects.requireNonNull(sign) != Sign.EMPTY) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
            return;
        }


        field.getField().put(selectedIndex, Sign.CROSS);

        if (checkWin(response, currentSession, field)) {
            return;
        }

        int emptyIndex = field.getEmptyFieldIndex();

        if (emptyIndex >= 0) {
            field.getField().put(emptyIndex, Sign.NOUGHT);
            if (checkWin(response, currentSession, field)) {
                return;
            }
            List<Sign> data = field.getFieldData();

            currentSession.setAttribute("field", field);
            currentSession.setAttribute("data", data);


            response.sendRedirect("/index.jsp");
        }
        else {
            currentSession.setAttribute("draw", true);

            List<Sign> data = field.getFieldData();

            currentSession.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return;
        }
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        int res = 0;
        try {
            res = Integer.parseInt(click);
        } catch (NumberFormatException ignored) {
        }
        return res;
    }

    private Field getField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (fieldAttribute == null || Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is invalid, try again.");
        }
        return (Field) fieldAttribute;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (winner == Sign.CROSS || winner == Sign.NOUGHT) {
            currentSession.setAttribute("winner", winner);

            List<Sign> data = field.getFieldData();

            currentSession.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }

}