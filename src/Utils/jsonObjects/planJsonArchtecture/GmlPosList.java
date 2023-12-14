
package Utils.jsonObjects.planJsonArchtecture;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "srsDimension",
        "content"
})
@Generated("jsonschema2pojo")
public class GmlPosList {

    @JsonProperty("srsDimension")
    private int srsDimension;
    @JsonProperty("content")
    private String content;

    @JsonProperty("srsDimension")
    public int getSrsDimension() {
        return srsDimension;
    }

    @JsonProperty("srsDimension")
    public void setSrsDimension(int srsDimension) {
        this.srsDimension = srsDimension;
    }

    public GmlPosList withSrsDimension(int srsDimension) {
        this.srsDimension = srsDimension;
        return this;
    }

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    public GmlPosList withContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GmlPosList
                .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("srsDimension");
        sb.append('=');
        sb.append(this.srsDimension);
        sb.append(',');
        sb.append("content");
        sb.append('=');
        sb.append(((this.content == null) ? "<null>" : this.content));
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
        result = ((result * 31) + this.srsDimension);
        result = ((result * 31) + ((this.content == null) ? 0 : this.content.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GmlPosList) == false) {
            return false;
        }
        GmlPosList rhs = ((GmlPosList) other);
        return ((this.srsDimension == rhs.srsDimension) &&
                ((this.content == rhs.content) ||
                        ((this.content != null) &&
                                this.content.equals(rhs.content))));
    }

}
