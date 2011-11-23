package org.openmuc.jsml.structures.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openmuc.jsml.structures.OctetString;
import org.openmuc.jsml.structures.SML_Message;
import org.openmuc.jsml.structures.SML_MessageBody;
import org.openmuc.jsml.structures.SML_PublicOpenReq;
import org.openmuc.jsml.structures.Unsigned8;

public class CheckingSMLMessageCRCTest {

	@Test
	public void decodeSMLMessageWithShortCRC() throws IOException {
		byte[] message = new byte[] { 0x76, 0x09, (byte) 0x98, 0x43, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x62, 0x04,
				0x62, 0x00, 0x72, 0x63, 0x01, 0x01, 0x76, 0x01, 0x07, (byte) 0x93, (byte) 0x81, 0x7d, (byte) 0x9e,
				(byte) 0xbb, 0x10, 0x09, (byte) 0x88, 0x16, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0b, 0x06, 0x45, 0x4d,
				0x48, 0x01, 0x00, 0x1d, 0x46, 0x5c, 0x79, 0x01, 0x01, 0x62, (byte) 0xea, 0x00, 0x76, 0x09, (byte) 0x99,
				0x43, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x62, 0x05, 0x62, 0x00, 0x72, 0x63, 0x07, 0x01, 0x77, 0x01,
				0x0b, 0x06, 0x45, 0x4d, 0x48, 0x01, 0x00, 0x1d, 0x46, 0x5c, 0x79, 0x01, 0x72, 0x62, 0x01, 0x65, 0x01,
				0x5e, (byte) 0x8d, 0x41, 0x77, 0x77, 0x07, (byte) 0x81, (byte) 0x81, (byte) 0xc7, (byte) 0x82, 0x03,
				(byte) 0xff, 0x01, 0x01, 0x01, 0x01, 0x04, 0x45, 0x4d, 0x48, 0x01, 0x77, 0x07, 0x01, 0x00, 0x00, 0x00,
				0x09, (byte) 0xff, 0x01, 0x01, 0x01, 0x01, 0x0b, 0x06, 0x45, 0x4d, 0x48, 0x01, 0x00, 0x1d, 0x46, 0x5c,
				0x79, 0x01, 0x77, 0x07, 0x01, 0x00, 0x01, 0x08, 0x00, (byte) 0xff, 0x63, 0x01, (byte) 0x82, 0x01, 0x62,
				0x1e, 0x52, (byte) 0xff, 0x54, 0x23, 0x79, 0x71, 0x01, 0x77, 0x07, 0x01, 0x00, 0x01, 0x08, 0x01,
				(byte) 0xff, 0x01, 0x01, 0x62, 0x1e, 0x52, (byte) 0xff, 0x54, 0x23, 0x79, 0x71, 0x01, 0x77, 0x07, 0x01,
				0x00, 0x01, 0x08, 0x02, (byte) 0xff, 0x01, 0x01, 0x62, 0x1e, 0x52, (byte) 0xff, 0x52, 0x00, 0x01, 0x77,
				0x07, 0x01, 0x00, 0x0f, 0x07, 0x00, (byte) 0xff, 0x01, 0x01, 0x62, 0x1b, 0x52, (byte) 0xff, 0x53, 0x03,
				(byte) 0xba, 0x01, 0x77, 0x07, (byte) 0x81, (byte) 0x81, (byte) 0xc7, (byte) 0x82, (byte) 0x05,
				(byte) 0xff, 0x01, 0x01, 0x01, 0x01, (byte) 0x83, 0x02, 0x17, (byte) 0x98, 0x6f, (byte) 0xca, 0x5f,
				0x7b, 0x2b, 0x50, (byte) 0x8c, 0x75, (byte) 0x8e, 0x54, 0x7c, 0x1c, (byte) 0xc5, 0x0f, 0x53,
				(byte) 0x8e, (byte) 0x8f, (byte) 0x8c, 0x17, (byte) 0xd2, 0x5c, (byte) 0xa7, 0x78, 0x5a, (byte) 0xb8,
				0x61, 0x56, 0x44, (byte) 0xcf, 0x2c, (byte) 0x9b, (byte) 0x95, (byte) 0xb3, 0x78, 0x51, 0x7e, 0x33,
				0x62, 0x58, 0x22, 0x60, (byte) 0xaf, 0x5c, 0x1b, (byte) 0x88, 0x7c, 0x01, 0x01, 0x72, 0x62, 0x01, 0x65,
				0x01, 0x78, (byte) 0x9a, 0x77, 0x63, 0x47, (byte) 0xf8, 0x00, 0x76, 0x09, (byte) 0x9a, 0x43, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x62, 0x06, 0x62, 0x00, 0x72, 0x63, 0x02, 0x01, 0x71, 0x01, 0x63, 0x60,
				(byte) 0x8c, 0x00, 0x00, 0x00, 0x00, 0x1b, 0x1b, 0x1b, 0x1b, 0x1a, 0x03, 0x51, 0x23 };
		DataInputStream is = new DataInputStream(new ByteArrayInputStream(message));
		SML_Message smlMessage = new SML_Message();
		// smlMessage.decode(is);
		Assert.assertTrue(smlMessage.decode(is));

	}

	@Test
	public void decodeSMLMessageWithShortCRCButLongEncoding() throws IOException {

		OctetString iReqField = new OctetString("0x0700000000000000");
		OctetString iClientId = new OctetString("0xcbec60477fd7");
		OctetString iServerId = new OctetString("0x1111111111111111");
		OctetString iUsername = new OctetString("edema");
		OctetString iPassword = new OctetString("edema");
		OctetString iTtransId = new OctetString("0x1500000000000000");

		SML_PublicOpenReq oreq = new SML_PublicOpenReq(null, iClientId, iReqField, iServerId, iUsername, iPassword,
				null);
		SML_Message msg = new SML_Message(iTtransId, new Unsigned8(0x04), new Unsigned8(0x00), new SML_MessageBody(
				SML_MessageBody.OpenRequest, oreq));

		ByteArrayOutputStream bso = new ByteArrayOutputStream(50);
		msg.code(new DataOutputStream(bso));

		ByteArrayInputStream bsi = new ByteArrayInputStream(bso.toByteArray());

		msg = new SML_Message();
		Assert.assertTrue(msg.decode(new DataInputStream(bsi)));
		msg.print();

	}

}