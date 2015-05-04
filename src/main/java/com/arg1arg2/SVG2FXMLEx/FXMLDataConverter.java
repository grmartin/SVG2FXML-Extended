package com.arg1arg2.SVG2FXMLEx;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Pattern;

import at.bestsolution.efxclipse.formats.svg.converter.FXMLConverter;
import at.bestsolution.efxclipse.formats.svg.svg.SvgSvgElement;
import org.eclipse.xtend2.lib.StringConcatenation;

import static org.apache.commons.lang3.StringEscapeUtils.escapeXml10;

/**
 * Created by grmartin on 5/4/15.
 */
public class FXMLDataConverter extends FXMLConverter {
    private boolean importAdded;

    public FXMLDataConverter(SvgSvgElement rootElement) {
        super(rootElement);
        this.importAdded = false;
    }

    @Override
    protected CharSequence _handle(SvgSvgElement element) {
        StringWriter stringWriter = new StringWriter();
        boolean hasExpounded = false;

        try {
            XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(stringWriter);

            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(new StringReader(super._handle(element).toString()));
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

                        // TODO: GRM: For good form we should likely close our Data tag, however... easier said than done.
                        appendRoot(generateDataElement(element).toString(), writer);
                    }
                }
                if (!alreadyAdded) writer.add(event);

            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        stringWriter.flush();

        return stringWriter.toString();

    }

    private void appendRoot(String xmlString, XMLEventWriter writer) throws XMLStreamException {
        appendRoot(XMLInputFactory.newInstance().createXMLEventReader(new StringReader(xmlString)), writer);
    }

    private void appendRoot(XMLEventReader reader, XMLEventWriter writer) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent subDocEvent = reader.nextEvent();

            if (subDocEvent.isStartDocument() | subDocEvent.isEndDocument()) continue;

            writer.add(subDocEvent);
        }
    }

    private CharSequence generateDataElement(SvgSvgElement element) {
        StringConcatenation _builder = new StringConcatenation();

        _builder.append("<Data ");

        ifDo(_builder, "class", element.getClass_());
        ifDo(_builder, "style", element.getStyle());
        ifDo(_builder, "externalResourcesRequired", element.getExternalResourcesRequired());
        ifDo(_builder, "x", element.getX());
        ifDo(_builder, "y", element.getY());
        ifDo(_builder, "width", element.getWidth());
        ifDo(_builder, "height", element.getHeight());
        ifDo(_builder, "viewBox", element.getViewBox());
        ifDo(_builder, "preserveAspectRatio", element.getPreserveAspectRatio());
        ifDo(_builder, "zoomAndPan", element.getZoomAndPan().getLiteral());
        ifDo(_builder, "version", String.valueOf(element.getVersion()));
        ifDo(_builder, "baseProfile", element.getBaseProfile());
        ifDo(_builder, "contentScriptType", element.getContentScriptType());
        ifDo(_builder, "contentStyleType", element.getContentStyleType());
        ifDo(_builder, "styleSheet", element.getStyleSheet());

        _builder.append("/>");

        return _builder;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.length() == 0 || s.trim().length() == 0;
    }

    void ifDo(StringConcatenation builder, String name, String value) {
        if (isEmpty(name) || isEmpty(value)) return;

        name = safeName(name);
        value = escape(value);

        if (isEmpty(name) || isEmpty(value)) return;

        builder.append(String.format("%s=\"%s\" ", name, value));
    }

    private static final Pattern NCNamePattern = Pattern.compile("(?i)^([A-Za-z_\\xc0-\\xd6\\xd8-\\xf6\u00f8-\u02ff\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD][A-Za-z_\\xc0-\\xd6\\xd8-\\xf6\u00f8-\u02ff\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD-.0-9\\xb7\u0300-\u036f\u203f-\u2040]*?)$");

    private String safeName(String name) {
        if (NCNamePattern.matcher(name).matches()) return name;

        return name.replaceAll("(?i)[^A-Za-z_\\xc0-\\xd6\\xd8-\\xf6\u00f8-\u02ff\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD-.0-9\\xb7\u0300-\u036f\u203f-\u2040]", "");
    }

    private String escape(String value) {
        return escapeXml10(value);
    }
}
