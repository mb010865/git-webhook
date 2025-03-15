FROM registry.access.redhat.com/ubi9/ubi-minimal
WORKDIR /app
RUN microdnf install git openssh-clients -y && microdnf clean all
COPY target/git-webhook-1.0.0-SNAPSHOT-runner /app/git-webhook
# Note: Repo paths and SSH keys are mounted at runtime, not copied here
RUN mkdir -p /home/user/.ssh && chown 1001:0 /home/user/.ssh
RUN mkdir -p /config && chown 1001:0 /config
RUN chmod +x /app/git-webhook
USER 1001
EXPOSE 8080
CMD ["/app/git-webhook", "-Dquarkus.http.host=0.0.0.0"]