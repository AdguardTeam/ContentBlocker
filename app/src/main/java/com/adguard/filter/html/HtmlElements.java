/**
 This file is part of Adguard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2016 Performix LLC. All rights reserved.

 Adguard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 Adguard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 Adguard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.filter.html;

import java.util.Arrays;
import java.util.List;

/**
 * Html elements enumeration
 */
public class HtmlElements {

    public static final String A = "a";
    public static final String ABBR = "abbr";
    public static final String ACRONYM = "acronym";
    public static final String ADDRESS = "address";
    public static final String APPLET = "applet";
    public static final String AREA = "area";
    public static final String B = "b";
    public static final String BASE = "base";
    public static final String BASEFONT = "basefont";
    public static final String BDO = "bdo";
    public static final String BIG = "big";
    public static final String BLOCKQUOTE = "blockquote";
    public static final String BR = "br";
    public static final String BODY = "body";
    public static final String BUTTON = "button";
    public static final String CAPTION = "caption";
    public static final String CENTER = "center";
    public static final String CITE = "cite";
    public static final String CODE = "code";
    public static final String COL = "col";
    public static final String COLGROUP = "colgroup";
    public static final String DD = "dd";
    public static final String DEL = "del";
    public static final String DFN = "dfn";
    public static final String DIR = "dir";
    public static final String DIV = "div";
    public static final String DL = "dl";
    public static final String DT = "dt";
    public static final String EM = "em";
    public static final String EMBED = "embed";
    public static final String FIELDSET = "fieldset";
    public static final String FONT = "font";
    public static final String FORM = "form";
    public static final String FRAME = "frame";
    public static final String FRAMESET = "frameset";
    public static final String H1 = "h1";
    public static final String H2 = "h2";
    public static final String H3 = "h3";
    public static final String H4 = "h4";
    public static final String H5 = "h5";
    public static final String H6 = "h6";
    public static final String HR = "hr";
    public static final String HEAD = "head";
    public static final String HTML = "html";
    public static final String I = "i";
    public static final String IFRAME = "iframe";
    public static final String IMG = "img";
    public static final String INPUT = "input";
    public static final String INS = "ins";
    public static final String ISINDEX = "isindex";
    public static final String KBD = "kbd";
    public static final String LABEL = "label";
    public static final String LEGEND = "legend";
    public static final String LI = "li";
    public static final String LINK = "link";
    public static final String MAP = "map";
    public static final String MENU = "menu";
    public static final String META = "meta";
    public static final String NOFRAMES = "noframes";
    public static final String NOSCRIPT = "noscript";
    public static final String OBJECT = "object";
    public static final String OL = "ol";
    public static final String OPTGROUP = "optgroup";
    public static final String OPTION = "option";
    public static final String P = "p";
    public static final String PARAM = "param";
    public static final String PRE = "pre";
    public static final String Q = "q";
    public static final String S = "s";
    public static final String SAMP = "samp";
    public static final String SCRIPT = "script";
    public static final String SELECT = "select";
    public static final String SMALL = "small";
    public static final String SPAN = "span";
    public static final String STRIKE = "strike";
    public static final String STRONG = "strong";
    public static final String STYLE = "style";
    public static final String SUB = "sub";
    public static final String SUP = "sup";
    public static final String TABLE = "table";
    public static final String TBODY = "tbody";
    public static final String TD = "td";
    public static final String TEXTAREA = "textarea";
    public static final String TFOOT = "tfoot";
    public static final String TH = "th";
    public static final String THEAD = "thead";
    public static final String TITLE = "title";
    public static final String TR = "tr";
    public static final String TT = "tt";
    public static final String U = "u";
    public static final String UL = "ul";
    public static final String VAR = "var";

    // Contains empty (void) tag names
    public final static List<String> EMPTY_TAGS = Arrays.asList(BR, BASE, COL, LINK, PARAM, META, FRAME, HR, IMG, INPUT, EMBED);
}
