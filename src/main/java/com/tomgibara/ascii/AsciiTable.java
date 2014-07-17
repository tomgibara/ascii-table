package com.tomgibara.ascii;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg12.SVG12DOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.StyleHandler;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;

public class AsciiTable {

	private static final String[] SYMBOLS = {
		"NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL", "BS",  "HT",  "LF",  "VT",  "FF",  "CR",  "SO",  "SI",
		"DLE", "DC1", "DC2", "DC3", "DC4", "NAK", "SYN", "ETB", "CAN", "EM",  "SUB", "ESC", "FS",  "GS",  "RS",  "US",
		" ",   "!",   "\"",  "#",   "$",   "%",   "&",   "'",   "(",   ")",   "*",   "+",   ",",   "-",   ".",   "/",
		"0",   "1",   "2",   "3",   "4",   "5",   "6",   "7",   "8",   "9",   ":",   ";",   "<",   "=",   ">",   "?",
		"@",   "A",   "B",   "C",   "D",   "E",   "F",   "G",   "H",   "I",   "J",   "K",   "L",   "M",   "N",   "O",
		"P",   "Q",   "R",   "S",   "T",   "U",   "V",   "W",   "X",   "Y",   "Z",   "[",   "\\",  "]",   "^",   "_",
		"`",   "a",   "b",   "c",   "d",   "e",   "f",   "g",   "h",   "i",   "j",   "k",   "l",   "m",   "n",   "o",
		"p",   "q",   "r",   "s",   "t",   "u",   "v",   "w",   "x",   "y",   "z",   "{",   "|",   "}",   "~",   "DEL"
	};

	private static final String[] REFS = {
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
		"", "excl", "quot", "num", "dollar", "percnt", "amp", "apos", "lpar", "rpar", "ast", "plus", "comma", "", "period", "sol",
		"", "", "", "", "", "", "", "", "", "", "colon", "semi", "lt", "equals", "gt", "quest",
		"commat", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
		"", "", "", "", "", "", "", "", "", "", "", "lsqb", "bsol", "rsqb", "hat", "lowbar",
		"grave", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
	};

	private static final String[] CTRL = {
		"@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
		"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[", "\\", "]", "^", "_",
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
		"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "?",
	};

