# Installation guide

`oc run chartmuseum --image=chartmuseum/chartmuseum:latest --port=8080 --env="STORAGE=local" --env="STORAGE_LOCAL_ROOTDIR=/tmp"`

Note that we're using an ephemeral folder on purpose, to be able to reset the charts when we rollout the chartmusem deployment.

```
oc expose dc/chartmuseum
oc expose service/chartmuseum
```