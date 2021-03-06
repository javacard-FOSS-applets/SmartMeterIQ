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
// Created 1.11.08 by Hendrik
// 
// attribute expression signatures, consisting of a hash and a Bignat, 
//   on the host side
// 
// $Id: Host_signature.java,v 1.19 2009-06-19 20:37:36 tews Exp $

package ds.ov2.front;

import java.util.Arrays;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

import ds.ov2.util.Misc_host;
import ds.ov2.util.BigIntUtil;
import ds.ov2.util.APDU_Serializable;
import ds.ov2.util.APDU_byte_array;
import ds.ov2.util.Serializable_array;
import ds.ov2.bignat.APDU_BigInteger;


/** 
 * Host data type for {@link Signature Signature's} with signature
 * checking. Used to receive objects of type {@link Signature} from
 * the card. This class further implements the validity check for
 * signatures. This class does not implement sending a signature to
 * the card, because signatures are established on the card in a
 * completely differnt way.
 * <P>
 *
 * For a general description of signatures, the validity check and the
 * hash-size issue, see {@link Signature}.
 * <P>
 *
 * This is a host data type. It is compatible with nothing. The card
 * data type {@link Signature} is compatible with this class. 
 *
 * @author Hendrik Tews
 * @version $Revision: 1.19 $
 * @commitdate $Date: 2009-06-19 20:37:36 $ by $Author: tews $
 * @environment host
 * @CPP no cpp preprocessing needed
 */
