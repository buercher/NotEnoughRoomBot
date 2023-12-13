
package jsonObjects.planJsonArchtecture;

import java.util.List;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "xmlns:gml",
        "xmlns:ms",
        "xmlns:ogc",
        "xsi:schemaLocation",
        "xmlns:xsi",
        "gml:featureMember",
        "xmlns:wfs",
        "gml:boundedBy"
})
@Generated("jsonschema2pojo")
public class WfsFeatureCollection {

    @JsonProperty("xmlns:gml")
    private String xmlnsGml;
    @JsonProperty("xmlns:ms")
    private String xmlnsMs;
    @JsonProperty("xmlns:ogc")
    private String xmlnsOgc;
    @JsonProperty("xsi:schemaLocation")
    private String xsiSchemaLocation;
    @JsonProperty("xmlns:xsi")
    private String xmlnsXsi;
    @JsonProperty("gml:featureMember")
    @Valid
    private List<GmlFeatureMember> gmlFeatureMember;
    @JsonProperty("xmlns:wfs")
    private String xmlnsWfs;
    @JsonProperty("gml:boundedBy")
    @Valid
    private GmlBoundedBy__1 gmlBoundedBy;

    @JsonProperty("xmlns:gml")
    public String getXmlnsGml() {
        return xmlnsGml;
    }

    @JsonProperty("xmlns:gml")
    public void setXmlnsGml(String xmlnsGml) {
        this.xmlnsGml = xmlnsGml;
    }

    public WfsFeatureCollection withXmlnsGml(String xmlnsGml) {
        this.xmlnsGml = xmlnsGml;
        return this;
    }

    @JsonProperty("xmlns:ms")
    public String getXmlnsMs() {
        return xmlnsMs;
    }

    @JsonProperty("xmlns:ms")
    public void setXmlnsMs(String xmlnsMs) {
        this.xmlnsMs = xmlnsMs;
    }

    public WfsFeatureCollection withXmlnsMs(String xmlnsMs) {
        this.xmlnsMs = xmlnsMs;
        return this;
    }

    @JsonProperty("xmlns:ogc")
    public String getXmlnsOgc() {
        return xmlnsOgc;
    }

    @JsonProperty("xmlns:ogc")
    public void setXmlnsOgc(String xmlnsOgc) {
        this.xmlnsOgc = xmlnsOgc;
    }

    public WfsFeatureCollection withXmlnsOgc(String xmlnsOgc) {
        this.xmlnsOgc = xmlnsOgc;
        return this;
    }

    @JsonProperty("xsi:schemaLocation")
    public String getXsiSchemaLocation() {
        return xsiSchemaLocation;
    }

    @JsonProperty("xsi:schemaLocation")
    public void setXsiSchemaLocation(String xsiSchemaLocation) {
        this.xsiSchemaLocation = xsiSchemaLocation;
    }

    public WfsFeatureCollection withXsiSchemaLocation(String xsiSchemaLocation) {
        this.xsiSchemaLocation = xsiSchemaLocation;
        return this;
    }

    @JsonProperty("xmlns:xsi")
    public String getXmlnsXsi() {
        return xmlnsXsi;
    }

    @JsonProperty("xmlns:xsi")
    public void setXmlnsXsi(String xmlnsXsi) {
        this.xmlnsXsi = xmlnsXsi;
    }

    public WfsFeatureCollection withXmlnsXsi(String xmlnsXsi) {
        this.xmlnsXsi = xmlnsXsi;
        return this;
    }

    @JsonProperty("gml:featureMember")
    public List<GmlFeatureMember> getGmlFeatureMember() {
        return gmlFeatureMember;
    }

    @JsonProperty("gml:featureMember")
    public void setGmlFeatureMember(List<GmlFeatureMember> gmlFeatureMember) {
        this.gmlFeatureMember = gmlFeatureMember;
    }

    public WfsFeatureCollection withGmlFeatureMember(List<GmlFeatureMember> gmlFeatureMember) {
        this.gmlFeatureMember = gmlFeatureMember;
        return this;
    }

    @JsonProperty("xmlns:wfs")
    public String getXmlnsWfs() {
        return xmlnsWfs;
    }

    @JsonProperty("xmlns:wfs")
    public void setXmlnsWfs(String xmlnsWfs) {
        this.xmlnsWfs = xmlnsWfs;
    }

    public WfsFeatureCollection withXmlnsWfs(String xmlnsWfs) {
        this.xmlnsWfs = xmlnsWfs;
        return this;
    }

    @JsonProperty("gml:boundedBy")
    public GmlBoundedBy__1 getGmlBoundedBy() {
        return gmlBoundedBy;
    }

