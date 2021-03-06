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
// Created 28.8.08 by Hendrik
// 
// byte buffers that are resizable up to a certain maximum
// 
// $Id: Resizable_buffer.java,v 1.12 2009-06-19 20:37:36 tews Exp $

#include <config>

#ifdef PACKAGE
  package PACKAGE;
#else
  package ds.ov2.util;
#endif

#ifdef JAVACARD_APPLET
import javacard.framework.Util;
import javacard.framework.JCSystem;
#endif



/** 
 * {@link APDU_Serializable} wrapper around a byte array that can
 * provide the illusion of different sizes. Used in the test frame for
 * the OV-chip protocol layer. The resize method {@link #set_size
 * set_size} is always available in contrast to the test-frame-only
 * resize methods in the bignat library. The underlying byte array in
 * {@link #buf} does of course never change its size.
 *
 * @author Hendrik Tews
 * @version $Revision: 1.12 $
 * @commitdate $Date: 2009-06-19 20:37:36 $ by $Author: tews $
 * @environment host, card
 * @CPP This class uses the following cpp defines:
 *   <a href="../../../overview-summary.html#PACKAGE">PACKAGE</a>,
 *   <a href="../../../overview-summary.html#PUBLIC">PUBLIC</a>,
 *   <a href="../../../overview-summary.html#ASSERT">ASSERT</a>,
 *   <a href="../../../overview-summary.html#JAVACARD_APPLET">JAVACARD_APPLET<a>
 */
PUBLIC class Resizable_buffer implements APDU_Serializable
{
    /**
     * 
     * Current size of the buffer. Is always smaller than {@link
     * #buf}{@code .length}.
     */
    private short size;
    

    /**
     * The buffer. The portion of the buffer that is used is
     * determined by the {@link #size} field.
     * 
     */
    public byte[] buf = null;


    /**
     * Current pretended size in bytes. Size for
     * the OV-chip protocol layer, see 
     * {@link ds.ov2.util.APDU_Serializable#size APDU_Serializable.size()}.
     *
     * @return pretended size in bytes
     */
    public short size() {
        return size;
    }


    /**
     * 
     * Set the pretended size. If {@code size} is larger than the
     * {@link #Resizable_buffer(short, boolean) constructor} argument
     * {@code max_size}, which determines the length of the underlying
     * byte array {@link #buf}, the set size request is ignored.
     * 
     * @param size new pretended size, should be smaller than the
     * {@code max_size} argument of the {@link
     * #Resizable_buffer(short, boolean) constructor}.
     */
    public void set_size(short size) {
        if(size <= buf.length)
            this.size = size;
    }


    /**
     * 
     * Normal constructor.
     * 
     * @param max_size maximal size of the buffer, this is also the
     * initial pretended size
     * @param in_ram allocate in transient RAM memory on the card
     */
    public Resizable_buffer(short max_size, boolean in_ram) {
        if(in_ram)
            buf = Misc.allocate_transient_byte_array(max_size);
        else
            buf = new byte[max_size];
        set_size(max_size);
        return;
    }


    #ifndef JAVACARD_APPLET
        /**
         * 
         * Convenience constructor for the host driver. Calls {@link
         * #Resizable_buffer(short, boolean)
         * Resizable_buffer(max_size, false)}.
         * <P>
         *
         * Only available if <a
         * href="../../../overview-summary.html#JAVACARD_APPLET">JAVACARD_APPLET<a>
         * is not defined.
         * 
         * @param max_size maximal size of the buffer, this is also the
         * initial pretended size
         */
        public Resizable_buffer(short max_size) {
            this(max_size, false);
        }
    #endif


    // // not needed at the moment
    // public Resizable_buffer clone() {
    //  try {
    //      Resizable_buffer o = (Resizable_buffer)super.clone();
    //      o.buf = this.buf.clone();
    //      ASSERT(o.buf != this.buf);
    //      return o;
    //  }
    //  catch(CloneNotSupportedException e) {
    //      ASSERT(false);
    //      return this;
    //  }
    // }

    //########################################################################
    // APDU_Serializable interface
    // 


    /**
     * Compatibility check for the OV-chip protocol layer.
     * See <a href="APDU_Serializable.html#apdu_compatibility">
     * the compatibility check 
     * explanations</a> and also
     * {@link ds.ov2.util.APDU_Serializable#is_compatible_with 
     * APDU_Serializable.is_compatible_with}.
     * <P>
     *
     * This object is only compatible to instances of this class with
     * the same pretended size.
     *
     * @param o actual argument or result
     * @return true if {@code o} is an instance of Resizable_buffer
     * with the same pretended size
     */
    public boolean is_compatible_with(Object o) {
        if(o instanceof Resizable_buffer) {
            return this.size() == ((Resizable_buffer)o).size();
        }
        return false;
    }


    /**
     * Serialize this buffer. See {@link 
     * ds.ov2.util.APDU_Serializable#to_byte_array 
     * APDU_Serializable.to_byte_array}.
     *
     * @param len available space in {@code byte_array}
     * @param this_index number of bytes that
     * have already been written in preceeding calls
     * @param byte_array data array to serialize the state into
     * @param byte_index index in {@code byte_array} 
     * @return the number of bytes actually written, except for the case 
     * where serialization finished by writing precisely 
     * {@code len} bytes, in this case {@code len + 1} is 
     * returned.
     */
    public short to_byte_array(short len, short this_index, 
                               byte[] byte_array, short byte_index) {
        ASSERT(this_index < size);
        short max = 
            (short)(this_index + len) <= size ? 
                        len : (short)(size - this_index);
        Misc.array_copy(buf, this_index, byte_array, byte_index, max);
        if((short)(this_index + len) == size)
            return (short)(len + 1);
        else
            return max;
    }


    /**
     * Deserialize this buffer. See {@link 
     * ds.ov2.util.APDU_Serializable#from_byte_array 
     * APDU_Serializable.from_byte_array}.
     *
     * @param len available data in {@code byte_array}
     * @param this_index number of bytes that
     * have already been read in preceeding calls
     * @param byte_array data array to deserialize from
     * @param byte_index index in {@code byte_array} 
     * @return the number of bytes actually read, except for the case 
     * where deserialization finished by reading precisely 
     * {@code len} bytes, in this case {@code len + 1} is 
     * returned.
     */
    public short from_byte_array(short len, short this_index,
                                 byte[] byte_array, short byte_index) {
        ASSERT(this_index < size);
        short max = 
            (short)(this_index + len) <= size ? 
                      len : (short)(size - this_index);
        Misc.array_copy(byte_array, byte_index, buf, this_index, max);
        if((short)(this_index + len) == size)
            return (short)(len + 1);
        else
            return max;
    }
}
