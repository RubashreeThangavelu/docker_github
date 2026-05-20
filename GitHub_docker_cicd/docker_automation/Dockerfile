FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app
# create user
RUN groupadd -r appgroup && useradd -r -g appgroup -m appuser

# create restricted folder
RUN mkdir -p /app/restricted_backup \
 && chown appuser:appgroup /app/restricted_backup \
 && chmod 500 /app/restricted_backup

# Copy dependencies first (cache optimization)
COPY pom.xml .
RUN mvn org.apache.maven.plugins:maven-dependency-plugin:3.6.1:go-offline


# Copy source
COPY src ./src

# Copy testng suite
COPY src/test/resources/testng.xml ./testng.xml

# Init script
COPY init-data.sh /init-data.sh
RUN chmod +x /init-data.sh

ENV TZ=Asia/Kolkata

# Run tests safely (no manual rm)
CMD ["tail", "-f", "/dev/null"]
