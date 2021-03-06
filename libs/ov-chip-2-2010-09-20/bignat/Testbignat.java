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
// Created 12.8.08 by Hendrik
//
// bignat test frame
//
// $Id: Testbignat.java,v 1.36 2010-09-20 11:20:15 tews Exp $

#include "bignatconfig"

package ds.ov2.bignat;

import java.math.BigInteger;
import java.util.Random;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import ds.ov2.util.BigIntUtil;
import ds.ov2.util.Convert_serializable;
import ds.ov2.util.Reference;
import ds.ov2.util.BigInteger_inputs;
import ds.ov2.util.Commandline;
import ds.ov2.util.Option;
import ds.ov2.util.Bool_option;
import ds.ov2.util.Int_option;
import ds.ov2.util.String_option;
import ds.ov2.util.BigInt_input_option;
import ds.ov2.util.BigInt_hex_input_option;
import ds.ov2.util.Value_option;
import ds.ov2.util.Parse_commandline;
import ds.ov2.bignat.Convenience.Timed_result;


/** 
 * Cardless test frame for the Bignat library. Tests various
 * functionality of {@link Bignat}, {@link Vector} and {@link Modulus}
 * and of some host data types on a conventional Java virtual machine,
 * that is, <em>not</em> on a Java Card.
 * <P>
 *
 * The test frame and parts of the tested sources require
 * preprocessing with cpp, the C preprocessor. So compile with {@code
 * make} and run the test frame with the script {@code run-testbignat}
 * in the directory of the original sources ({@code src/bignat}). 
 * <P>
 *
 * The single tests are enabled with command line options. To see what
 * test are available look at the options list produced by {@code -h}
 * or {@code -help}. Apart from the options that enable specific tests
 * there are also general options for setting the Bignat size or the
 * number of rounds the tests are performed. Not all tests are
 * sensible to all options, using test data files is for instance only
 * supported in the vector exponent test (option {@code -vec-exp}).
 * <P>
 *
 * The tests usually run with random data. In case an error is
 * detected the test frame prints the relevant numbers for the test
 * that failed and exits immediately. To repeat a test with some given
 * numbers, numbers can be passed in via the option {@code -i} or
 * {@code -hex}. The latter accepts the hex output produced by the
 * test frame itself, that is, dots in the hex number are filtered
 * out. 
 * <P>
 * 
 *
 * By using different defines for the cpp preprocessing different
 * versions of the code can be selected (and tested). The following
 * defines can be used.
 * <DL>
 * <DT>BIGNAT_USE_INT
 * <DD>Use the int/long configuration of the Bignat library instead of
 *     byte/short.
 *
 * <DT>OPT_DOUBLE_ADD
 * <DD>Optimization: In <a
 * href="package-summary.html#montgomery_factor">Montgomery
 * multiplication</a> do addition and Montgomery correction in one
 * loop.
 *
 * <DT>OPT_SKIP_DEVIDE
 * <DD>Optimization: Skip final division in Montgomery multiplication
 * if not strictly neccessary.
 * 
 * <DT>OPT_SPECIAL_SQUARE
 * <DD>Optimization: Use a special squaring method instead of the
 * general multiplication for squaring.
 * </DL>
 * I tested the optimizations in this test frame. Here they only have
 * marginal effect and two of them even slow down the code. I have not
 * yet done measurements on the card.
 * <P>
 *
 * The defines should be put into the {@code CPPFLAGS} makefile
 * variable with a {@code -D} switch prepended. For instance
 * <PRE>
 *    CPPFLAGS:=-DOPT_SPECIAL_SQUARE
 * </PRE>
 * in src/ConfigMakefile or src/bignat/LocalMakefile. To set them on
 * the command line use
 * <PRE>
 *    make 'CPPFLAGS=-DOPT_SPECIAL_SQUARE'
 * </PRE>
 * <P>
 *
 * For a number of general topics <a
 * href="package-summary.html#package_description">see also the package
 * description.</a>
 *
 * @CPP This class uses the following cpp defines:
 *   <a href="../../../overview-summary.html#DOUBLE_DIGIT_TYPE">DOUBLE_DIGIT_TYPE</a>
 *
 * @author Hendrik Tews
 * @version $Revision: 1.36 $
 * @commitdate $Date: 2010-09-20 11:20:15 $ by $Author: tews $
 * @environment host
 */
public class Testbignat {

    /**
     * 
     * Short name for diagnostics printing. Currently {@value}.
     */
    public static final String short_application_name = "Testbignat";

    /**
     * 
     * Long name for diagnostics printing. Currently {@value}.
     */
    public static final String long_application_name = "Bignat Testframe";


    // configuration section

    /**
     * 
     * Remember the {@code -add} option.
     */
    private static Reference<Boolean> run_add = new Reference<Boolean>(false);

    /**
     * 
     * Remember the {@code -times-minus} option.
     */
    private static Reference<Boolean> run_times_minus = 
        new Reference<Boolean>(false);

    /**
     * 
     * Remember the {@code -div} option.
     */
    private static Reference<Boolean> run_remainder_divide = 
        new Reference<Boolean>(false);

    /**
     * 
     * Remember the {@code -mult} option.
     */
    private static Reference<Boolean> run_slow_mult = 
        new Reference<Boolean>(false);

    /**
     * 
     * Remember the {@code -double-mult} option.
     */
    private static Reference<Boolean> run_double_mult = 
        new Reference<Boolean>(false);

    /**
     * 
     * Remember the {@code -mont-mult} option.
     */
    private static Reference<Boolean> run_mont_mult = 
        new Reference<Boolean>(false);

    /**
     * 
     * Remember the {@code -exp} option.
     */
    private static Reference<Boolean> run_exponent = 
        new Reference<Boolean>(false);

    /**
     * 
     * Remember the {@code -vec-exp} option.
     */
    private static Reference<Boolean> run_vector_exponent = 
        new Reference<Boolean>(false);

    /**
     * 
     * Remember the {@code -conv-exp} option.
     */
    private static Reference<Boolean> run_convenience_exponent = 
        new Reference<Boolean>(false);

    /**
     * 
     * Remember the {@code -conv-vec-exp} option.
     */
    private static Reference<Boolean> run_convenience_vector_exponent = 
        new Reference<Boolean>(false);

    /**
     * 
     * Remember the {@code -mult-square} option.
     */
    private static Reference<Boolean> run_square_mult = 
        new Reference<Boolean>(false);


    /**
     * 
     * Remember the {@code -mult-square-4} option.
     */
    private static Reference<Boolean> run_square_mult_4 = 
        new Reference<Boolean>(false);


    /**
     * 
     * Records whether {@link #bignat_size} has been changed via the
     * {@link -size} option.
     */
    private static boolean bignat_size_set = false;


    /**
     * 
     * Bignat size of the {@code -size} option. Defaults to 12.
     */
    private static short bignat_size = 12;

    /**
     * 
     * Montgomery size. Size used for numbers that come in touch with
     * <a href="package-summary.html#montgomery_factor">Montgomery
     * multiplication</a>. Depends on {@link #bignat_size} and
     * therefore on the {@code -size} option. Two digits less than
     * {@code #bignat_size}. The number of bytes dependes on {@link
     * Bignat#size_multiplier}.
     */
    private static short mont_bignat_size;


    /**
     * 
     * Value of the {@code -vec-exp-size} option. Defaults to 5.
     */
    private static Reference<Integer> vector_exponent_length = 
        new Reference<Integer>(5);


    /**
     * 
     * Value of the {@code -vec-exp-var} option. Controls for how much
     * bases precomputed base factors are used in the vector exponent
     * test (testing {@link Vector#exponent_mod Vector.exponent_mod}).
     * Defaults to -1. A value of {@code -1} means to use randomly
     * many precomputed factors.
     */
    private static Reference<Integer> vector_exponent_variable_bases = 
        new Reference<Integer>(-1);


    /**
     * 
     * Bit size parameter for random BigInteger creation. Depends on
     * {@link #bignat_size}.
     */
    private static int max_bits = max_bits_from_size(bignat_size);


    /**
     * 
     * Value of the {@code -rounds} option. Number of rounds every
     * test is run. Defaults to 5000.
     */
    private static Reference<Integer> rounds = new Reference<Integer>(5000);


    /**
     * 
     * Value of the {@code -write} option. File to write test data to.
     * Default value null means to not write the test data.
     */
    private static Reference<String> write_to_file = new Reference<String>();

    /**
     * 
     * Value of the {@code -read} option. File to read test data from.
     * Default value null means to not read any data from file.
     */
    private static Reference<String> read_from_file = new Reference<String>();


    /**
     * 
     * Verbosity. Controls the amount of messages printed during the
     * run. Set to 1 by {@code -v}, to 5 by {@code -d}, to 10 by
     * {@code -dd} and to 15 by {@code -ddd}.
     */
    private static Reference<Integer> verbosity = new Reference<Integer>(0);


    /**
     * 
     * User provided inputs.
     */
    private static BigInteger_inputs fix_inputs = new BigInteger_inputs();


