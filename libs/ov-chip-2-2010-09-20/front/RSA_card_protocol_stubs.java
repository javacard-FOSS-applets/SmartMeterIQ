// DO NOT EDIT! DO NOT EDIT! DO NOT EDIT! DO NOT EDIT! DO NOT EDIT! 
//
// This file has been generated automatically from RSA_card_protocol.id
// by some sort of idl compiler.

package ds.ov2.front;

import java.io.PrintWriter;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import ds.ov2.util.APDU_Serializable;
import ds.ov2.util.Host_protocol;
import java.math.BigInteger;
import ds.ov2.util.APDU_byte;
import ds.ov2.util.APDU_short;
import ds.ov2.util.APDU_boolean;
import ds.ov2.bignat.APDU_BigInteger;
import ds.ov2.bignat.Host_modulus;
import ds.ov2.bignat.Host_vector;


/**
 * Stub code for running methods on the card.
 * Defines one stub method for each protocol step in RSA_card_protocol.id.
 * The stub methods are the top entry point into the
 * OV-chip protocol layer for host driver code.
 * Each stub method performs the following actions:
 * <OL>
 * <LI>argument conversion (for instance from
 *     {@link java.math.BigInteger BigInteger} to
 *     {@link ds.ov2.bignat.Bignat Bignat})</LI>
 * <LI>transfers the arguments to the card
 *     (possibly using several APDU's)</LI>
 * <LI>invokes the right method on the card</LI>
 * <LI>transfers the results back (again with possibly
 *     several APDU's)</LI>
 * <LI>result conversion</LI>
 * <LI>and finally packages several results into one
 *     tuple object</LI>
 * </OL>
 * 
 * @author idl compiler
 * @version automatically generated from RSA_card_protocol.id
 * @environment host
 * @CPP no cpp preprocessing needed
 */
public class RSA_card_protocol_stubs {

    /**
     * A protocol description instance from RSA_card_protocol.id. Used to access
     * the host initializers, which are additional parameters for
     * the APDU type constructors of arguments or results.
     * Initialized in the constructor.
     */
    private RSA_card_protocol_description protocol_description;


    /**
     * The output channel for debugging information of the 
     * OV-chip protocol layer.
     * Initialized in the constructor.
     */
    private PrintWriter out;


    /**
     * Controls apdutool line printing. Initialized in the constructor,
     * if true, the OV-chip protocol layer prints apdutool lines as 
     * part of its debugging output.
     */
    private boolean with_apdu_script;

    //#########################################################################
    //#########################################################################
    // 
    // Protocol allocate
    // 
    //#########################################################################
    //#########################################################################

    //#########################################################################
    // Step allocate
    // 

    /**
     * Host protocol instance for step allocate of protocol allocate.
     * Initialized via {@link #init_hp_allocate init_hp_allocate} 
     * (which is called from the constructor).
     */
    private Host_protocol hp_allocate;

    /**
     * Initialization method for {@link #hp_allocate}.
     *
     * @param d description instance for RSA_card_protocol.id
     * @param out the debugging out channel, {@code null} for disabling 
     *         debugging output
     * @param script whether this step prints apdutool lines
     */
    private void init_hp_allocate(RSA_card_protocol_description d,
                                    PrintWriter out, boolean script) 
    {
        hp_allocate = 
            new Host_protocol(d.allocate_protocol,
                              d.allocate_step,
                              out,
                              script,
                              "allocate"
                              );
    }


