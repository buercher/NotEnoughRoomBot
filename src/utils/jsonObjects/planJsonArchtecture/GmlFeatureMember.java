package utils.jsonObjects.planJsonArchtecture;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ms:batiments_wmsquery"
})
@Generated("jsonschema2pojo")
public class GmlFeatureMember {

    @JsonProperty("ms:batiments_wmsquery")
    @Valid
    private MsBatimentsWmsquery msBatimentsWmsquery;

    @JsonProperty("ms:batiments_wmsquery")
    public MsBatimentsWmsquery getMsBatimentsWmsquery() {
        return msBatimentsWmsquery;
    }

    @JsonProperty("ms:batiments_wmsquery")
    public void setMsBatimentsWmsquery(MsBatimentsWmsquery msBatimentsWmsquery) {
        this.msBatimentsWmsquery = msBatimentsWmsquery;
    }

    public GmlFeatureMember withMsBatimentsWmsquery(MsBatimentsWmsquery msBatimentsWmsquery) {
        this.msBatimentsWmsquery = msBatimentsWmsquery;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GmlFeatureMember
                .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("msBatimentsWmsquery");
        sb.append('=');
        sb.append(((this.msBatimentsWmsquery == null) ? "<null>" : this.msBatimentsWmsquery));
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
        result = ((result * 31) + ((this.msBatimentsWmsquery == null) ? 0 : this.msBatimentsWmsquery.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GmlFeatureMember) == false) {
            return false;
        }
        GmlFeatureMember rhs = ((GmlFeatureMember) other);
        return ((this.msBatimentsWmsquery == rhs.msBatimentsWmsquery) ||
                ((this.msBatimentsWmsquery != null) &&
                        this.msBatimentsWmsquery.equals(rhs.msBatimentsWmsquery)));
    }

}