    /**
     * 
     * Static class, object creation disabled.
     */
    protected Testbignat() {}


    /**
     * 
     * Declaration of all options that this test frame recognizes.
     */
    public static Option[] options = new Option[] {
        new Bool_option("-add",         run_add,
                        "test addition"),
        new Bool_option("-times-minus", run_times_minus, 
                        "test times_minus"),
        new Bool_option("-div",         run_remainder_divide,
                        "test remainder_divide"),
        new Bool_option("-mult",        run_slow_mult,
                        "test normal modulo multiplication"),
        new Bool_option("-double-mult", run_double_mult,
                        "test multiplication with double sized result"),
        new Bool_option("-mont-mult",   run_mont_mult,
                        "test Montgomery multilication"),
        new Bool_option("-exp",         run_exponent,
                        "test modular exponent"),
        new Bool_option("-vec-exp",     run_vector_exponent,
                        "test vector exponent"),
        new Bool_option("-conv-exp",    run_convenience_exponent,
                        "test Convenience.exponent_mod"),
        new Bool_option("-conv-vec-exp",run_convenience_vector_exponent,
                        "test Convenience.vector_exponent_mod"),
        new Bool_option("-mult-square", run_square_mult,
                        "test Bignat.squared_rsa_mult"),
        new Bool_option("-mult-square-4", run_square_mult_4,
                        "test Bignat.squared_rsa_mult_4"),
        new Int_option("-size", null, "n", "set Bignat size") {
            public void matched(Commandline cl) {
                int x = get_int_argument(cl);
                if(x <= 0 || x > Short.MAX_VALUE) {
                    System.err.println("size must be a positive short");
                    System.exit(1);
                }
                bignat_size = (short)x;
                max_bits = max_bits_from_size(x);
                bignat_size_set = true;
            }
        },
        new Int_option("-vec-exp-size", vector_exponent_length, "n",
                       "set number of positions for vector exponent"),
        
        new Int_option("-vec-exp-var", vector_exponent_variable_bases,
                       "n", 
                       "set number of variable bases for vector exponent"),
        new Int_option("-rounds", rounds, "n", "run n rounds of each test"),
        new String_option("-write", write_to_file, "file",
                          "write test data into file"),
        new String_option("-read", read_from_file, "file", 
                          "read test data from file"),
        new BigInt_input_option("-i", fix_inputs, "n", 
                                "provide decimal n as input for the test"),
        new BigInt_hex_input_option("-hex", fix_inputs, "n", 
                                    "provide hex n as input for the test"),
        new Value_option<Integer>("-d", verbosity, 5, "be more verbose"),
        new Value_option<Integer>("-dd", verbosity, 10, 
                                  "output many debug/progress messages"),
        new Value_option<Integer>("-ddd", verbosity, 15, 
                                  "output anything which might be useful")
    };


    /**
     * 
     * Main test frame method. Parses the command line and runs all
     * tests that have been enabled.
     * 
     * @param args option array
     * @throws FileNotFoundException in case the test data file
     * specified via {@code -read} does not exist
     * @throws IOException for errors when reading or writing the test
     * data files
     */
    public static void main(String[] args) 
        throws FileNotFoundException, IOException
    {
        new Parse_commandline(options, short_application_name).parse(args);

        mont_bignat_size = (short)(bignat_size - 2 * Bignat.size_multiplier);
        assert mont_bignat_size >= 0;
        Random rand = new Random();
        Bignat.verbosity = verbosity.ref;

        BufferedReader data_in = null;
        PrintWriter data_out = null;

        if(read_from_file.ref != null) {
            System.out.format("read test data from file %s\n", 
                              read_from_file.ref);
            data_in = new BufferedReader(new FileReader(read_from_file.ref));
        }

        if(write_to_file.ref != null) {
            System.out.format("write test data to file %s\n", 
                              write_to_file.ref);
            data_out = new PrintWriter(write_to_file.ref);
        }

        if(run_add.ref)
            test_add(rand);
        if(run_times_minus.ref)
            test_times_minus(rand);
        if(run_remainder_divide.ref)
            test_remainder_divide(rand);
        if(run_slow_mult.ref)
            test_mult_mod(rand);
        if(run_double_mult.ref)
            test_double_mult(rand);
        if(run_mont_mult.ref)
            test_montgomery_mult(rand);
        if(run_exponent.ref)
            test_exponent(rand, data_in, data_out);
        if(run_vector_exponent.ref)
            test_vector_exponent(rand, data_in, data_out);
        if(run_convenience_exponent.ref)
            test_convenience_exponent(rand);
        if(run_convenience_vector_exponent.ref)
            test_convenience_vector_exponent(rand);
        if(run_square_mult.ref)
            test_square_mult(rand);
        if(run_square_mult_4.ref)
            test_square_mult_4(rand);
    }


    /**
     * 
     * Computes the successor of {@code numBits} argument for the
     * {@link BigInteger#BigInteger(int, Random)} constructor out of a
     * Bignat byte size. The successor is needed because the result is
     * used like 
     * <PRE>
     *    new BigInteger(rand.nextInt(max_bits_from_size(...)), rand)
     * </PRE>
     * where {@code rand} is an instance of {@link Random}.
     * 
     * @param size size in bytes
     * @return {@code 8 * size + 1}
     */
    private static int max_bits_from_size(int size) {
        return 8 * size +1;
    }


    // Intro_format must be a format string for one integer argument.
    /**
     * 
     * Prints an array of BigIntegers in decimal and dotted hex.
     * Argument {@code intro_format} is a format string for the
     * heading, for instance "array foo with %d elements\n". It must
     * contain at most one integer conversion and should be terminated
     * with a newline. An empty intro format effectively discards the
     * heading. Argument {@code line_start} is printed at the
     * beginning of each line, for instance "foo".
     * 
     * @param intro_format Format string for the heading with at most
     * one integer conversion (for the number of elements in {@code bi})
     * @param line_start array name string printed for each array element
     * @param bi the BigIntegers to be printed
     */
    public static void print_bi_array(String intro_format, String line_start, 
                                      BigInteger bi[]) {
        System.out.format(intro_format, bi.length);
        for(int i = 0; i < bi.length; i++) {
            System.out.format("%s[%d]: %s\n", line_start, i, bi[i]);
            System.out.format("%s[%d]: %s\n", line_start, i, 
                              BigIntUtil.to_byte_hex_string(bi[i]));
        }
        return;
    }


    /**
     * 
     * Test {@link Bignat#add Bignat.add} once. Takes the summands
     * from the fixed inputs, if available. Otherwise generates random
     * summands of a random size below the configured Bignat size. <P>
     *
     * Contains no protection against generating an overflow in {@link
     * Bignat#add Bignat.add}, which would be reported as a failure. 
     * 
     * @param rand randomness source
     * @return true if the test succeeded.
     * @todo fix random generation of x_bi, why mont_bignat_size?
     * @todo protect against overflow
     */
    public static boolean test_add_once(Random rand) {
        BigInteger x_bi, y_bi;

        if(fix_inputs.has_n_inputs(2, "addition")) {
            x_bi = fix_inputs.pop();
            y_bi = fix_inputs.pop();
        }
        else {
            x_bi = 
                new BigInteger(rand.nextInt(max_bits_from_size(
                                                       mont_bignat_size)),
                               rand);
            y_bi = new BigInteger(rand.nextInt(max_bits), rand);
        }

        BigInteger sum_bi = x_bi.add(y_bi);

        Bignat x_bn = Convenience.bn_from_bi(bignat_size, x_bi);
        Bignat y_bn = Convenience.bn_from_bi(bignat_size, y_bi);
        Bignat sum_bn = 
            new Bignat((short)(bignat_size + Bignat.size_multiplier));

        if(verbosity.ref >= 5) {
            System.out.format("%s + %s = %s\n",
                              x_bi, y_bi, sum_bi);
            System.out.format("%s + %s = %s\n", 
                              BigIntUtil.to_byte_hex_string(x_bi), 
                              BigIntUtil.to_byte_hex_string(y_bi), 
                              BigIntUtil.to_byte_hex_string(sum_bi));
        }

        sum_bn.copy(x_bn);
        sum_bn.add(y_bn);

        BigInteger sum_bnbi = Convenience.bi_from_bn(sum_bn);

        boolean success = sum_bi.compareTo(sum_bnbi) == 0;
        if(! success || verbosity.ref > 5) {
            if(success)
                System.out.println("success");
            else
                System.out.format("%s: %s + %s != %s\n",
                                  "FAILURE add",
                                  x_bi, y_bi, 
                                  sum_bnbi);
        }
        return success;
    }


    /**
     * 
     * Test {@link Bignat#add Bignat.add} {@link #rounds} times. Exit
     * if a failure is detected. If fixed inputs are available, they
     * will be used.
     * 
     * @param rand randomness source
     */
    public static void test_add(Random rand) {
        for(int i = 0; i < rounds.ref; i++) {
            if(!test_add_once(rand)) {
                System.out.println(
                   "\nERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ");
                System.exit(1);
            }
        }
        System.out.println(
            "\nSUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS");
        return;
    }


