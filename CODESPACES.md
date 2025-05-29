# GitHub Codespaces Configuration

This document explains the changes made to make this project work with GitHub Codespaces.

## Changes Made

### 1. devcontainer.json

- Changed `workspaceFolder` from `/dev-container-quarkus` to `/workspaces/${localWorkspaceFolderBasename}` (GitHub Codespaces standard path)
- Updated deprecated VS Code settings:
  - Replaced `terminal.integrated.shell.linux` with `terminal.integrated.defaultProfile.linux`
- Added additional VS Code extensions for better Java development experience:
  - `redhat.java`
  - `visualstudioexptteam.vscodeintellicode`
- Added Java-specific VS Code settings:
  - `java.server.launchMode`: "Standard"
  - `java.configuration.updateBuildConfiguration`: "automatic"
- Added more ports to forward:
  - 5432 (PostgreSQL)
  - 9092 (Kafka)
- Added `remoteUser`: "root" to ensure proper permissions
- Added `features` section with Java configuration (set to "none" since Java is installed in the Dockerfile)

### 2. docker-compose.yml

- Updated volume mounts for the quarkus-dev service:
  - Changed from specific directory mounts to mounting the parent directory to `/workspaces`
  - Added `:cached` option for better performance
- Updated `working_dir` from `/dev-container-quarkus` to `/workspaces/dev-container-quarkus`

### 3. Dockerfile

- Updated `WORKDIR` from `/dev-container-quarkus` to `/workspaces/dev-container-quarkus`
- Updated `VOLUME` from `/dev-container-quarkus` to `/workspaces`

## Using with GitHub Codespaces

1. Push this repository to GitHub
2. Navigate to the repository on GitHub
3. Click the "Code" button
4. Select the "Codespaces" tab
5. Click "Create codespace on main"

GitHub will create a new Codespace with the configured development environment, including:
- Quarkus development environment
- PostgreSQL database
- Kafka message broker
- All necessary tools and extensions

## Troubleshooting

If you encounter any issues:

1. Check the Codespaces logs for any error messages
2. Ensure all services are running with `docker-compose ps`
3. Check the application logs with `docker-compose logs quarkus-dev`

### Known Issues

#### localWorkspaceFolderBasename Variable

The `${localWorkspaceFolderBasename}` variable is only available within the devcontainer.json context and is not automatically passed to docker-compose.yml or Dockerfile. To address this issue, we've replaced the variable with a hardcoded value (`dev-container-quarkus`) in both docker-compose.yml and Dockerfile.

If you rename your repository or clone it with a different name, you may need to update these hardcoded values to match your actual folder name.
