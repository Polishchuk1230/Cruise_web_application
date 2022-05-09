package ua.com.cruises.model.tags;

import jakarta.servlet.jsp.JspWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.com.cruises.model.Boat;
import ua.com.cruises.model.Crew;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public class BoatPresentationTag extends LocalizedTag {
    private static Logger logger = LogManager.getLogger(BoatPresentationTag.class);
    private Boat obj;

    @Override
    public int doStartTag() {
        Crew crew = obj.getCrew();
        String crewStringRepresentation = crew == null ?
                "[" + localeMessage("tag.boat.message.no.crew").orElse("???") + "]" :
                crew.getCadres().stream()
                        .filter(Objects::nonNull)
                        .map(cadre -> String.format("%s %s [%s: %s]",
                                cadre.getName(),
                                cadre.getSurname(),
                                localeMessage("tag.boat.message.crew.position").orElse("???"),
                                cadre.getPosition()))
                        .collect(Collectors.joining("<br />  "));

        try {
            pageContext.getOut().print(String.format("""
                    🛳 %s[%d], %s[%s], %s[%d]<br />
                    👥 %s:<br />
                    %s
                                    """,
                    localeMessage("tag.boat.message.id").orElse("???"), obj.getId(),
                    localeMessage("tag.boat.message.name").orElse("???"), obj.getName(),
                    localeMessage("tag.boat.message.capacity").orElse("???"), obj.getCapacity(),
                    localeMessage("tag.boat.message.crew").orElse("???"), crewStringRepresentation));
        } catch (IOException e) {
            logger.error(e);
        }

        return SKIP_BODY;
    }

    public Boat getObj() {
        return obj;
    }

    public void setObj(Boat obj) {
        this.obj = obj;
    }
}
