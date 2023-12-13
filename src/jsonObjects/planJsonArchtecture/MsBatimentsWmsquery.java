
package jsonObjects.planJsonArchtecture;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ms:room_abr_link",
        "ms:geom",
        "ms:pdf_link",
        "ms:room_uti_a",
        "gml:boundedBy",
        "ms:room_place",
        "ms:search_url"
})
@Generated("jsonschema2pojo")
public class MsBatimentsWmsquery {

    @JsonProperty("ms:room_abr_link")
    private String msRoomAbrLink;
    @JsonProperty("ms:geom")
    @Valid
    private MsGeom msGeom;
    @JsonProperty("ms:pdf_link")
    private String msPdfLink;
    @JsonProperty("ms:room_uti_a")
    private String msRoomUtiA;
    @JsonProperty("gml:boundedBy")
    @Valid
    private GmlBoundedBy gmlBoundedBy;
    @JsonProperty("ms:room_place")
    private String msRoomPlace;
    @JsonProperty("ms:search_url")
    private String msSearchUrl;

    @JsonProperty("ms:room_abr_link")
    public String getMsRoomAbrLink() {
        return msRoomAbrLink;
    }

    @JsonProperty("ms:room_abr_link")
    public void setMsRoomAbrLink(String msRoomAbrLink) {
        this.msRoomAbrLink = msRoomAbrLink;
    }

    public MsBatimentsWmsquery withMsRoomAbrLink(String msRoomAbrLink) {
        this.msRoomAbrLink = msRoomAbrLink;
        return this;
    }

    @JsonProperty("ms:geom")
    public MsGeom getMsGeom() {
        return msGeom;
    }

    @JsonProperty("ms:geom")
    public void setMsGeom(MsGeom msGeom) {
        this.msGeom = msGeom;
    }

    public MsBatimentsWmsquery withMsGeom(MsGeom msGeom) {
        this.msGeom = msGeom;
        return this;
    }

    @JsonProperty("ms:pdf_link")
    public String getMsPdfLink() {
        return msPdfLink;
    }

    @JsonProperty("ms:pdf_link")
    public void setMsPdfLink(String msPdfLink) {
        this.msPdfLink = msPdfLink;
    }

    public MsBatimentsWmsquery withMsPdfLink(String msPdfLink) {
        this.msPdfLink = msPdfLink;
        return this;
    }

    @JsonProperty("ms:room_uti_a")
    public String getMsRoomUtiA() {
        return msRoomUtiA;
    }

    @JsonProperty("ms:room_uti_a")
    public void setMsRoomUtiA(String msRoomUtiA) {
        this.msRoomUtiA = msRoomUtiA;
    }

    public MsBatimentsWmsquery withMsRoomUtiA(String msRoomUtiA) {
        this.msRoomUtiA = msRoomUtiA;
        return this;
    }

    @JsonProperty("gml:boundedBy")
    public GmlBoundedBy getGmlBoundedBy() {
        return gmlBoundedBy;
    }

    @JsonProperty("gml:boundedBy")
    public void setGmlBoundedBy(GmlBoundedBy gmlBoundedBy) {
        this.gmlBoundedBy = gmlBoundedBy;
    }

    public MsBatimentsWmsquery withGmlBoundedBy(GmlBoundedBy gmlBoundedBy) {
        this.gmlBoundedBy = gmlBoundedBy;
        return this;
    }

    @JsonProperty("ms:room_place")
    public String getMsRoomPlace() {
        return msRoomPlace;
    }

    @JsonProperty("ms:room_place")
    public void setMsRoomPlace(String msRoomPlace) {
        this.msRoomPlace = msRoomPlace;
    }

    public MsBatimentsWmsquery withMsRoomPlace(String msRoomPlace) {
        this.msRoomPlace = msRoomPlace;
        return this;
    }

    @JsonProperty("ms:search_url")
    public String getMsSearchUrl() {
        return msSearchUrl;
    }