    /**
     * 
     * Test {@link Bignat#times_minus Bignat.times_minus} once. Test
     * arguments are generated randomly in a fashion to simulate the
     * use of {@link Bignat#times_minus times_minus} in division.
     * Underflows are avoided. No fixed inputs are used.
     * 
     * @param rand randomness source
     */
    public static void test_times_minus(Random rand) {
        BigInteger two_bi = BigInteger.ONE.add(BigInteger.ONE);

        BigInteger a = new BigInteger(rand.nextInt(max_bits), rand);
        BigInteger b = new BigInteger(rand.nextInt(max_bits), rand);

        // ensure a >= b
        if(a.compareTo(b) < 0) {
            BigInteger x = a;
            a = b;
            b = x;
        }

        BigInteger c = a.divide(b);

        // a = new BigInteger("953052646609826698858773");
        // b = new BigInteger("923239019437");
        // c = new BigInteger("240");

        // a = new BigInteger("48284");
        // b = new BigInteger("227");
        // c = new BigInteger("212");


        // BigInteger uses 2-complement, therefore positive values have
        // sometimes a leading zero (to ensure the 0-sign bit).
        byte[] c_bytes = c.toByteArray();
        boolean c_with_leading_zero = c_bytes[0] == 0;

        int c_significant_bytes = c.toByteArray().length;
        if(c_with_leading_zero)
            c_significant_bytes--;
        int b_shift = (c_significant_bytes -1) / Bignat.size_multiplier;

        // Make a BigInteger from the first digit (byte) of c.
        // Prepend a zero digit to avoid misinterpretation as negative number.
        // For BIGNAT_USE_INT we must of course take the first 32 bits.
        byte[] d;
        if(Bignat.use_byte_digits) {
            d = new byte[]{0,0};
            d[1] = c_with_leading_zero ? c_bytes[1] : c_bytes[0];
        }
        else {                  // BIGNAT_USE_INT
            d = new byte[]{0,0,0,0,0};
            int to_copy = c_significant_bytes % 4;
            if(to_copy == 0)
                to_copy = 4;
            System.arraycopy(c_bytes,
                             c_with_leading_zero ? 1 : 0,
                             d,
                             5 - to_copy,
                             to_copy);
        }
        BigInteger d_bi = new BigInteger(d);

        BigInteger b_shifted = 
            b.multiply(two_bi.pow(Bignat.digit_len * b_shift));
        BigInteger e = a.subtract(b_shifted.multiply(d_bi));

        System.out.format("%s - %s * (%s << %d) =\n%s - %s * %s = %s\n",
                          a, d_bi, b, b_shift, a, d_bi, b_shifted, e);
        System.out.format("%s - %s * (%s << %d) =\n%s - %s * %s =\n%s\n",
                          BigIntUtil.to_byte_hex_string(a), 
                          BigIntUtil.to_byte_hex_string(d_bi), 
                          BigIntUtil.to_byte_hex_string(b), 
                          b_shift, 
                          BigIntUtil.to_byte_hex_string(a), 
                          BigIntUtil.to_byte_hex_string(d_bi), 
                          BigIntUtil.to_byte_hex_string(b_shifted), 
                          BigIntUtil.to_byte_hex_string(e));

        Bignat bna = Convenience.bn_from_bi(bignat_size, a);
        Bignat bnb = Convenience.bn_from_bi(bignat_size, b);
        bna.times_minus(bnb, (short)(b_shift), 
                        (DOUBLE_DIGIT_TYPE)(d_bi.longValue()));

        BigInteger r = Convenience.bi_from_bn(bna);

        System.out.format("%s: %s\n",
                          r.compareTo(e) == 0 ? "SUCCESS" : "FAILURE",
                          r);

        return;
    }


    /**
     * 
     * Test {@link Bignat#remainder_divide Bignat.remainder_divide}
     * once. If fixed inputs are available the divident and divisor
     * are taken in this order from the fixed inputs. Otherwise they
     * are generated randomly in such a way that with a probability of
     * 5 percent the divident is bigger than the divisor.
     * 
     * @param rand randomness source
     * @return true if the test succeeded
     */
    public static boolean test_remainder_divide_once(Random rand) {
        BigInteger divident_bi;
        BigInteger divisor_bi;

        if(fix_inputs.has_n_inputs(2, "remainder divide")) {
            divident_bi = fix_inputs.pop();
            divisor_bi = fix_inputs.pop();
        }
        else {
            do {
                divident_bi = new BigInteger(rand.nextInt(max_bits), rand);
                divisor_bi = new BigInteger(rand.nextInt(max_bits), rand);

                if(rand.nextFloat() < 0.05) {
                    if(divident_bi.compareTo(divisor_bi) > 0) {
                        BigInteger x = divident_bi;
                        divident_bi = divisor_bi;
                        divisor_bi = x;
                    }
                }
                else {
                    if(divident_bi.compareTo(divisor_bi) < 0) {
                        BigInteger x = divident_bi;
                        divident_bi = divisor_bi;
                        divisor_bi = x;
                    }
                }
            }
            while(divisor_bi.compareTo(BigInteger.ZERO) == 0);
        }

        BigInteger[] res = divident_bi.divideAndRemainder(divisor_bi);
        BigInteger quotient_bi = res[0];
        BigInteger remainder_bi = res[1];

        int divident_size, divisor_size, quotient_size;

        if(bignat_size_set) {
            divisor_size = bignat_size;
            divident_size = bignat_size;
            quotient_size = bignat_size;
        }
        else {
            divident_size = (divident_bi.bitLength() + 7) / 8;
            if(divident_size == 0)
                divident_size = 1;
            divisor_size = (divisor_bi.bitLength() + 7) / 8;
            quotient_size = divident_size - divisor_size + 1;
            if(quotient_size < 1)
                quotient_size = 1;

            if(rand.nextFloat() < 0.1) {
                int x = rand.nextInt(bignat_size);
                divident_size += x;
                quotient_size += x;
            }
            if(rand.nextFloat() < 0.1)
                divisor_size += rand.nextInt(bignat_size);
            if(rand.nextFloat() < 0.1)
                quotient_size += rand.nextInt(bignat_size);
        }


        if(verbosity.ref > 5) {
            System.out.format("%s / %s = %s rem %s\n",
                              divident_bi, divisor_bi,
                              quotient_bi, remainder_bi);
            System.out.format("sizes: div %d dis %d quo %d\n",
                              divident_size, divisor_size, quotient_size);
            System.out.format("%s / %s = %s rem %s\n",
                              BigIntUtil.to_byte_hex_string(divident_bi), 
                              BigIntUtil.to_byte_hex_string(divisor_bi),
                              BigIntUtil.to_byte_hex_string(quotient_bi),
                              BigIntUtil.to_byte_hex_string(remainder_bi));
        }
        Bignat divident_bn = Convenience.bn_from_bi(divident_size, divident_bi);
        Bignat divisor_bn = Convenience.bn_from_bi(divisor_size, divisor_bi);
        Bignat quotient_bn = new Bignat((short)quotient_size);

        // System.out.format("%s / %s = %s rem %s\n",
        //                to_byte_hex_string(divident_bi),
        //                to_byte_hex_string(divisor_bi),
        //                to_byte_hex_string(quotient_bi),
        //                to_byte_hex_string(remainder_bi));

        divident_bn.remainder_divide(divisor_bn, quotient_bn);

        BigInteger quotient_bnbi = Convenience.bi_from_bn(quotient_bn);
        BigInteger remainder_bnbi = Convenience.bi_from_bn(divident_bn);

        boolean success =
            quotient_bi.compareTo(quotient_bnbi) == 0 &&
            remainder_bi.compareTo(remainder_bnbi) == 0;
        if(! success || verbosity.ref > 5) {
            if(success)
                System.out.println("success");
            else
                System.out.format("%s: %s / %s != %s rem %s\n",
                                  "FAILURE remainder_divide",
                                  divident_bi, divisor_bi,
                                  quotient_bnbi, remainder_bnbi);
        }
        return success;
    }


    /**
     * 
     * Test {@link Bignat#remainder_divide Bignat.remainder_divide}
     * {@link #rounds} times. Exists if a test fails. If fixed inputs
     * are available, they will be used.
     * 
     * @param rand randomness source
     */
    public static void test_remainder_divide(Random rand) {
        for(int i = 0; i < rounds.ref; i++) {
            if(!test_remainder_divide_once(rand)) {
                System.out.println(
                    "\nERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ");
                System.exit(1);
            }
        }
        System.out.println(
            "\nSUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS");
        return;
    }


