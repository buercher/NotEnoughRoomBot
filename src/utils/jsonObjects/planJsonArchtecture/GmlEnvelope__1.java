
package utils.jsonObjects.planJsonArchtecture;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "srsName",
        "gml:lowerCorner",
        "gml:upperCorner"
})
@Generated("jsonschema2pojo")
public class GmlEnvelope__1 {

    @JsonProperty("srsName")
    private String srsName;
    @JsonProperty("gml:lowerCorner")
    private String gmlLowerCorner;
    @JsonProperty("gml:upperCorner")
    private String gmlUpperCorner;

    @JsonProperty("srsName")
    public String getSrsName() {
        return srsName;
    }

    @JsonProperty("srsName")
    public void setSrsName(String srsName) {
        this.srsName = srsName;
    }

    public GmlEnvelope__1 withSrsName(String srsName) {
        this.srsName = srsName;
        return this;
    }

    @JsonProperty("gml:lowerCorner")
    public String getGmlLowerCorner() {
        return gmlLowerCorner;
    }

    @JsonProperty("gml:lowerCorner")
    public void setGmlLowerCorner(String gmlLowerCorner) {
        this.gmlLowerCorner = gmlLowerCorner;
    }

    public GmlEnvelope__1 withGmlLowerCorner(String gmlLowerCorner) {
        this.gmlLowerCorner = gmlLowerCorner;
        return this;
    }

    @JsonProperty("gml:upperCorner")
    public String getGmlUpperCorner() {
        return gmlUpperCorner;
    }

    @JsonProperty("gml:upperCorner")
    public void setGmlUpperCorner(String gmlUpperCorner) {
        this.gmlUpperCorner = gmlUpperCorner;
    }

    public GmlEnvelope__1 withGmlUpperCorner(String gmlUpperCorner) {
        this.gmlUpperCorner = gmlUpperCorner;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GmlEnvelope__1
                .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("srsName");
        sb.append('=');
        sb.append(((this.srsName == null) ? "<null>" : this.srsName));
        sb.append(',');
        sb.append("gmlLowerCorner");
        sb.append('=');
        sb.append(((this.gmlLowerCorner == null) ? "<null>" : this.gmlLowerCorner));
        sb.append(',');
        sb.append("gmlUpperCorner");
        sb.append('=');
        sb.append(((this.gmlUpperCorner == null) ? "<null>" : this.gmlUpperCorner));
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
        result = ((result * 31) + ((this.gmlUpperCorner == null) ? 0 : this.gmlUpperCorner.hashCode()));
        result = ((result * 31) + ((this.gmlLowerCorner == null) ? 0 : this.gmlLowerCorner.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GmlEnvelope__1) == false) {
            return false;
        }
        GmlEnvelope__1 rhs = ((GmlEnvelope__1) other);
        return ((((this.srsName == rhs.srsName) ||
                ((this.srsName != null) &&
                        this.srsName.equals(rhs.srsName))) &&
                ((this.gmlUpperCorner == rhs.gmlUpperCorner) ||
                        ((this.gmlUpperCorner != null) &&
                                this.gmlUpperCorner.equals(rhs.gmlUpperCorner)))) &&
                ((this.gmlLowerCorner == rhs.gmlLowerCorner) ||
                        ((this.gmlLowerCorner != null) &&
                                this.gmlLowerCorner.equals(rhs.gmlLowerCorner))));
    }

}
