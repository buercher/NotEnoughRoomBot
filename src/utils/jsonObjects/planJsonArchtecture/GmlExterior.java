package utils.jsonObjects.planJsonArchtecture;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "gml:LinearRing"
})
@Generated("jsonschema2pojo")
public class GmlExterior {

    @JsonProperty("gml:LinearRing")
    @Valid
    private GmlLinearRing gmlLinearRing;

    @JsonProperty("gml:LinearRing")
    public GmlLinearRing getGmlLinearRing() {
        return gmlLinearRing;
    }

    @JsonProperty("gml:LinearRing")
    public void setGmlLinearRing(GmlLinearRing gmlLinearRing) {
        this.gmlLinearRing = gmlLinearRing;
    }

    public GmlExterior withGmlLinearRing(GmlLinearRing gmlLinearRing) {
        this.gmlLinearRing = gmlLinearRing;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GmlExterior
                .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("gmlLinearRing");
        sb.append('=');
        sb.append(((this.gmlLinearRing == null) ? "<null>" : this.gmlLinearRing));
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
        result = ((result * 31) + ((this.gmlLinearRing == null) ? 0 : this.gmlLinearRing.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GmlExterior) == false) {
            return false;
        }
        GmlExterior rhs = ((GmlExterior) other);
        return ((this.gmlLinearRing == rhs.gmlLinearRing) ||
                ((this.gmlLinearRing != null) &&
                        this.gmlLinearRing.equals(rhs.gmlLinearRing)));
    }

}