	private static final String[] NAMES = {
		"NULL", "START OF\nHEADING", "START OF\nTEXT", "END OF\nTEXT", "END OF\nTRANSM.", "ENQUIRY", "ACKNOWL-\nEDGE", "BELL", "BACKSP.", "CHARACT.\nTAB'TION", "LINE\nFEED", "LINE\nTAB'TION", "FORM\nFEED", "CARRIAGE\nRETURN", "SHIFT\nOUT", "SHIFT\nIN",
		"DATALINK\nESCAPE", "DEVICE\nCONTROL1", "DEVICE\nCONTROL2", "DEVICE\nCONTROL3", "DEVICE\nCONTROL4", "NEG.ACK-\nNOWLEDGE", "SYNCHR.\nIDLE", "END OF\nTRANS.", "CANCEL", "END OF\nMEDIUM", "SUBS-\nTITUTE", "ESCAPE", "INFO.\nSEP. 4", "INFO.\nSEP. 3", "INFO.\nSEP. 2", "INFO.\nSEP. 1",
		"SPACE", "EXCLAM.\nMARK", "QUOT.\nMARK", "NUMBER\nSIGN", "DOLLAR\nSIGN", "PERCENT\nSIGN", "AMPER-\nSAND", "APOS-\nTROPHE", "LEFT\nPAREN.", "RIGHT\nPAREN.", "ASTERISK", "PLUS\nSIGN", "COMMA", "HYPHEN-\nMINUS", "FULL\nSTOP", "SOLIDUS",
		"DIGIT\nZERO", "DIGIT\nONE", "DIGIT\nTWO", "DIGIT\nTHREE", "DIGIT\nFOUR", "DIGIT\nFIVE", "DIGIT\nSIX", "DIGIT\nSEVEN", "DIGIT\nEIGHT", "DIGIT\nNINE", "COLON", "SEMI-\nCOLON", "LS.-THAN\nSIGN", "EQUALS\nSIGN", "GR.-THAN\nSIGN", "QUEST-\nION MARK",
		"COMM'IAL\nAT", "LATIN CP\nLETTER A", "LATIN CP\nLETTER B", "LATIN CP\nLETTER C", "LATIN CP\nLETTER D", "LATIN CP\nLETTER E", "LATIN CP\nLETTER F", "LATIN CP\nLETTER G", "LATIN CP\nLETTER H", "LATIN CP\nLETTER I", "LATIN CP\nLETTER J", "LATIN CP\nLETTER K", "LATIN CP\nLETTER L", "LATIN CP\nLETTER M", "LATIN CP\nLETTER N", "LATIN CP\nLETTER O",
		"LATIN CP\nLETTER P", "LATIN CP\nLETTER Q", "LATIN CP\nLETTER R", "LATIN CP\nLETTER S", "LATIN CP\nLETTER T", "LATIN CP\nLETTER U", "LATIN CP\nLETTER V", "LATIN CP\nLETTER W", "LATIN CP\nLETTER X", "LATIN CP\nLETTER Y", "LATIN CP\nLETTER Z", "LEFT SQ.\nBRACKET", "REVERSE\nSOLIDUS", "RT. SQR.\nBRACKET", "CIRCUM'X\nACCENT", "LOW LINE",
		"GRAVE\nACCENT", "LATIN SM\nLETTER A", "LATIN SM\nLETTER B", "LATIN SM\nLETTER C", "LATIN SM\nLETTER D", "LATIN SM\nLETTER E", "LATIN SM\nLETTER F", "LATIN SM\nLETTER G", "LATIN SM\nLETTER H", "LATIN SM\nLETTER I", "LATIN SM\nLETTER J", "LATIN SM\nLETTER K", "LATIN SM\nLETTER L", "LATIN SM\nLETTER M", "LATIN SM\nLETTER N", "LATIN SM\nLETTER O",
		"LATIN SM\nLETTER P", "LATIN SM\nLETTER Q", "LATIN SM\nLETTER R", "LATIN SM\nLETTER S", "LATIN SM\nLETTER T", "LATIN SM\nLETTER U", "LATIN SM\nLETTER V", "LATIN SM\nLETTER W", "LATIN SM\nLETTER X", "LATIN SM\nLETTER Y", "LATIN SM\nLETTER Z", "L. CURLY\nBRACKET", "VERTICAL\nLINE", "R. CURLY\nBRACKET", "TILDE", "DELETE",
	};

	private static String dataUrl(byte[] bytes) {
		return "data:font/ttf;base64," + BaseEncoding.base64().encode(bytes);
	}

	private static final Color lightGrey = new Color(0xffe0e0e0);

	private static boolean isControl(int index) {
		return index < 32 || index == 127;
	}

	private static void reportUsage() {
		System.out.println("Usage: java -jar AsciiTable.jar png|svg <output-path>");
		System.exit(1);
	}

	public static void main(String... args) throws IOException, FontFormatException {
		if (args.length != 2) {
			reportUsage();
		}
		String type = args[0].toUpperCase();
		if (!type.equals("PNG") && !type.equals("SVG")) {
			reportUsage();
		}
		boolean png = type.equals("PNG");
		File file = new File(args[1]);
		new AsciiTable(png, file).output();
	}

	private final boolean png;
	private final File file;
	private String className;

	public AsciiTable(boolean png, File file) {
		this.png = png;
		this.file = file;
	}