    @JsonProperty("ms:search_url")
    public void setMsSearchUrl(String msSearchUrl) {
        this.msSearchUrl = msSearchUrl;
    }

    public MsBatimentsWmsquery withMsSearchUrl(String msSearchUrl) {
        this.msSearchUrl = msSearchUrl;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MsBatimentsWmsquery
                .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("msRoomAbrLink");
        sb.append('=');
        sb.append(((this.msRoomAbrLink == null) ? "<null>" : this.msRoomAbrLink));
        sb.append(',');
        sb.append("msGeom");
        sb.append('=');
        sb.append(((this.msGeom == null) ? "<null>" : this.msGeom));
        sb.append(',');
        sb.append("msPdfLink");
        sb.append('=');
        sb.append(((this.msPdfLink == null) ? "<null>" : this.msPdfLink));
        sb.append(',');
        sb.append("msRoomUtiA");
        sb.append('=');
        sb.append(((this.msRoomUtiA == null) ? "<null>" : this.msRoomUtiA));
        sb.append(',');
        sb.append("gmlBoundedBy");
        sb.append('=');
        sb.append(((this.gmlBoundedBy == null) ? "<null>" : this.gmlBoundedBy));
        sb.append(',');
        sb.append("msRoomPlace");
        sb.append('=');
        sb.append(((this.msRoomPlace == null) ? "<null>" : this.msRoomPlace));
        sb.append(',');
        sb.append("msSearchUrl");
        sb.append('=');
        sb.append(((this.msSearchUrl == null) ? "<null>" : this.msSearchUrl));
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
        result = ((result * 31) + ((this.msRoomUtiA == null) ? 0 : this.msRoomUtiA.hashCode()));
        result = ((result * 31) + ((this.msGeom == null) ? 0 : this.msGeom.hashCode()));
        result = ((result * 31) + ((this.msPdfLink == null) ? 0 : this.msPdfLink.hashCode()));
        result = ((result * 31) + ((this.gmlBoundedBy == null) ? 0 : this.gmlBoundedBy.hashCode()));
        result = ((result * 31) + ((this.msSearchUrl == null) ? 0 : this.msSearchUrl.hashCode()));
        result = ((result * 31) + ((this.msRoomAbrLink == null) ? 0 : this.msRoomAbrLink.hashCode()));
        result = ((result * 31) + ((this.msRoomPlace == null) ? 0 : this.msRoomPlace.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MsBatimentsWmsquery) == false) {
            return false;
        }
        MsBatimentsWmsquery rhs = ((MsBatimentsWmsquery) other);
        return ((((((((this.msRoomUtiA == rhs.msRoomUtiA) ||
                ((this.msRoomUtiA != null) &&
                        this.msRoomUtiA.equals(rhs.msRoomUtiA))) &&
                ((this.msGeom == rhs.msGeom) ||
                        ((this.msGeom != null) &&
                                this.msGeom.equals(rhs.msGeom)))) &&
                ((this.msPdfLink == rhs.msPdfLink) ||
                        ((this.msPdfLink != null) &&
                                this.msPdfLink.equals(rhs.msPdfLink)))) &&
                ((this.gmlBoundedBy == rhs.gmlBoundedBy) ||
                        ((this.gmlBoundedBy != null) &&
                                this.gmlBoundedBy.equals(rhs.gmlBoundedBy)))) &&
                ((this.msSearchUrl == rhs.msSearchUrl) ||
                        ((this.msSearchUrl != null) &&
                                this.msSearchUrl.equals(rhs.msSearchUrl)))) &&
                ((this.msRoomAbrLink == rhs.msRoomAbrLink) ||
                        ((this.msRoomAbrLink != null) &&
                                this.msRoomAbrLink.equals(rhs.msRoomAbrLink)))) &&
                ((this.msRoomPlace == rhs.msRoomPlace) ||
                        ((this.msRoomPlace != null) &&
                                this.msRoomPlace.equals(rhs.msRoomPlace))));
    }

}
