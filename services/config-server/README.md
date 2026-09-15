# Config Server

Provides centralized configuration on port `8888` using Spring Cloud Config Server's native backend. Versioned configuration files are stored under `src/main/resources/config-repo` and packaged inside the image, so no Nexus, Artifactory, or OpenShift Maven repository is required.

## Security

Sensitive values are represented with `{cipher}` placeholders. Set `CONFIG_ENCRYPT_KEY` as an OpenShift Secret; never commit the encryption key. The server's encrypt/decrypt endpoints can be used to prepare values before placing them in the config repository. Replace the example encrypted placeholders in `config-repo` with ciphertext generated using the deployed key; plaintext secrets must not be committed.

Required environment variables for a deployment include:

- `CONFIG_ENCRYPT_KEY`
- `CONFIG_SERVER_USERNAME`
- `CONFIG_SERVER_PASSWORD`

The config server itself should be protected by an OpenShift Service and NetworkPolicy, not exposed publicly.

## Endpoints

- `GET /{application}/{profile}` serves configuration.
- `GET /encrypt/status` reports encryption availability.
- `POST /encrypt` encrypts a value when authentication is configured.
- `GET /actuator/health` exposes health status.

Services may import this server with `spring.config.import=optional:configserver:http://config-server:8888` when deployed. `optional:` keeps local development possible when the Config Server is not running.