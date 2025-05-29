FROM fedora:41

LABEL maintainer="Jeremy's DevOps Team" \
      description="Fedora 41 with OpenJDK 21, Maven, Quarkus CLI 3.20.0, and kcat for Quarkus development"

# Set environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
    MAVEN_HOME=/usr/share/maven \
    MAVEN_VERSION=3.9.5 \
    MAVEN_OPTS="-Xmx1024m" \
    PATH=$PATH:/usr/lib/jvm/java-21-openjdk/bin:/usr/share/maven/bin:/root/.quarkus/bin

# Install required packages
# Group installation commands to reduce layrs and optimize caching
RUN dnf -y update && \
    dnf -y install \
    java-21-openjdk-devel \
    maven \
    wget \
    curl \
    unzip \
    git \
    jq \
    which \
    tar \
    gzip \
    procps \
    findutils \
    # Install kcat (formerly kafkacat)
    librdkafka-devel \
    kcat && \
    # Clean up to reduce image size
    dnf clean all && \
    rm -rf /var/cache/dnf

RUN echo "Fetching latest Quarkus CLI version..." && \
    LATEST_QUARKUS_VERSION=$(curl -s https://repo1.maven.org/maven2/io/quarkus/quarkus-cli/maven-metadata.xml | grep -oP "(?<=<latest>).*(?=</latest>)") && \
    echo "Latest Quarkus version is: ${LATEST_QUARKUS_VERSION}" && \
    echo "QUARKUS_VERSION=${LATEST_QUARKUS_VERSION}" >> /etc/environment && \
    echo "QUARKUS_CLI_VERSION=${LATEST_QUARKUS_VERSION}" >> /etc/environment && \
    export QUARKUS_VERSION=${LATEST_QUARKUS_VERSION} && \
    export QUARKUS_CLI_VERSION=${LATEST_QUARKUS_VERSION}

# Install JBang and Quarkus CLI
RUN curl -Ls https://sh.jbang.dev | bash -s - app setup && \
    curl -Ls https://sh.jbang.dev | bash -s - trust add https://repo1.maven.org/maven2/io/quarkus/quarkus-cli/ && \
    curl -Ls https://sh.jbang.dev | bash -s - app install --fresh --force quarkus@quarkusio && \
    mkdir -p /root/.m2 && \
    echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" \
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 \
                          https://maven.apache.org/xsd/settings-1.0.0.xsd"> \
      <localRepository>/root/.m2/repository</localRepository> \
      <interactiveMode>false</interactiveMode> \
      <mirrors> \
        <mirror> \
          <id>maven-default-http-blocker</id> \
          <mirrorOf>external:http:*</mirrorOf> \
          <name>Pseudo repository to mirror external repositories initially using HTTP.</name> \
          <url>http://0.0.0.0/</url> \
          <blocked>true</blocked> \
        </mirror> \
      </mirrors> \
      <profiles> \
        <profile> \
          <id>quarkus</id> \
          <repositories> \
            <repository> \
              <id>central</id> \
              <name>Maven Central</name> \
              <url>https://repo.maven.apache.org/maven2</url> \
            </repository> \
            <repository> \
              <id>quarkus-repo</id> \
              <name>Quarkus Repository</name> \
              <url>https://repository.jboss.org/nexus/content/groups/public</url> \
            </repository> \
          </repositories> \
        </profile> \
      </profiles> \
      <activeProfiles> \
        <activeProfile>quarkus</activeProfile> \
      </activeProfiles> \
    </settings>' > /root/.m2/settings.xml

ENV PATH="/root/.jbang/bin:${PATH}"

# Pre-download Quarkus Maven plugin to improve startup time for new projects
RUN mvn dependency:get -Dartifact=io.quarkus:quarkus-maven-plugin:LATEST:jar -DremoteRepositories=central::default::https://repo.maven.apache.org/maven2 && \
    mvn dependency:get -Dartifact=io.quarkus:quarkus-bom:LATEST:pom -DremoteRepositories=central::default::https://repo.maven.apache.org/maven2

# Verify all installations work properly
RUN echo "Verifying installations:" && \
    java -version && \
    mvn -version && \
    kcat -V && \
    quarkus --version && \
    echo "All tools successfully installed"

WORKDIR /workspaces/${localWorkspaceFolderBasename}

VOLUME /workspaces

# Expose common ports for Quarkus development
EXPOSE 8080 8443 5005

# Launch in Quarkus dev mode via the CLI
CMD ["quarkus", "dev", "-Dquarkus.http.host=0.0.0.0"]
