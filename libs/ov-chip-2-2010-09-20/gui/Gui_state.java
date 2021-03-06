// 
// OV-chip 2.0 project
// 
// Digital Security (DS) group at Radboud Universiteit Nijmegen
// 
// Copyright (C) 2008, 2009
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of
// the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License in file COPYING in this or one of the
// parent directories for more details.
// 
// Created 19.12.08 by Hendrik
// 
// state of the GUI that is not maintained in the GUI objects themselves
// 
// $Id: Gui_state.java,v 1.16 2010-02-18 14:02:08 tews Exp $

package ds.ov2.gui;

import java.util.LinkedList;
import java.io.PrintWriter;
import javax.smartcardio.CardTerminal;

import ds.ov2.util.Reference;
import ds.ov2.front.Applet_type;

/** 
 * Global state fields of the GUI.
 * <P>
 *
 * Static class.
 *
 * @author Hendrik Tews
 * @version $Revision: 1.16 $
 * @commitdate $Date: 2010-02-18 14:02:08 $ by $Author: tews $
 * @environment host
 * @CPP no cpp preprocessing needed
 */
public class Gui_state {

    /**
     * 
     * Static class, object creation disabled.
     */
    protected Gui_state() {}

    /**
     * 
     * PTLS parameter file. Set via option {@code -load-ptls-params}.
     * When set the GUI loads PTLS parameters from the indicated file
     * on startup.
     */
    public static Reference<String> ptls_params_file = new Reference<String>();

    /**
     * 
     * Verbosity level for stdout of the GUI.
     */
    public static int verbosity = 15;


    /**
     * 
     * If true warn about key sizes about we know the applet will crash.
     */
    public static boolean warn_about_invalid_key_sizes = true;


    /**
     * 
     * If true warn about key sizes that will lead to transaction
     * times greater than about 2 minutes.
     */
    public static boolean warn_about_long_key_sizes = true;


    /**
     * 
     * Output channel for all progress and debug messages.
     */
    public static PrintWriter out = new PrintWriter(System.out, true);


    /**
     * 
     * Default card reader number.
     */
    public static final Reference<Integer> default_reader_number =
        new Reference<Integer>(0);

    /**
     * 
     * If true, use the jcop emulator as default terminal. Set with
     * option {@code -jcop}.
     */
    public static final Reference<Boolean> default_jcop = 
        new Reference<Boolean>(false); 


    /**
     * 
     * Default jcop emulator port.
     */
    public static final int default_jcop_port = 8015;


    /**
     * 
     * Configured jcop ports. Every {@code -jcop-port} option adds one
     * number here. 
     */
    public static LinkedList<Integer> jcop_ports = new LinkedList<Integer>();


    /**
     * 
     * The card terminal the GUI is attached to.
     */
    public static CardTerminal card_terminal = null;


    /**
     * 
     * The default file name of the square 4 applet.
     */
    public static final String default_square_4_applet_file_name = 
        "../front/" + Applet_type.SQUARED4_RSA_APPLET.applet_file();


    /**
     * 
     * The default file name of the square 2 applet.
     */
    public static final String default_square_2_applet_file_name = 
        "../front/" + Applet_type.SQUARED_RSA_APPLET.applet_file();


    /**
     * 
     * The default file name of the plain coprocessor applet.
     */
    public static final String default_plain_applet_file_name = 
        "../front/" + Applet_type.PLAIN_RSA_APPLET.applet_file();


    /**
     * 
     * The default file name of the pure Montgomerizing applet.
     */
    public static final String default_mont_applet_file_name = 
        "../front/" + Applet_type.MONT_RSA_APPLET.applet_file();


    /**
     * 
     * The square 2 applet file name; can be changed in the
     * config window.
     */
    public static String square_4_applet_file_name = 
        default_square_4_applet_file_name;


    /**
     * 
     * The square 4 applet file name; can be changed in the
     * config window.
     */
    public static String square_2_applet_file_name = 
        default_square_2_applet_file_name;


    /**
     * 
     * The plain coprocessor applet file name; can be changed in the
     * config window.
     */
    public static String plain_applet_file_name = 
        default_plain_applet_file_name;


    /**
     * 
     * The pure Montgomerizing applet file name; can be changed in the
     * config window.
     */
    public static String mont_applet_file_name = 
        default_mont_applet_file_name;


    /**
     * 
     * Return the configured cap file name for an applet type.
     * 
     * @param type the applet type
     * @return configured cap file name
     */
    public static String applet_file_name(Applet_type type) {
        String cap_file = null;

        switch(type) {
        case PLAIN_RSA_APPLET:
            cap_file = plain_applet_file_name;
            break;
        case MONT_RSA_APPLET:
            cap_file = mont_applet_file_name;
            break;
        case SQUARED_RSA_APPLET:
            cap_file = square_2_applet_file_name;
            break;
        case SQUARED4_RSA_APPLET:
            cap_file = square_4_applet_file_name;
            break;
        default:
            assert false;
        }

        return cap_file;
    }
}