    /**
     * 
     * Test {@link Bignat#mult_mod Bignat.mult_mod} once. If fixed
     * inputs are available the factors and the modulus are taken from
     * the fixed inputs in this order. Otherwise they are generated
     * randomly with the side condition that the modulus is non-zero.
     * 
     * @param rand randomness source
     * @return true if the test succeeded
     * @todo fix random generation, why mont_bignat_size?
     */
    public static boolean test_mult_mod_once(Random rand) {
        BigInteger x_bi, y_bi, mod_bi;

        if(bignat_size < 3) {
            System.err.println("size must be greater than 2 for multmod");
            System.exit(1);
        }

        if(fix_inputs.has_n_inputs(3, "slow modulo multiplication")) {
            x_bi = fix_inputs.pop();
            y_bi = fix_inputs.pop();
            mod_bi = fix_inputs.pop();
        }
        else {
            x_bi = 
                new BigInteger(rand.nextInt(max_bits_from_size(
                                                       mont_bignat_size)),
                               rand);
            y_bi = new BigInteger(rand.nextInt(max_bits), rand);
            do {
                mod_bi =
                    new BigInteger(
                              rand.nextInt(max_bits_from_size(
                                                      mont_bignat_size)),
                              rand);
            }
            while(mod_bi.compareTo(BigInteger.ZERO) == 0);
        }

        BigInteger prod_bi = x_bi.multiply(y_bi).mod(mod_bi);

        Bignat x_bn = Convenience.bn_from_bi(bignat_size, x_bi);
        Bignat y_bn = Convenience.bn_from_bi(bignat_size, y_bi);
        Bignat mod_bn = Convenience.bn_from_bi(bignat_size, mod_bi);
        Bignat prod_bn = new Bignat(bignat_size);

        if(verbosity.ref >= 5) {
            System.out.format("%s * %s mod %s = %s\n",
                              x_bi, y_bi, mod_bi, prod_bi);
            System.out.format("%s * %s mod %s = %s\n", 
                              BigIntUtil.to_byte_hex_string(x_bi), 
                              BigIntUtil.to_byte_hex_string(y_bi), 
                              BigIntUtil.to_byte_hex_string(mod_bi),
                              BigIntUtil.to_byte_hex_string(prod_bi));
        }
        // System.out.format("%s / %s = %s rem %s\n",
        //                to_byte_hex_string(x_bi),
        //                to_byte_hex_string(y_bi),
        //                to_byte_hex_string(quotient_bi),
        //                to_byte_hex_string(remainder_bi));

        prod_bn.mult_mod(x_bn, y_bn, mod_bn);

        BigInteger prod_bnbi = Convenience.bi_from_bn(prod_bn);

        boolean success = prod_bi.compareTo(prod_bnbi) == 0;
        if(! success || verbosity.ref > 5) {
            if(success)
                System.out.println("success");
            else
                System.out.format("%s: %s * %s mod %s != %s\n",
                                  "FAILURE mult_mod",
                                  x_bi, y_bi, mod_bi,
                                  prod_bnbi);
        }
        return success;
    }


    /**
     * 
     * Test {@link Bignat#mult_mod Bignat.mult_mod} {@link #rounds}
     * times. Exists on the first error. If fixed inputs are
     * available, they will be used.
     * 
     * @param rand randomness source
     */
    public static void test_mult_mod(Random rand) {
        for(int i = 0; i < rounds.ref; i++) {
            if(!test_mult_mod_once(rand)) {
                System.out.println(
                    "\nERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ");
                System.exit(1);
            }
        }
        System.out.println(
            "\nSUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS");
        return;
    }


    /**
     * 
     * Test {@link Bignat#mult Bignat.mult} once. If fixed inputs are
     * available the factors are taken from there. Otherwise they are
     * generated randomly.
     * 
     * @param rand randomness source
     * @return true if the test succeeds
     * @todo fix random generation of x_bi
     */
    public static boolean test_double_mult_once(Random rand) {
        BigInteger x_bi, y_bi;

        if(fix_inputs.has_n_inputs(2, "slow modulo multiplication")) {
            x_bi = fix_inputs.pop();
            y_bi = fix_inputs.pop();
        }
        else {
            x_bi = 
                new BigInteger(rand.nextInt(max_bits_from_size(
                                                       mont_bignat_size)),
                               rand);
            y_bi = new BigInteger(rand.nextInt(max_bits), rand);
        }

        BigInteger prod_bi = x_bi.multiply(y_bi);

        Bignat x_bn = Convenience.bn_from_bi(bignat_size, x_bi);
        Bignat y_bn = Convenience.bn_from_bi(bignat_size, y_bi);
        Bignat prod_bn = new Bignat((short)(2 * bignat_size));

        if(verbosity.ref >= 5) {
            System.out.format("%s * %s = %s\n",
                              x_bi, y_bi, prod_bi);
            System.out.format("%s * %s = %s\n", 
                              BigIntUtil.to_byte_hex_string(x_bi), 
                              BigIntUtil.to_byte_hex_string(y_bi), 
                              BigIntUtil.to_byte_hex_string(prod_bi));
        }

        prod_bn.mult(x_bn, y_bn);

        BigInteger prod_bnbi = Convenience.bi_from_bn(prod_bn);

        boolean success = prod_bi.compareTo(prod_bnbi) == 0;
        if(! success || verbosity.ref > 5) {
            if(success)
                System.out.println("success");
            else
                System.out.format("%s: %s * %s != %s\n",
                                  "FAILURE mult_mod",
                                  x_bi, y_bi, 
                                  prod_bnbi);
        }
        return success;
    }


    /**
     * 
     * Test {@link Bignat#mult Bignat.mult} {@link #rounds} times.
     * Exists on the first error. If fixed inputs are available they
     * will be used.
     * 
     * @param  rand randomness source
     */
    public static void test_double_mult(Random rand) {
        for(int i = 0; i < rounds.ref; i++) {
            if(!test_double_mult_once(rand)) {
                System.out.println(
                   "\nERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ");
                System.exit(1);
            }
        }
        System.out.println(
            "\nSUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS");
        return;
    }


    /**
     * 
     * Test {@link Bignat#montgomery_mult Bignat.montgomery_mult}
     * once. If fixed inputs are available the factors and the modulus
     * are taken in this order from there. Otherwise they are
     * generated randomly such that the modulus is odd.
     * 
     * @param rand randomness source
     * @return true if the test succeeds
     */
    public static boolean test_montgomery_mult_once(Random rand) {
        BigInteger x_bi, y_bi, mod_bi;

        if(bignat_size < 3) {
            System.err.println("size must be greater than 2 " +
                               "for montgomery multiplication");
            System.exit(1);
        }

        if(fix_inputs.has_n_inputs(3, "montgomery mult")) {
            x_bi = fix_inputs.pop();
            y_bi = fix_inputs.pop();
            mod_bi = fix_inputs.pop();
        }
        else {
            int bit_size = max_bits_from_size(mont_bignat_size);
            x_bi = new BigInteger(rand.nextInt(bit_size), rand);
            y_bi = new BigInteger(rand.nextInt(bit_size), rand);
            mod_bi = new BigInteger(rand.nextInt(bit_size), rand);
            // make mod_bi odd
            mod_bi = mod_bi.setBit(0);
        }

        BigInteger prod_bi = x_bi.multiply(y_bi).mod(mod_bi);

        if(verbosity.ref >= 5) {
            System.out.format("## %s * %s mod %s = %s\n",
                              x_bi, y_bi, mod_bi, prod_bi);
            System.out.format("## x = %s\n" +
                              "## y = %s\n" +
                              "## m = %s\n",
                              BigIntUtil.to_byte_hex_string(x_bi),
                              BigIntUtil.to_byte_hex_string(y_bi),
                              BigIntUtil.to_byte_hex_string(mod_bi));
        }

        Bignat x_bn = Convenience.bn_from_bi(bignat_size, x_bi);
        Bignat y_bn = Convenience.bn_from_bi(bignat_size, y_bi);
        Modulus mod_bn = new Modulus(bignat_size, false);
        Host_modulus hmod = new Host_modulus(bignat_size, mod_bi);
        Convert_serializable.to(hmod, mod_bn);

        // XXXXX delete next line
        Bignat temp = new Bignat(bignat_size);
        Bignat montgomery_factor = 
            Convenience.bn_from_bi(bignat_size, 
                                   hmod.mont_fac.pow(2).mod(mod_bi));

        if(verbosity.ref > 4) {
            System.out.format("## mont fac = %s\n" +
                              "##          = %s\n",
                              hmod.mont_fac,
                              montgomery_factor.to_hex_string());
            System.out.format("## lbi = %03d = %02X\n",
                              hmod.last_digit_inverse, 
                              hmod.last_digit_inverse);
        }

        Bignat x_mont_bn = new Bignat(bignat_size);
        x_mont_bn.montgomery_mult(x_bn, montgomery_factor, mod_bn);
        if(verbosity.ref > 4) {
            System.out.format("x: %s * %s = %s\n",
                              x_bn.to_hex_string(),
                              montgomery_factor.to_hex_string(),
                              x_mont_bn.to_hex_string());
        }

        Bignat y_mont_bn = new Bignat(bignat_size);
        y_mont_bn.montgomery_mult(y_bn, montgomery_factor, mod_bn);
        if(verbosity.ref > 4) {
            System.out.format("y: %s * %s = %s\n",
                              y_bn.to_hex_string(),
                              montgomery_factor.to_hex_string(),
                              y_mont_bn.to_hex_string());
        }


        Bignat prod_mont_bn = new Bignat(bignat_size);
        prod_mont_bn.montgomery_mult(x_mont_bn, y_mont_bn, mod_bn);
        if(verbosity.ref > 4) {
            System.out.format("prod: %s * %s = %s\n",
                              x_bn.to_hex_string(),
                              y_bn.to_hex_string(),
                              prod_mont_bn.to_hex_string());
        }

        Bignat one_bn = Convenience.bn_from_bi(bignat_size, BigInteger.ONE);

        Bignat prod_bn = new Bignat(bignat_size);
        prod_bn.montgomery_mult(prod_mont_bn, one_bn, mod_bn);

        BigInteger prod_bnbi = Convenience.bi_from_bn(prod_bn);

        boolean success = prod_bi.compareTo(prod_bnbi) == 0;
        if(! success || verbosity.ref > 5) {
            if(success)
                System.out.println("success");
            else
                System.out.format("%s: %s * %s mod %s != %s\n",
                                  "FAILURE mult_mod",
                                  x_bi, y_bi, mod_bi,
                                  prod_bnbi);
        }
        return success;
    }


