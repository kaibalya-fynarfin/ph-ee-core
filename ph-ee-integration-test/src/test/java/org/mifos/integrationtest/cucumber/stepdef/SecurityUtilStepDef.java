package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.mifos.connector.common.util.SecurityUtil;
import org.mifos.integrationtest.config.JWSKeyConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class SecurityUtilStepDef extends BaseStepDef {

    @Autowired
    JWSKeyConfig jwsKeyConfig;

    @Given("generate random data")
    public void generateRandomData() {
        // Write code here that turns the phrase above into concrete actions
        scenarioScopeState.randomData = UUID.randomUUID().toString();
        logger.debug("Random data: {}", scenarioScopeState.randomData);
    }

    @When("encrypt the data with the {string}")
    public void encryptTheDataWithThe(String encryptionKey) throws NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        scenarioScopeState.encryptedData = SecurityUtil.encryptUsingPublicKey(scenarioScopeState.randomData, encryptionKey);
        logger.debug("Encrypted data: {}", scenarioScopeState.encryptedData);
    }

    @Then("encrypted data is not null")
    public void encryptedDataIsNotNull() {
        assertThat(scenarioScopeState.encryptedData).isNotNull();
    }

    @When("encrypted data is decrypted using the {string}")
    public void encryptedDataIsDecryptedUsingThe(String decryptionKey) throws NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        scenarioScopeState.decryptedData = SecurityUtil.decryptUsingPrivateKey(scenarioScopeState.encryptedData, decryptionKey);
        logger.debug("Decrypted data: {} ", scenarioScopeState.decryptedData);
    }

    @Then("compare the decrypted data with the original data")
    public void compareTheDecryptedDataWithTheOriginalData() {
        assertThat(scenarioScopeState.decryptedData).isEqualTo(scenarioScopeState.randomData);
    }

    @Given("I have public key {string}")
    public void setPublicKey(String publicKeyString) {
        scenarioScopeState.publicKeyString = publicKeyString;
        assertThat(scenarioScopeState.publicKeyString).isNotEmpty();
    }

    @And("I have private key")
    public void setPrivateKey() {
        assertThat(jwsKeyConfig).isNotNull();
        scenarioScopeState.privateKeyString = jwsKeyConfig.privateKey;
        assertThat(scenarioScopeState.privateKeyString).isNotEmpty();
    }

    @When("I get the publicKey object from string")
    public void getPublicKeyObject() throws NoSuchAlgorithmException, InvalidKeySpecException {
        scenarioScopeState.publicKey = SecurityUtil.getPublicKeyFromString(scenarioScopeState.publicKeyString);
        assertThat(scenarioScopeState.publicKey).isNotNull();
    }

    @Then("I should be able to get string from publicKey object")
    public void getPublicKeyStringFromPublicKey() {
        String pkString = SecurityUtil.getStringFromPublicKey(this.scenarioScopeState.publicKey);
        assertThat(pkString).isNotEmpty();
        scenarioScopeState.newPublicKeyString = pkString;
        logger.debug("Parsed public key: {}", pkString);
    }

    @And("It should be equal to original key")
    public void comparePublicKeyString() {
        assertThat(scenarioScopeState.publicKeyString).isEqualTo(scenarioScopeState.newPublicKeyString);
    }

}
