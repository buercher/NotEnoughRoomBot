
package utils.jsonObjects.planJsonArchtecture;

import java.util.List;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "srsName",
        "gml:surfaceMember"
})
@Generated("jsonschema2pojo")
public class GmlMultiSurface {

    @JsonProperty("srsName")
    private String srsName;
    @JsonProperty("gml:surfaceMember")
    @Valid
    private List<GmlSurfaceMember> gmlSurfaceMember;

    @JsonProperty("srsName")
    public String getSrsName() {
        return srsName;
    }

    @JsonProperty("srsName")
    public void setSrsName(String srsName) {
        this.srsName = srsName;
    }

    public GmlMultiSurface withSrsName(String srsName) {
        this.srsName = srsName;
        return this;
    }

    @JsonProperty("gml:surfaceMember")
    public List<GmlSurfaceMember> getGmlSurfaceMember() {
        return gmlSurfaceMember;
    }

    @JsonProperty("gml:surfaceMember")
    public void setGmlSurfaceMember(List<GmlSurfaceMember> gmlSurfaceMember) {
        this.gmlSurfaceMember = gmlSurfaceMember;
    }

    public GmlMultiSurface withGmlSurfaceMember(List<GmlSurfaceMember> gmlSurfaceMember) {
        this.gmlSurfaceMember = gmlSurfaceMember;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GmlMultiSurface
                .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("srsName");
        sb.append('=');
        sb.append(((this.srsName == null) ? "<null>" : this.srsName));
        sb.append(',');
        sb.append("gmlSurfaceMember");
        sb.append('=');
        sb.append(((this.gmlSurfaceMember == null) ? "<null>" : this.gmlSurfaceMember));
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
        result = ((result * 31) + ((this.srsName == null) ? 0 : this.srsName.hashCode()));
        result = ((result * 31) + ((this.gmlSurfaceMember == null) ? 0 : this.gmlSurfaceMember.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GmlMultiSurface) == false) {
            return false;
        }
        GmlMultiSurface rhs = ((GmlMultiSurface) other);
        return (((this.srsName == rhs.srsName) ||
                ((this.srsName != null) &&
                        this.srsName.equals(rhs.srsName))) &&
                ((this.gmlSurfaceMember == rhs.gmlSurfaceMember) ||
                        ((this.gmlSurfaceMember != null) &&
                                this.gmlSurfaceMember.equals(rhs.gmlSurfaceMember))));
    }

}
