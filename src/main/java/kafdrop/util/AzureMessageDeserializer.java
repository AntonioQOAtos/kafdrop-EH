package kafdrop.util;


import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroDeserializer;
import com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroSerializerConfig;
import kafdrop.config.AzureSchemaRegistryConfiguration;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;


import java.nio.ByteBuffer;
import java.util.HashMap;

@Slf4j
public final class AzureMessageDeserializer implements MessageDeserializer {
  private final String topicName;
  private final KafkaAvroDeserializer deserializer;
  private final AzureSchemaRegistryConfiguration.AzureSchemaRegistryProperties azureSchemaRegistryProperties;

  public AzureMessageDeserializer(String topicName, AzureSchemaRegistryConfiguration.AzureSchemaRegistryProperties azureSchemaRegistryProperties) {
    this.topicName = topicName;
    this.deserializer = getDeserializer(azureSchemaRegistryProperties);
    this.azureSchemaRegistryProperties = azureSchemaRegistryProperties;
  }

  @Override
  public String deserializeMessage(ByteBuffer buffer) {
    // Convert byte buffer to byte array
    final var bytes = ByteUtils.convertToByteArray(buffer);
    return deserializer.deserialize(topicName, bytes).toString();
  }

  private static KafkaAvroDeserializer getDeserializer(AzureSchemaRegistryConfiguration.AzureSchemaRegistryProperties azureSchemaRegistryProperties) {
    final var config = new HashMap<String, Object>();
    log.info("azureSchemaRegistryProperties [{}]", azureSchemaRegistryProperties);
    ClientSecretCredential azureCredential = new ClientSecretCredentialBuilder()
            .clientId(azureSchemaRegistryProperties.getSchemaRegistryClientId()).tenantId(azureSchemaRegistryProperties.getSchemaRegistryTenantId())
            .clientSecret(azureSchemaRegistryProperties.getSchemaRegistryClientSecret()).build();
    config.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_CREDENTIAL_CONFIG, azureCredential);
    // Schema Registry configs
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroDeserializer.class);
    config.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, azureSchemaRegistryProperties.getRegistryUrl());
    config.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS_CONFIG, true);
    config.put(KafkaAvroSerializerConfig.SCHEMA_GROUP_CONFIG, azureSchemaRegistryProperties.getSchemaGroup());

    final var kafkaAvroDeserializer = new KafkaAvroDeserializer();
    kafkaAvroDeserializer.configure(config, false);
    return kafkaAvroDeserializer;
  }
}
