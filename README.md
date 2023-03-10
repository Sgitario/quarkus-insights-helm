# Quarkus Insights Helm Demostration

Requirements:
- Logged in an OpenShift instance
- Logged in [quay.io](https://quay.io) using [docker](https://www.docker.com/)
- ArgoCD up and running (see setup guide [here](./argocd-installation.md)): https://demo-argocd-server-demo.snowdrop-eu-de-1-bx2-4x16-0c576f1a70d464f092d8591997631748-0000.eu-de.containers.appdomain.cloud/
- ChartMuseum up and running (see setup guide [here](./chartmuseum-installation.md)): http://chartmuseum-helm-repository.snowdrop-eu-de-1-bx2-4x16-0c576f1a70d464f092d8591997631748-0000.eu-de.containers.appdomain.cloud/api/charts
- Repo link: https://github.com/Sgitario/quarkus-insights-helm

## First showcase: getting started, container registry, kubernetes, predefined properties

1.- Create Quarkus application

```
quarkus create app --extension resteasy-reactive-jackson org:acme
cd acme
```

2.- Run in Dev mode

```
mvn quarkus:dev
```

3.- Configure container image docker

```
quarkus extension add container-image-docker
```

Then, configure it:

```
quarkus.container-image.build=true
quarkus.container-image.push=true
quarkus.container-image.image=quay.io/jcarvaja/demo:0.0.1
```

And build:

```
mvn clean install -DskipTests
```

4.- Configure kubernetes resources

```
quarkus extension add openshift
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
quarkus extension add helm
```

Specify output directory

```
quarkus.helm.output-directory=../helm
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
url: https://demo-argocd-server-demo.snowdrop-eu-de-1-bx2-4x16-0c576f1a70d464f092d8591997631748-0000.eu-de.containers.appdomain.cloud/applications
repo: https://github.com/Sgitario/quarkus-insights-helm

## Second Showcase: postgresql, helm, mapping user properties

1.- Configure REST Data with Panache

```
quarkus extension add quarkus-hibernate-orm-rest-data-panache
quarkus extension add jdbc-postgresql
```

add entity

```java
@Entity
public class Fruit extends PanacheEntity {
    public String name;
}
```

add the REST Data with Panache entity:

```java
public interface FruitResource extends PanacheEntityResource<Fruit, Long> {
}
```

add import.sql:

```sql
insert into Fruit(id, name) values (1, 'apple');
insert into Fruit(id, name) values (2, 'banana');
```

browse:

`localhost:8080/fruit`

2.- Configure Hibernate ORM for production:

Initialize data
```
quarkus.hibernate-orm.sql-load-script=import.sql
quarkus.hibernate-orm.database.generation=drop-and-create
```

JDBC URL
```
quarkus.datasource.jdbc.url=${POSTGRESQL_URL}
quarkus.datasource.username=${POSTGRESQL_USERNAME}
quarkus.datasource.password=${POSTGRESQL_PASSWORD}
```

3.- Container image is ready

build and push

4.- Configure Kubernetes

```
quarkus.openshift.env.vars.POSTGRESQL_URL=jdbc:postgresql://host:1111/database
quarkus.openshift.env.vars.POSTGRESQL_USERNAME=user
quarkus.openshift.env.vars.POSTGRESQL_PASSWORD=pass
```

Explain that this will be mapped in values.yml file!!!

5.- Configure Helm

add postgresql:

```
quarkus.helm.dependencies.postgresql.repository=https://charts.bitnami.com/bitnami
quarkus.helm.dependencies.postgresql.version=11.9.1
quarkus.helm.dependencies.postgresql.alias=db
```

configure postgresql:

```
quarkus.helm.values."db.auth.database".value=my_database
quarkus.helm.values."db.auth.username".value=user
quarkus.helm.values."db.auth.password".value=pass
```

```
quarkus.helm.values."db.volumePermissions.enabled".value-as-bool=false
quarkus.helm.values."db.volumePermissions.securityContext.runAsUser".value=auto
quarkus.helm.values."db.securityContext.enabled".value-as-bool=false
quarkus.helm.values."db.shmVolume.chmod.enabled".value-as-bool=false
quarkus.helm.values."db.primary.containerSecurityContext.enabled".value-as-bool=false
quarkus.helm.values."db.primary.containerSecurityContext.runAsUser".value=auto
quarkus.helm.values."db.primary.podSecurityContext.enabled".value-as-bool=false
quarkus.helm.values."db.primary.podSecurityContext.fsGroup".value=auto
```

overwrite jdbc url:

```
quarkus.helm.values."app.envs.POSTGRESQL_URL".value=jdbc:postgresql://demo-db:5432/my_database
```

6.- Provide user values.yaml

create in src/main/helm/values.yaml

```
app:
  envs:
    POSTGRESQL_URL: jdbc:postgresql://demo-db:5432/my_database
db:
  auth:
    database: my_database
    password: pass
    username: user
  securityContext:
    enabled: false
  volumePermissions:
    securityContext:
      runAsUser: auto
    enabled: false
  shmVolume:
    chmod:
      enabled: false
  primary:
    podSecurityContext:
      fsGroup: auto
      enabled: false
    containerSecurityContext:
      runAsUser: auto
      enabled: false
```

7.- ArgoCD: update application from github repository, showcase

8.- Delete application in ArgoCD

9.- Configure helm repository

```
quarkus.helm.repository.type=CHARTMUSEUM
quarkus.helm.repository.url=http://chartmuseum-helm-repository.snowdrop-eu-de-1-bx2-4x16-0c576f1a70d464f092d8591997631748-0000.eu-de.containers.appdomain.cloud/api/charts
quarkus.helm.repository.push=true
quarkus.helm.repository.deployment-target=openshift
```

10.- ArgoCD: create the application again from the Helm repository

## Third Showcase: mapping more properties, helm profiles

1.- Map ImagePullPolicy

Explain about YAMLPath: https://github.com/yaml-path/YamlPath

Using wildcards:

```
quarkus.helm.values.imagePullPolicy.paths=*.containers.(name == demo).imagePullPolicy
```

And multiple locations:

```
quarkus.helm.values.labelVersion.paths=*.labels.'app.kubernetes.io/version',*.matchLabels.'app.kubernetes.io/version'
```

And overwrite the value:

```
quarkus.helm.values.labelVersion.paths=*.labels.'app.kubernetes.io/version',*.matchLabels.'app.kubernetes.io/version'
quarkus.helm.values.labelVersion.value=0.0.5
```

2.- Helm profiles

Add host to route (it also works for Ingress)
```
quarkus.openshift.route.host=prod-host
```

Create new dev profile:

```
quarkus.helm.values.host.value=dev-host
quarkus.helm.values.host.profile=dev
```