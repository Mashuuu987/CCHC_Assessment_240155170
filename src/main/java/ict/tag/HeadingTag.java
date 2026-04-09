package ict.tag;

import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.StringWriter;

public class HeadingTag extends SimpleTagSupport {

    private String bgColor;
    private String color = null;
    private String align = "CENTER";
    private String fontSize = "36";
    private String fontList = "Arial, Helvetica, sans-serif";
    private String border = "0";
    private String width = null;

    public void setBgColor(String bgColor1) {
        bgColor = bgColor1;
    }

    public void setColor(String color1) {
        color = color1;
    }

    public void setAlign(String align1) {
        align = align1;
    }

    public void setFontSize(String fontSize1) {
        fontSize = fontSize1;
    }

    public void setFontList(String fontList1) {
        fontList = fontList1;
    }

    public void setBorder(String border1) {
        border = border1;
    }

    public void setWidth(String width1) {
        width = width1;
    }

    public void doTag() {
        try {
            PageContext pageContext = (PageContext) getJspContext();
            JspWriter out = pageContext.getOut();
            out.print("<table border=" + border
                    + " bgcolor= '" + bgColor + "'"
                    + " algin =' " + align + "'");
            if (width != null) {
                out.print(" width=' " + width + "'");
            }
            out.print("><tr><td>");
            out.print("<span style='"
                    + "font-size: " + fontSize + "px; "
                    + "font-family: " + fontList + "; ");
            if (color != null) {
                out.print("color: " + color + "; ");
            }
            out.print("'> ");
            
            StringWriter sw = new StringWriter();
            getJspBody().invoke(sw);
            
            getJspContext().getOut().println(sw.toString());
            out.print("</td></tr>");
            out.print("</span></table>");
        } catch (Exception e) {
        }
    }
}

