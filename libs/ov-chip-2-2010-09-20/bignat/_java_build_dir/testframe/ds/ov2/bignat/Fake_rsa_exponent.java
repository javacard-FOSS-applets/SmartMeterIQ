//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//!!!   DO NOT EDIT OR CHANGE THIS FILE. CHANGE THE ORIGINAL INSTEAD.      !!!
//!!!   THIS FILE HAS BEEN GENERATED BY CPP AND SED,                       !!!
//!!!   BECAUSE JAVA DOES NOT SUPPORT CONDITIONAL COMPILATION.             !!!
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//# 1 "/tmp/tews/ov-dist/ov-chip-2-2010-09-20/bignat/Fake_rsa_exponent.java"
//# 1 "<built-in>"
//# 1 "<command-line>"
//# 1 "/tmp/tews/ov-dist/ov-chip-2-2010-09-20/bignat/Fake_rsa_exponent.java"
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
// Created 12.11.08 by Hendrik
// 
// fake RSA_exponent class for test frames
// 
// $Id: Fake_rsa_exponent.java,v 1.14 2009-05-20 14:16:57 tews Exp $


package ds.ov2.bignat;

import java.math.BigInteger;
import ds.ov2.util.Convert_serializable;


/** 
 * {@link RSA_exponent_interface} implementation for the host. Uses
 * {@link BigInteger} internally. Provides nothing more
 * than just {@link BigInteger#modPow BigInteger.modPow} with a
 * strange interface.
 * <P>
 *
 * This class makes it possible to use {@link Vector#mont_rsa_exponent_mod
 * Vector.mont_rsa_exponent_mod} and code that depends on it on the host,
 * for instance in a test frame. <P>
 *
 * As far as possible other code is written in terms of {@link
 * RSA_exponent_interface} to avoid cpp trickery. However, when it
 * comes to constructor calls, there is no way around it. For those
 * few places the cpp macro <a
 * href="../../../overview-summary.html#RSA_EXPONENT">RSA_EXPONENT<a>
 * is used. It expands to either {@link RSA_exponent} or to
 * Fake_rsa_exponent. <P>
 *
 * For a number of general topics <a
 * href="package-summary.html#package_description">see also the package
 * description.</a>
 *
 *
 * @author Hendrik Tews
 * @version $Revision: 1.14 $
 * @commitdate $Date: 2009-05-20 14:16:57 $ by $Author: tews $
 * @environment host
 * @CPP no cpp preprocessing needed for this class
 */
public class Fake_rsa_exponent implements RSA_exponent_interface {

    /**
     * 
     * The configured modulus. Set in {@link #set_modulus set_modulus}.
     */
    private BigInteger modulus = null;


    /**
     * 
     * The configured exponent. Set in {@link #set_exponent set_exponent}.
     */
    private BigInteger exponent = null;


    /**
     * 
     * Empty method. Only here to comply with {@link
     * RSA_exponent_interface}.
     * 
     * @param key_size ignored argument
     */
    public void init_key(short key_size) {
        return;
    }


    /**
     * 
     * Non-allocating constructor. Empty, because there is nothing to
     * do here.
     */
    public Fake_rsa_exponent() {
        return;
    }


    /**
     * 
     * Empty method. Only here to comply with {@link RSA_exponent_interface}.
     * 
     * @param key_byte_size ignored argument
     */
    public void allocate(short key_byte_size) {
        return;
    }


    /**
     * 
     * Allocating constructor. Calls the {@link #Fake_rsa_exponent()
     * non-allocating constructor} and {@link #allocate allocate} as
     * prescribed, although they are all empty.
     * 
     * @param key_byte_size ignored argument
     */
    public Fake_rsa_exponent(short key_byte_size) {
        this();
        allocate(key_byte_size);
    }


    /**
     * 
     * Saves the modulus {@code mod} for later use in {@link
     * #modulus}.
     * 
     * @param mod modulus to use later in {@link #exponent exponent}
     * @param offset this argument is ignored in this class
     */
    public void set_modulus(Bignat mod, short offset) {
        APDU_BigInteger mod_bi = new APDU_BigInteger(mod.size());
        Convert_serializable.from(mod_bi, mod);
        modulus = mod_bi.value;
        return;
    }


    /**
     * 
     * Saves the exponent {@code exponent} for later use in {@link
     * #modulus}.
     * 
     * @param exponent exponent to use later in {@link #power power}
     * or {@link #fixed_power fixed_power}
     * @param temp this argument is ignored in this class
     * @param offset this argument is ignored in this class
     */
    public void set_exponent(Bignat exponent, Bignat temp, short offset) {
        APDU_BigInteger exp_bi = new APDU_BigInteger(exponent.size());
        Convert_serializable.from(exp_bi, exponent);
        this.exponent = exp_bi.value;
        return;
    }


    /**
     *
     * Modular power with preconfigured modulus and exponent. Sets
     * {@code result} to {@code base}^{@code exp} mod {@code modulus},
     * where the {@code modulus} and {@code exp} must have been
     * configured before with {@link #set_modulus set_modulus} and
     * {@link #set_exponent set_exponent}, respectively. 
     *
     * @param base 
     * @param result reference for storing the result
     * @param offset this argument is ignored in this class
     */
    public void fixed_power(Bignat base, Bignat result, short offset) {
        APDU_BigInteger base_bi = new APDU_BigInteger(base.size());

        Convert_serializable.from(base_bi, base);

        BigInteger res_bi = base_bi.value.modPow(exponent, modulus);
        Convert_serializable.to(new APDU_BigInteger(result.size(), res_bi),
                                result);
    }

    /**
     * 
     * Modular exponent. Sets {@code result} to {@code base}^{@code
     * exp} (modulo {@code modulus}), where the {@code modulus} must have
     * been configured before with {@link #set_modulus set_modulus}. 
     * <P>
     * 
     * Computations are done with {@link BigInteger}, data conversion
     * with {@link Convert_serializable}.
     *
     * @param base base
     * @param exp exponent
     * @param result gets the result base^exponent modulo {@link #modulus}
     * @param offset this argument is ignored in this class
     */
    public void power(Bignat base, Bignat exp, Bignat result, short offset) {
        APDU_BigInteger base_bi = new APDU_BigInteger(base.size());
        APDU_BigInteger exp_bi = new APDU_BigInteger(exp.size());

        Convert_serializable.from(base_bi, base);
        Convert_serializable.from(exp_bi, exp);

        BigInteger res_bi = base_bi.value.modPow(exp_bi.value, modulus);
        Convert_serializable.to(new APDU_BigInteger(result.size(), res_bi),
                                result);
    }
}