    /**
     * Call step allocate of protocol allocate
     * on the card.
     * 
     * @param _cc communication channel to the applet, must not be null
     * @param _short_bignat_size_host_arg argument short_bignat_size to be converted to APDU_short
     * @param _long_bignat_size_host_arg argument long_bignat_size to be converted to APDU_short
     * @param _attribute_length_host_arg argument attribute_length to be converted to APDU_short
     * @param _mont_correction_len_host_arg argument mont_correction_len to be converted to APDU_short
     * @throws CardException in case of communication errors
     */
    public void allocate_call(CardChannel _cc,
                              int _short_bignat_size_host_arg,
                              int _long_bignat_size_host_arg,
                              int _attribute_length_host_arg,
                              int _mont_correction_len_host_arg)
        throws CardException
    {
        APDU_Serializable[] call_args = new APDU_Serializable[]{
            new APDU_short(_short_bignat_size_host_arg),
            new APDU_short(_long_bignat_size_host_arg),
            new APDU_short(_attribute_length_host_arg),
            new APDU_short(_mont_correction_len_host_arg)
        };

        APDU_Serializable[] call_res = null;

        hp_allocate.run_step(_cc, call_args, call_res);
        return;
    }


    //#########################################################################
    //#########################################################################
    // 
    // Protocol initialize
    // 
    //#########################################################################
    //#########################################################################

    //#########################################################################
    // Step init_data
    // 

    /**
     * Host protocol instance for step init_data of protocol initialize.
     * Initialized via {@link #init_hp_init_data init_hp_init_data} 
     * (which is called from {@link #delayed_init}).
     */
    private Host_protocol hp_init_data;

    /**
     * Initialization method for {@link #hp_init_data}.
     *
     * @param d description instance for RSA_card_protocol.id
     * @param out the debugging out channel, {@code null} for disabling 
     *         debugging output
     * @param script whether this step prints apdutool lines
     */
    private void init_hp_init_data(RSA_card_protocol_description d,
                                    PrintWriter out, boolean script) 
    {
        hp_init_data = 
            new Host_protocol(d.initialize_protocol,
                              d.init_data_step,
                              out,
                              script,
                              "initialize"
                              );
    }


    /**
     * Call step init_data of protocol initialize
     * on the card.
     * 
     * @param _cc communication channel to the applet, must not be null
     * @param _data_n_host_arg argument data.n to be converted to Modulus
     * @param _data_ptls_key_host_arg argument data.ptls_key to be converted to Bignat
     * @param _data_bases_host_arg argument data.bases to be converted to Vector
     * @param _data_base_factors_host_arg argument data.base_factors to be converted to Vector
     * @param _data_current_attributes_host_arg argument data.current_attributes to be converted to Vector
     * @param _data_montgomerized_one_host_arg argument data.montgomerized_one to be converted to Bignat
     * @param _data_montgomery_corrections_host_arg argument data.montgomery_corrections to be converted to Vector
     * @throws CardException in case of communication errors
     */
    public void init_data_call(CardChannel _cc,
                               Host_modulus _data_n_host_arg,
                               BigInteger _data_ptls_key_host_arg,
                               Host_vector _data_bases_host_arg,
                               Host_vector _data_base_factors_host_arg,
                               Host_vector _data_current_attributes_host_arg,
                               BigInteger _data_montgomerized_one_host_arg,
                               Host_vector _data_montgomery_corrections_host_arg)
        throws CardException
    {
        APDU_Serializable[] call_args = new APDU_Serializable[]{
            _data_n_host_arg,
            new APDU_BigInteger(protocol_description.data.ptls_key.size(), _data_ptls_key_host_arg),
            _data_bases_host_arg,
            _data_base_factors_host_arg,
            _data_current_attributes_host_arg,
            new APDU_BigInteger(protocol_description.data.montgomerized_one.size(), _data_montgomerized_one_host_arg),
            _data_montgomery_corrections_host_arg
        };

        APDU_Serializable[] call_res = null;

        hp_init_data.run_step(_cc, call_args, call_res);
        return;
    }


    //#########################################################################
    //#########################################################################
    // 
    // Protocol resign
    // 
    //#########################################################################
    //#########################################################################

    //#########################################################################
    // Step get_signature
    // 

    /**
     * Host protocol instance for step get_signature of protocol resign.
     * Initialized via {@link #init_hp_get_signature init_hp_get_signature} 
     * (which is called from {@link #delayed_init}).
     */
    private Host_protocol hp_get_signature;

