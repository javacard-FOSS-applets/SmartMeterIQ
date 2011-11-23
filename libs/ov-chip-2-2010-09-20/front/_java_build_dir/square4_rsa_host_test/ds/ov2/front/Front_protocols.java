//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//!!!   DO NOT EDIT OR CHANGE THIS FILE. CHANGE THE ORIGINAL INSTEAD.      !!!
//!!!   THIS FILE HAS BEEN GENERATED BY CPP AND SED,                       !!!
//!!!   BECAUSE JAVA DOES NOT SUPPORT CONDITIONAL COMPILATION.             !!!
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//# 1 "/tmp/tews/ov-dist/ov-chip-2-2010-09-20/front/Front_protocols.java"
//# 1 "<built-in>"
//# 1 "<command-line>"
//# 1 "/tmp/tews/ov-dist/ov-chip-2-2010-09-20/front/Front_protocols.java"
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
// Created 13.11.08 by Hendrik
// 
// registered protocols for the RSA applet
// 
// $Id: Front_protocols.java,v 1.5 2009-06-18 11:57:38 tews Exp $

//# 1 "./config" 1
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
// Created 5.11.08 by Hendrik
// 
// preprocessor config directives
// 
// $Id: config,v 1.16 2010-02-18 12:40:38 tews Exp $
//# 200 "./config"
/// Local Variables:
/// mode: c
/// End:
//# 26 "/tmp/tews/ov-dist/ov-chip-2-2010-09-20/front/Front_protocols.java" 2




  package ds.ov2.front;



   import ds.ov2.util.Protocol;
   import ds.ov2.util.Registered_protocols;



/** 
 * 
 * Central point of the OV-chip RSA applet. There is precisely one
 * instance of this class in the RSA applet. This instance bundles all
 * the functionality of the applet in the array of registered
 * protocols. This class is also responsible for creating/initializing
 * the complete object structure of the RSA applet.
 *
 * @author Hendrik Tews
 * @version $Revision: 1.5 $
 * @commitdate $Date: 2009-06-18 11:57:38 $ by $Author: tews $
 * @environment host, card
 * @CPP This class uses the following cpp defines:
 *   <a href="../../../overview-summary.html#PACKAGE">PACKAGE</a>,
 *   <a href="../../../overview-summary.html#PUBLIC">PUBLIC</a>,
 *   <a href="../../../overview-summary.html#TESTFRAME">TESTFRAME</a>
 *
 */
public class Front_protocols {

    /**
     * 
     * Description of the normal RSA card protocols.
     */
    public RSA_card_protocol_description rsa_description;



        /**
         * 
         * Description of the RSA card debug protocols. Only available
         * if TESTFRAME is defined.
         */
        public RSA_card_debug_description rsa_debug;



    /**
     * 
     * Array of registered protocols. Allocated in constructor, loaded
     * in {@link #init_protocols}.
     */
    private Protocol[] protocols;


    /**
     * 
     * Service instance for protocol arrays.
     */
    public final Registered_protocols registered_protocols;


    /**
     * 
     * Load protocols array. Called in the constructor and after the
     * allocate protocol. Before the allocate protocol all other
     * protocols are invalid (they are null).
     */
    public void init_protocols() {
        short i = 0;
        protocols[i++] = rsa_description.allocate_protocol;

        // The mem_size protocol is not delayed, it must come before
        // all the other delayed protocols. Similarly for the reset
        // protocol.

            protocols[i++] = rsa_debug.mem_size_protocol;
            protocols[i++] = rsa_debug.reset_applet_state_protocol;

        protocols[i++] = rsa_description.initialize_protocol;
        protocols[i++] = rsa_description.resign_protocol;
        protocols[i++] = rsa_description.gate_protocol;


            protocols[i++] = rsa_debug.status_protocol;


        registered_protocols.set_protocols(protocols);
        return;
    }


    /**
     * 
     * Central creation/allocation point of the OV-chip RSA applet.
     * This constructor creates all protocol descriptions (and thereby
     * all test applet objects) and calls {@link
     * #init_protocols} to allocate and initialize the
     * protocols array, which is then registered in {@link
     * #registered_protocols}.
     * 
     */
    public Front_protocols() {
        rsa_description = new RSA_card_protocol_description(this);


            rsa_debug = new RSA_card_debug_description(rsa_description);


        registered_protocols = new Registered_protocols();


            protocols = new Protocol[7];




        init_protocols();
    }


    /**
     * 
     * Update arguments and results of all steps in all protocols.
     * Needed because the RSA OV-chip applet switches references in
     * the resign step, see {@link
     * RSA_plain_card#switch_to_new_attributes
     * RSA_plain_card.switch_to_new_attributes} and {@link
     * RSA_mont_card#switch_to_new_attributes
     * RSA_mont_card.switch_to_new_attributes}.
     */
    public void update_steps() {
        rsa_description.update_get_signature_step();
        rsa_description.update_commit_step();

            rsa_debug.update_get_step();

    }
}