package org.hyperledger.indy.sdk.ledger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hyperledger.indy.sdk.IndyIntegrationTestWithPoolAndSingleWallet;
import org.hyperledger.indy.sdk.utils.PoolUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GetTxnRequestTest extends IndyIntegrationTestWithPoolAndSingleWallet {

	@Test
	public void testBuildGetTxnRequestWorks() throws Exception {
		int data = 1;
		String expectedResult = String.format("\"identifier\":\"%s\"," +
				"\"operation\":{" +
				"\"type\":\"3\"," +
				"\"data\":%s" +
				"}", DID, data);

		String getTxnRequest = Ledger.buildGetTxnRequest(DID, data).get();
		assertTrue(getTxnRequest.replace("\\", "").contains(expectedResult));
	}

	private static Set<String> keySet(JSONObject jsonObject) {
		HashSet<String> set = new HashSet<String>();
		Iterator<String> keys = jsonObject.keys();
		while (keys.hasNext()) {
			set.add(keys.next());
		}
		return set;
	}
	/**
	 * Determine if two JSONObjects are similar.
	 * They must contain the same set of names which must be associated with
	 * similar values.
	 *
	 * @param that The other JSONObject
	 * @param other The other JSONObject
	 * @return true if they are equal
	 */
	private static boolean similar(JSONArray that, Object other) {
		try {
			if (!(other instanceof JSONArray)) {
				return false;
			}
			if (that.length() != ((JSONArray)other).length()) {
				return false;
			}
			for (int i=0; i<that.length(); i++) {
				Object valueThis = that.get(i);
				Object valueOther = ((JSONArray)other).get(i);
				if (valueThis instanceof JSONObject) {
					if (!similar((JSONObject)valueThis,valueOther)) {
						return false;
					}
				} else if (valueThis instanceof JSONArray) {
					if (!similar((JSONArray)valueThis,valueOther)) {
						return false;
					}
				} else if (!valueThis.equals(valueOther)) {
					return false;
				}
			}
			return true;
		} catch (Throwable exception) {
			return false;
		}
	}
	private static boolean similar(JSONObject that, Object other) {
		try {
			if (!(other instanceof JSONObject)) {
				return false;
			}
			Set<String> set = keySet(that);
			if (!set.equals((keySet((JSONObject)other)))) {
				return false;
			}
			Iterator<String> iterator = set.iterator();
			while (iterator.hasNext()) {
				String name = iterator.next();
				Object valueThis = that.get(name);
				Object valueOther = ((JSONObject)other).get(name);
				if (valueThis instanceof JSONObject) {
					if (!similar((JSONObject)valueThis,valueOther)) {
						return false;
					}
				} else if (valueThis instanceof JSONArray) {
					if (!similar((JSONArray)valueThis,valueOther)) {
						return false;
					}
				} else if (!valueThis.equals(valueOther)) {
					return false;
				}
			}
			return true;
		} catch (Throwable exception) {
			return false;
		}
	}

	@Test(timeout = PoolUtils.TEST_TIMEOUT_FOR_REQUEST_ENSURE)
	public void testGetTxnRequestWorks() throws Exception {
		String did = createStoreAndPublishDidFromTrustee();

		String schemaRequest = Ledger.buildSchemaRequest(did, SCHEMA_DATA).get();
		String schemaResponse = Ledger.signAndSubmitRequest(pool, wallet, did, schemaRequest).get();

		JSONObject schemaResponseObj = new JSONObject(schemaResponse);
		int seqNo = schemaResponseObj.getJSONObject("result").getInt("seqNo");

		String getTxnRequest = Ledger.buildGetTxnRequest(did, seqNo).get();
		String expectedData = "{\"name\":\"gvt\",\"version\":\"1.0\",\"attr_names\": [\"name\"]}";

		String getTxnResponse = PoolUtils.ensurePreviousRequestApplied(pool, getTxnRequest, response -> {
			JSONObject getTxnResponseObj = new JSONObject(response);
			JSONObject schemaTransactionObj = getTxnResponseObj.getJSONObject("result").getJSONObject("data");

			return similar(new JSONObject(expectedData),schemaTransactionObj.getJSONObject("data"));
		});
		assertNotNull(getTxnResponse);
	}

	@Test
	public void testGetTxnRequestWorksForInvalidSeqNo() throws Exception {
		String did = createStoreAndPublishDidFromTrustee();

		String schemaRequest = Ledger.buildSchemaRequest(did, SCHEMA_DATA).get();
		String schemaResponse = Ledger.signAndSubmitRequest(pool, wallet, did, schemaRequest).get();

		JSONObject schemaResponseObj = new JSONObject(schemaResponse);
		int seqNo = schemaResponseObj.getJSONObject("result").getInt("seqNo") + 1;

		String getTxnRequest = Ledger.buildGetTxnRequest(did, seqNo).get();
		String getTxnResponse = Ledger.submitRequest(pool, getTxnRequest).get();

		JSONObject getTxnResponseObj = new JSONObject(getTxnResponse);
		assertTrue(getTxnResponseObj.getJSONObject("result").isNull("data"));
	}
}