    /**
     * Initialization method for {@link #hp_get_signature}.
     *
     * @param d description instance for RSA_card_protocol.id
     * @param out the debugging out channel, {@code null} for disabling 
     *         debugging output
     * @param script whether this step prints apdutool lines
     */
    private void init_hp_get_signature(RSA_card_protocol_description d,
                                    PrintWriter out, boolean script) 
    {
        hp_get_signature = 
            new Host_protocol(d.resign_protocol,
                              d.get_signature_step,
                              out,
                              script,
                              "resign get attribute signature"
                              );
    }


    /**
     * Result record for step get_signature of
     * protocol resign.
     */
    public static class Get_signature_result {
        /**
         * Duration of the call in nanoseconds. The measurement
         * includes (de-)serialization but not the
         * allocation of argument and result arrays.
         */
        public final long duration;

        /**
         * Return value data.applet_id converted from APDU_byte.
         */
        public final byte data_applet_id;
        /**
         * Return value data.current_blinded_a converted from Bignat.
         */
        public final BigInteger data_current_blinded_a;
        /**
         * Return value data.current_signature converted from Signature.
         */
        public final Host_signature data_current_signature;
        /**
         * Return record constructor.
         */
        public Get_signature_result(
                    long ad,
                    byte a0,
                    BigInteger a1,
                    Host_signature a2) {
            duration = ad;
            data_applet_id = a0;
            data_current_blinded_a = a1;
            data_current_signature = a2;
        }
    }


    /**
     * Call step get_signature of protocol resign
     * on the card.
     * 
     * @param _cc communication channel to the applet, must not be null
     * @return Get_signature_result record containing all results, including the duration of the call.
     * @throws CardException in case of communication errors
     */
    public Get_signature_result get_signature_call(CardChannel _cc)
        throws CardException
    {
        APDU_Serializable[] call_args = null;

        APDU_byte _data_applet_id_host_res = new APDU_byte();
        APDU_BigInteger _data_current_blinded_a_host_res = new APDU_BigInteger(protocol_description.data.current_blinded_a.size());
        Host_signature _data_current_signature_host_res = new Host_signature(protocol_description.data.current_signature.sig_short_size, protocol_description.data.current_signature.sig_long_size, protocol_description.data.current_signature.applet_id);
        APDU_Serializable[] call_res = new APDU_Serializable[]{
            _data_applet_id_host_res,
            _data_current_blinded_a_host_res,
            _data_current_signature_host_res
        };

        long start = System.nanoTime();
        hp_get_signature.run_step(_cc, call_args, call_res);
        long duration = System.nanoTime() - start;
        return new Get_signature_result(duration, _data_applet_id_host_res.value, _data_current_blinded_a_host_res.value, _data_current_signature_host_res);
    }


    //#########################################################################
    // Step make_sig_hash
    // 

    /**
     * Host protocol instance for step make_sig_hash of protocol resign.
     * Initialized via {@link #init_hp_make_sig_hash init_hp_make_sig_hash} 
     * (which is called from {@link #delayed_init}).
     */
    private Host_protocol hp_make_sig_hash;

    /**
     * Initialization method for {@link #hp_make_sig_hash}.
     *
     * @param d description instance for RSA_card_protocol.id
     * @param out the debugging out channel, {@code null} for disabling 
     *         debugging output
     * @param script whether this step prints apdutool lines
     */
    private void init_hp_make_sig_hash(RSA_card_protocol_description d,
                                    PrintWriter out, boolean script) 
    {
        hp_make_sig_hash = 
            new Host_protocol(d.resign_protocol,
                              d.make_sig_hash_step,
                              out,
                              script,
                              "resign make hash"
                              );
    }


    /**
     * Result record for step make_sig_hash of
     * protocol resign.
     */
    public static class Make_sig_hash_result {
        /**
         * Duration of the call in nanoseconds. The measurement
         * includes (de-)serialization but not the
         * allocation of argument and result arrays.
         */
        public final long duration;

