package ua.com.cruises.model.tags;

import jakarta.servlet.jsp.JspWriter;
import ua.com.cruises.model.Cruise;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public class CruisePresentationTag extends LocalizedTag {
    private Cruise obj;

    @Override
    public int doStartTag() {
        //Preparing the cruise's route string presentation to format:
        //⚓Port1 → ⚓Port2 → ... → ⚓PortN
        String route = obj.getPorts().stream()
                .map(port -> "⚓"+port.getCity())
                .collect(Collectors.joining(" → "));

        String status = switch (obj.getCruiseStatus()) {
            case REGISTRATION_IN_PROGRESS ->
                    localeMessage("tag.cruise.message.status.registration.in.progress").orElse("???");
            case REGISTRATION_CLOSED ->
                    localeMessage("tag.cruise.message.status.registration.closed").orElse("???");
            case IN_PROGRESS ->
                    localeMessage("tag.cruise.message.status.in.progress").orElse("???");
            case COMPLETED ->
                    localeMessage("tag.cruise.message.status.completed").orElse("???");
        };

        String representation = String.format(
                """
                        🛳[%s] 💺[%d/%d]<br />
                        📅[%s : %s] [%s: %s]<br />
                        🗺[%s]<br />
                        $[%d]<br />
                """,
                obj.getBoat().getName(), obj.getBookedSeats(), obj.getBoat().getCapacity(),
                obj.getStartDate(), obj.getEndDate(),
                localeMessage("tag.cruise.message.status").orElse("???"), status,
                route,
                obj.getCost()
        );

        //Also adds a Boat's Crew to the representation (if it pulled out from DB)
        if (obj.getBoat() != null && obj.getBoat().getCrew() != null) {
            String crewStringRepresentation =
                    "<br />👥 " + localeMessage("tag.boat.message.crew").orElse("???") + ":<br />"
                    + obj.getBoat().getCrew().getCadres().stream()
                            .filter(Objects::nonNull)
                            .map(cadre -> String.format("%s %s [%s: %s]",
                                    cadre.getName(),
                                    cadre.getSurname(),
                                    localeMessage("tag.boat.message.crew.position").orElse("???"),
                                    cadre.getPosition()))
                            .collect(Collectors.joining("<br />  "));
            representation += crewStringRepresentation;
        }

        try {
            pageContext.getOut().print(representation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SKIP_BODY;
    }

    public Cruise getObj() {
        return obj;
    }

    public void setObj(Cruise obj) {
        this.obj = obj;
    }
}
