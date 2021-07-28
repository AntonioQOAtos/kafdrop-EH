package kafdrop.config;

import java.io.*;
import java.util.*;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroSerializerConfig;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.*;


@Component
@ConfigurationProperties(prefix = "kafka")
@Data
public final class KafkaConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaConfiguration.class);

  private String brokerConnect;
  private Boolean isSecured = false;
  private String saslMechanism;
  private String securityProtocol;
  private String truststoreFile;
  private String propertiesFile;
  private String keystoreFile;

  private String schemaRegistryClientId;
  private String schemaRegistryTenantId;
  private String schemaRegistryClientSecret;
  private String saslJaasConfig;
  private String registryUrl;
  private String schemaGroup;

  public void applyCommon(Properties properties) {
    properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, brokerConnect);
    if (isSecured) {
      LOG.warn("The 'isSecured' property is deprecated; consult README.md on the preferred way to configure security");
      properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
      properties.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
    }

    LOG.info("Checking truststore file {}", truststoreFile);
    if (new File(truststoreFile).isFile()) {
      LOG.info("Assigning truststore location to {}", truststoreFile);
      properties.put("ssl.truststore.location", truststoreFile);
    }

    LOG.info("Checking keystore file {}", keystoreFile);
    if (new File(keystoreFile).isFile()) {
      LOG.info("Assigning keystore location to {}", keystoreFile);
      properties.put("ssl.keystore.location", keystoreFile);
    }
    if (!StringUtils.isEmpty(saslJaasConfig) ) {
      properties.put("sasl.mechanism", "PLAIN");
      properties.put("sasl.jaas.config", saslJaasConfig);
      properties.put("security.protocol", "SASL_SSL");
    }
    /*
    if (!StringUtils.isEmpty(schemaRegistryClientId) && !StringUtils.isEmpty(schemaRegistryTenantId)
            && !StringUtils.isEmpty(schemaRegistryClientSecret)) {
      ClientSecretCredential azureCredential = new ClientSecretCredentialBuilder()
              .clientId(schemaRegistryClientId).tenantId(schemaRegistryTenantId)
              .clientSecret(schemaRegistryClientSecret).build();
      properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_CREDENTIAL_CONFIG, azureCredential);


      properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

      // Schema Registry configs
      properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroSerializer.class);
      properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, registryUrl);
      properties.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS_CONFIG, true);
      properties.put(KafkaAvroSerializerConfig.SCHEMA_GROUP_CONFIG, schemaGroup);
    }
   */
    LOG.info("Checking properties file {}", propertiesFile);
    final var propertiesFile = new File(this.propertiesFile);
    if (propertiesFile.isFile()) {
      LOG.info("Loading properties from {}", this.propertiesFile);
      final var propertyOverrides = new Properties();
      try (var propsReader = new BufferedReader(new FileReader(propertiesFile))) {
        propertyOverrides.load(propsReader);
      } catch (IOException e) {
        throw new KafkaConfigurationException(e);
      }
      properties.putAll(propertyOverrides);
    }
  }
}