        /**
         * Return value data.sig_remainder converted from Bignat.
         */
        public final BigInteger data_sig_remainder;
        /**
         * Return record constructor.
         */
        public Make_sig_hash_result(
                    long ad,
                    BigInteger a0) {
            duration = ad;
            data_sig_remainder = a0;
        }
    }


    /**
     * Call step make_sig_hash of protocol resign
     * on the card.
     * 
     * @param _cc communication channel to the applet, must not be null
     * @param _data_host_alpha_host_arg argument data.host_alpha to be converted to Bignat
     * @param _data_remainders_host_arg argument data.remainders to be converted to Vector
     * @return Make_sig_hash_result record containing all results, including the duration of the call.
     * @throws CardException in case of communication errors
     */
    public Make_sig_hash_result make_sig_hash_call(CardChannel _cc,
                                         BigInteger _data_host_alpha_host_arg,
                                         Host_vector _data_remainders_host_arg)
        throws CardException
    {
        APDU_Serializable[] call_args = new APDU_Serializable[]{
            new APDU_BigInteger(protocol_description.data.host_alpha.size(), _data_host_alpha_host_arg),
            _data_remainders_host_arg
        };

        APDU_BigInteger _data_sig_remainder_host_res = new APDU_BigInteger(protocol_description.data.sig_remainder.size());
        APDU_Serializable[] call_res = new APDU_Serializable[]{
            _data_sig_remainder_host_res
        };

        long start = System.nanoTime();
        hp_make_sig_hash.run_step(_cc, call_args, call_res);
        long duration = System.nanoTime() - start;
        return new Make_sig_hash_result(duration, _data_sig_remainder_host_res.value);
    }


    //#########################################################################
    // Step finish_signature
    // 

    /**
     * Host protocol instance for step finish_signature of protocol resign.
     * Initialized via {@link #init_hp_finish_signature init_hp_finish_signature} 
     * (which is called from {@link #delayed_init}).
     */
    private Host_protocol hp_finish_signature;

    /**
     * Initialization method for {@link #hp_finish_signature}.
     *
     * @param d description instance for RSA_card_protocol.id
     * @param out the debugging out channel, {@code null} for disabling 
     *         debugging output
     * @param script whether this step prints apdutool lines
     */
    private void init_hp_finish_signature(RSA_card_protocol_description d,
                                    PrintWriter out, boolean script) 
    {
        hp_finish_signature = 
            new Host_protocol(d.resign_protocol,
                              d.finish_signature_step,
                              out,
                              script,
                              "signature finish"
                              );
    }


    /**
     * Result record for step finish_signature of
     * protocol resign.
     */
    public static class Finish_signature_result {
        /**
         * Duration of the call in nanoseconds. The measurement
         * includes (de-)serialization but not the
         * allocation of argument and result arrays.
         */
        public final long duration;

        /**
         * Return value signature_accepted converted from APDU_boolean.
         */
        public final boolean signature_accepted;
        /**
         * Return record constructor.
         */
        public Finish_signature_result(
                    long ad,
                    boolean a0) {
            duration = ad;
            signature_accepted = a0;
        }
    }


    /**
     * Call step finish_signature of protocol resign
     * on the card.
     * 
     * @param _cc communication channel to the applet, must not be null
     * @param _data_host_response_host_arg argument data.host_response to be converted to Bignat
     * @return Finish_signature_result record containing all results, including the duration of the call.
     * @throws CardException in case of communication errors
     */
    public Finish_signature_result finish_signature_call(CardChannel _cc,
                                         BigInteger _data_host_response_host_arg)
        throws CardException
    {
        APDU_Serializable[] call_args = new APDU_Serializable[]{
            new APDU_BigInteger(protocol_description.data.host_response.size(), _data_host_response_host_arg)
        };

        APDU_boolean _signature_accepted_host_res = new APDU_boolean();
        APDU_Serializable[] call_res = new APDU_Serializable[]{
            _signature_accepted_host_res
        };

        long start = System.nanoTime();
        hp_finish_signature.run_step(_cc, call_args, call_res);
        long duration = System.nanoTime() - start;
        return new Finish_signature_result(duration, _signature_accepted_host_res.value);
    }