    @JsonProperty("gml:boundedBy")
    public void setGmlBoundedBy(GmlBoundedBy__1 gmlBoundedBy) {
        this.gmlBoundedBy = gmlBoundedBy;
    }

    public WfsFeatureCollection withGmlBoundedBy(GmlBoundedBy__1 gmlBoundedBy) {
        this.gmlBoundedBy = gmlBoundedBy;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(WfsFeatureCollection
                .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("xmlnsGml");
        sb.append('=');
        sb.append(((this.xmlnsGml == null) ? "<null>" : this.xmlnsGml));
        sb.append(',');
        sb.append("xmlnsMs");
        sb.append('=');
        sb.append(((this.xmlnsMs == null) ? "<null>" : this.xmlnsMs));
        sb.append(',');
        sb.append("xmlnsOgc");
        sb.append('=');
        sb.append(((this.xmlnsOgc == null) ? "<null>" : this.xmlnsOgc));
        sb.append(',');
        sb.append("xsiSchemaLocation");
        sb.append('=');
        sb.append(((this.xsiSchemaLocation == null) ? "<null>" : this.xsiSchemaLocation));
        sb.append(',');
        sb.append("xmlnsXsi");
        sb.append('=');
        sb.append(((this.xmlnsXsi == null) ? "<null>" : this.xmlnsXsi));
        sb.append(',');
        sb.append("gmlFeatureMember");
        sb.append('=');
        sb.append(((this.gmlFeatureMember == null) ? "<null>" : this.gmlFeatureMember));
        sb.append(',');
        sb.append("xmlnsWfs");
        sb.append('=');
        sb.append(((this.xmlnsWfs == null) ? "<null>" : this.xmlnsWfs));
        sb.append(',');
        sb.append("gmlBoundedBy");
        sb.append('=');
        sb.append(((this.gmlBoundedBy == null) ? "<null>" : this.gmlBoundedBy));
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
        result = ((result * 31) + ((this.xmlnsXsi == null) ? 0 : this.xmlnsXsi.hashCode()));
        result = ((result * 31) + ((this.xmlnsMs == null) ? 0 : this.xmlnsMs.hashCode()));
        result = ((result * 31) + ((this.gmlBoundedBy == null) ? 0 : this.gmlBoundedBy.hashCode()));
        result = ((result * 31) + ((this.xsiSchemaLocation == null) ? 0 : this.xsiSchemaLocation.hashCode()));
        result = ((result * 31) + ((this.xmlnsGml == null) ? 0 : this.xmlnsGml.hashCode()));
        result = ((result * 31) + ((this.gmlFeatureMember == null) ? 0 : this.gmlFeatureMember.hashCode()));
        result = ((result * 31) + ((this.xmlnsWfs == null) ? 0 : this.xmlnsWfs.hashCode()));
        result = ((result * 31) + ((this.xmlnsOgc == null) ? 0 : this.xmlnsOgc.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof WfsFeatureCollection) == false) {
            return false;
        }
        WfsFeatureCollection rhs = ((WfsFeatureCollection) other);
        return (((((((((this.xmlnsXsi == rhs.xmlnsXsi) ||
                ((this.xmlnsXsi != null) &&
                        this.xmlnsXsi.equals(rhs.xmlnsXsi))) &&
                ((this.xmlnsMs == rhs.xmlnsMs) ||
                        ((this.xmlnsMs != null) &&
                                this.xmlnsMs.equals(rhs.xmlnsMs)))) &&
                ((this.gmlBoundedBy == rhs.gmlBoundedBy) ||
                        ((this.gmlBoundedBy != null) &&
                                this.gmlBoundedBy.equals(rhs.gmlBoundedBy)))) &&
                ((this.xsiSchemaLocation == rhs.xsiSchemaLocation) ||
                        ((this.xsiSchemaLocation != null) &&
                                this.xsiSchemaLocation.equals(rhs.xsiSchemaLocation)))) &&
                ((this.xmlnsGml == rhs.xmlnsGml) ||
                        ((this.xmlnsGml != null) &&
                                this.xmlnsGml.equals(rhs.xmlnsGml)))) &&
                ((this.gmlFeatureMember == rhs.gmlFeatureMember) ||
                        ((this.gmlFeatureMember != null) &&
                                this.gmlFeatureMember.equals(rhs.gmlFeatureMember)))) &&
                ((this.xmlnsWfs == rhs.xmlnsWfs) ||
                        ((this.xmlnsWfs != null) &&
                                this.xmlnsWfs.equals(rhs.xmlnsWfs)))) &&
                ((this.xmlnsOgc == rhs.xmlnsOgc) ||
                        ((this.xmlnsOgc != null) &&
                                this.xmlnsOgc.equals(rhs.xmlnsOgc))));
    }

}
