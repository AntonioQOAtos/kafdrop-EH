package kafdrop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
public class AzureSchemaRegistryConfiguration {
    @Component
    @ConfigurationProperties(prefix = "azure")
    public static final class AzureSchemaRegistryProperties {

        private String schemaRegistryClientId;
        private String schemaRegistryTenantId;
        private String schemaRegistryClientSecret;
        private String saslJaasConfig;
        private String registryUrl;
        private String schemaGroup;

        public String getSchemaRegistryClientId() {
            return schemaRegistryClientId;
        }

        public void setSchemaRegistryClientId(String schemaRegistryClientId) {
            this.schemaRegistryClientId = schemaRegistryClientId;
        }

        public String getSchemaRegistryTenantId() {
            return schemaRegistryTenantId;
        }

        public void setSchemaRegistryTenantId(String schemaRegistryTenantId) {
            this.schemaRegistryTenantId = schemaRegistryTenantId;
        }

        public String getSchemaRegistryClientSecret() {
            return schemaRegistryClientSecret;
        }

        public void setSchemaRegistryClientSecret(String schemaRegistryClientSecret) {
            this.schemaRegistryClientSecret = schemaRegistryClientSecret;
        }

        public String getSaslJaasConfig() {
            return saslJaasConfig;
        }

        public void setSaslJaasConfig(String saslJaasConfig) {
            this.saslJaasConfig = saslJaasConfig;
        }

        public String getRegistryUrl() {
            return registryUrl;
        }

        public void setRegistryUrl(String registryUrl) {
            this.registryUrl = registryUrl;
        }

        public String getSchemaGroup() {
            return schemaGroup;
        }

        public void setSchemaGroup(String schemaGroup) {
            this.schemaGroup = schemaGroup;
        }

        @Override
        public String toString() {
            return "AzureSchemaRegistryProperties{" +
                    "schemaRegistryClientId='" + schemaRegistryClientId + '\'' +
                    ", schemaRegistryTenantId='" + schemaRegistryTenantId + '\'' +
                    ", schemaRegistryClientSecret='" + schemaRegistryClientSecret + '\'' +
                    ", saslJaasConfig='" + saslJaasConfig + '\'' +
                    ", registryUrl='" + registryUrl + '\'' +
                    ", schemaGroup='" + schemaGroup + '\'' +
                    '}';
        }
    }
}
