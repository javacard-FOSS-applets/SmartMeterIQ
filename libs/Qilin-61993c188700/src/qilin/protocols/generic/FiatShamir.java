package qilin.protocols.generic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import qilin.comm.Channel;
import qilin.comm.Message;
import qilin.comm.QueueChannel;
import qilin.primitives.StreamingRandomOracle;
import qilin.protocols.SigmaProtocol;
import qilin.util.EncodingUtils;

/**
 * Converts a sigma protocol into a non-interactive proof.
 * 
 * @author talm
 *
 */
public class FiatShamir {
	
	/**
	 * A class that wraps a channel and can act as a randomness generator by putting the received messages through a
	 * random oracle.
	 *
	 */
	public static class FiatShamirRandomChannel extends Random implements Channel {
		private static final long serialVersionUID = 1L;
		
		/**
		 * The wrapped channel.
		 */
		Channel toPeer;
		
		/**
		 * The random oracle used to convert messages sent in the channel to 
		 * "randomness".
		 */
		StreamingRandomOracle oracle;
		
		/**
		 * A flag that specifies whether messages sent over the FiatShamir channel should be dropped.
		 * This is set to true when using the FiatShamir channel for verification. 
		 */
		boolean dropSent;
		
		/**
		 * A counter for the number of random bytes requested.
		 */
		int counter;
		
		/**
		 * Used to hold the byte representation of message length (in a form acceptable to the {@link #oracle}).
		 */
		byte[] lenArr;
		
		public FiatShamirRandomChannel(Channel toPeer, StreamingRandomOracle oracle, boolean dropSent) {
			this.toPeer = toPeer;
			this.oracle = oracle;
			this.dropSent = dropSent;
			oracle.reset();
			lenArr = new byte[4];
			counter = 0;
		}
		
		@Override
		public Message newMessage() {
			return toPeer.newMessage();
		}

		/**
		 * Receive a message from the prover. The message is read from the wrapped channel {@link FiatShamirRandomChannel#toPeer}
		 * and its contents are used to update the random oracle seed before being returned.
		 * 
		 * @return the message read from the prover.
		 */
		@Override
		public Message receive() throws IOException {
			Message inMsg = toPeer.receive();
			Message outMsg = toPeer.newMessage();
			
			// We add the message length to the digest so 
			// that two separate messages will have a different digest
			// than one long one with the same data.
			EncodingUtils.encode(inMsg.length(), lenArr, 0);
			oracle.update(lenArr, 0, 4);
			counter = 0;
			
			ByteBuffer buf;
			
			while ((buf = inMsg.readBuf()) != null) {
				buf.compact();
				byte[] bufArr = buf.array();
				oracle.update(bufArr, buf.arrayOffset(), buf.position());
				outMsg.writeBuf(buf);
			}
			
			return outMsg;
		}

		/**
		 * Send a message to the prover. The message is sent through the wrapped channel  {@link FiatShamirRandomChannel#toPeer}.
		 * If {@link FiatShamirRandomChannel#dropSent} is true, the message is not sent (this happens when this channel
		 * is used to verify a message -- the real prover doesn't exist). 
		 */
		@Override
		public void send(Message msg) throws IOException {
			if (!dropSent)
				toPeer.send(msg);
		}

		/**
		 * Return pseudorandom bits output by the {@link #oracle}. Always returns 32 bits (ignoring its input).
		 */
		@Override
		protected int next(int bits) {
			byte[] hash = oracle.digest(counter++, 4);

			return EncodingUtils.decodeInt(hash, 0);
		}
		
	}

	
	public static class Verifier<Params> extends ProtocolPartyBase {
		SigmaProtocol.Verifier<Params> verifier;
		Queue<Message> prover2verifier;
		Queue<Message> verifier2prover;
		
		FiatShamirRandomChannel verifier2ProverChannel;
		
		StreamingRandomOracle oracle;
		
		protected void initVerifier2ProverChannel() {
			QueueChannel qc = new QueueChannel(prover2verifier, verifier2prover);
			verifier2ProverChannel = new FiatShamirRandomChannel(qc, oracle, true);
		}

		public Verifier(SigmaProtocol.Verifier<Params>  verifier, StreamingRandomOracle oracle) {
			this.verifier = verifier;
			this.oracle = oracle;
			prover2verifier = new LinkedBlockingQueue<Message>();
			verifier2prover = new LinkedBlockingQueue<Message>();
		}
		
		protected void resetInternalState() {
			oracle.reset(); // This resets the FiatShamirRandomChannel
			prover2verifier.clear();
			verifier2prover.clear();
		}
		
		@Override
		public void init() throws IOException, InterruptedException {
			initVerifier2ProverChannel();

			// interactive initialization is not allowed, nor is use of randomness
			// (otherwise the FiatShamir prover might not get the same results).
			// We set the channel and randomness to null to help catch this 
			// as a runtime error.
			verifier.setParameters(null, null);
			verifier.init();
		}

