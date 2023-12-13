
package jsonObjects.planJsonArchtecture;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "gml:posList"
})
@Generated("jsonschema2pojo")
public class GmlLinearRing {

    @JsonProperty("gml:posList")
    @Valid
    private GmlPosList gmlPosList;

    @JsonProperty("gml:posList")
    public GmlPosList getGmlPosList() {
        return gmlPosList;
    }

    @JsonProperty("gml:posList")
    public void setGmlPosList(GmlPosList gmlPosList) {
        this.gmlPosList = gmlPosList;
    }

    public GmlLinearRing withGmlPosList(GmlPosList gmlPosList) {
        this.gmlPosList = gmlPosList;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GmlLinearRing
                .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("gmlPosList");
        sb.append('=');
        sb.append(((this.gmlPosList == null) ? "<null>" : this.gmlPosList));
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
        result = ((result * 31) + ((this.gmlPosList == null) ? 0 : this.gmlPosList.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GmlLinearRing) == false) {
            return false;
        }
        GmlLinearRing rhs = ((GmlLinearRing) other);
        return ((this.gmlPosList == rhs.gmlPosList) ||
                ((this.gmlPosList != null) &&
                        this.gmlPosList.equals(rhs.gmlPosList)));
    }

}
