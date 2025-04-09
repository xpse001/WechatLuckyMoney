package com.example.myapplication.model;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "msg")
public class RedPackContent {


    @JacksonXmlProperty(localName = "fromusername")
    private String fromusername;

    @JacksonXmlProperty(localName = "appmsg")
    private AppMsg appMsg;

    public static class AppMsg {
        @JacksonXmlProperty(localName = "url")
        private String url;

        @JacksonXmlProperty(localName = "type")
        private String type;
        @JacksonXmlProperty(localName = "title")
        private String title;

        @JacksonXmlProperty(localName = "thumburl")
        private String thumburl;


        @JacksonXmlProperty(localName = "wcpayinfo")
        private Wcpayinfo wcpayinfo;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThumburl() {
            return thumburl;
        }

        public void setThumburl(String thumburl) {
            this.thumburl = thumburl;
        }

        public Wcpayinfo getWcpayinfo() {
            return wcpayinfo;
        }

        public void setWcpayinfo(Wcpayinfo wcpayinfo) {
            this.wcpayinfo = wcpayinfo;
        }

        @Override
        public String toString() {
            return "AppMsg{" +
                    "url='" + url + '\'' +
                    ", type='" + type + '\'' +
                    ", title='" + title + '\'' +
                    ", thumburl='" + thumburl + '\'' +
                    ", wcpayinfo=" + wcpayinfo +
                    '}';
        }
    }

    public static class Wcpayinfo {
        @JacksonXmlProperty(localName = "url")
        private String url;
        @JacksonXmlProperty(localName = "nativeurl")
        private String nativeurl;

        @JacksonXmlProperty(localName = "sceneid")
        private String sceneid;

        @JacksonXmlProperty(localName = "innertype")
        private String innertype;

        @JacksonXmlProperty(localName = "paymsgid")
        private String paymsgid;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getNativeurl() {
            return nativeurl;
        }

        public void setNativeurl(String nativeurl) {
            this.nativeurl = nativeurl;
        }

        public String getSceneid() {
            return sceneid;
        }

        public void setSceneid(String sceneid) {
            this.sceneid = sceneid;
        }

        public String getInnertype() {
            return innertype;
        }

        public void setInnertype(String innertype) {
            this.innertype = innertype;
        }

        public String getPaymsgid() {
            return paymsgid;
        }

        public void setPaymsgid(String paymsgid) {
            this.paymsgid = paymsgid;
        }

        @Override
        public String toString() {
            return "Wcpayinfo{" +
                    "url='" + url + '\'' +
                    ", nativeurl='" + nativeurl + '\'' +
                    ", sceneid='" + sceneid + '\'' +
                    ", innertype='" + innertype + '\'' +
                    ", paymsgid='" + paymsgid + '\'' +
                    '}';
        }
    }


    public String getFromusername() {
        return fromusername;
    }

    public void setFromusername(String fromusername) {
        this.fromusername = fromusername;
    }

    public AppMsg getAppMsg() {
        return appMsg;
    }

    public void setAppMsg(AppMsg appMsg) {
        this.appMsg = appMsg;
    }

    @Override
    public String toString() {
        return "RedPackContent{" +
                ", appMsg=" + appMsg +
                ", fromusername='" + fromusername + '\'' +
                '}';
    }
}
