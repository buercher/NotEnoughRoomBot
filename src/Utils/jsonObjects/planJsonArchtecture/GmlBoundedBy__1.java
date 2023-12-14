
package Utils.jsonObjects.planJsonArchtecture;

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
public class GmlBoundedBy__1 {

    @JsonProperty("gml:Envelope")
    @Valid
    private GmlEnvelope__1 gmlEnvelope;

    @JsonProperty("gml:Envelope")
    public GmlEnvelope__1 getGmlEnvelope() {
        return gmlEnvelope;
    }

    @JsonProperty("gml:Envelope")
    public void setGmlEnvelope(GmlEnvelope__1 gmlEnvelope) {
        this.gmlEnvelope = gmlEnvelope;
    }

    public GmlBoundedBy__1 withGmlEnvelope(GmlEnvelope__1 gmlEnvelope) {
        this.gmlEnvelope = gmlEnvelope;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GmlBoundedBy__1
                .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("gmlEnvelope");
        sb.append('=');
        sb.append(((this.gmlEnvelope == null) ? "<null>" : this.gmlEnvelope));
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
        result = ((result * 31) + ((this.gmlEnvelope == null) ? 0 : this.gmlEnvelope.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GmlBoundedBy__1) == false) {
            return false;
        }
        GmlBoundedBy__1 rhs = ((GmlBoundedBy__1) other);
        return (
                (this.gmlEnvelope == rhs.gmlEnvelope) ||
                        ((this.gmlEnvelope != null) &&
                                this.gmlEnvelope.equals(rhs.gmlEnvelope)));
    }

}