    /**
     * 
     * Test {@link Bignat#montgomery_mult Bignat.montgomery_mult}
     * {@link #rounds} times. Exists on the first error. If fixed
     * inputs are available, they will be used.
     * 
     * @param rand randomness source
     */
    public static void test_montgomery_mult(Random rand) {
        for(int i = 0; i < rounds.ref; i++) {
            if(!test_montgomery_mult_once(rand)) {
                System.out.println(
                    "\nERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR ");
                System.exit(1);
            }
        }
        System.out.println(
            "\nSUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS");
        return;
    }


    /**
     * 
     * Test {@link Bignat#exponent_mod Bignat.exponent_mod} once.
     * Honours the {@code -read} and {@code -write} options. If 3
     * fixed inputs are available the base, the exponent and the
     * modulus are taken from there. Otherwise, if {@code -read} was
     * given, the data is taken from the next 3 lines of the input
     * file. Otherwise they are generated randomly such that the base
     * and the modulus have {@link #mont_bignat_size} significant
     * bytes, and the exponent has 10 percent of this size. If {@code
     * -write} was given the data is written to the output file.
     * <P>
     *
     * The time needed to compute the exponent with both the Bignat
     * library and with {@link BigInteger} is measured and returned.
     * For the Bignat library only the time for {@link
     * Bignat#exponent_mod Bignat.exponent_mod} is measured without
     * the necessary preparations. <P>
     *
     * Exists if the test fails.
     * 
     * 
     * @param rand randomness source
     * @param data_in input data file or null for no input
     * @param data_out output data file or null for no output
     * throws IOException if reading from {@code data_in} fails
     */
    public static long[] test_exponent_once(Random rand,
                                            BufferedReader data_in,
                                            PrintWriter data_out) 
        throws IOException
    {
        if(bignat_size < 3) {
            System.err.println("size must be greater than 2 " +
                               "for exponent");
            System.exit(1);
        }

        if(verbosity.ref > 4)
            System.out.println("#### Exponent test");

        // Initialize base, pow and the modulus.
        BigInteger base = null;
        BigInteger exponent = null;
        BigInteger mod = null;

        int base_bit_size = max_bits_from_size(mont_bignat_size);
        int exp_bit_size = 8 * mont_bignat_size / 10 + 1;

        short exp_byte_size = (short)((exp_bit_size + 7) / 8);

        if(exp_byte_size % Bignat.size_multiplier != 0)
            exp_byte_size += 
                Bignat.size_multiplier - exp_byte_size % Bignat.size_multiplier;

        if(verbosity.ref > 4)
            System.out.format("#### base %d bits (%d bytes) " +
                              "exponent %d bits (%d bytes)\n",
                              base_bit_size, bignat_size,
                              exp_bit_size, exp_byte_size);

        // Initialize from command line arguments?
        if(fix_inputs.has_n_inputs(3, "exponent")) {
            base = fix_inputs.pop();
            exponent = fix_inputs.pop();
            mod = fix_inputs.pop();
        }
        // or from file?
        else if(data_in != null) {
            base = new BigInteger(data_in.readLine());
            exponent = new BigInteger(data_in.readLine());
            mod = new BigInteger(data_in.readLine());
        }
        // Or initialize randomly?
        else {
            //base = new BigInteger(rand.nextInt(bit_size), rand);
            base = new BigInteger(base_bit_size -1, rand);
            //exponent = new BigInteger(rand.nextInt(bit_size), rand);
            exponent = new BigInteger(exp_bit_size -1, rand);
            //mod = new BigInteger(rand.nextInt(bit_size), rand);
            mod = new BigInteger(base_bit_size -1, rand);
            // make mod odd
            mod = mod.setBit(0);
        }

        // need to write out test data?
        if(data_out != null) {
            data_out.println(base.toString());
            data_out.println(exponent.toString());
            data_out.println(mod.toString());
        }

        if(verbosity.ref >= 5) {
            System.out.format("## base = %s\n" +
                              "##      = %s\n" +
                              "## expo = %s\n" +
                              "##      = %s\n" +
                              "## mod  = %s\n" +
                              "##      = %s\n", 
                              base,
                              BigIntUtil.to_byte_hex_string(base),
                              exponent,
                              BigIntUtil.to_byte_hex_string(exponent),
                              mod,
                              BigIntUtil.to_byte_hex_string(mod));
        }

        // Compute the expected (right) result.
        long start = System.nanoTime();
        BigInteger result = base.modPow(exponent, mod);
        long bi_duration = System.nanoTime() - start;

        if(verbosity.ref >= 5) {
            System.out.format("## result = %s\n", result);
            System.out.format("##        = %s\n", 
                              BigIntUtil.to_byte_hex_string(result));
        }

        // Prepare modulus, this will compute all necessary incredients 
        // for montgomery multiplication internally and store them in hmod.
        Host_modulus hmod = new Host_modulus(bignat_size, mod);
        Modulus bn_mod = new Modulus(bignat_size, false);
        Convert_serializable.to(hmod, bn_mod);
        Bignat bn_one = Convenience.bn_from_bi(bignat_size, hmod.mont_fac);

        if(verbosity.ref > 4) {
            System.out.format("## mont fac = %s\n" +
                              "##          = %s\n",
                              hmod.mont_fac,
                              BigIntUtil.to_byte_hex_string(hmod.mont_fac));
            System.out.format("## lbi = %03d = %02X\n",
                              hmod.last_digit_inverse, 
                              hmod.last_digit_inverse);
            System.out.format("## mont inv = %s\n" +
                              "##          = %s\n",
                              hmod.demont_fac,
                              BigIntUtil.to_byte_hex_string(hmod.demont_fac));
        }

        // Montgomerize the base.
        BigInteger base_mont = base.multiply(hmod.mont_fac).mod(mod);
        if(verbosity.ref > 9)
            System.out.format("## mont base = %s\n" +
                              "##           = %s\n",
                              base_mont,
                              BigIntUtil.to_byte_hex_string(base_mont));

        // Prepare Bignat (Java Card) versions of base and exponent.
        Bignat bn_base_mont = Convenience.bn_from_bi(bignat_size, base_mont);
        Bignat bn_exponent = Convenience.bn_from_bi(exp_byte_size, exponent);

        // Allocate result and a temporary.
        Bignat bn_result = new Bignat(bignat_size);
        Bignat temp = new Bignat(bignat_size);

        // Compute the exponent, result will be montgomerized, 
        // because the base is.
        start = System.nanoTime();
        bn_result.exponent_mod(bn_base_mont, bn_exponent, bn_mod, 
                               bn_one, temp);
        long bn_duration = System.nanoTime() - start;

        BigInteger bi_result_mont = Convenience.bi_from_bn(bn_result);

        if(verbosity.ref > 14)
            System.out.format("## mont result %s\n", bi_result_mont);

        // Get real result.
        BigInteger bi_result = 
            bi_result_mont.multiply(hmod.demont_fac).mod(mod);

        if(bi_result.compareTo(result) == 0 && verbosity.ref >= 5)
            System.out.println("## SUCCESS");

        if(bi_result.compareTo(result) != 0) {
            System.out.println("## FAILURE");
            System.out.format("## got %s = %s\n", 
                              bi_result, 
                              BigIntUtil.to_byte_hex_string(bi_result));
            if(verbosity.ref < 5) {
                System.out.format("## base = %s\n" +
                                  "##      = %s\n" +
                                  "## expo = %s\n" +
                                  "##      = %s\n" +
                                  "## mod  = %s\n" +
                                  "##      = %s\n", 
                                  base,
                                  BigIntUtil.to_byte_hex_string(base),
                                  exponent,
                                  BigIntUtil.to_byte_hex_string(exponent),
                                  mod,
                                  BigIntUtil.to_byte_hex_string(mod));
            }
            System.exit(1);
        }

        return new long[]{bn_duration, bi_duration};
    }


