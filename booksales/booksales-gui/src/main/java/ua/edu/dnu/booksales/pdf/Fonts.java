package ua.edu.dnu.booksales.pdf;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;

public class Fonts {
    public static final String FONT_PATH = "fonts/times.ttf";
    public static final Font TITLE_FONT =
            FontFactory.getFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED,14, Font.BOLD);
    public static final Font TEXT_FONT =
            FontFactory.getFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14);
    public static final Font TABLE_ITEM_FONT =
            FontFactory.getFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);
}
