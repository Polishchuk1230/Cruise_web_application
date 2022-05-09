package ua.com.cruises.model.tags;

import jakarta.servlet.jsp.JspWriter;
import ua.com.cruises.model.Order;

import java.io.IOException;

public class OrderPresentationTag extends LocalizedTag {
    private Order obj;

    @Override
    public int doStartTag() {
        JspWriter out = pageContext.getOut();
        try {
            out.print(
                    String.format(
                            """
                                    %s: %s<br />
                                    🛳[%s] 💺[%d] $[%d]<br />
                                    📅[%s]<br />
                                    ✓[%s]
                            """,
                            localeMessage("tag.order.message.ordered.by").orElse("???"), obj.getUser().getUsername(),
                            obj.getCruise().getBoat().getName(), obj.getSeats(), obj.getCruise().getCost()*obj.getSeats(),
                            obj.getBookTime(),
                            obj.isConfirmed()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SKIP_BODY;
    }

    public Order getObj() {
        return obj;
    }
    public void setObj(Order obj) {
        this.obj = obj;
    }
}
