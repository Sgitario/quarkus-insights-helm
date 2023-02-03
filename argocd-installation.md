# Installation guide

Install via operator

## Expose ArgoCD in OpenShift

```yaml
apiVersion: argoproj.io/v1alpha1
kind: ArgoCD
metadata:
  name: demo-argocd
  namespace: argocd
spec:
  controller:
    env:
    - name: ARGOCD_APPLICATION_CONTROLLER_REPO_SERVER_TIMEOUT_SECONDS
      value: '10'
  server:
    insecure: true
    route:
      enabled: true
```

## Get Username and Password

```
ARGOCD_NAMESPACE=argocd
kubectl -n $ARGOCD_NAMESPACE get secret demo-argocd-cluster -o jsonpath='{.data.admin\.password}' | base64 -d
```
USER is admin!