    /**
     * 
     * Test {@link Bignat#exponent_mod Bignat.exponent_mod} twice
     * {@link #rounds} times. The first time for warming the cache and
     * JIT compilation, the second time for real measurements. Exists
     * immediately in case of an error. 
     * <P>
     *
     * If fixed inputs are available, they will be used.
     * Otherwise, if {@code -read} was given, the test data is taken
     * from that file. After the first {@link #rounds} test the file
     * is reopened to do the real measurements with exactly the same
     * data. <P>
     *
     * Without {@code -read} the test data is generated randomly. If
     * {@code -write} was given the data is written to that file
     * during the first {@link #rounds} tests and read from there
     * afterwards. 
     * <P>
     *
     * Exists on the first error. 
     * 
     * @param rand randomness source
     * @param data_in input data file or null for no input file
     * @param data_out output data file or null for no output
     * @throws FileNotFoundException  if reopening {@link
     * #read_from_file} fails
     * throws IOException if reading from {@code data_in} fails XXXX
     */
    public static void test_exponent(Random rand, 
                                     BufferedReader data_in,
                                     PrintWriter data_out) 
        throws FileNotFoundException, IOException
    {
        System.out.println("###### Exponent");
        System.out.format("#### size %d\n", bignat_size);

        long bn_duration = 0;
        long bi_duration = 0;
        for(int i = 0; i < rounds.ref; i++) {
            long[] d = test_exponent_once(rand, data_in, data_out);
            bn_duration += d[0];
            bi_duration += d[1];
        }

        System.out.format("### 1. bignat time %.5f sec "+
                          "(%.3f sec for %d rounds)\n",
                          bn_duration / 1E9 / rounds.ref,
                          bn_duration / 1E9,
                          rounds.ref);
        System.out.format("### 1. BigInt time %.5f sec "+
                          "(%.3f sec for %d rounds)\n",
                          bi_duration / 1E9 / rounds.ref,
                          bi_duration / 1E9,
                          rounds.ref);
        System.out.format("### 1. bignat/BigInt ratio %.3f\n",
                          ((float)bn_duration) / bi_duration);

        if(data_in != null) {
            data_in.close();
            data_in = new BufferedReader(new FileReader(read_from_file.ref));
        }
        
        if(data_out != null) {
            data_out.close();
            data_out = null;

            if(data_in == null) {
                data_in = new BufferedReader(new FileReader(write_to_file.ref));
                System.out.format("Use same data for second round from %s\n",
                                  write_to_file.ref);
            }
        }

        bn_duration = 0;
        bi_duration = 0;
        for(int i = 0; i < rounds.ref; i++) {
            long[] d = test_exponent_once(rand, data_in, data_out);
            bn_duration += d[0];
            bi_duration += d[1];
        }

        System.out.format("### 2. bignat time %.5f sec "+
                          "(%.3f sec for %d rounds)\n",
                          bn_duration / 1E9 / rounds.ref,
                          bn_duration / 1E9,
                          rounds.ref);
        System.out.format("### 2. BigInt time %.5f sec "+
                          "(%.3f sec for %d rounds)\n",
                          bi_duration / 1E9 / rounds.ref,
                          bi_duration / 1E9,
                          rounds.ref);
        System.out.format("### 2. bignat/BigInt ratio %.3f\n",
                          ((float)bn_duration) / bi_duration);
    }



    /**
     * 
     * Tests {@link Vector#exponent_mod Vector.exponent_mod} once.
     * Honours the {@code -read} and {@code -write} options. If enough
     * fixed inputs are present {@link #vector_exponent_length} bases,
     * the same number of exponents and the modulus are taken from
     * there in this order. Otherwise, if {@code -read} was given, the
     * data is taken from the next {@code 2 *}{@link
     * #vector_exponent_length}{@code + 1} lines of the input file.
     * Otherwise the data is generated randomly such that bases and
     * modulus have {@link #mont_bignat_size} significant bytes and
     * the exponents are 10 percent of this size. If {@code -write} was
     * given the data is written to the output file.
     * <P>
     *
     * The time needed to compute the multi-exponent with both the
     * Bignat library and with {@link BigInteger} is measured and
     * returned. For the Bignat library only the time for {@link
     * Vector#exponent_mod Vector.exponent_mod} is measured without
     * the necessary preparations.
     * <P>
     *
     * Exits if an error is detected. 
     * 
     * 
     * @param rand randomness source
     * @param data_in input data file or null for no input
     * @param data_out output data file or null for no output
     * @return a pair {bignat_time, biginteger_time} of the measured
     * computation time in nanoseconds.
     * throws IOException if reading from {@code data_in} fails
     */
    public static long[] test_vector_exponent_once(Random rand,
                                                   BufferedReader data_in,
                                                   PrintWriter data_out) 
        throws IOException
    {
        if(bignat_size < 3) {
            System.err.println("size must be greater than 2 " +
                               "for vector exponent");
            System.exit(1);
        }

        if(verbosity.ref > 4)
            System.out.println("#### Vector exponent test");

        // Initialize base, pow and the modulus.
        BigInteger base[] = new BigInteger[vector_exponent_length.ref];
        BigInteger pow[] = new BigInteger[vector_exponent_length.ref];
        BigInteger mod = null;

        int base_bit_size = max_bits_from_size(mont_bignat_size);
        int exp_bit_size = 8 * mont_bignat_size / 10 + 1;
        // exp_bit_size = 8 * mont_bignat_size / 5 + 1;
        exp_bit_size = 160;

        short exp_byte_size = (short)((exp_bit_size + 7) / 8);
        // exp_byte_size = bignat_size;

        if(exp_byte_size % Bignat.size_multiplier != 0)
            exp_byte_size += Bignat.size_multiplier - 
                exp_byte_size % Bignat.size_multiplier;

        if(verbosity.ref > 4)
            System.out.format("#### base %d bits (%d bytes) " +
                              "exponent %d bits (%d bytes)\n",
                              base_bit_size, bignat_size,
                              exp_bit_size, exp_byte_size);

        // Initialize from command line arguments?
        if(fix_inputs.has_n_inputs(vector_exponent_length.ref * 2 + 1, 
                                   "vector exponent")) 
            {
                for(int i = 0; i < vector_exponent_length.ref; i++)
                    base[i] = fix_inputs.pop();
                for(int i = 0; i < vector_exponent_length.ref; i++)
                    pow[i] = fix_inputs.pop();
                mod = fix_inputs.pop();
            }
        // or from file?
        else if(data_in != null) {
            for(int i = 0; i < vector_exponent_length.ref; i++)
                base[i] = new BigInteger(data_in.readLine());
            for(int i = 0; i < vector_exponent_length.ref; i++)
                pow[i] = new BigInteger(data_in.readLine());
            mod = new BigInteger(data_in.readLine());
        }
        // Or initialize randomly?
        else {
            for(int i = 0; i < vector_exponent_length.ref; i++)
                //base[i] = new BigInteger(rand.nextInt(bit_size), rand);
                base[i] = new BigInteger(base_bit_size -1, rand);
            for(int i = 0; i < vector_exponent_length.ref; i++)
                //pow[i] = new BigInteger(rand.nextInt(bit_size), rand);
                pow[i] = new BigInteger(exp_bit_size -1, rand);
            //mod = new BigInteger(rand.nextInt(bit_size), rand);
            mod = new BigInteger(base_bit_size -1, rand);
            // make mod odd
            mod = mod.setBit(0);
        }

        // need to write out test data?
        if(data_out != null) {
            for(int i = 0; i < vector_exponent_length.ref; i++)
                data_out.println(base[i].toString());
            for(int i = 0; i < vector_exponent_length.ref; i++)
                data_out.println(pow[i].toString());
            data_out.println(mod.toString());
        }

        if(verbosity.ref >= 5) {
            print_bi_array(String.format("## %%d bases %d bits\n", 
                                         base_bit_size),
                           "## base", base);
            print_bi_array(String.format("## %%d exponents %d bits\n",
                                         exp_bit_size),
                           "## expo", pow);
            System.out.format("## mod = %s\n", mod);
            System.out.format("## mod = %s\n", 
                              BigIntUtil.to_byte_hex_string(mod));
        }

        // Compute the expected (right) result.
        long start = System.nanoTime();
        BigInteger result = BigInteger.ONE;
        for(int i = 0; i < vector_exponent_length.ref; i++) {
            result = result.multiply(base[i].modPow(pow[i], mod));
            result = result.mod(mod);
        }
        long bi_duration = System.nanoTime() - start;

        if(verbosity.ref >= 5) {
            System.out.format("## result = %s\n", result);
            System.out.format("## result = %s\n", 
                              BigIntUtil.to_byte_hex_string(result));
        }

        // Prepare modulus, this will compute all necessary incredients 
        // for montgomery multiplication internally and store them in hmod.
        Host_modulus hmod = new Host_modulus(bignat_size, mod);
        Modulus bn_mod = new Modulus(bignat_size, false);
        Convert_serializable.to(hmod, bn_mod);
        Bignat bn_one = Convenience.bn_from_bi(bignat_size, hmod.mont_fac);

        if(verbosity.ref > 4) {
            System.out.format("## mont fac = %s\n" +
                              "##          = %s\n",
                              hmod.mont_fac,
                              BigIntUtil.to_byte_hex_string(hmod.mont_fac));
            System.out.format("## lbi = %03d = %02X\n",
                              hmod.last_digit_inverse, 
                              hmod.last_digit_inverse);
            System.out.format("## mont inv = %s\n" +
                              "##          = %s\n",
                              hmod.demont_fac,
                              BigIntUtil.to_byte_hex_string(hmod.demont_fac));
        }

        // Montgomerize the base and fill it into a Host_vector.
        Host_vector hbase_mont = 
            Host_vector.make_montgomerized_vector(bignat_size, base, hmod);
        if(verbosity.ref > 9)
            print_bi_array("## montgomerized base\n", "## mbase", hbase_mont.a);

        // Decide how many factors of the base will be precomputed and 
        // set them up in a factors Host_vector.
        int pre_computed = vector_exponent_variable_bases.ref;
        if(pre_computed < 0)
            pre_computed = rand.nextInt(vector_exponent_length.ref + 1);
        BigInteger short_base[] = new BigInteger[pre_computed];
        System.arraycopy(base, 0, short_base, 0, pre_computed);
        Host_vector factors = 
            Host_vector.make_montgomerized_factors(bignat_size, 
                                                   short_base, hmod);

        if(verbosity.ref >= 5)
            System.out.format("## %d precomputed (montgomerized) factors\n", 
                              pre_computed);
        if(verbosity.ref >= 10)
            print_bi_array("", "## fac", factors.a);
            
        // Prepare Bignat (Java Card) versions of base, pow and factors.
        Vector bn_base = new Vector(bignat_size, 
                                    vector_exponent_length.ref.shortValue(),
                                    true, false);
        Vector bn_pow = new Vector(exp_byte_size, 
                                   vector_exponent_length.ref.shortValue(),
                                   false, false);
        Vector bn_factors = null;
        if(pre_computed > 0)
            bn_factors = new Vector(bignat_size, factors.get_length(), 
                                    false, false);

        // Allocate result and a temporary.
        Bignat bn_result = new Bignat(bignat_size);
        Bignat temp = new Bignat(bignat_size);

        // Convert BigInteger arguments into Bignat arguments.
        Convert_serializable.to(hbase_mont, bn_base);
        Convert_serializable.to(new Host_vector(exp_byte_size, pow), bn_pow);
        if(pre_computed > 0)
            Convert_serializable.to(factors, bn_factors);

        // Compute the exponent, result will be montgomerized, since the 
        // bn_base and the bn_factors are.
        start = System.nanoTime();
        bn_base.exponent_mod(bn_pow, bn_mod, (short)pre_computed, 
                             bn_factors, bn_one, bn_result, temp);
        long bn_duration = System.nanoTime() - start;

        BigInteger bi_result_mont = Convenience.bi_from_bn(bn_result);

        if(verbosity.ref > 14)
            System.out.format("## mont result %s\n", bi_result_mont);

        // Get real result.
        bn_result.demontgomerize(bn_mod);
        BigInteger bi_result = Convenience.bi_from_bn(bn_result);
        // BigInteger bi_result = result;

        if(bi_result.compareTo(result) == 0 && verbosity.ref >= 5)
            System.out.println("## SUCCESS");

        if(bi_result.compareTo(result) != 0) {
            System.out.println("## FAILURE");
            System.out.format("## got %s = %s\n", 
                              bi_result, 
                              BigIntUtil.to_byte_hex_string(bi_result));
            if(verbosity.ref < 5) {
                print_bi_array("## %d bases\n", "## base", base);
                print_bi_array("## %d exponents\n", "## expo", pow);
                System.out.format("## mod = %s\n", mod);
                System.out.format("## %d precomputed factors\n", pre_computed);
            }
            System.exit(1);
        }

        return new long[]{bn_duration, bi_duration};
    }


