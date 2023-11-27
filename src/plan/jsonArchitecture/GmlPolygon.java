
package plan.jsonArchitecture;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gml:exterior"
})
@Generated("jsonschema2pojo")
public class GmlPolygon {

    @JsonProperty("gml:exterior")
    @Valid
    private GmlExterior gmlExterior;

    @JsonProperty("gml:exterior")
    public GmlExterior getGmlExterior() {
        return gmlExterior;
    }

    @JsonProperty("gml:exterior")
    public void setGmlExterior(GmlExterior gmlExterior) {
        this.gmlExterior = gmlExterior;
    }

    public GmlPolygon withGmlExterior(GmlExterior gmlExterior) {
        this.gmlExterior = gmlExterior;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GmlPolygon.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("gmlExterior");
        sb.append('=');
        sb.append(((this.gmlExterior == null)?"<null>":this.gmlExterior));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.gmlExterior == null)? 0 :this.gmlExterior.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GmlPolygon) == false) {
            return false;
        }
        GmlPolygon rhs = ((GmlPolygon) other);
        return ((this.gmlExterior == rhs.gmlExterior)||((this.gmlExterior!= null)&&this.gmlExterior.equals(rhs.gmlExterior)));
    }

}
