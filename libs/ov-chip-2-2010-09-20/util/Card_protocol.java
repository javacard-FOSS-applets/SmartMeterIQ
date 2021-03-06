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
// Created 25.8.08 by Hendrik
// 
// process a protocol on the card
// 
// $Id: Card_protocol.java,v 1.13 2009-04-09 10:42:16 tews Exp $

#include <config>

#ifdef PACKAGE
  package PACKAGE;
#else
  package ds.ov2.util;
#endif

import javacard.framework.APDU;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;


/** 
 * Applet side of the OV-chip protocol implementation. This class is
 * the heart of the OV-chip protocol on the card. Every applet should
 * contain precisely one instance of this class. Under normal
 * circumstances the user does not have to care about it, because this
 * instance is created, maintained and used in {@link
 * Protocol_applet}, see {@link Protocol_applet#card_protocol}.
 * <P>
 *
 * The singleton instance in {@link Protocol_applet#card_protocol}
 * processes all incomming APDU's with {@link #process process}. First
 * it will be checked if the protocol identification, the step number
 * and the batch meet the expectations. Then, for argument APDU's the
 * data is transfered to the declared arguments of the current
 * protocol step. If all arguments have been received the method of
 * the current step is called. Finally the results are transfered back
 * to the host with response APDU's.
 * <P>
 *
 * So this class implements all protocol-layer related features,
 * execpt for managing the registered protocols (see {@link
 * Registered_protocols}) and selecting new protocols (see {@link
 * Protocol_applet#process Protocol_applet.process} and {@link
 * Registered_protocols#get_protocol
 * Registered_protocols.get_protocol}). 
 * <P>
 *
 * The singleton instance in {@link Protocol_applet#card_protocol}
 * keeps the current protocol and the current step in its local state.
 * Additionally it remembers where to continue with argument
 * deserialization or result serialization for the next APDU. All this
 * internal state is updated appropriately when processing APDU's.
 * <P>
 *
 * The implementation is just a matter of interpreting the data
 * structures of the protocols and protocol steps. No big surprises
 * here, it is only a bit cumbersome because Java Card 2.2.1 only
 * guarantees an APDU buffer size of 32 bytes.
 *
 * 
 * @author Hendrik Tews
 * @version $Revision: 1.13 $
 * @commitdate $Date: 2009-04-09 10:42:16 $ by $Author: tews $
 * @environment card
 * @CPP This class uses the following cpp defines:
 *   <a href="../../../overview-summary.html#PACKAGE">PACKAGE</a>,
 *   <a href="../../../overview-summary.html#PUBLIC">PUBLIC</a>,
 *   <a href="../../../overview-summary.html#ASSERT">ASSERT</a>,
 *   <a href="../../../overview-summary.html#ASSERT_TAG">ASSERT_TAG<a>
 */