	public void output() throws IOException, FontFormatException {
		Class<?> clss = getClass();
		byte[] regFontBytes;
		try (InputStream in = clss.getResourceAsStream("Cousine-Regular-Compact.ttf")) {
			regFontBytes = ByteStreams.toByteArray(in);
		}
		byte[] bldFontBytes;
		try (InputStream in = clss.getResourceAsStream("Cousine-Bold-Compact.ttf")) {
			bldFontBytes = ByteStreams.toByteArray(in);
		}
		
		Font font = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(regFontBytes));
		Font boldFont = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(bldFontBytes));

		String blank;
		try (InputStream in = clss.getResourceAsStream("blank.svg")) {
			byte[] bytes = ByteStreams.toByteArray(in);
			blank = new String(bytes, Charsets.UTF_8)
				.replace("REGULAR_URL", dataUrl(regFontBytes))
				.replace("BOLD_URL", dataUrl(bldFontBytes));
		}
		
		if (png) {
			BufferedImage image = new BufferedImage(1800, 1800, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			draw(g, font, boldFont);

			ImageIO.write(image, "PNG", file);
		} else {
			URL url = AsciiTable.class.getResource("blank.svg");
			String parser = XMLResourceDescriptor.getXMLParserClassName();
			SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
			SVGDocument document = factory.createSVGDocument(url.toString(), new StringReader(blank));

			String svgNs = SVG12DOMImplementation.SVG_NAMESPACE_URI;
			SVGGraphics2D g = new SVGGraphics2D(document);
			g.setTopLevelGroup((Element) document.getDocumentElement().getElementsByTagNameNS(svgNs, "g").item(0));
			ClassStyleHandler handler = new ClassStyleHandler(g.getGeneratorContext().getStyleHandler());
			g.getGeneratorContext().setStyleHandler(handler);
			g.setSVGCanvasSize(new Dimension(1800, 1800));
			draw(g, font, boldFont);

			try (FileWriter out = new FileWriter(file)) {
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.transform(new DOMSource(document), new StreamResult(out));
			} catch (TransformerException e) {
				throw new IOException("Failed to transform document", e);
			}
		}
	}

	void draw(Graphics2D g, Font font, Font boldFont) {
		//Note font sizing is chosen to match css @import
		Font largeFont = font.deriveFont(80f);
		Font mediumFont = font.deriveFont(40f);
		Font smallFont = font.deriveFont(24f);
		Font smallestFont = font.deriveFont(18f);
		Font smallBoldFont = boldFont.deriveFont(24f);

		//Note classes match class names in blank.svg
		className = "blank";
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1800, 1800);
		className = null;

		// boxes
		Rectangle2D.Float[] boxes = new Rectangle2D.Float[128];
		for (int i = 0; i < 128; i++) {
			int x = 100 + (i % 16) * 100;
			int y = 100 + (i / 16) * 200;
			Rectangle2D.Float box = new Rectangle2D.Float(x, y, 100, 200);
			boxes[i] = box;
			className = isControl(i) ? "control" : "printable";
			g.setColor(isControl(i) ? lightGrey : Color.WHITE);
			g.fill(box);
		}

		// control codes
		className = "ctrl";
		g.setColor(Color.BLACK);
		g.setFont(smallBoldFont);
		float inset = 5f;
		int numRefWidth = g.getFontMetrics().stringWidth("&#");
		for (int i = 0; i < 128; i++) {
			String ctrl = CTRL[i];
			if (ctrl.isEmpty()) continue;
			ctrl = "^" + ctrl;
			Rectangle2D.Float box = boxes[i];
			g.drawString(ctrl, box.x + inset + numRefWidth, box.y + 60);
		}
		className = null;

		// numeric entity references
		className = "num-ref";
		g.setFont(smallFont);
		for (int i = 0; i < 128; i++) {
			Rectangle2D.Float box = boxes[i];
			float baseline = box.y + 30f;
			String str = Integer.toString(i);
			g.drawString(str, box.x + inset + numRefWidth, baseline);
			if (!isControl(i)) {
				g.setColor(lightGrey);
				int wid = g.getFontMetrics().stringWidth(str);
				className = "num-ref syntax";
				g.drawString("&#", box.x + inset, baseline);
				g.drawString(";", box.x + inset + numRefWidth + wid, baseline);
				className = "num-ref";
				g.setColor(Color.BLACK);
			}
		}
		className = null;

		// character entity references
		className = "char-ref";
		g.setFont(smallestFont);
		int ampWidth = g.getFontMetrics().stringWidth("&");
		for (int i = 0; i < 128; i++) {
			String ref = REFS[i];
			if (ref.isEmpty()) continue;
			Rectangle2D.Float box = boxes[i];
			float baseline = box.y + 55f;
			g.drawString(ref, box.x + inset + ampWidth, baseline);
			if (!isControl(i)) {
				g.setColor(lightGrey);
				int wid = g.getFontMetrics().stringWidth(ref);
				className = "char-ref syntax";
				g.drawString("&", box.x + inset, baseline);
				g.drawString(";", box.x + inset + ampWidth + wid, baseline);
				className = "char-ref";
				g.setColor(Color.BLACK);
			}
		}
		className = null;

		// glyphs and abbr.
		for (int i = 0; i < 128; i++) {
			Rectangle2D.Float box = boxes[i];
			String symb = SYMBOLS[i];
			boolean glyph = symb.length() == 1;
			className = glyph ? "glyph" : "abbr";
			g.setFont(glyph ? largeFont : mediumFont);
			Rectangle2D bounds = g.getFontMetrics().getStringBounds(symb, g);
			float offset = glyph ? 130 : 120;
			g.drawString(symb, (float) (box.getCenterX() - bounds.getWidth() * 0.5), box.y + offset);
		}
		className = null;

		// names
		className = "name";
		g.setFont(smallestFont);
		for (int i = 0; i < 128; i++) {
			String name = NAMES[i];
			if (name.startsWith("LATIN") || name.isEmpty()) continue;
			Rectangle2D.Float box = boxes[i];
			String[] names = name.split("\\n");
			for (int j = 0; j < names.length; j++) {
				g.drawString(names[j], box.x + 6, box.y + 174 + j * 20);
			}
		}
		className = null;

		// lines
		className = "line";
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2f));
		g.draw(new Rectangle2D.Float(100f, 100f, 1600f, 1600f));
		for (int i = 1; i < 16; i++) {
			int x = 100 + 100 * i;
			g.drawLine(x, 100, x, 1700);
		}
		for (int i = 1; i < 8; i++) {
			int y = 100 + 200 * i;
			g.drawLine(100, y, 1700, y);
		}
		className = null;

		// row and column headings
		className = "heading";
		float size = 100f / 3f;
		Rectangle2D.Float[] colBoxes = new Rectangle2D.Float[16];
		for (int i = 0; i < 16; i++) {
			float x = 100 + 100 * i + 100 - size;
			float y = 100 - size;
			Rectangle2D.Float box = new Rectangle2D.Float(x, y, size, size);
			colBoxes[i] = box;
			g.fill(box);
		}

		Rectangle2D.Float[] rowBoxes = new Rectangle2D.Float[16];
		for (int i = 0; i < 8; i++) {
			float x = 100 - size;
			float y = 100 + 200 * i;
			Rectangle2D.Float box = new Rectangle2D.Float(x, y, size, size);
			rowBoxes[i] = box;
			g.fill(box);
		}

		g.setColor(Color.WHITE);
		g.setFont(smallBoldFont);
		for (int i = 0; i < 16; i++) {
			String label = Integer.toString(i, 16).toUpperCase();
			Rectangle2D.Float box = colBoxes[i];
			Rectangle2D bounds = g.getFontMetrics().getStringBounds(label, g);
			g.drawString(label, (float) (box.getCenterX() - bounds.getWidth() * 0.5), box.y + 24);
		}
		
		for (int i = 0; i < 8; i++) {
			String label = Integer.toString(i, 16).toUpperCase();
			Rectangle2D.Float box = rowBoxes[i];
			Rectangle2D bounds = g.getFontMetrics().getStringBounds(label, g);
			g.drawString(label, (float) (box.getCenterX() - bounds.getWidth() * 0.5), box.y + 24);
		}
		className = null;

		className = "line";
		g.setColor(Color.BLACK);
		for (int i = 0; i < 16; i++) {
			g.draw(colBoxes[i]);
		}

		for (int i = 0; i < 8; i++) {
			g.draw(rowBoxes[i]);
		}
		className = null;

		className = "caption";
		int captionBaseline = 1750;
		g.setFont(smallFont);
		{
			String caption = String.format("ASCII code table including entity references, control codes and Unicode names (%s)", getClass().getPackage().getImplementationVersion());
			g.drawString(caption, 100, captionBaseline);
		}
		className = null;

		className = "copyright";
		g.setFont(smallFont);
		{
			String username = System.getProperty("user.fullname");
			if (username == null) username = System.getProperty("user.name");
			if (username != null) {
				String copyright = String.format("\u00a9 %1$s %2$tB %2$tY", username, new Date());
				Rectangle2D bounds = g.getFontMetrics().getStringBounds(copyright, g);
				g.drawString(copyright, 1700 - (float) bounds.getWidth(), captionBaseline);
			}
		}
		className = null;
	}

	public class ClassStyleHandler implements StyleHandler {

		private final StyleHandler handler;

		public ClassStyleHandler(StyleHandler handler) {
			this.handler = handler;
		}

		public void setStyle(Element el, @SuppressWarnings("rawtypes") Map props, SVGGeneratorContext context) {
			if (className != null) {
				String clss = el.getNodeName().equals("g") ? className + "_group" : className;
				el.setAttribute("class", clss);
			} else {
				handler.setStyle(el, props, context);
			}
		}

	}

}
