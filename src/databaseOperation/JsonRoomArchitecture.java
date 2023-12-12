
package databaseOperation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Rooms",
        "Source",
        "Buildings",
        "Plan_Name",
        "Pdf_Link",
        "Plan_Link",
        "Type",
        "Places",
        "Horaire",
        "Name"
})
@Generated("jsonschema2pojo")
public class JsonRoomArchitecture {

    @JsonProperty("Rooms")
    private String rooms;
    @JsonProperty("Source")
    private String source;
    @JsonProperty("Buildings")
    private String buildings;
    @JsonProperty("Plan_Name")
    private String planName;
    @JsonProperty("Pdf_Link")
    private String pdfLink;
    @JsonProperty("Plan_Link")
    private String planLink;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Places")
    private String places;
    @JsonProperty("Horaire")
    @Valid
    private List<Integer> horaire = new ArrayList<Integer>();
    @JsonProperty("Name")
    private String name;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public JsonRoomArchitecture() {
    }

    /**
     * @param horaire
     * @param rooms
     * @param places
     * @param buildings
     * @param name
     * @param planName
     * @param source
     * @param pdfLink
     * @param type
     * @param planLink
     */
    public JsonRoomArchitecture(String rooms, String source, String buildings, String planName, String pdfLink, String planLink, String type, String places, List<Integer> horaire, String name) {
        super();
        this.rooms = rooms;
        this.source = source;
        this.buildings = buildings;
        this.planName = planName;
        this.pdfLink = pdfLink;
        this.planLink = planLink;
        this.type = type;
        this.places = places;
        this.horaire = horaire;
        this.name = name;
    }

    @JsonProperty("Rooms")
    public String getRooms() {
        return rooms;
    }

    @JsonProperty("Rooms")
    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public JsonRoomArchitecture withRooms(String rooms) {
        this.rooms = rooms;
        return this;
    }

    @JsonProperty("Source")
    public String getSource() {
        return source;
    }

    @JsonProperty("Source")
    public void setSource(String source) {
        this.source = source;
    }

    public JsonRoomArchitecture withSource(String source) {
        this.source = source;
        return this;
    }

    @JsonProperty("Buildings")
    public String getBuildings() {
        return buildings;
    }

    @JsonProperty("Buildings")
    public void setBuildings(String buildings) {
        this.buildings = buildings;
    }

    public JsonRoomArchitecture withBuildings(String buildings) {
        this.buildings = buildings;
        return this;
    }

    @JsonProperty("Plan_Name")
    public String getPlanName() {
        return planName;
    }

    @JsonProperty("Plan_Name")
    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public JsonRoomArchitecture withPlanName(String planName) {
        this.planName = planName;
        return this;
    }

    @JsonProperty("Pdf_Link")
    public String getPdfLink() {
        return pdfLink;
    }

