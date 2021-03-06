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
// Created 27.8.08 by Hendrik
// 
// data protocol: card implementation
// 
// $Id: Data_protocol_card.java,v 1.8 2009-04-22 08:04:55 tews Exp $

#include <config>

#ifdef PACKAGE
  package PACKAGE;
#else
  package ds.ov2.test;
#endif

import javacard.framework.ISOException;


#ifdef JAVADOC
  import ds.ov2.util.Resizable_buffer;
#endif


/** 
 * Card functionality for the data protocols of Data_protocol.id.
 *
 * @author Hendrik Tews
 * @version $Revision: 1.8 $
 * @commitdate $Date: 2009-04-22 08:04:55 $ by $Author: tews $
 * @environment card
 * @CPP This class uses the following cpp defines:
 *   <a href="../../../overview-summary.html#PACKAGE">PACKAGE</a>,
 *   <a href="../../../overview-summary.html#PUBLIC">PUBLIC</a>
 */
PUBLIC class Data_protocol_card {
    // This is a singleton class.
    // The data protocol consists of a few steps that simply receive
    // and transmit bigger amounts of data.


    /**
     * 
     * Empty constructor.
     */
    Data_protocol_card() {
        return;
    }


    /**
     * 
     * Check if the buffer {@code b} contains the expected data-check
     * pattern. The host fills the buffer with the consecutive numbers
     * from 0 to 255, repeated as often as necessary over all
     * arguments it sends to the card. This method checks if this
     * pattern arrived here in the applet.
     * 
     * @param b buffer to check
     * @param count start value for the first byte of the buffer
     * @return the next expected byte contents for the first byte of
     * the next buffer to check
     * @throws ISOException with value {@link
     * ds.ov2.util.Response_status#OV_TEST_FAILED_00} {@code | 0x01} in
     * case the buffer
     * contains unexpected data.
     */
    private byte check_buffer(Resizable_buffer b, byte count) {
        for(short i = 0; i < b.size(); i++) {
            if(b.buf[i] != count) {
                ISOException.throwIt((short)(
                                     Response_status.OV_TEST_FAILED_00 | 0x01));
            }
            count++;
        }
        return count;
    }


    /**
     * 
     * Fill the buffer with the data-check pattern. The data-check
     * pattern consists of the consecutive numbers from 0 to 255,
     * repeated as often as encessary over all arguments or results.
     * 
     * @param b buffer to fill
     * @param count start value for the first byte of {@code b}
     * @return the first byte for the next buffer
     */
    private byte fill_buffer(Resizable_buffer b, byte count) {
        for(short i = 0; i < b.size(); i++) {
            b.buf[i] = count++;
        }
        return count;
    }


    /**
     * 
     * Check and fill all 10 buffers. This method checks if all
     * argument buffers contain the data-check pattern and writes this
     * pattern into the result buffers.
     * 
     * @param a1 argument buffer
     * @param a2 argument buffer
     * @param a3 argument buffer
     * @param a4 argument buffer
     * @param a5 argument buffer
     * @param b1 result buffers
     * @param b2 result buffers
     * @param b3 result buffers
     * @param b4 result buffers
     * @param b5 result buffers
     */
    public void check(Resizable_buffer a1, 
                      Resizable_buffer a2, 
                      Resizable_buffer a3, 
                      Resizable_buffer a4, 
                      Resizable_buffer a5, 
                      Resizable_buffer b1, 
                      Resizable_buffer b2, 
                      Resizable_buffer b3, 
                      Resizable_buffer b4, 
                      Resizable_buffer b5) {
        byte count = 0;
        count = check_buffer(a1, count);
        count = check_buffer(a2, count);
        count = check_buffer(a3, count);
        count = check_buffer(a4, count);
        count = check_buffer(a5, count);
        count = 0;
        count = fill_buffer(b1, count);
        count = fill_buffer(b2, count);
        count = fill_buffer(b3, count);
        count = fill_buffer(b4, count);
        count = fill_buffer(b5, count);
        return;
    }

    // set_size is not here. I consider it as kind of meta method, which 
    // configures properties of the check method. Therefore I put it in
    // the meta level (Data_protocol_description), where it has convenient
    // access to buf_0..9.

}