    //#########################################################################
    //#########################################################################
    // 
    // Protocol gate
    // 
    //#########################################################################
    //#########################################################################

    //#########################################################################
    // Step commit
    // 

    /**
     * Host protocol instance for step commit of protocol gate.
     * Initialized via {@link #init_hp_commit init_hp_commit} 
     * (which is called from {@link #delayed_init}).
     */
    private Host_protocol hp_commit;

    /**
     * Initialization method for {@link #hp_commit}.
     *
     * @param d description instance for RSA_card_protocol.id
     * @param out the debugging out channel, {@code null} for disabling 
     *         debugging output
     * @param script whether this step prints apdutool lines
     */
    private void init_hp_commit(RSA_card_protocol_description d,
                                    PrintWriter out, boolean script) 
    {
        hp_commit = 
            new Host_protocol(d.gate_protocol,
                              d.commit_step,
                              out,
                              script,
                              "gate commit"
                              );
    }


    /**
     * Result record for step commit of
     * protocol gate.
     */
    public static class Commit_result {
        /**
         * Duration of the call in nanoseconds. The measurement
         * includes (de-)serialization but not the
         * allocation of argument and result arrays.
         */
        public final long duration;

        /**
         * Return value data.applet_id converted from APDU_byte.
         */
        public final byte data_applet_id;
        /**
         * Return value data.current_blinded_a converted from Bignat.
         */
        public final BigInteger data_current_blinded_a;
        /**
         * Return value data.current_signature converted from Signature.
         */
        public final Host_signature data_current_signature;
        /**
         * Return value data.result converted from Bignat.
         */
        public final BigInteger data_result;
        /**
         * Return record constructor.
         */
        public Commit_result(
                    long ad,
                    byte a0,
                    BigInteger a1,
                    Host_signature a2,
                    BigInteger a3) {
            duration = ad;
            data_applet_id = a0;
            data_current_blinded_a = a1;
            data_current_signature = a2;
            data_result = a3;
        }
    }


    /**
     * Call step commit of protocol gate
     * on the card.
     * 
     * @param _cc communication channel to the applet, must not be null
     * @return Commit_result record containing all results, including the duration of the call.
     * @throws CardException in case of communication errors
     */
    public Commit_result commit_call(CardChannel _cc)
        throws CardException
    {
        APDU_Serializable[] call_args = null;

        APDU_byte _data_applet_id_host_res = new APDU_byte();
        APDU_BigInteger _data_current_blinded_a_host_res = new APDU_BigInteger(protocol_description.data.current_blinded_a.size());
        Host_signature _data_current_signature_host_res = new Host_signature(protocol_description.data.current_signature.sig_short_size, protocol_description.data.current_signature.sig_long_size, protocol_description.data.current_signature.applet_id);
        APDU_BigInteger _data_result_host_res = new APDU_BigInteger(protocol_description.data.result.size());
        APDU_Serializable[] call_res = new APDU_Serializable[]{
            _data_applet_id_host_res,
            _data_current_blinded_a_host_res,
            _data_current_signature_host_res,
            _data_result_host_res
        };

        long start = System.nanoTime();
        hp_commit.run_step(_cc, call_args, call_res);
        long duration = System.nanoTime() - start;
        return new Commit_result(duration, _data_applet_id_host_res.value, _data_current_blinded_a_host_res.value, _data_current_signature_host_res, _data_result_host_res.value);
    }


    //#########################################################################
    // Step respond
    // 

    /**
     * Host protocol instance for step respond of protocol gate.
     * Initialized via {@link #init_hp_respond init_hp_respond} 
     * (which is called from {@link #delayed_init}).
     */
    private Host_protocol hp_respond;

