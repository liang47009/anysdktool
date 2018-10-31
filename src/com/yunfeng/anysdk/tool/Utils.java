package com.yunfeng.anysdk.tool;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

public class Utils {

    public static Document readDocument(File file) {
        return readDocument(file.getAbsolutePath());
    }

    private static Document readDocument(String file) {
        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        // 通过read方法读取一个文件 转换成Document对象
        Document document = null;
        try {
            document = reader.read(new File(file));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static Document createDocument() {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");

        Element author1 = root.addElement("author")
                .addAttribute("name", "James")
                .addAttribute("location", "UK")
                .addText("James Strachan");

        Element author2 = root.addElement("author")
                .addAttribute("name", "Bob")
                .addAttribute("location", "US")
                .addText("Bob McWhirter");

        return document;
    }
//    private static String json2Xml(JSONObject json, String rootName) {
//        String sXml = "";
//        XMLSerializer xmlSerializer = new XMLSerializer();
//        xmlSerializer.setTypeHintsEnabled(false);
//        xmlSerializer.setRootName(rootName);
//        String sContent = xmlSerializer.write(json);
//        try {
//            Document docCon = DocumentHelper.parseText(sContent);
//            sXml = docCon.getRootElement().asXML();
//            //System.out.println(sXml);
//
//        } catch (DocumentException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return sXml;
//    }
//
//    public String xml2Json(String xmlString) {
//        XMLSerializer xmlSerializer = new XMLSerializer();
//        //xmlSerializer.setRootName("result" );
//        JSON json = xmlSerializer.read(xmlString);
//        return json.toString(1);
//    }

}
