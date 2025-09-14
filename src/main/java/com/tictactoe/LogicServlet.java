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

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        Field field = extractField(session);

        if (checkWin(response, session, field)) {
            return;
        }

        int index = getSelectedIndex(request);

        Sign currentSign = field.getFieldData().get(index);

        // Проверка, что в ячейке нет знака
        if (currentSign != Sign.EMPTY) {
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
            rd.forward(request, response);
            return;
        }
        // Ставим крестик
        field.getField().put(index, Sign.CROSS);
        if (checkWin(response, session, field)) {
            return;
        }

        // Ставим нолик
        int emptyIndex = field.getEmptyFieldIndex();
        if (emptyIndex >= 0 && emptyIndex <= 8) {
            field.getField().put(emptyIndex, Sign.NOUGHT);
            if (checkWin(response, session, field)) {
                return;
            }
        }


        List<Sign> data = field.getFieldData();

        session.setAttribute("data", data);
        session.setAttribute("field", field);

        response.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest request){
        String index = request.getParameter("click");
        boolean isNumeric = index.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(index) : 0;
    }

    private Field extractField(HttpSession session){
        Object fieldAttribute = session.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()){
            session.invalidate();
            throw new RuntimeException("Field is not a valid field. Session has been broken");
        }
        return (Field) fieldAttribute;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession session, Field field) throws IOException {
        Sign winnerSign = field.checkWin();

        if (Sign.CROSS == winnerSign || Sign.NOUGHT == winnerSign) {
            // Атрибут, сохраняющий победителя
            session.setAttribute("winner", winnerSign);
            // Список значков
            List<Sign> data = field.getFieldData();
            // Обновление списка значков в сессии
            session.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return true;
        }

        return false;
    }
}