    @JsonProperty("Pdf_Link")
    public void setPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
    }

    public JsonRoomArchitecture withPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
        return this;
    }

    @JsonProperty("Plan_Link")
    public String getPlanLink() {
        return planLink;
    }

    @JsonProperty("Plan_Link")
    public void setPlanLink(String planLink) {
        this.planLink = planLink;
    }

    public JsonRoomArchitecture withPlanLink(String planLink) {
        this.planLink = planLink;
        return this;
    }

    @JsonProperty("Type")
    public String getType() {
        return type;
    }

    @JsonProperty("Type")
    public void setType(String type) {
        this.type = type;
    }

    public JsonRoomArchitecture withType(String type) {
        this.type = type;
        return this;
    }

    @JsonProperty("Places")
    public String getPlaces() {
        return places;
    }

    @JsonProperty("Places")
    public void setPlaces(String places) {
        this.places = places;
    }

    public JsonRoomArchitecture withPlaces(String places) {
        this.places = places;
        return this;
    }

    @JsonProperty("Horaire")
    public List<Integer> getHoraire() {
        return horaire;
    }

    @JsonProperty("Horaire")
    public void setHoraire(List<Integer> horaire) {
        this.horaire = horaire;
    }

    public JsonRoomArchitecture withHoraire(List<Integer> horaire) {
        this.horaire = horaire;
        return this;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    public JsonRoomArchitecture withName(String name) {
        this.name = name;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public JsonRoomArchitecture withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(JsonRoomArchitecture.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("rooms");
        sb.append('=');
        sb.append(((this.rooms == null) ? "<null>" : this.rooms));
        sb.append(',');
        sb.append("source");
        sb.append('=');
        sb.append(((this.source == null) ? "<null>" : this.source));
        sb.append(',');
        sb.append("buildings");
        sb.append('=');
        sb.append(((this.buildings == null) ? "<null>" : this.buildings));
        sb.append(',');
        sb.append("planName");
        sb.append('=');
        sb.append(((this.planName == null) ? "<null>" : this.planName));
        sb.append(',');
        sb.append("pdfLink");
        sb.append('=');
        sb.append(((this.pdfLink == null) ? "<null>" : this.pdfLink));
        sb.append(',');
        sb.append("planLink");
        sb.append('=');
        sb.append(((this.planLink == null) ? "<null>" : this.planLink));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null) ? "<null>" : this.type));
        sb.append(',');
        sb.append("places");
        sb.append('=');
        sb.append(((this.places == null) ? "<null>" : this.places));
        sb.append(',');
        sb.append("horaire");
        sb.append('=');
        sb.append(((this.horaire == null) ? "<null>" : this.horaire));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null) ? "<null>" : this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result * 31) + ((this.horaire == null) ? 0 : this.horaire.hashCode()));
        result = ((result * 31) + ((this.rooms == null) ? 0 : this.rooms.hashCode()));
        result = ((result * 31) + ((this.places == null) ? 0 : this.places.hashCode()));
        result = ((result * 31) + ((this.buildings == null) ? 0 : this.buildings.hashCode()));
        result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
        result = ((result * 31) + ((this.planName == null) ? 0 : this.planName.hashCode()));
        result = ((result * 31) + ((this.source == null) ? 0 : this.source.hashCode()));
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        result = ((result * 31) + ((this.pdfLink == null) ? 0 : this.pdfLink.hashCode()));
        result = ((result * 31) + ((this.type == null) ? 0 : this.type.hashCode()));
        result = ((result * 31) + ((this.planLink == null) ? 0 : this.planLink.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof JsonRoomArchitecture) == false) {
            return false;
        }
        JsonRoomArchitecture rhs = ((JsonRoomArchitecture) other);
        return ((((((((((((this.horaire == rhs.horaire) ||
                ((this.horaire != null) &&
                        this.horaire.equals(rhs.horaire))) &&
                ((this.rooms == rhs.rooms) ||
                        ((this.rooms != null) &&
                                this.rooms.equals(rhs.rooms)))) &&
                ((this.places == rhs.places) ||
                        ((this.places != null) &&
                                this.places.equals(rhs.places)))) &&
                ((this.buildings == rhs.buildings) ||
                        ((this.buildings != null) &&
                                this.buildings.equals(rhs.buildings)))) &&
                ((this.name == rhs.name) ||
                        ((this.name != null) &&
                                this.name.equals(rhs.name)))) &&
                ((this.planName == rhs.planName) ||
                        ((this.planName != null) &&
                                this.planName.equals(rhs.planName)))) &&
                ((this.source == rhs.source) ||
                        ((this.source != null) &&
                                this.source.equals(rhs.source)))) &&
                ((this.additionalProperties == rhs.additionalProperties) ||
                        ((this.additionalProperties != null) &&
                                this.additionalProperties.equals(rhs.additionalProperties)))) &&
                ((this.pdfLink == rhs.pdfLink) ||
                        ((this.pdfLink != null) &&
                                this.pdfLink.equals(rhs.pdfLink)))) &&
                ((this.type == rhs.type) ||
                        ((this.type != null) &&
                                this.type.equals(rhs.type)))) &&
                ((this.planLink == rhs.planLink) ||
                        ((this.planLink != null) &&
                                this.planLink.equals(rhs.planLink))));
    }

}
