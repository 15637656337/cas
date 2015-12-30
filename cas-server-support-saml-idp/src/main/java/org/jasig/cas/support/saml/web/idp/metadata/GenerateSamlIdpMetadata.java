package org.jasig.cas.support.saml.web.idp.metadata;

import net.shibboleth.idp.installer.metadata.MetadataGenerator;
import net.shibboleth.idp.installer.metadata.MetadataGeneratorParameters;
import net.shibboleth.utilities.java.support.security.SelfSignedCertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The {@link GenerateSamlIdpMetadata} is responsible for
 * generating metadata and required certificates for signing
 * and encryption.
 *
 * @author Misagh Moayyed
 * @since 4.3.0
 */
public final class GenerateSamlIdpMetadata {
    private static final String URI_SUBJECT_ALTNAME_POSTFIX = "idp/metadata";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final File metadataFile;

    private final String entityId;
    private final String scope;
    private final String hostName;

    private final File signingCertFile;
    private final File signingKeyFile;

    private final File encryptionCertFile;
    private final File encryptionCertKeyFile;

    /**
     * Instantiates a new Generate saml metadata.
     *
     * @param metadataLocation the metadata location
     * @param hostName the host name
     * @param entityId the entity id
     * @param scope the scope
     */
    public GenerateSamlIdpMetadata(final File metadataLocation, final String hostName,
                                   final String entityId,
                                   final String scope) {
        this.entityId = entityId;
        this.hostName = hostName;
        this.scope = scope;

        if (!metadataLocation.exists()) {
            if (!metadataLocation.mkdir()) {
                throw new IllegalArgumentException("Metadata location cannot be located/created");
            }
        }
        this.metadataFile = new File(metadataLocation, "idp-metadata.xml");

        this.signingCertFile = new File(metadataLocation, "idp-signing.crt");
        this.signingKeyFile = new File(metadataLocation, "idp-signing.key");

        this.encryptionCertFile = new File(metadataLocation, "idp-encryption.crt");
        this.encryptionCertKeyFile = new File(metadataLocation, "idp-encryption.key");
    }

    public File getMetadataFile() {
        return metadataFile;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getScope() {
        return scope;
    }

    public String getHostName() {
        return hostName;
    }

    public File getSigningCertFile() {
        return signingCertFile;
    }

    public File getEncryptionCertFile() {
        return encryptionCertFile;
    }

    public boolean isMetadataMissing() {
        return !this.metadataFile.exists();
    }

    /**
     * Generate the certificates for signing, encryption and then metadata.
     */
    public void generate() {
        try {
            logger.debug("Creating self-sign certificate for signing...");
            buildSelfSignedSigningCert();

            logger.debug("Creating self-sign certificate for encryption...");
            buildSelfSignedEncryptionCert();

            logger.debug("Creating metadata...");
            buildMetadataGeneratorParameters();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Build self signed encryption cert.
     *
     * @throws Exception the exception
     */
    protected void buildSelfSignedEncryptionCert() throws Exception {
        final SelfSignedCertificateGenerator generator = new SelfSignedCertificateGenerator();
        generator.setHostName(this.hostName);
        generator.setCertificateFile(this.encryptionCertFile);
        generator.setPrivateKeyFile(this.encryptionCertKeyFile);
        generator.setURISubjectAltNames(Arrays.asList(this.hostName.concat(URI_SUBJECT_ALTNAME_POSTFIX)));
        generator.generate();
    }

    /**
     * Build self signed signing cert.
     *
     * @throws Exception the exception
     */
    protected void buildSelfSignedSigningCert() throws Exception {
        final SelfSignedCertificateGenerator generator = new SelfSignedCertificateGenerator();
        generator.setHostName(this.hostName);
        generator.setCertificateFile(this.signingCertFile);
        generator.setPrivateKeyFile(this.signingKeyFile);
        generator.setURISubjectAltNames(Arrays.asList(this.hostName.concat(URI_SUBJECT_ALTNAME_POSTFIX)));
        generator.generate();
    }

    /**
     * Build metadata generator parameters by passing the encryption,
     * signing and back-channel certs to the parameter generator.
     * @throws IOException Thrown if cert files are missing, or metadata file inaccessible.
     */
    protected void buildMetadataGeneratorParameters() throws IOException {
        final MetadataGenerator generator = new MetadataGenerator(this.metadataFile);
        final MetadataGeneratorParameters parameters = new MetadataGeneratorParameters();

        parameters.setEncryptionCert(this.encryptionCertFile);
        parameters.setSigningCert(this.signingCertFile);

        final List<List<String>> signing = new ArrayList<>(2);
        List<String> value = parameters.getBackchannelCert();
        if (null != value) {
            signing.add(value);
        }
        value = parameters.getSigningCert();
        if (null != value) {
            signing.add(value);
        }
        generator.setSigningCerts(signing);
        value = parameters.getEncryptionCert();
        if (null != value) {
            generator.setEncryptionCerts(Collections.singletonList(value));
        }

        generator.setDNSName(this.hostName);
        generator.setEntityID(this.entityId);
        generator.setScope(this.scope);
        generator.setSAML2AttributeQueryCommented(true);
        generator.setSAML2LogoutCommented(true);

        generator.generate();
    }
}