    /**
     * 
     * Tests {@link Vector#exponent_mod Vector.exponent_mod} twice
     * {@link #rounds} times. The first time for warming the cache and
     * JIT compilation, the second time for real measurements. Exists
     * immediately in case of an error. 
     * <P>
     *
     * If sufficient fixed inputs are available, they will be used.
     * Every round discards the first {@code 2 *}{@link
     * #vector_exponent_length}{@code + 1} fixed inputs. Otherwise, if
     * {@code -read} was given, the test data is taken from that file.
     * After the first {@link #rounds} test the file is reopened to do
     * the real measurements with exactly the same data. <P>
     *
     * Without {@code -read} the test data is generated randomly. If
     * {@code -write} was given the data is written to that file
     * during the first {@link #rounds} tests and read from there
     * afterwards. 
     * <P>
     *
     * Exists on the first error. 
     * 
     * @param rand randomness source
     * @param data_in input data file or null for no input file
     * @param data_out output data file or null for no output
     * @throws FileNotFoundException if reopening {@link
     * #read_from_file} fails
     * @throws IOException if reading from {@code data_in} fails or
     * some other file error occurs
     */
    public static void test_vector_exponent(Random rand, 
                                            BufferedReader data_in,
                                            PrintWriter data_out) 
        throws FileNotFoundException, IOException
    {
        System.out.println("###### Vector exponent");
        System.out.format("#### size %d\n", bignat_size);

        long bn_duration = 0;
        long bi_duration = 0;
        for(int i = 0; i < rounds.ref; i++) {
            long[] d = test_vector_exponent_once(rand, data_in, data_out);
            bn_duration += d[0];
            bi_duration += d[1];
        }

        System.out.format("### 1. bignat time %.5f sec "+
                          "(%.3f sec for %d rounds)\n",
                          bn_duration / 1E9 / rounds.ref,
                          bn_duration / 1E9,
                          rounds.ref);
        System.out.format("### 1. BigInt time %.5f sec "+
                          "(%.3f sec for %d rounds)\n",
                          bi_duration / 1E9 / rounds.ref,
                          bi_duration / 1E9,
                          rounds.ref);
        System.out.format("### 1. bignat/BigInt ratio %.3f\n",
                          ((float)bn_duration) / bi_duration);

        if(data_in != null) {
            data_in.close();
            data_in = new BufferedReader(new FileReader(read_from_file.ref));
        }
        
        if(data_out != null) {
            data_out.close();
            data_out = null;

            if(data_in == null) {
                data_in = new BufferedReader(new FileReader(write_to_file.ref));
                System.out.format("Use same data for second round from %s\n",
                                  write_to_file.ref);
            }
        }

        bn_duration = 0;
        bi_duration = 0;
        for(int i = 0; i < rounds.ref; i++) {
            long[] d = test_vector_exponent_once(rand, data_in, data_out);
            bn_duration += d[0];
            bi_duration += d[1];
        }

        System.out.format("### 2. bignat time %.5f sec "+
                          "(%.3f sec for %d rounds)\n",
                          bn_duration / 1E9 / rounds.ref,
                          bn_duration / 1E9,
                          rounds.ref);
        System.out.format("### 2. BigInt time %.5f sec "+
                          "(%.3f sec for %d rounds)\n",
                          bi_duration / 1E9 / rounds.ref,
                          bi_duration / 1E9,
                          rounds.ref);
        System.out.format("### 2. bignat/BigInt ratio %.3f\n",
                          ((float)bn_duration) / bi_duration);
    }


    /**
     * 
     * Test {@link Convenience#exponent_mod Convenience.exponent_mod}
     * {@link #rounds} times with randomly generated numbers. Exit if
     * a failure is detected.
     * 
     * @param rand randomness source
     */
    public static void test_convenience_exponent(Random rand) {
        System.out.println("###### Convenience.exponent_mod");

        for(int i = 0; i < rounds.ref; i++) {
            BigInteger base = 
                new BigInteger(rand.nextInt(bignat_size * 8), rand);
            BigInteger exponent = 
                new BigInteger(rand.nextInt(bignat_size * 8), rand);
            BigInteger modulus =
                new BigInteger(rand.nextInt(bignat_size * 8), rand);
            modulus = modulus.setBit(0);

            BigInteger r = base.modPow(exponent, modulus);
            Timed_result tr = 
                Convenience.exponent_mod(base, exponent, modulus);

            if(r.compareTo(tr.result) == 0) {
                if(verbosity.ref >= 5)
                    System.out.format("## Success (%.3f seconds)\n",
                                      tr.duration / 1E9);
            }
            else {
                System.out.format("FAILURE\n" +
                                  "## base = %s\n" +
                                  "## exp = %s\n" +
                                  "## mod = %s\n" +
                                  "## res = %s\n" +
                                  "## got = %s\n",
                                  base, exponent, 
                                  modulus, r, tr.result);
                System.exit(1);
            }
        }
    }