    /**
     * Initialization method for {@link #hp_respond}.
     *
     * @param d description instance for RSA_card_protocol.id
     * @param out the debugging out channel, {@code null} for disabling 
     *         debugging output
     * @param script whether this step prints apdutool lines
     */
    private void init_hp_respond(RSA_card_protocol_description d,
                                    PrintWriter out, boolean script) 
    {
        hp_respond = 
            new Host_protocol(d.gate_protocol,
                              d.respond_step,
                              out,
                              script,
                              "gate card response"
                              );
    }


    /**
     * Result record for step respond of
     * protocol gate.
     */
    public static class Respond_result {
        /**
         * Duration of the call in nanoseconds. The measurement
         * includes (de-)serialization but not the
         * allocation of argument and result arrays.
         */
        public final long duration;

        /**
         * Return value data.remainders converted from Vector.
         */
        public final Host_vector data_remainders;
        /**
         * Return value data.result converted from Bignat.
         */
        public final BigInteger data_result;
        /**
         * Return record constructor.
         */
        public Respond_result(
                    long ad,
                    Host_vector a0,
                    BigInteger a1) {
            duration = ad;
            data_remainders = a0;
            data_result = a1;
        }
    }


    /**
     * Call step respond of protocol gate
     * on the card.
     * 
     * @param _cc communication channel to the applet, must not be null
     * @param _data_gamma_beta_3_host_arg argument data.gamma_beta_3 to be converted to Bignat
     * @return Respond_result record containing all results, including the duration of the call.
     * @throws CardException in case of communication errors
     */
    public Respond_result respond_call(CardChannel _cc,
                                       BigInteger _data_gamma_beta_3_host_arg)
        throws CardException
    {
        APDU_Serializable[] call_args = new APDU_Serializable[]{
            new APDU_BigInteger(protocol_description.data.gamma_beta_3.size(), _data_gamma_beta_3_host_arg)
        };

        Host_vector _data_remainders_host_res = new Host_vector(protocol_description.data.remainders.get_bignat_size(), protocol_description.data.remainders.get_length());
        APDU_BigInteger _data_result_host_res = new APDU_BigInteger(protocol_description.data.result.size());
        APDU_Serializable[] call_res = new APDU_Serializable[]{
            _data_remainders_host_res,
            _data_result_host_res
        };

        long start = System.nanoTime();
        hp_respond.run_step(_cc, call_args, call_res);
        long duration = System.nanoTime() - start;
        return new Respond_result(duration, _data_remainders_host_res, _data_result_host_res.value);
    }


    //#########################################################################
    // Delayed stub initialization
    // 

    /**
     * Delayed initialization.
     * Initialize protocols annotated with
     * <EM>delayed protocol init</EM> in RSA_card_protocol.id.
     */
    public void delayed_init() {
        init_hp_init_data(protocol_description, out, with_apdu_script);
        init_hp_get_signature(protocol_description, out, with_apdu_script);
        init_hp_make_sig_hash(protocol_description, out, with_apdu_script);
        init_hp_finish_signature(protocol_description, out, with_apdu_script);
        init_hp_commit(protocol_description, out, with_apdu_script);
        init_hp_respond(protocol_description, out, with_apdu_script);
    }


    //#########################################################################
    // Constructor
    // 

    /**
     * Stub constructor. Initializes all non-delayed host protocol
     * instances from RSA_card_protocol.id. Delayed protocols must be initialized
     * separately with {@link #delayed_init} at the appropriate moment.
     *
     * @param d protocol description instance for RSA_card_protocol.id
     * @param o channel for printing debugging information, pass null 
     *           for disabling debugging information
     * @param script if true, print apdutool lines for all APDUs as part 
     *          of the debugging information.
     */
    public RSA_card_protocol_stubs(RSA_card_protocol_description d,
                               PrintWriter o, 
                               boolean script) {
        protocol_description = d;
        out = o;
        with_apdu_script = script;
        // initialize the Host_protocols
        init_hp_allocate(protocol_description, out, with_apdu_script);
    }
}

