package jsonObjects;

import java.util.*;
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
        "Source",
        "Horaire"
})
@Generated("jsonschema2pojo")
public class Datajson {

    @JsonProperty("Source")
    private String source;
    @JsonProperty("Horaire")
    @Valid
    private Set<Integer> horaire = new TreeSet<Integer>();
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public Datajson() {
    }

    /**
     * @param horaire
     * @param source
     */
    public Datajson(String source, Set<Integer> horaire) {
        super();
        this.source = source;
        this.horaire = horaire;
    }

    @JsonProperty("Source")
    public String getSource() {
        return source;
    }

    @JsonProperty("Source")
    public void setSource(String source) {
        this.source = source;
    }

    public Datajson withSource(String source) {
        this.source = source;
        return this;
    }

    @JsonProperty("Horaire")
    public Set<Integer> getHoraire() {
        return horaire;
    }

    @JsonProperty("Horaire")
    public void setHoraire(Set<Integer> horaire) {
        this.horaire = horaire;
    }

    public Datajson withHoraire(Set<Integer> horaire) {
        this.horaire = horaire;
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

    public Datajson withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Datajson
                        .class
                        .getName())
                .append('@')
                .append(Integer
                        .toHexString(System
                                .identityHashCode(this)))
                .append('[');
        sb.append("source");
        sb.append('=');
        sb.append(((this.source == null) ? "<null>" : this.source));
        sb.append(',');
        sb.append("horaire");
        sb.append('=');
        sb.append(((this.horaire == null) ? "<null>" : this.horaire));
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
        result = ((result * 31) + ((this.source == null) ? 0 : this.source.hashCode()));
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Datajson) == false) {
            return false;
        }
        Datajson rhs = ((Datajson) other);
        return ((((this.horaire == rhs.horaire) ||
                ((this.horaire != null) &&
                        this.horaire.equals(rhs.horaire))) &&
                ((this.source == rhs.source) ||
                        ((this.source != null) &&
                                this.source.equals(rhs.source)))) &&
                ((this.additionalProperties == rhs.additionalProperties) ||
                        ((this.additionalProperties != null) &&
                                this.additionalProperties.equals(rhs.additionalProperties))));
    }

}
