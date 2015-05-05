package com.arg1arg2.SVG2FXMLEx;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import at.bestsolution.efxclipse.formats.svg.converter.FXMLConverter;
import at.bestsolution.efxclipse.formats.svg.svg.SvgSvgElement;
import com.arg1arg2.data.xml.stream.XMLEventFactory;
import com.arg1arg2.data.xml.stream.XMLInputFactory;
import com.arg1arg2.data.xml.stream.XMLOutputFactory;
import org.eclipse.xtext.xbase.lib.Pair;

/**
 * Created by grmartin on 5/4/15.
 */
public class FXMLDataConverter extends FXMLConverter {
    private final XMLInputFactory inputFactory;
    private final XMLOutputFactory outputFactory;
    private final XMLEventFactory eventFactory;
    private boolean importAdded;

    public FXMLDataConverter(SvgSvgElement rootElement) {
        super(rootElement);
        this.importAdded = false;
        this.outputFactory = XMLOutputFactory.newFactory();
        this.inputFactory = XMLInputFactory.newFactory();
        this.eventFactory = XMLEventFactory.newFactory();
    }

    @Override
    protected CharSequence _handle(SvgSvgElement element) {
        StringWriter stringWriter = new StringWriter();
        boolean hasExpounded = false;

        try {
            XMLEventWriter writer = outputFactory.createXMLEventWriter(stringWriter);

            XMLEventReader reader = inputFactory.createXMLEventReader(new StringReader(super._handle(element).toString()));
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                boolean alreadyAdded = false;

                if (event.isStartElement()) {
                    if (event.asStartElement().getName().equals(QName.valueOf("Group")) && !hasExpounded) {
                        hasExpounded = true;
                        writer.add(event);
                        alreadyAdded = true;

                        if (!importAdded) {
                            writer.add(XMLEventFactory.newFactory().createProcessingInstruction("import", "com.arg1arg2.jfx.Data"));
                            importAdded = true;
                        }
//
//                        // TODO: GRM: For good form we should likely close our Data tag, however... easier said than done.
//                        appendRoot(generateDataElement(element).toString(), writer);

                        appendRoot(generateDataElementReader(element), writer);
                    }
                }
                if (!alreadyAdded) writer.add(event);

            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        stringWriter.flush();

        return stringWriter.toString().replaceFirst("<\\?xml.*?\\?>", "");

    }

    private void appendRoot(String xmlString, XMLEventWriter writer) throws XMLStreamException {
        appendRoot(inputFactory.createXMLEventReader(new StringReader(xmlString)), writer);
    }

    private void appendRoot(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent subDocEvent = reader.nextEvent();

            if (subDocEvent.isStartDocument() | subDocEvent.isEndDocument() | subDocEvent.isProcessingInstruction()) continue;

            writer.add(subDocEvent);
        }
    }

    private XMLEventReader generateDataElementReader(SvgSvgElement element) {
        XMLEventReader returnValue = null;
        try {
            Pair<XMLEventWriter, StringWriter> p  = outputFactory.createXMLEventStringWriterPair();

            XMLEventWriter xml = p.getKey();
            StringWriter string = p.getValue();
            Namespace ns = eventFactory.createNamespace("fx", "http://javafx.com/fxml");

            QName dataName = new QName("Data");

            xml.add(eventFactory.createStartElement(dataName, null, null));

            xml.add(ns);
            xml.add(eventFactory.createAttribute(ns, "id", "svg_root_element"));
            xml.add(eventFactory.createAttribute("id", "svg_root_element"));

            ifDo(xml, "class", element.getClass_());
            ifDo(xml, "style", element.getStyle());
            ifDo(xml, "externalResourcesRequired", element.getExternalResourcesRequired());
            ifDo(xml, "x", element.getX());
            ifDo(xml, "y", element.getY());
            ifDo(xml, "width", element.getWidth());
            ifDo(xml, "height", element.getHeight());
            ifDo(xml, "viewBox", element.getViewBox());
            ifDo(xml, "preserveAspectRatio", element.getPreserveAspectRatio());
            ifDo(xml, "zoomAndPan", element.getZoomAndPan().getLiteral());
            ifDo(xml, "version", String.valueOf(element.getVersion()));
            ifDo(xml, "baseProfile", element.getBaseProfile());
            ifDo(xml, "contentScriptType", element.getContentScriptType());
            ifDo(xml, "contentStyleType", element.getContentStyleType());
            ifDo(xml, "styleSheet", element.getStyleSheet());

            xml.add(eventFactory.createEndElement(dataName, null));

            xml.flush();
            string.flush();

            returnValue = inputFactory.createXMLEventStringReaderPair(string.toString()).getKey();

            xml.close();
            try {
                string.close();
            } catch (IOException ignored) { }

        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.length() == 0 || s.trim().length() == 0;
    }

    void ifDo(XMLEventWriter builder, String name, String value) throws XMLStreamException {
        if (isEmpty(name) || isEmpty(value)) return;

        builder.add(eventFactory.createAttribute(name, value));
    }
}
