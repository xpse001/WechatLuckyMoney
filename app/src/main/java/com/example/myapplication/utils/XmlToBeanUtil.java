package com.example.myapplication.utils;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlToBeanUtil {


    private static XmlMapper xmlMapper;


    static {
        // 使用 Woodstox 配置
        WstxInputFactory inputFactory = new WstxInputFactory();
        WstxOutputFactory outputFactory = new WstxOutputFactory();
        XmlFactory xmlFactory = new XmlFactory(inputFactory, outputFactory);
        xmlMapper = new XmlMapper(xmlFactory);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T convertXmlToBean(String xml, Class<T> tClass) throws Exception {
        return xmlMapper.readValue(xml, tClass);
    }

}
