
package plan.jsonArchitecture;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gml:MultiSurface"
})
@Generated("jsonschema2pojo")
public class MsGeom {

    @JsonProperty("gml:MultiSurface")
    @Valid
    private GmlMultiSurface gmlMultiSurface;

    @JsonProperty("gml:MultiSurface")
    public GmlMultiSurface getGmlMultiSurface() {
        return gmlMultiSurface;
    }

    @JsonProperty("gml:MultiSurface")
    public void setGmlMultiSurface(GmlMultiSurface gmlMultiSurface) {
        this.gmlMultiSurface = gmlMultiSurface;
    }

    public MsGeom withGmlMultiSurface(GmlMultiSurface gmlMultiSurface) {
        this.gmlMultiSurface = gmlMultiSurface;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MsGeom.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("gmlMultiSurface");
        sb.append('=');
        sb.append(((this.gmlMultiSurface == null)?"<null>":this.gmlMultiSurface));
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
        result = ((result* 31)+((this.gmlMultiSurface == null)? 0 :this.gmlMultiSurface.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MsGeom) == false) {
            return false;
        }
        MsGeom rhs = ((MsGeom) other);
        return ((this.gmlMultiSurface == rhs.gmlMultiSurface)||((this.gmlMultiSurface!= null)&&this.gmlMultiSurface.equals(rhs.gmlMultiSurface)));
    }

}