public class Host_signature
    extends Serializable_array
    implements APDU_Serializable
{
    /** The hash part of the signature. Initialized in {@link
     * #from_byte_array from_byte_array}. 
     */
    /* package */ byte[] hash;

    /** The number part of the signature. Initialized in {@link
     * #from_byte_array from_byte_array}.
     */
    /* package */ BigInteger number;


    /** APDU container for the hash. */
    private APDU_byte_array apdu_hash;

    /** APDU container for the number. */
    private APDU_BigInteger apdu_number;

    /** Array of the two APDU containers in support for {@link
        Serializable_array}. */
    private APDU_Serializable[] serializable_contents;

    /** SHA-1 hash instance. */
    private MessageDigest digest;


    /**
     * 
     * Debug channel. If non-null print debug information to this
     * channel. Must be set from the outside.
     */
    public static PrintWriter out = null;


    /**
     * 
     * Create a new host signature object. Internal numbers and
     * buffers depend on the exponent and base sizes as well as on the
     * applet that we communicate with (see {@link Hash_size}).
     * 
     * @param short_bignat_size size of the exponent bignats in bytes
     * @param long_bignat_size size of the base bignats in bytes
     * @param applet_id the applet ID
     * @throws RuntimeException with a cause of type
     * NoSuchAlgorithmException if the SHA-1 hash function cannot be
     * found. 
     */
    public Host_signature(short short_bignat_size, short long_bignat_size,
                          byte applet_id) 
    {
        apdu_hash = new APDU_byte_array
                          (Hash_size.hash_size(short_bignat_size, applet_id));
        hash = apdu_hash.buf;
        apdu_number = new APDU_BigInteger(long_bignat_size);

        serializable_contents = new APDU_Serializable[2];
        serializable_contents[0] = apdu_hash;
        serializable_contents[1] = apdu_number;

        try {
            digest = MessageDigest.getInstance("SHA");
        }
        catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return;
    }


    /**
     * 
     * Signature check. Check whether {@link #hash} and {@link
     * #number} forms a valid signature for the blinded attribute
     * expression {@code blinded_a} with respect to the PTLS
     * parameters {@code params}. 
     * <P>
     *
     * If the argument {@code montgomerized} is true this method
     * performs a check for a montgomerized signature, that is a
     * signature where numbers must be montgomerized before they are
     * fed into the hash function. Further, when {@code montgomerized}
     * is true it is assumed that {@code blinded_a} is already
     * montgomerized. In essence, for a signature from the
     * Montgomerizing applet {@code montgomerized} should be true and
     * otherwise false.
     * <P>
     *
     * Restrictions of the hash size (see {@link Signature} and {@link
     * Hash_size}) are taken into account as necessary. 
     * 
     * The source code of this method is affected by Brand's patents
     * on selective disclosure protocols that are now in the posession
     * of Microsoft. Microsoft lawyers are still pondering our request
     * from January 2009 for making the full source code publically
     * available. The source code of this method is therefore
     * currently not publically available. The detailed operations of
     * this method are:
     * <DL>
     * <DT><STRONG>XXXXXXXXXXXXXXX</STRONG>
     * <DD>
     * <DT><STRONG></STRONG>
     * <DD>
     * <DT><STRONG></STRONG>
     * <DD>
     * <DT><STRONG></STRONG>
     * <DD>
     * </DL>
     *
     * 
     * @param blinded_a the blinded attribute expression that was
     * signed; in montgomerized form if {@code montgomerized} is true
     * @param params the PTLS parameters
     * @param montgomerized true if the signature and {@code
     * blinded_a} come from the montgomerizing applet
     * @return true if the signature is valid, false otherwise
     */
    private boolean check_signature(BigInteger blinded_a, 
                                    PTLS_rsa_parameters params,
                                    boolean montgomerized) 
    {
        // START PATENT CUT
        The code of this method is covered by patents owned by Microsoft.
        Microsoft lawyers are still pondering our request from January
        2009 to permit the distribution of the complete sources.
        See ``Rethinking Public Key Infrastructures and Digital
        Certificates: Building in Privacy'' by Brands or ``Performance
        issues of Selective Disclosure and Blinded Issuing Protocols
        on Java Card'' by Tews and Jacobs for a description of the
        algorithm to fill in here. 
        // END PATENT CUT
    }


    /**
     * 
     * Debug and exception wrapper for {@link #check_signature}.
     * Prints the arguments to {@link #out} if that is non-null and
     * prints information about escaping runtime exceptions to {@link
     * #out} or {@link System#err}. Does not catch any exception, only
     * prints information and rethrows them.
     * 
     * @param blinded_a the blinded attribute expression that was
     * signed; in montgomerized form if {@code montgomerized} is true
     * @param params the PTLS parameters
     * @param montgomerized true if the signature and {@code
     * blinded_a} come from the montgomerizing applet
     * @return true if the signature is valid, false otherwise
     */
    public boolean check_signature_ex(BigInteger blinded_a,
                                      PTLS_rsa_parameters params,
                                      boolean montgomerized) 
    {
        try {
            if(out != null) {
                BigInteger demont_fac;
                if(montgomerized)
                    demont_fac = params.hmod.demont_fac;
                else
                    demont_fac = BigInteger.ONE;

                out.format("check %s signature\n" +
                           "   c  = %s\n" +
                           "   r  = %s\n" +
                           "   A' = %s\n" +
                           "   ha inv = %s\n",
                           montgomerized ? "montgomerized" : "plain",
                           Misc_host.to_byte_hex_string(hash),
                           BigIntUtil.to_byte_hex_string(
                                             number.multiply(demont_fac)),
                           BigIntUtil.to_byte_hex_string(
                                             blinded_a.multiply(demont_fac)),
                           BigIntUtil.to_byte_hex_string(
                               params.ptls_public_key.multiply(blinded_a).
                               multiply(demont_fac).modInverse(params.n)));
            }
            return check_signature(blinded_a, params, montgomerized);
        }
        catch(ArithmeticException e) {
            PrintWriter o = out != null ? out : 
                new PrintWriter(System.err, true);
            o.format("check %s signature exception %s\n" +
                     "   c  = %s\n" +
                     "   r  = %s\n" +
                     "   A' = %s\n",
                     montgomerized ? "montgomerized" : "plain",
                     e,
                     Misc_host.to_byte_hex_string(hash),
                     BigIntUtil.to_byte_hex_string(number),
                     BigIntUtil.to_byte_hex_string(blinded_a));
            throw e;
        }
    }
            

    /**
     * 
     * Return the size of the hash {@link #hash}. Needed for the
     * compatibility check in {@link Signature#is_compatible_with
     * Signature.is_compatible_with}. 
     * 
     * @return the size of the hash in bytes
     */
    /* package */ int get_hash_size() {
        return apdu_hash.size();
    }


    /**
     * 
     * Return the size of the number {@link #number}. Needed for the
     * compatibility check in {@link Signature#is_compatible_with
     * Signature.is_compatible_with}. 
     * 
     * @return the size of {@link #number} in bytes
     */
    /* package */ int get_number_size() {
        return apdu_number.size();
    }


    //########################################################################
    // Serializable_array support
    // 

    /**
     * Return {@link #serializable_contents} in support for abstract
     * {@link Serializable_array}.
     *
     * @return array of objects to (de-)serialize
     */
    protected APDU_Serializable[] get_array() {
        return serializable_contents;
    }


    /**
     * Return 2 as effective size in support for abstract
     * {@link Serializable_array}.
     *
     * @return 2
     */
    public short get_length() {
        return 2;
    }


    /**
     * Deserialization of this object for the OV-chip protocol layer.
     * See {@link ds.ov2.util.APDU_Serializable#from_byte_array
     * APDU_Serializable.from_byte_array}.
     * <P>
     *
     * Overridden here to initialize {@link #number} from the APDU
     * wrapper after deserialization finished.
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
        short res = super.from_byte_array(len, this_index, 
                                          byte_array, byte_index);
        if(res != len)
            number = apdu_number.value;
        return res;
    }
}
