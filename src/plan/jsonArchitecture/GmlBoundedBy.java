
package plan.jsonArchitecture;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gml:Envelope"
})
@Generated("jsonschema2pojo")
public class GmlBoundedBy {

    @JsonProperty("gml:Envelope")
    @Valid
    private GmlEnvelope gmlEnvelope;

    @JsonProperty("gml:Envelope")
    public GmlEnvelope getGmlEnvelope() {
        return gmlEnvelope;
    }

    @JsonProperty("gml:Envelope")
    public void setGmlEnvelope(GmlEnvelope gmlEnvelope) {
        this.gmlEnvelope = gmlEnvelope;
    }

    public GmlBoundedBy withGmlEnvelope(GmlEnvelope gmlEnvelope) {
        this.gmlEnvelope = gmlEnvelope;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GmlBoundedBy.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("gmlEnvelope");
        sb.append('=');
        sb.append(((this.gmlEnvelope == null)?"<null>":this.gmlEnvelope));
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
        result = ((result* 31)+((this.gmlEnvelope == null)? 0 :this.gmlEnvelope.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GmlBoundedBy) == false) {
            return false;
        }
        GmlBoundedBy rhs = ((GmlBoundedBy) other);
        return ((this.gmlEnvelope == rhs.gmlEnvelope)||((this.gmlEnvelope!= null)&&this.gmlEnvelope.equals(rhs.gmlEnvelope)));
    }

}
