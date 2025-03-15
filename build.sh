mvn package -Pnative
podman build -t registry.ti20.de/public/git-webhook:latest .
podman kube play git-webhook-pod.yaml