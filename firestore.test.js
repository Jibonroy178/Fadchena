const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user profiles or scans", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("scans").get());
  await assertFails(unauthDb.collection("community_reports").get());
});

test("Authenticated user: can create and read own user profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Roy",
      email: "alice@example.com",
      createdAt: now,
      lastLoginAt: now,
    })
  );

  const doc = await aliceDb.collection("users").doc(ALICE_UID).get();
  assertSucceeds(Promise.resolve(doc));
});

test("Cross-user isolation: Bob cannot read or edit Alice profile or scans", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const now = new Date();
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      email: "alice@test.com",
      createdAt: now,
      lastLoginAt: now,
    });
    await context.firestore().collection("users").doc(ALICE_UID).collection("scans").doc("scan1").set({
      id: "scan1",
      userId: ALICE_UID,
      scanType: "SMS",
      inputSummary: "Bkash offer test",
      riskLevel: "HIGH_RISK",
      evidenceQuality: "STRONG",
      createdAt: now,
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("scans").doc("scan1").get());
});

test("Community Reports: Authenticated user can create and read community reports", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("community_reports").doc("rep1").set({
      id: "rep1",
      userId: ALICE_UID,
      userDisplayName: "Alice",
      target: "01712345678",
      category: "PHONE",
      description: "Fake lottery scam call pretending to be Nagad support",
      lossAmountBdt: 5000,
      evidenceNote: "Recorded audio and screenshot saved",
      status: "PENDING",
      reportCount: 1,
      reportedAt: now,
    })
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertSucceeds(bobDb.collection("community_reports").doc("rep1").get());
  // Bob cannot delete Alice's report
  await assertFails(bobDb.collection("community_reports").doc("rep1").delete());
});
