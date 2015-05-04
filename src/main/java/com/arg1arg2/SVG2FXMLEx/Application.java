package com.arg1arg2.SVG2FXMLEx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import at.bestsolution.efxclipse.formats.svg.handler.XMLLoader;
import at.bestsolution.efxclipse.formats.svg.svg.SvgSvgElement;

/**
 * Created by grmartin on 4/26/15.
 */
public class Application {
    public static void main(String[] args) throws IOException {
        String fromFile = args[0];
        String toFile = args[1];
        Object in;
        File outFile1;
        if(fromFile.startsWith("http")) {
            URL outFile = new URL(fromFile);
            in = outFile.openStream();
        } else {
            outFile1 = new File(fromFile);
            in = new FileInputStream(outFile1);
        }

        outFile1 = new File(toFile);
        XMLLoader l = new XMLLoader();
        SvgSvgElement svgRoot = l.loadDocument(outFile1.getAbsolutePath(), (InputStream)in);

        FXMLDataConverter converter = new FXMLDataConverter(svgRoot);
        FileOutputStream out = new FileOutputStream(outFile1);
        out.write(converter.generate().toString().getBytes());
        out.close();
    }
}