		public boolean verify(Params params) throws IOException, InterruptedException {
			resetInternalState();
			
			// Read the single message generated by the FiatShamir prover.
			Message proofMsg = toPeer.receive();

			// The message encodes multiple messages (that would have been sent
			// by the prover in the interactive sigma protocol). 
			// We decode them and put them in the prover2verifier queue.
			while(proofMsg.length() > 0) {
				Message msg = toPeer.newMessage();
				int len = EncodingUtils.decodeInt(proofMsg);

				msg.write(proofMsg, len);
				prover2verifier.add(msg);
			}

			verifier.setParameters(verifier2ProverChannel, verifier2ProverChannel);
			return verifier.verify(params);
		}
	}
	
	public static class Prover<VerifierParams, ProverParams extends VerifierParams>  extends Verifier<VerifierParams>  {
		SigmaProtocol.Prover<ProverParams> prover;

		Message proofMsg;
		Prover2VerifierChannel prover2VerifierChannel;
		Verifier2ProverChannel internalProver2VerifierChannel;
		
		public Prover(SigmaProtocol.Prover<ProverParams> prover, SigmaProtocol.Verifier<VerifierParams> verifier, StreamingRandomOracle oracle) {
			super(verifier, oracle);
			this.prover = prover;
			
			prover2VerifierChannel = new Prover2VerifierChannel();

			internalProver2VerifierChannel = new Verifier2ProverChannel();
			verifier2ProverChannel = new FiatShamirRandomChannel(internalProver2VerifierChannel, oracle, false);
		}

		@Override
		protected void resetInternalState() {
			super.resetInternalState();
			prover2VerifierChannel.reset();
			internalProver2VerifierChannel.reset();
			proofMsg = toPeer.newMessage();
		}
		
		private static class Escape extends RuntimeException {
			private static final long serialVersionUID = 6575279048671519226L;
		}

		private class Prover2VerifierChannel implements Channel {
			boolean hasRead;
			VerifierParams verifierParams;
			
			public void setVerifierParams(VerifierParams params) {
				this.verifierParams = params;
			}
		
			public void reset() {
				hasRead = false;
			}
			
			public Prover2VerifierChannel() {
				reset();
			}
			
			@Override
			public Message receive() throws IOException {
				if (verifier2prover.isEmpty()) {
					// Only allow the prover to invoke the verifier once; 
					// this generates all the "challenge phase" messages.
					assert hasRead == false; 
					hasRead = true;
					try {
						verifier.verify(verifierParams);
					} catch (Escape e) {
						// This was expected
					}
				}
				return verifier2prover.remove();
			}

			@Override
			public void send(Message msg) throws IOException {
				Message msgCopy = new Message();

				// We concatenate all prover messages into a single proofMsg. 
				// We have to read out the message and write a copy.
				EncodingUtils.encode(msg.length(), proofMsg);
				ByteBuffer buf;
				while ((buf = msg.readBuf()) != null) {
					byte[] bufarr = buf.array();
					proofMsg.write(bufarr, buf.arrayOffset() + buf.position(), buf.remaining());
					buf.compact();
					msgCopy.writeBuf(buf);
				}
				
				prover2verifier.add(msgCopy);
			}

			@Override
			public Message newMessage() {
				return new Message();
			}
		}

		private class Verifier2ProverChannel implements Channel {
			boolean hasWritten;


			public void reset() {
				hasWritten = false;
			}
			
			public Verifier2ProverChannel() {
				reset();
			}
			
			@Override
			public Message receive() throws IOException {
				if (hasWritten) {
					// The verifier is expecting to read the response now. However, there is no response (since the
					// FiatShamir prover won't compute a response until the verifier returns).
					// We throw an Escape that will be caught by the call invoking verifier.verify(). 
					throw new Escape();
				}
				return prover2verifier.remove();
			}

			@Override
			public void send(Message msg) throws IOException {
				hasWritten = true;
				verifier2prover.add(msg);
			}

			@Override
			public Message newMessage() {
				return new Message();
			}
		}

		protected void initVerifier2ProverChannel() {
		}
		
		@Override
		public void init() throws IOException, InterruptedException  {
			super.init();

			// interactive initialization is not allowed
			// (we set the channel to null to help catch this 
			// as a runtime error)
			prover.setParameters(null, rand);
			prover.init();
		}
		
		public void prove(ProverParams params) throws IOException, InterruptedException {
			resetInternalState();
			
			prover2VerifierChannel.setVerifierParams(params);
			prover.setParameters(prover2VerifierChannel, rand);
			verifier.setParameters(verifier2ProverChannel, verifier2ProverChannel);

			prover.prove(params);

			toPeer.send(proofMsg);
			proofMsg = null;
		}
	}
}