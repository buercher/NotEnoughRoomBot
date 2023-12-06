
package plan.jsonArchitecture;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "wfs:FeatureCollection"
})
@Generated("jsonschema2pojo")
public class JsonRoom {

    @JsonProperty("wfs:FeatureCollection")
    @Valid
    private WfsFeatureCollection wfsFeatureCollection;

    @JsonProperty("wfs:FeatureCollection")
    public WfsFeatureCollection getWfsFeatureCollection() {
        return wfsFeatureCollection;
    }

    @JsonProperty("wfs:FeatureCollection")
    public void setWfsFeatureCollection(WfsFeatureCollection wfsFeatureCollection) {
        this.wfsFeatureCollection = wfsFeatureCollection;
    }

    public JsonRoom withWfsFeatureCollection(WfsFeatureCollection wfsFeatureCollection) {
        this.wfsFeatureCollection = wfsFeatureCollection;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(JsonRoom
                .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("wfsFeatureCollection");
        sb.append('=');
        sb.append(((this.wfsFeatureCollection == null) ? "<null>" : this.wfsFeatureCollection));
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
        result = ((result * 31) + ((this.wfsFeatureCollection == null) ? 0 : this.wfsFeatureCollection.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof JsonRoom) == false) {
            return false;
        }
        JsonRoom rhs = ((JsonRoom) other);
        return ((this.wfsFeatureCollection == rhs.wfsFeatureCollection) ||
                ((this.wfsFeatureCollection != null) &&
                        this.wfsFeatureCollection.equals(rhs.wfsFeatureCollection)));
    }

}
