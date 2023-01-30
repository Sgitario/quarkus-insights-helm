# Quarkus Insights Helm Demostration

Requirements:
- Logged in an OpenShift instance
- Logged in [quay.io](https://quay.io) using [docker](https://www.docker.com/)
- ArgoCD up and running (see setup guide [here](./argocd-installation.md))
- ChartMuseum up and running (see setup guide [here](./chartmuseum-installation.md))

## First showcase: getting started, container registry, kubernetes, predefined properties

1.- Create Quarkus application

```
mvn io.quarkus.platform:quarkus-maven-plugin:2.16.0.Final:create \
    -DprojectGroupId=org.acme \
    -DprojectArtifactId=demo
    -Dextensions="resteasy-reactive"
cd demo
```

2.- Run in Dev mode

```
mvn quarkus:dev
```

3.- Configure container image docker

```
./mvnw quarkus:add-extension -Dextensions='container-image-docker'
```

Then, configure it:

```
quarkus.container-image.build=true
quarkus.container-image.push=true

quarkus.container-image.registry=quay.io
quarkus.container-image.group=jcarvaja
// or 
quarkus.container-image.image=quay.io/jcarvaja/demo:0.0.1
```

And build:

```
mvn clean install -DskipTests
```

4.- Configure kubernetes resources

```
./mvnw quarkus:add-extension -Dextensions='openshift'
```

And select the container builder to docker

```
quarkus.container-image.builder=docker
```

Expose route:

```
quarkus.openshift.route.expose=true
```

Use Deployment kind:

```
quarkus.openshift.deployment-kind=Deployment
```

5.- Configure helm resources

```
./mvnw quarkus:add-extension -Dextensions='helm'
```

Specify output directory

```
quarkus.helm.output-directory=/home/jcarvaja/sources/personal/quarkus-insights-helm
```

Commit and push!
```
cd /home/jcarvaja/sources/personal/quarkus-insights-helm
git checkout -b demo origin/main
git add --all
git commit -m "Create helm charts"
git push origin demo
```

6.- ArgoCD: create application from github repository, showcase

## Second Showcase: postgresql, helm, mapping user properties