
package utils.jsonObjects.planJsonArchtecture;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "gml:Polygon"
})
@Generated("jsonschema2pojo")
public class GmlSurfaceMember {

    @JsonProperty("gml:Polygon")
    @Valid
    private GmlPolygon gmlPolygon;

    @JsonProperty("gml:Polygon")
    public GmlPolygon getGmlPolygon() {
        return gmlPolygon;
    }

    @JsonProperty("gml:Polygon")
    public void setGmlPolygon(GmlPolygon gmlPolygon) {
        this.gmlPolygon = gmlPolygon;
    }

    public GmlSurfaceMember withGmlPolygon(GmlPolygon gmlPolygon) {
        this.gmlPolygon = gmlPolygon;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GmlSurfaceMember
                .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("gmlPolygon");
        sb.append('=');
        sb.append(((this.gmlPolygon == null) ? "<null>" : this.gmlPolygon));
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
        result = ((result * 31) + ((this.gmlPolygon == null) ? 0 : this.gmlPolygon.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GmlSurfaceMember) == false) {
            return false;
        }
        GmlSurfaceMember rhs = ((GmlSurfaceMember) other);
        return ((this.gmlPolygon == rhs.gmlPolygon) ||
                ((this.gmlPolygon != null) &&
                        this.gmlPolygon.equals(rhs.gmlPolygon)));
    }

}
