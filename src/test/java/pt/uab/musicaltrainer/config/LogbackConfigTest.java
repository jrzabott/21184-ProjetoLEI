package pt.uab.musicaltrainer.config;

import org.junit.jupiter.api.Test;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;
import java.util.regex.*;

import static org.junit.jupiter.api.Assertions.*;

class LogbackConfigTest {

    private static final String LOGBACK_PATH = "src/main/resources/logback-spring.xml";

    @Test
    void naoDeveConterVariaveisIndefinidas() throws Exception {
        Document doc = parseXml();
        Set<String> declared = collectDeclaredKeys(doc);
        Set<String> referenced = collectReferencedVars(doc);

        Set<String> undefined = new HashSet<>(referenced);
        undefined.removeAll(declared);

        assertTrue(undefined.isEmpty(),
            "Variaveis nao declaradas em logback-spring.xml: " + undefined);
    }

    @Test
    void fileNamePatternDeveUsarPrecisaoDiaria() throws Exception {
        Document doc = parseXml();
        NodeList patterns = doc.getElementsByTagName("fileNamePattern");

        assertTrue(patterns.getLength() > 0, "Nenhum fileNamePattern encontrado");

        for (int i = 0; i < patterns.getLength(); i++) {
            String pattern = patterns.item(i).getTextContent();
            assertFalse(pattern.contains("'T'HH-mm-ss"),
                "fileNamePattern usa precisao em segundos — maxHistory esgota em 30s: " + pattern);
        }
    }

    private Document parseXml() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new File(LOGBACK_PATH));
    }

    private Set<String> collectDeclaredKeys(Document doc) {
        Set<String> keys = new HashSet<>();
        addAttributeValues(doc, "property", "name", keys);
        addAttributeValues(doc, "timestamp", "key", keys);
        return keys;
    }

    private void addAttributeValues(Document doc, String tagName, String attrName, Set<String> result) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element el) {
                String val = el.getAttribute(attrName);
                if (val != null && !val.isEmpty()) result.add(val);
            }
        }
    }

    private Set<String> collectReferencedVars(Document doc) {
        Set<String> refs = new HashSet<>();
        Pattern p = Pattern.compile("\\$\\{([^}:]+)(?::-[^}]*)?\\}");
        collectFromNode(doc.getDocumentElement(), p, refs);
        return refs;
    }

    private void collectFromNode(Node node, Pattern p, Set<String> refs) {
        if (node instanceof Element el) {
            NamedNodeMap attrs = el.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                Matcher m = p.matcher(attrs.item(i).getNodeValue());
                while (m.find()) refs.add(m.group(1));
            }
        }
        if (node.getNodeType() == Node.TEXT_NODE) {
            Matcher m = p.matcher(node.getNodeValue());
            while (m.find()) refs.add(m.group(1));
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            collectFromNode(children.item(i), p, refs);
        }
    }
}
