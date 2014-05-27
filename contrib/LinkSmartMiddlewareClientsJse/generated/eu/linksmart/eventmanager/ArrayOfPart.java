
package eu.linksmart.eventmanager;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ArrayOfPart {

    @XmlElement(name = "Part", nillable = true, namespace = "http://eventmanager.linksmart.eu")
    protected List<Part> part;

 
    public List<Part> getPart() {
        if (part == null) {
            part = new ArrayList<Part>();
        }
        return this.part;
    }

}