PUBLIC class Card_protocol {

    /**
     * 
     * The current protocol or null if no protocol is selected. Reset
     * to null after the last result APDU of the last step of a
     * protocol. Then the outside has to set the next current protocol
     * with {@link #set_protocol set_protocol}. Normally this is done
     * automatically in {@link Protocol_applet#process
     * Protocol_applet.process}. <P>
     *
     * The INS byte of the next incoming APDU must be equal to the
     * protocol identification of this protocol.
     */
    private Protocol protocol = null;


    /**
     * 
     * The number of the current protocol step. Automatically
     * increased after each step.
     * <P>
     *
     * The P1 byte of the next incoming APDU must be equal to this
     * number. 
     */
    private short protocol_step = 0;


    /**
     * 
     * The index of the argument or result that we are currentely
     * working on. If the {@link #batch} is positive it is an index of
     * an argument in the array of declared arguments of the current
     * step. If the {@link #batch} is negative it is an index of a
     * result in the array of declared results or the current step.
     */
    private short variable_index = 0;


    /**
     * 
     * The number of bytes of the current argument or result that have
     * already been received or sent.
     */
    private short data_index = 0;


    /**
     * 
     * The batch number. The batch counts the APDU's in one step. For
     * argument APDU's the first bit is 0 and the batch counts the
     * argument APDU's from 0 upwards. For result APDU's the first bit
     * is 1 and the batch counts from -128 upwards. The first response
     * is sent in the response APDU of the last argument APDU,
     * therefore the first result APDU has batch -127. 
     * <P>
     *
     * The P2 byte of the next incomming APDU must be equal to the
     * batch. 
     */
    private byte batch = 0;


    /**
     * 
     * Total number of result bytes still to send. Used for checking
     * the response length (LE byte) of the result APDU's. Not used
     * when receiving arguments.
     */
    private short total_still_to_send = 0;

    
    /**
     * 
     * Constructor. There is only one instance of this class necessary
     * in each applet. This is automatically created and maintained in
     * {@link Protocol_applet}. This constructor does not initialize
     * anything. Initialization happens in {@link #set_protocol
     * set_protocol}. 
     */
    Card_protocol() {
        return;
    }


    /**
     * 
     * Returns true if a protocol is selected.
     * 
     * @return true if a protocol is currently selected.
     */
    public boolean protocol_running() {
        return protocol != null;
    }


    /**
     * 
     * Set the next selected protocol. (Re-)Initialize the local state
     * to prepare for the first APDU of this protocol.
     * 
     * @param protocol next selected protocol
     */
    public void set_protocol(Protocol protocol) {
        this.protocol = protocol;
        protocol_step = 0;
        variable_index = 0;
        data_index = 0;
        batch = 0;
        return;
    }


    /**
     * 
     * Clears the current protocol selection. Afterwards no protocol
     * is selected.
     */
    public void clear_protocol() {
        protocol = null;
    }


    /**
     * 
     * Finish current protocol step. Called as the last action in the
     * last result APDU. Advances {@link #protocol_step} and
     * initializes the local state for the next step (set {@link
     * #variable_index}, {@link #data_index} and {@link #batch} all to
     * 0). If the last step of a protocol is finished the current
     * protocol selection is cleared.
     */
    private void finish_step() {
        // Sent all results for this step. Turn to the next step.
        protocol_step++;
        if(protocol_step == protocol.steps.length) {
            // Finished the whole protocol.
            protocol = null;
        }
        else {
            // Set up everything for receive phase of next step.
            variable_index = 0;
            data_index = 0;
            batch = 0;
        }
    }       


    /**
     * 
     * Start or continue to send the results to the host. This method
     * is called in the last argument APDU, just after the step method
     * has finished, and afterwards for all result APDU that follow. 
     * <P>
     *
     * This method serializes the declared results of the current
     * step, advances {@link #data_index}, {@link #variable_index},
     * {@link #total_still_to_send} and {@link #batch} as needed and
     * sends the serialized data to the host. After the last result
     * byte has been sent the current step is finished with {@link
     * #finish_step}. 
     * <P>
     *
     * This method checks the expected response length (the LE byte)
     * as far as this is possible. An {@link ISOException} is thrown
     * for wrong values.
     * 
     * @param apdu the incoming APDU
     * @param buf the APDU buffer, must be equal to {@code apdu.getBuffer()}.
     * @param step the current protocol step, must be equal to {@code
     * protocol.steps[protocol_step]}
     * @throws ISOException with status {@link
     * ISO7816#SW_CORRECT_LENGTH_00 SW_CORRECT_LENGTH_00} if the
     * expected response length (the LE byte) is too small
     */
    // Start or continue to send the results.
    // buf must be the APDU buffer, step is the current protocol step,
    // variable_index contains the index of the result we start
    // or continue to send and, finally, data_index points to the next
    // byte to be sent in this value.
    private void process_send(APDU apdu, byte[] buf, Protocol_step step) {
        if(step.results == null || variable_index >= step.results.length) {
            // No results to send. Finish this step.
            ASSERT_TAG(total_still_to_send == 0, 0x01);
            finish_step();
        }
        else {
            // At this point:
            // - variable_index denotes the result value, where serialization
            //   must continue
            // - in this value serialization must continue at data_index
            // - total_still_to_send > 0 number of bytes to be send in total
            APDU_Serializable result = step.results[variable_index];
            short this_apdu_to_send = apdu.setOutgoing();
            // if(this_apdu_to_send == 256)
            //  // XXX can I send 256 bytes back???
            //  this_apdu_to_send = 255;
            if(total_still_to_send > this_apdu_to_send &&
               this_apdu_to_send < 255) {
                // inappropriate send size
                ISOException.throwIt((short)(
                    ISO7816.SW_CORRECT_LENGTH_00 | 
                      (total_still_to_send >= 255 ? 0xff : 
                       (byte)total_still_to_send)));
            } 
            if(this_apdu_to_send > total_still_to_send) {
                // Sometimes setOutgoing returns only an upper bound.
                this_apdu_to_send = total_still_to_send;
            }
            apdu.setOutgoingLength(this_apdu_to_send);
            total_still_to_send -= this_apdu_to_send;
            
            short used, buf_index, buf_available; // used in loop
            while(this_apdu_to_send > 0) {
                // buf_index points to the next byte in buf to fill.
                buf_index = 0;
                // buf_available counts the bytes we can still copy 
                // into buf.
                buf_available = (short)(buf.length);
                if(buf_available > this_apdu_to_send) {
                    // Our buffer is bigger than what we can send now.
                    buf_available = this_apdu_to_send;
                }

                while(buf_available > 0) {
                    // At this point:
                    // - buf is the apdu buffer
                    // - it has buf_available bytes free at index buf_index
                    // - it has buf_index bytes to be send at 0 .. buf_index -1
                    // - we still have to send this_apdu_to_send bytes 
                    //   in this APDU
                    // - after that there are total_still_to_send bytes awaiting
                    // - result points to a result value not yet 
                    //   completely serialized
                    // - serialization continues at data_index in result
                    used = result.to_byte_array(buf_available, data_index,
                                                buf, buf_index);
                    if(used == buf_available) {
                        // result has more data to send.
                        // In case result would have been finished with
                        // precisely buf_available bytes we would have gotten
                        // used == buf_available +1 !!
                        buf_available = 0;
                        // buf is full, but keep buf_index valid, because 
                        // it is used below to determine which portion of
                        // buf to send.
                        buf_index += used;
                        data_index += used;
                        break;  // take a shortcut out
                    }
                    else {
                        // Serialization of result is finished.
                        if(used == (short)(buf_available + 1))
                            used = buf_available;
                        buf_available -= used;
                        buf_index += used;

                        // Take next result value.
                        variable_index++;
                        if(variable_index >= step.results.length) {
                            // All results are finished. Only need to send
                            // the last bytes in buf. Check that we are 
                            // not expected to send more data.
                            ASSERT_TAG(this_apdu_to_send == buf_index, 0x02);
                            ASSERT_TAG(buf_available == 0, 0x03);
                            ASSERT_TAG(total_still_to_send == 0, 0x04);
                            break;
                        }
                        result = step.results[variable_index];
                        data_index = 0;
                    }
                }
                // Send data in buf.
                apdu.sendBytes((short)0, buf_index);
                this_apdu_to_send -= buf_index;
            }
            // Finished this APDU.
            // Finished sending too? Turn to next step then.
            if(variable_index >= step.results.length) {
                finish_step();
            }
            else {
                // Otherwise wait for the next batch.
                batch += 1;
                ASSERT_TAG(batch < 0, 0x05);
            }
            // Simply return now to give ISO7816.SW_NO_ERROR back.
        }
        return;
    }


    /**
     * 
     * Call the method for the current step and start sending the
     * results afterwards. This method only calls the step method and
     * prepares the local state for sending the results ({@link
     * #variable_index} and {@link #data_index} are reset to 0, {@link
     * #batch} is set to 0x80 and {@link #total_still_to_send} is
     * initialized to the total size of the declared results). Sending
     * the first results is then done by {@link #process_send process_send}.
     * 
     * @param apdu the incoming APDU
     * @param buf the APDU buffer, must be equal to {@code apdu.getBuffer()}.
     * @param step the current protocol step, must be equal to {@code
     * protocol.steps[protocol_step]}
     * @throws ISOException with status {@link
     * ISO7816#SW_CORRECT_LENGTH_00 SW_CORRECT_LENGTH_00} if the
     * expected response length (the LE byte) of the last argument
     * APDU is too small
     */
    private void process_method(APDU apdu, byte[] buf, Protocol_step step) {
        //step.method.method(apdu, buf);
        step.method.method();

        // The results are now in step.results. Set up sending phase.
        variable_index = 0;     // start with the first result value
        data_index = 0;         // start at its first byte
        batch = (byte)0x80;     // set first bit and reset batch

        // Keep the number of bytes remaining to be sent 
        // in total_still_to_send in order to check the LE values.
        total_still_to_send = step.get_result_size();
        process_send(apdu, buf, step);
        return;
    }


    /**
     * 
     * Process the next argument APDU. If there are still arguments to
     * receive then deserialize the data of this APDU into the
     * declared arguments. Divert to {@link #process_method
     * process_method} after all arguments have been received. Advance
     * {@link #variable_index}, {@link #data_index} and {@link #batch}
     * as needed. <P>
     *
     * Checks that the host does not send too much data. Throw an
     * {@link ISOException} otherwise. (If the host sends too little
     * data than the next incomming APDU will yield a batch mismatch error.)
     * 
     * @param apdu the incoming APDU
     * @param buf the APDU buffer, must be equal to {@code apdu.getBuffer()}.
     * @param step the current protocol step, must be equal to {@code
     * protocol.steps[protocol_step]}
     * @throws ISOException with status {@link
     * ISO7816#SW_BYTES_REMAINING_00 SW_BYTES_REMAINING_00} if the
     * host has sent too much data
     * @throws ISOException with status {@link
     * ISO7816#SW_CORRECT_LENGTH_00 SW_CORRECT_LENGTH_00} if the
     * expected response length (the LE byte) of the last argument
     * APDU is too small
     */
    private void process_receive(APDU apdu, byte[] buf, Protocol_step step) {
        if(step.arguments == null || variable_index >= step.arguments.length) {
            // No arguments to deserialize. Proceed.
            process_method(apdu, buf, step);
        }
        else {
            // The argument which we start or continue to read.
            APDU_Serializable argument = step.arguments[variable_index];
            // still_to_read contains the number of bytes that we still
            // expect to read after finishing the current APDU buffer.
            short still_to_read = (short)(buf[ISO7816.OFFSET_LC] & 0xff);
            // read_count contains the number of bytes in the current
            // APDU buffer not yet processed.
            short read_count = apdu.setIncomingAndReceive();
            still_to_read -= read_count;
            // buf_index points to the first unprocessed byte.
            short buf_index = ISO7816.OFFSET_CDATA;
            short used = 0;

            outer_loop: 
            while(true) {
                do {
                    // At this point:
                    // - buf is the APDU buffer
                    // - read_count > 0
                    // - buf contains read_count new bytes at index buf_index
                    // - argument still needs some data
                    // - deserialization of argument must continue at data_index
                    // - still_to_read bytes are available by refilling the 
                    //   buffer buf
                    used = argument.from_byte_array(read_count, data_index,
                                                    buf, buf_index);

                    if(used == read_count) {
                        // argument needs more data.
                        // In case argument would have been finished with
                        // precisely read_count bytes we would have
                        // used == read_count +1 !!
                        read_count = 0;
                        data_index += used;
                        break;  // take a shortcut out to read more bytes
                    }
                    else {
                        // Deserialization of argument is finished.
                        // If used < read_count there is data left.
                        // If used == read_count + 1 all data has been used.
                        if(used == (short)(read_count + 1))
                            used = read_count;

                        buf_index += used;
                        read_count -= used;
                        variable_index++;
                        // Finished all arguments? 
                        // Leave the outermost loop then!
                        if(variable_index >= step.arguments.length) {
                            break outer_loop;
                        }
                        argument = step.arguments[variable_index];
                        data_index = 0;
                    }
                } while(read_count > 0);

                // The current buffer is finished, but we need some more data.
                if(still_to_read == 0) {
                    // No more data in this APDU.
                    break outer_loop;
                }
                buf_index = 0;
                read_count = apdu.receiveBytes((short)0);
                ASSERT_TAG(read_count > 0, 0x06);
                still_to_read -= read_count;
                ASSERT_TAG(still_to_read >= 0, 0x07);
            }

            // Either we finished all data of this APDU,
            // or we deserialized all arguments.
            if(variable_index >= step.arguments.length) {
                // All arguments are deserialized.
                // Make sure no data is left.
                if(read_count > 0 || still_to_read > 0) {
                    ISOException.throwIt((short)(
                        ISO7816.SW_BYTES_REMAINING_00 |
                           (short)(read_count + still_to_read) & 0xff));
                }
                process_method(apdu, buf, step);
            }
            else {
                // Finished this APDU but need more data.
                // Increment the batch and return;
                batch += 1;
                ASSERT_TAG(batch > 0, 0x08);
                apdu.setOutgoingAndSend((short)0, (short)0);
            }
        }
        return;
    }


    /**
     * 
     * Process the next APDU in the currently selected protocol. This
     * method checks it the protocol identification number, the step
     * number and the batch in the incoming APDU match our local state. If
     * one of these checks fails an {@link ISOException} is thrown
     * with an appropriate response status. 
     * <P>
     *
     * Depending on the {@link #batch} the APDU is delegated to either
     * {@link #process_receive process_receive} or {@link
     * #process_send process_send}. <P>
     *
     * Asserts that a current protocol is selected.
     * <P>
     *
     * In case of any error the protocol selection is cleared and the
     * current protocol aborted. 
     * 
     * @param apdu the incoming APDU
     * @param buf the APDU buffer, must be equal to {@code apdu.getBuffer()}.
     * @throws ISOException with status {@link
     * Response_status#OV_UNEXPECTED_PROTOCOL_ID
     * OV_UNEXPECTED_PROTOCOL_ID} for a protocol identification number
     * mismatch
     * @throws ISOException with status {@link
     * Response_status#OV_UNEXPECTED_PROTOCOL_STEP
     * OV_UNEXPECTED_PROTOCOL_STEP} for a protocol step number mismatch
     * @throws ISOException with status {@link
     * Response_status#OV_UNEXPECTED_BATCH OV_UNEXPECTED_BATCH} for a
     * batch mismatch
     * @throws ISOException with status {@link
     * ISO7816#SW_BYTES_REMAINING_00 SW_BYTES_REMAINING_00} if the
     * host has sent too much data
     * @throws ISOException with status {@link
     * ISO7816#SW_CORRECT_LENGTH_00 SW_CORRECT_LENGTH_00} if the
     * expected response length (the LE byte) of the last argument
     * APDU or an result APDU is too small
     */
    public void process(APDU apdu, byte[] buf) {
        ASSERT_TAG(protocol != null, 0x09);

        Protocol_step step = protocol.steps[protocol_step];

        // Check protocol and step identifier and batch.
        if((buf[ISO7816.OFFSET_INS] & 0xff) != protocol.protocol_id) {
            clear_protocol();
            ISOException.throwIt(Response_status.OV_UNEXPECTED_PROTOCOL_ID);
        }

        if((buf[ISO7816.OFFSET_P1] & 0xff) != step.step_identifier) {
            clear_protocol();
            ISOException.throwIt(Response_status.OV_UNEXPECTED_PROTOCOL_STEP);
        }

        if((buf[ISO7816.OFFSET_P2] & 0xff) != (batch & 0xff)) {
            clear_protocol();
            ISOException.throwIt(Response_status.OV_UNEXPECTED_BATCH);
        }

        try {
            if(batch >= 0)
                process_receive(apdu, buf, step);
            else
                process_send(apdu, buf, step);
        }
        catch(RuntimeException e) {
            clear_protocol();
            throw e;
        }
    }
}