    /**
     * 
     * Test {@link Convenience#vector_exponent_mod
     * Convenience.vector_exponent_mod} {@link #rounds} times with
     * randomly generated numbers. Exit if a failure is detected.
     * 
     * @param rand randomness source
     */
    public static void test_convenience_vector_exponent(Random rand) {
        System.out.println("###### Convenience.vector_exponent_mod");

        for(int i = 0; i < rounds.ref; i++) {
            BigInteger[] bases = new BigInteger[vector_exponent_length.ref];
            BigInteger[] exponents = new BigInteger[vector_exponent_length.ref];
            for(int j = 0; j < vector_exponent_length.ref; j++) {
                bases[j] = new BigInteger(rand.nextInt(bignat_size * 8), rand);
                exponents[j] = 
                    new BigInteger(rand.nextInt(bignat_size * 8), rand);
            }
            BigInteger modulus =
                new BigInteger(rand.nextInt(bignat_size * 8), rand);
            modulus = modulus.setBit(0);

            BigInteger r = BigInteger.ONE;
            for(int j = 0; j < vector_exponent_length.ref; j++) {
                r = r.multiply(bases[j].modPow(exponents[j], modulus))
                    .mod(modulus);
            }
            Timed_result tr = 
                Convenience.vector_exponent_mod(bases, exponents, modulus);

            if(r.compareTo(tr.result) == 0) {
                if(verbosity.ref >= 5)
                    System.out.format("## Success (%d bases %.3f seconds)\n",
                                      vector_exponent_length.ref,
                                      tr.duration / 1E9);
            }
            else {
                System.out.println("FAILURE");
                print_bi_array("## %d bases", "## base", bases);
                print_bi_array("## %d exponents", "## exp", exponents);
                System.out.format("## mod = %s\n" +
                                  "## res = %s\n" +
                                  "## got = %s\n",
                                  modulus, r, tr.result);
                System.exit(1);
            }
        }
    }


    /**
     * 
     * Test {@link Bignat#squared_rsa_mult_2 Bignat.squared_rsa_mult_2}
     * {@link #rounds} times. Exists on the first error. If fixed
     * inputs are available, they will be used.
     * 
     * @param rand randomness source
     */
    public static void test_square_mult(Random rand) {
        System.out.println("###### Bignat.squared_rsa_mult");
        
        BigInteger x_bi, y_bi, mod_bi;

        for(int i = 0; i < rounds.ref; i++) {
            if(fix_inputs.has_n_inputs(3, "squaring multiplication")) {
                x_bi = fix_inputs.pop();
                y_bi = fix_inputs.pop();
                mod_bi = fix_inputs.pop();
            }
            else {
                int bit_size = max_bits_from_size(bignat_size) -1;
                // Let the first bit empty, as required.
                bit_size--;
                // x_bi = new BigInteger(rand.nextInt(bit_size), rand);
                // y_bi = new BigInteger(rand.nextInt(bit_size), rand);
                // mod_bi = new BigInteger(rand.nextInt(bit_size), rand);
                x_bi = new BigInteger(bit_size, rand);
                y_bi = new BigInteger(bit_size, rand);
                mod_bi = new BigInteger(bit_size, rand);
                // make mod_bi odd
                mod_bi = mod_bi.setBit(0);
            }

            BigInteger prod_bi = x_bi.multiply(y_bi).mod(mod_bi);

            if(verbosity.ref >= 5) {
                System.out.format("## %s * %s mod %s = %s\n",
                                  x_bi, y_bi, mod_bi, prod_bi);
                System.out.format("## x = %s\n" +
                                  "## y = %s\n" +
                                  "## m = %s\n",
                                  BigIntUtil.to_byte_hex_string(x_bi),
                                  BigIntUtil.to_byte_hex_string(y_bi),
                                  BigIntUtil.to_byte_hex_string(mod_bi));
            }

            Bignat x_bn = Convenience.bn_from_bi(bignat_size, x_bi);
            Bignat y_bn = Convenience.bn_from_bi(bignat_size, y_bi);
            Modulus mod_bn = new Modulus(bignat_size, false);
            Host_modulus hmod = new Host_modulus(bignat_size, mod_bi);
            Convert_serializable.to(hmod, mod_bn);

            Bignat prod_bn = new Bignat(bignat_size);
            Bignat temp = new Bignat(bignat_size);

            Fake_rsa_exponent fake_rsa_exp = 
                new Fake_rsa_exponent(bignat_size);
            fake_rsa_exp.set_modulus(mod_bn.m, (short)0);

            Bignat two = new Bignat(bignat_size);
            two.two();
            fake_rsa_exp.set_exponent(two, temp, (short)0);

            prod_bn.squared_rsa_mult_2(x_bn, y_bn, mod_bn, fake_rsa_exp, temp);

            BigInteger prod_bnbi = Convenience.bi_from_bn(prod_bn);

            boolean success = prod_bi.compareTo(prod_bnbi) == 0;
            if(! success || verbosity.ref > 5) {
                if(success)
                    System.out.println("success");
                else {
                    System.out.format("%s: %s * %s mod %s != %s\n",
                                      "FAILURE squared_rsa_mult",
                                      x_bi, y_bi, mod_bi,
                                      prod_bnbi);
                    System.out.println
                        ("\nERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR");
                    System.exit(1);
                }
            }
        }

        System.out.println(
            "\nSUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS");
    }


    /**
     * 
     * Test {@link Bignat#squared_rsa_mult_4 Bignat.squared_rsa_mult_4}
     * {@link #rounds} times. Exists on the first error. If fixed
     * inputs are available, they will be used.
     * 
     * @param rand randomness source
     */
    public static void test_square_mult_4(Random rand) {
        System.out.println("###### Bignat.squared_rsa_mult_4");
        
        BigInteger x_bi, y_bi, mod_bi;

        for(int i = 0; i < rounds.ref; i++) {
            if(fix_inputs.has_n_inputs(3, "squaring multiplication")) {
                x_bi = fix_inputs.pop();
                y_bi = fix_inputs.pop();
                mod_bi = fix_inputs.pop();
            }
            else {
                int bit_size = max_bits_from_size(bignat_size) -1;
                // Let the first two bits empty, as required.
                bit_size -= 2;
                // x_bi = new BigInteger(rand.nextInt(bit_size), rand);
                // y_bi = new BigInteger(rand.nextInt(bit_size), rand);
                // mod_bi = new BigInteger(rand.nextInt(bit_size), rand);
                x_bi = new BigInteger(bit_size, rand);
                y_bi = new BigInteger(bit_size, rand);
                mod_bi = new BigInteger(bit_size, rand);
                // ensure mod_bi = 1 (modulo 4)
                mod_bi = mod_bi.setBit(0);
                mod_bi = mod_bi.clearBit(1);
            }

            BigInteger prod_bi = x_bi.multiply(y_bi).mod(mod_bi);

            if(verbosity.ref >= 5) {
                System.out.format("## %s * %s mod %s = %s\n",
                                  x_bi, y_bi, mod_bi, prod_bi);
                System.out.format("## x = %s\n" +
                                  "## y = %s\n" +
                                  "## m = %s\n",
                                  BigIntUtil.to_byte_hex_string(x_bi),
                                  BigIntUtil.to_byte_hex_string(y_bi),
                                  BigIntUtil.to_byte_hex_string(mod_bi));
            }

            Bignat x_bn = Convenience.bn_from_bi(bignat_size, x_bi);
            Bignat y_bn = Convenience.bn_from_bi(bignat_size, y_bi);
            Modulus mod_bn = new Modulus(bignat_size, false);
            mod_bn.allocate_multiples();
            Host_modulus hmod = new Host_modulus(bignat_size, mod_bi);
            Convert_serializable.to(hmod, mod_bn);

            Bignat prod_bn = new Bignat(bignat_size);
            Bignat temp = new Bignat(bignat_size);

            Fake_rsa_exponent fake_rsa_exp = 
                new Fake_rsa_exponent(bignat_size);
            fake_rsa_exp.set_modulus(mod_bn.m, (short)0);

            Bignat two = new Bignat(bignat_size);
            two.two();
            fake_rsa_exp.set_exponent(two, temp, (short)0);

            prod_bn.squared_rsa_mult_4(x_bn, y_bn, mod_bn, 
                                       fake_rsa_exp, temp);

            BigInteger prod_bnbi = Convenience.bi_from_bn(prod_bn);

            boolean success = prod_bi.compareTo(prod_bnbi) == 0;
            if(! success || verbosity.ref > 5) {
                if(success) {
                    System.out.println("success");
                    if(verbosity.ref > 5) {
                    System.out.format("## res = %s\n" +
                                      "##     = %s\n",
                                      prod_bnbi, 
                                      BigIntUtil.to_byte_hex_string(prod_bnbi));
                    }
                }
                else {
                    System.out.format("%s: %s * %s mod %s != %s\n",
                                      "FAILURE squared_rsa_mult",
                                      x_bi, y_bi, mod_bi,
                                      prod_bnbi);
                    System.out.println
                        ("\nERROR ERROR ERROR ERROR ERROR ERROR ERROR ERROR");
                    System.exit(1);
                }
            }
        }

        System.out.println(
            "\nSUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS SUCCESS");
    }

}
